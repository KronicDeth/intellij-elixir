package org.elixir_lang.model.psi.function

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.gotoDeclarationDestinationAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import java.io.File

/**
 * Behavioural Go To Declaration and Find Usages coverage for functions defined in decompiled BEAM modules, e.g.
 * `:queue.new/0` from the Erlang stdlib. Unlike source `def`s these resolve to `CallDefinitionImpl` decompiled PSI,
 * so they exercise the branch of the call reference that navigates into the `.beam` mirror rather than a source clause.
 */
class BeamFunctionGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/function"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        addBeamLibrary()
    }

    fun testCtrlClickOnRemoteBeamFunctionChoosesGotoDeclaration() {
        myFixture.configureByFiles("beam_qualified_call_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToRemoteBeamFunction() {
        myFixture.configureByFiles("beam_qualified_call_goto.ex")
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration should navigate into the decompiled :queue module", target)
        assertTrue(
            "Should land in the decompiled queue.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("queue")
        )
        assertTrue(
            "Should land on the decompiled `def new` definition (was '${target.text}')",
            target.text.contains("new")
        )
    }

    fun testFindUsagesFromRemoteBeamFunctionUsageFindsSourceUsages() {
        myFixture.configureByFiles("beam_qualified_call_find_usages.ex")
        assertEquals(
            "Find Usages from a `:queue.new()` call should find the two source call sites plus the within-beam " +
                "call site of `new/0` in the decompiled queue.beam",
            3,
            myFixture.nonDeclarationUsageCountAtCaret(project)
        )
    }

    fun testFindUsagesFromWithinBeamFunctionFindsSourceUsages() {
        myFixture.configureByFiles("beam_qualified_call_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "def new")
        assertEquals(
            "Find Usages on the `def new` clause inside the decompiled queue.beam should find the two source call " +
                "sites plus the within-beam call site of `new/0` - the same total as from a source `:queue.new()` " +
                "call of the same symbol",
            3,
            myFixture.nonDeclarationUsageCountAtCaret(project)
        )
    }

    fun testCtrlClickWithinBeamFunctionChoosesShowUsages() {
        myFixture.configureByFiles("beam_qualified_call_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "def new")
        myFixture.assertShowUsagesChosenAtCaret(
            "Ctrl+Click on the `def new` clause inside the decompiled queue.beam should choose show-usages"
        )
    }

    fun testGoToDeclarationFromWithinBeamFunctionUsageNavigatesToDefinition() {
        myFixture.configureByFiles("beam_qualified_call_find_usages.ex")
        // Caret on the `in_r` call in the body of `def cons(x, q), do: in_r(x, q)`; that is a usage of the
        // `in_r/2` function, defined in the same beam. `in_r/2` has several clauses, so Ctrl+Click offers a
        // chooser of declarations (outcome GTD) rather than jumping straight to one - exactly as it would for a
        // multi-clause source function. Before the fix the caret sat on the coarse `def cons` declaration and the
        // outcome was "show usages", so nothing navigated to `in_r`.
        openBeamAndMoveCaretTo("queue.beam", "do: in_r")
        myFixture.assertGotoDeclarationChosenAtCaret(
            "Ctrl+Click on the `in_r` call inside the decompiled queue.beam should navigate to its declaration"
        )
    }

    private fun openBeamAndMoveCaretTo(beamName: String, anchor: String) {
        val beamIo = File(File(testDataPath, "ebin").absoluteFile, beamName)
        val beamVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(beamIo)
        assertNotNull("Could not find decompiled $beamName in VFS", beamVf)
        myFixture.configureFromExistingVirtualFile(beamVf!!)
        val text = myFixture.editor.document.text
        val anchorIndex = text.indexOf(anchor)
        assertTrue("Anchor '$anchor' not found in decompiled $beamName", anchorIndex >= 0)
        myFixture.editor.caretModel.moveToOffset(anchorIndex + anchor.length - 1)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        try {
            removeBeamLibrary()
        } finally {
            super.tearDown()
        }
    }

    private fun addBeamLibrary() {
        val ebinDir = File(testDataPath, "ebin").absoluteFile
        assertTrue("ebin/ fixture directory not found at ${ebinDir.absolutePath}", ebinDir.isDirectory)

        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, ebinDir.path)

        val ebinVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ fixture directory in VFS", ebinVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(BEAM_LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(ebinVf!!, OrderRootType.CLASSES)
            libModel.commit()
            model.commit()

            ModuleRootModificationUtil.addDependency(myFixture.module, library)
        }
    }

    private fun removeBeamLibrary() {
        ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
            model.orderEntries
                .filter { it.presentableName == BEAM_LIBRARY_NAME }
                .forEach { model.removeOrderEntry(it) }
        }

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            libraryTable.getLibraryByName(BEAM_LIBRARY_NAME)?.let { library ->
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }

    companion object {
        private const val BEAM_LIBRARY_NAME = "function-goto-beam-lib"
    }
}
