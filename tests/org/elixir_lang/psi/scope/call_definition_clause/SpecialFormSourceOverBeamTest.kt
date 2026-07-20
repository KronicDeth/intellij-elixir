package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import java.io.File

/**
 * The `Kernel.SpecialForms` `__XXX__` macros reach unqualified completion through the *implicit*
 * `import Kernel.SpecialForms` that [org.elixir_lang.psi.scope.CallDefinitionClause.implicitImports]
 * performs on every scope walk - a different path from the explicit `import` one covered by
 * [org.elixir_lang.code_insight.completion.contributor.BeamCallDefinitionClauseTest].  That path stub-
 * indexes the module name and visits every `Kernel.SpecialForms` it finds, so when the module is in
 * scope as BOTH decompiled `ebin/Elixir.Kernel.SpecialForms.beam` and source `lib/special_forms.ex`
 * (the real mise/SDK layout), each macro was offered twice.
 *
 * These tests pin both fixture scenarios:
 *  - only the decompiled BEAM present -> each macro offered once (from the `CallDefinitionImpl` stub);
 *  - source `special_forms.ex` present alongside the BEAM -> source is preferred, the BEAM variants are
 *    dropped, and each macro is still offered exactly once (no duplicate).
 *
 * The special forms are offered through the same resolution path as every other implicitly-imported
 * macro, so a project without an Elixir SDK attached (neither source nor BEAM in scope) simply offers
 * nothing here - there is no special-cased synthetic fallback.
 */
class SpecialFormSourceOverBeamTest : PlatformTestCase() {

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/psi/scope/call_definition_clause/special_forms"

    /**
     * The shared light project module is reused across test methods, so library dependencies added here
     * leak into later tests.  Restore the module and project library table to a pristine state.
     */
    @Throws(Exception::class)
    override fun tearDown() {
        runAll(
            { removeAddedLibrariesAndDependencies() },
            { super.tearDown() },
        )
    }

    private fun removeAddedLibrariesAndDependencies() {
        ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
            model.orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .forEach { model.removeOrderEntry(it) }
        }

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        WriteAction.runAndWait<RuntimeException> {
            libraryTable.libraries
                .filter { (it.name ?: "").startsWith("special-forms-") }
                .forEach { libraryTable.removeLibrary(it) }
        }
    }

    /** Attaches `ebin/` (the decompiled `Kernel.SpecialForms` BEAM) as a CLASSES root only. */
    private fun addBeamLibrary() {
        addLibrary(withSource = false)
    }

    /**
     * Attaches BOTH roots, reproducing an SDK/mise layout where the same `Kernel.SpecialForms` module
     * is present as decompiled BEAM (CLASSES `ebin/`) and source (SOURCES `lib/`).
     */
    private fun addBeamLibraryWithSource() {
        addLibrary(withSource = true)
    }

    private fun addLibrary(withSource: Boolean) {
        val ebinDir = File(testDataPath, "ebin").absoluteFile
        val ebinVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ test data directory in VFS", ebinVirtualDir)

        val libVirtualDir = if (withSource) {
            val libDir = File(testDataPath, "lib").absoluteFile
            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(libDir).also {
                assertNotNull("Could not find lib/ test data directory in VFS", it)
            }
        } else {
            null
        }

        val libraryName = "special-forms-$name"
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val library = WriteAction.computeAndWait<Library, RuntimeException> {
            val lib = libraryTable.createLibrary(libraryName)
            val model = lib.modifiableModel
            model.addRoot(ebinVirtualDir!!, OrderRootType.CLASSES)
            libVirtualDir?.let { model.addRoot(it, OrderRootType.SOURCES) }
            model.commit()
            lib
        }

        ModuleRootModificationUtil.addDependency(myFixture.module, library)
    }

    private fun specialFormElementsAtCaret(): List<LookupElement> {
        myFixture.configureByFiles("unqualified_usage.ex")
        val lookupElements: Array<LookupElement>? = myFixture.complete(CompletionType.BASIC, 1)
        assertNotNull(NO_POPUP_MESSAGE, lookupElements)

        return lookupElements!!.filter { it.lookupString.startsWith("__") }
    }

    private fun assertEachOfferedOnce(elements: List<LookupElement>) {
        val duplicated = elements
            .groupingBy { it.lookupString }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        assertTrue(
            "Special-form macros offered more than once in unqualified completion: $duplicated",
            duplicated.isEmpty()
        )
    }

    /** BEAM only in scope: every special-form macro is offered exactly once from the decompiled stub. */
    fun testBeamOnlyOffersEachSpecialFormOnce() {
        addBeamLibrary()

        val elements = specialFormElementsAtCaret()

        assertTrue(
            "Expected the __ENV__ special form from Elixir.Kernel.SpecialForms.beam in completion",
            elements.any { it.lookupString == "__ENV__" }
        )
        assertEachOfferedOnce(elements)
    }

    /**
     * Source and BEAM both in scope (the real mise/SDK layout): the source `special_forms.ex` clauses
     * are preferred, the decompiled `CallDefinitionImpl` variants are dropped, and each macro is still
     * offered exactly once - the fix for the IDE duplicate.
     *
     * Special forms are offered like any other implicitly-imported macro: they come from the normal
     * `Kernel.SpecialForms` resolution, unfiltered by context (the compiler still rejects genuine
     * misuse), so every `__XXX__` clause the source defines is present.
     */
    fun testSourcePreferredOverBeamOffersEachSpecialFormOnce() {
        addBeamLibraryWithSource()

        val elements = specialFormElementsAtCaret()
        val lookupStrings = elements.map { it.lookupString }

        assertTrue(
            "Expected every source special_forms.ex macro in completion, got $lookupStrings",
            lookupStrings.containsAll(
                listOf("__MODULE__", "__ENV__", "__DIR__", "__CALLER__", "__STACKTRACE__")
            )
        )

        val beamVariants = elements.filter { it.psiElement is CallDefinitionImpl<*> }
        assertTrue(
            "Unqualified completion offered decompiled BEAM special-form variants " +
                "(${beamVariants.map { it.lookupString }}) even though source special_forms.ex is in " +
                "scope; source should be preferred over decompiled",
            beamVariants.isEmpty()
        )
        assertEachOfferedOnce(elements)
    }

    companion object {
        private const val NO_POPUP_MESSAGE =
            "No completion popup - a single match was auto-inserted; broaden the fixture/prefix so " +
                "at least two candidates remain"
    }
}
