package org.elixir_lang.documentation

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import java.io.File

/**
 * Tests that Quick Doc / hover documentation works for Erlang modules and functions
 * referenced via atom qualifier syntax (`:module` and `:module.function()`).
 *
 * Uses a real `queue.beam` file (OTP 27 with embedded docs) as a library class root.
 *
 * Covers Case 11 from issue #2691: Quick Doc for Erlang modules/functions via atom.
 *
 * Drives the real Ctrl+Q gesture at the caret (see [QuickDocumentationTestCase]) rather than
 * hand-resolving the atom/call reference, so it locks the user-visible behaviour.
 *
 * @see ErlangAtomQualifierHoverDocumentationOTP23Test for the OTP 23 decompiled-mirror variant
 */
class ErlangAtomQualifierHoverDocumentationTest : QuickDocumentationTestCase() {
    override fun setUp() {
        super.setUp()
        addBeamLibrary()
    }

    override fun tearDown() {
        try {
            removeBeamLibrary()
        } finally {
            super.tearDown()
        }
    }

    fun testModuleAtomHoverShowsModuleDocs() {
        myFixture.configureByFiles("module_atom_hover.ex")

        val hover = quickDocumentationAtCaret()
        assertNotNull("Hover documentation is null", hover)

        assertTrue("Expected module definition for :queue", hover!!.contains("<i>module</i> <b>:queue</b>"))
        assertTrue(
            "Expected non-empty module documentation content",
            hover.contains("This module provides (double-ended) FIFO queues")
        )
    }

    fun testAtomQualifiedFunctionHoverShowsSpecHead() {
        myFixture.configureByFiles("function_hover.ex")

        val hover = quickDocumentationAtCaret()
        assertNotNull("Hover documentation is null", hover)

        assertTrue("Expected module definition for :queue", hover!!.contains("<i>module</i> <b>:queue</b>"))
        assertTrue("Expected function doc body", hover.contains("Inserts"))
    }

    private fun addBeamLibrary() {
        val beamFile = File(testDataPath, "queue.beam")
        assertTrue("queue.beam not found at ${beamFile.absolutePath}", beamFile.exists())

        val beamDir = beamFile.parentFile.absolutePath
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, beamDir)

        val beamDirVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(beamDir))
        assertNotNull("Could not find beam test data directory: $beamDir", beamDirVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(beamDirVf!!, OrderRootType.CLASSES)
            libModel.commit()
            model.commit()

            ModuleRootModificationUtil.addDependency(myFixture.module, library)
        }
    }

    private fun removeBeamLibrary() {
        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val library = libraryTable.getLibraryByName(LIBRARY_NAME)
            if (library != null) {
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/documentation/erlang_atom_qualifier_hover"

    companion object {
        private const val LIBRARY_NAME = "erlang_hover_test_lib"
    }
}
