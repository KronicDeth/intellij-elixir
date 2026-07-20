package org.elixir_lang.model.psi.module

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.gotoDeclarationDestinationAtCaret
import java.io.File

/**
 * Behavioural Go To Declaration coverage for module aliases that resolve only to a compiled `.beam`
 * (e.g. `Code` from the Elixir stdlib). These resolve to the coarse `beam.psi.impl.ModuleImpl` stub, so
 * they exercise the [ModuleReference] branch that routes through `navigationElement` into the decompiled
 * mirror's `defmodule` - without it the beam result is silently dropped and Ctrl+Click does nothing.
 */
class BeamModuleGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        addBeamLibrary()
    }

    fun testCtrlClickOnBeamModuleQualifierChoosesGotoDeclaration() {
        myFixture.configureByFiles("beam_module_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromBeamModuleQualifierNavigatesToDecompiledDefmodule() {
        myFixture.configureByFiles("beam_module_goto.ex")
        assertNavigatesToDecompiledCode()
    }

    fun testCtrlClickOnBareBeamModuleNameChoosesGotoDeclaration() {
        myFixture.configureByFiles("beam_module_bare_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromBareBeamModuleNameNavigatesToDecompiledDefmodule() {
        myFixture.configureByFiles("beam_module_bare_goto.ex")
        assertNavigatesToDecompiledCode()
    }

    private fun assertNavigatesToDecompiledCode() {
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration should navigate into the decompiled Code module", target)
        assertTrue(
            "Should land in the decompiled Elixir.Code.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("Elixir.Code")
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
        private const val BEAM_LIBRARY_NAME = "module-goto-beam-lib"
    }
}
