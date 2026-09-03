package org.elixir_lang.reference.callable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import java.io.File

/**
 * A module defined both in the project's own source and in a Mix dependency must resolve to the
 * project's copy, so Go To Declaration lands in code the user can edit rather than in `deps/`.
 *
 * The fixture reproduces how a synced dependency is registered: its `lib` is a library SOURCES root
 * outside every content root, while the project's copy is an ordinary file inside one. Only the
 * SOURCES half is registered, because preferring source over a decompiled `.beam` is a separate step
 * already covered by [PreferSourceOverDecompiledTest].
 */
class PreferProjectOverMixDependencyTest : PlatformTestCase() {

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/reference/callable/prefer_project_over_dep"

    /**
     * Registers the dependency's `lib` on a library SOURCES root rather than in module content.
     *
     * @return the dependency's `shared.ex`, so a test can assert the resolver had a competing
     *   candidate to reject.
     */
    private fun addMixDependencyLibrary(libraryName: String): VirtualFile {
        val libDir = File(testDataPath, "deps/shared_dep/lib")
        assertTrue("deps/shared_dep/lib test data directory missing", libDir.exists())

        val libVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(libDir)
        assertNotNull("Could not find the dependency's lib/ directory in VFS", libVirtualDir)

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val library = runWriteAction {
            val lib = libraryTable.createLibrary(libraryName)
            val model = lib.modifiableModel
            model.addRoot(libVirtualDir!!, OrderRootType.SOURCES)
            model.commit()
            lib
        }

        ModuleRootModificationUtil.addDependency(myFixture.module, library)

        val dependencySource = libVirtualDir!!.findChild("shared.ex")
        assertNotNull("Dependency's shared.ex missing from the SOURCES root", dependencySource)

        return dependencySource!!
    }

    /**
     * Guards against a vacuous pass: if the dependency's copy were never indexed as library source
     * there would be nothing to prefer against, and every assertion below would hold for the wrong
     * reason.
     */
    private fun assertIsLibrarySource(dependencySource: VirtualFile) {
        assertTrue(
            "Dependency's shared.ex is not indexed as library source, so the test would pass vacuously",
            ProjectFileIndex.getInstance(project).isInLibrary(dependencySource)
        )
    }

    private fun assertAllUnderModuleContent(resolveResults: Array<ResolveResult>) {
        val contentRoots = ModuleRootManager.getInstance(myFixture.module).contentRoots.toSet()
        assertFalse("Module has no content roots to compare against", contentRoots.isEmpty())

        for (resolveResult in resolveResults) {
            val resolved = resolveResult.element
            assertNotNull("Resolve result has null element", resolved)

            val virtualFile = resolved!!.containingFile.originalFile.virtualFile
            assertNotNull("Resolve result has no virtual file", virtualFile)
            assertTrue(
                "Resolve result should come from the project's own source, but got ${virtualFile!!.path}",
                VfsUtilCore.isUnder(virtualFile, contentRoots)
            )
        }
    }

    fun testQualifiedCallResolvesToProjectAndNotDependency() {
        val dependencySource = addMixDependencyLibrary("shared-dep-qualified-call")

        myFixture.configureByFiles("caller.ex", "shared.ex")
        assertIsLibrarySource(dependencySource)

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { it.functionName() == "helper" }
        assertNotNull("Could not find Call for helper", call)

        val reference = call!!.reference
        assertNotNull("Call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val resolveResults = (reference as PsiPolyVariantReference).multiResolve(false)
        assertTrue("Expected at least one resolve result", resolveResults.isNotEmpty())

        assertAllUnderModuleContent(resolveResults)
    }

    fun testModuleReferenceResolvesToProjectAndNotDependency() {
        val dependencySource = addMixDependencyLibrary("shared-dep-module-ref")

        myFixture.configureByFiles("caller_module_ref.ex", "shared.ex")
        assertIsLibrarySource(dependencySource)

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val alias = generateSequence(elementAtCaret) { it.parent }
            .filterIsInstance<QualifiableAlias>()
            .firstOrNull { it.fullyQualifiedName() == "Shared" }
        assertNotNull("Could not find QualifiableAlias for Shared", alias)

        val reference = alias!!.reference
        assertNotNull("Module alias has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val resolveResults = (reference as PsiPolyVariantReference).multiResolve(false)
        assertTrue("Expected at least one resolve result for module Shared", resolveResults.isNotEmpty())

        assertAllUnderModuleContent(resolveResults)
    }

    private fun <T> runWriteAction(action: () -> T): T {
        var result: T? = null
        ApplicationManager.getApplication().runWriteAction {
            result = action()
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}
