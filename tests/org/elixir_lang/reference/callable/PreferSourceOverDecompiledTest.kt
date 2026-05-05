package org.elixir_lang.reference.callable

import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.capture.NonNumeric
import java.io.File

/**
 * Regression test: when both source (.ex) and decompiled (.beam) files exist for the same module,
 * navigation/resolution should prefer the source and NOT include decompiled results.
 *
 * Uses the real Code module's code.ex and Elixir.Code.beam from Elixir 1.13.4.
 *
 * The test simulates a real SDK setup:
 * - ebin/Elixir.Code.beam on the library's CLASSES root (classpath)
 * - lib/code.ex on the library's SOURCES root (sourcepath)
 */
class PreferSourceOverDecompiledTest : PlatformTestCase() {

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/prefer_source"

    private fun addSdkLibrary(libraryName: String) {
        val ebinDir = File(testDataPath, "ebin")
        assertTrue("ebin/ test data directory missing", ebinDir.exists())
        val libDir = File(testDataPath, "lib")
        assertTrue("lib/ test data directory missing", libDir.exists())

        val ebinVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ directory in VFS", ebinVirtualDir)
        val libVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(libDir)
        assertNotNull("Could not find lib/ directory in VFS", libVirtualDir)

        // Add as a library with:
        //   CLASSES root -> ebin/ (contains .beam files, like a real SDK classpath)
        //   SOURCES root -> lib/  (contains .ex files, like a real SDK sourcepath)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val library = runWriteAction {
            val lib = libraryTable.createLibrary(libraryName)
            val model = lib.modifiableModel
            model.addRoot(ebinVirtualDir!!, OrderRootType.CLASSES)
            model.addRoot(libVirtualDir!!, OrderRootType.SOURCES)
            model.commit()
            lib
        }

        // Add library to module dependencies
        ModuleRootModificationUtil.addDependency(myFixture.module, library)
    }

    fun testQualifiedCallResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam")

        // Configure the caller file (this is the user's project code)
        myFixture.configureByFiles("caller.ex")

        // Find the qualified call `Code.string_to_quoted`
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        // Navigate up to the qualified call
        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "string_to_quoted" }
        assertNotNull("Could not find Call for string_to_quoted", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        // KEY ASSERTION: None of the resolve results should come from a decompiled .beam file
        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }

        // Additional: verify at least one result comes from source code.ex
        val hasSourceResult = resolveResults.any { result ->
            result.element?.containingFile?.name == "code.ex"
        }
        assertTrue(
            "Expected at least one resolve result from source code.ex",
            hasSourceResult
        )
    }

    fun testUnqualifiedCallResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-unqualified")

        myFixture.configureByFiles("caller_unqualified.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "string_to_quoted" }
        assertNotNull("Could not find Call for string_to_quoted", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        // KEY ASSERTION: None of the resolve results should come from a decompiled .beam file
        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }
    }

    fun testCaptureResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-capture")

        myFixture.configureByFiles("caller_capture.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        // In a capture like `&Code.string_to_quoted/1`, the reference lives on the
        // NonNumeric capture operator (`&`), not on the inner Call node.
        val capture = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<NonNumeric>()
            .firstOrNull()
        assertNotNull("Could not find NonNumeric capture operator ancestor", capture)

        val reference = capture!!.reference
        assertNotNull("Capture operator has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        // KEY ASSERTION: None of the resolve results should come from a decompiled .beam file
        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Capture resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }

        // Verify at least one result comes from source code.ex
        val hasSourceResult = resolveResults.any { result ->
            result.element?.containingFile?.name == "code.ex"
        }
        assertTrue(
            "Expected at least one resolve result from source code.ex",
            hasSourceResult
        )
    }

    fun testModuleReferenceResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-module-ref")

        myFixture.configureByFiles("caller_module_ref.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        // Navigate up to the QualifiableAlias (the module name `Code`)
        val alias = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<QualifiableAlias>()
            .firstOrNull { it.fullyQualifiedName() == "Code" }
        assertNotNull("Could not find QualifiableAlias for Code", alias)

        val reference = alias!!.reference
        assertNotNull("Module alias has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result for module Code", resolveResults.isNotEmpty())

        // KEY ASSERTION: None of the resolve results should come from a decompiled .beam file
        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Module resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }
    }

    fun testAliasResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-alias")

        myFixture.configureByFiles("caller_alias.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        // Navigate up to the QualifiableAlias (the module name `Code`)
        val alias = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<QualifiableAlias>()
            .firstOrNull { it.fullyQualifiedName() == "Code" }
        assertNotNull("Could not find QualifiableAlias for Code", alias)

        val reference = alias!!.reference
        assertNotNull("Alias has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result for alias Code", resolveResults.isNotEmpty())

        // KEY ASSERTION: None of the resolve results should come from a decompiled .beam file
        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Alias resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }
    }

    fun testPipeCallResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-pipe")

        myFixture.configureByFiles("caller_pipe.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "string_to_quoted" }
        assertNotNull("Could not find Call for string_to_quoted", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Pipe call resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }

        val hasSourceResult = resolveResults.any { result ->
            result.element?.containingFile?.name == "code.ex"
        }
        assertTrue(
            "Expected at least one resolve result from source code.ex",
            hasSourceResult
        )
    }

    fun testImportOnlyResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-import-only")

        myFixture.configureByFiles("caller_import_only.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "string_to_quoted" }
        assertNotNull("Could not find Call for string_to_quoted", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Import-only resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }
    }

    fun testMultiArityCallResolvesToSourceOnly() {
        addSdkLibrary("elixir-code-beam-multi-arity")

        myFixture.configureByFiles("caller_multi_arity.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "string_to_quoted" }
        assertNotNull("Could not find Call for string_to_quoted", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val polyVariantReference = reference as PsiPolyVariantReference
        val resolveResults = polyVariantReference.multiResolve(false)

        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)
            val containingFile = resolved!!.containingFile.originalFile
            assertFalse(
                "Multi-arity resolve result should NOT come from decompiled .beam file, but got element in ${containingFile.name} (${containingFile.javaClass.simpleName})",
                containingFile is BeamFileImpl
            )
        }

        val hasSourceResult = resolveResults.any { result ->
            result.element?.containingFile?.name == "code.ex"
        }
        assertTrue(
            "Expected at least one resolve result from source code.ex",
            hasSourceResult
        )
    }

    private fun <T> runWriteAction(action: () -> T): T {
        var result: T? = null
        com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction {
            result = action()
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}
