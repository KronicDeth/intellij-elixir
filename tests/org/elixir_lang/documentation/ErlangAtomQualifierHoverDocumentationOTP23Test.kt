package org.elixir_lang.documentation

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import java.io.File

/**
 * Tests that Quick Doc / hover documentation works for Erlang modules and functions
 * using decompiled-source docs for OTP 23 fixtures.
 *
 * Uses a real `queue.beam` file (OTP 23, no embedded `Docs` chunk) paired with an
 * external `queue.chunk` file in the `<app>/doc/chunks/` directory. Decompilation reads
 * that external chunk via [org.elixir_lang.beam.chunk.beam_documentation.Documentation.fromExternalChunk]
 * and inlines docs into the decompiled mirror. Hover then resolves docs from the mirrored source.
 *
 * The OTP 23 external chunks use `application/erlang+html` format (structured Erlang
 * AST tuples like `{p, [], ["text"]}`), which is converted to Markdown by
 * `ErlangHtmlRenderer`.
 *
 * Covers Case 11 / Case 11a from issue #2691.
 *
 * @see ErlangAtomQualifierHoverDocumentationTest for the OTP 27 embedded-docs variant
 */
class ErlangAtomQualifierHoverDocumentationOTP23Test : PlatformTestCase() {
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

    fun testModuleAtomHoverShowsModuleDocsFromDecompilerMirror() {
        myFixture.configureByFiles("module_atom_hover.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val atom = elementAtCaret!!.parent
        val reference = atom.reference
        assertNotNull("No reference on module atom", reference)

        val resolved = reference!!.resolve()
        assertNotNull("Module atom did not resolve", resolved)

        val hover = ElixirDocumentationProvider().generateHoverDoc(resolved!!, atom)
        assertNotNull("Hover documentation is null -- decompiled mirror docs were not picked up", hover)

        assertTrue("Expected module definition for :queue", hover!!.contains("<i>module</i> <b>:queue</b>"))
        assertTrue(
            "Expected non-empty module documentation content from decompiled mirror docs",
            hover.contains("This module provides (double-ended) FIFO queues")
        )
    }

    fun testAtomQualifiedFunctionHoverShowsDocsFromDecompilerMirror() {
        myFixture.configureByFiles("function_hover.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = elementAtCaret!!.parent.parent
        val reference = call.reference
        assertNotNull("No reference on atom-qualified function call", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val resolved = reference!!.resolve()
        assertNotNull("Atom-qualified function call did not resolve", resolved)

        val hover = ElixirDocumentationProvider().generateHoverDoc(resolved!!, call)
        assertNotNull("Hover documentation is null -- decompiled mirror docs were not picked up", hover)

        assertTrue("Expected module definition for :queue", hover!!.contains("<i>module</i> <b>:queue</b>"))
        assertTrue("Expected function doc body from decompiled mirror docs", hover.contains("Inserts"))
    }

    /**
     * Adds the OTP 23 `stdlib` app directory as a library class root.
     *
     * The library root is `stdlib/` (the OTP app directory), which contains:
     * - `ebin/queue.beam` -- the BEAM file (no embedded `Docs` chunk)
     * - `doc/chunks/queue.chunk` -- external EEP-48 documentation
     *
     * [org.elixir_lang.beam.chunk.beam_documentation.Documentation.fromExternalChunk] navigates from `ebin/queue.beam` up to
     * `stdlib/` then down to `doc/chunks/queue.chunk`.
     */
    private fun addBeamLibrary() {
        val appDir = File(testDataPath, "stdlib")
        val beamFile = File(appDir, "ebin/queue.beam")
        assertTrue("queue.beam not found at ${beamFile.absolutePath}", beamFile.exists())

        val chunkFile = File(appDir, "doc/chunks/queue.chunk")
        assertTrue("queue.chunk not found at ${chunkFile.absolutePath}", chunkFile.exists())

        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, appDir.absolutePath)

        val appDirVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(appDir)
        assertNotNull("Could not find app directory: ${appDir.absolutePath}", appDirVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(appDirVf!!, OrderRootType.CLASSES)
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
        "testData/org/elixir_lang/documentation/erlang_atom_qualifier_hover_otp23"

    companion object {
        private const val LIBRARY_NAME = "erlang_hover_otp23_test_lib"
    }
}
