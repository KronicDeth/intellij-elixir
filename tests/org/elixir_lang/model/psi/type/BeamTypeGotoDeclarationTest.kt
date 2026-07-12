package org.elixir_lang.model.psi.type

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.gotoDeclarationTargetElement
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import java.io.File

/**
 * Behavioural Go To Declaration coverage for types defined in decompiled BEAM modules: remote Erlang
 * library types (`:queue.queue/0`) and the built-in types faked onto `:erlang` (`integer/0`). Unlike
 * source `@type`s these resolve to `TypeDefinitionImpl` decompiled PSI, so they exercise the branch of
 * the type reference that navigates into the `.beam` mirror rather than a source attribute.
 */
class BeamTypeGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        addBeamLibrary()
    }

    fun testCtrlClickOnRemoteBeamTypeChoosesGotoDeclaration() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToRemoteBeamType() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")
        val target = myFixture.gotoDeclarationTargetElement(project)
        assertNotNull("Go To Declaration should navigate into the decompiled :queue module", target)
        assertTrue(
            "Should land in the decompiled queue.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("queue")
        )
        assertTrue(
            "Should land on the decompiled `@type queue` definition (was '${target.text}')",
            target.text.startsWith("@type queue")
        )
    }

    fun testCtrlClickOnBuiltinTypeChoosesGotoDeclaration() {
        myFixture.configureByFiles("builtin_type_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToBuiltinType() {
        myFixture.configureByFiles("builtin_type_goto.ex")
        val target = myFixture.gotoDeclarationTargetElement(project)
        assertNotNull("Go To Declaration should navigate into the decompiled :erlang module", target)
        assertTrue(
            "Should land in the decompiled erlang.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("erlang")
        )
        assertTrue(
            "Should land on the decompiled `@type integer` definition (was '${target.text}')",
            target.text.startsWith("@type integer")
        )
    }

    fun testFindUsagesFromRemoteBeamTypeUsageFindsSourceUsages() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        assertEquals(
            "Find Usages from a `:queue.queue()` usage should find both source spec sites",
            2,
            myFixture.nonDeclarationUsageCountAtCaret(project)
        )
    }

    fun testFindUsagesFromBuiltinTypeUsageFindsSourceUsages() {
        myFixture.configureByFiles("builtin_type_find_usages.ex")
        assertTrue(
            "Find Usages from an `integer()` usage should find both source spec sites",
            myFixture.nonDeclarationUsageCountAtCaret(project) >= 2
        )
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
        private const val BEAM_LIBRARY_NAME = "type-goto-beam-lib"
    }
}
