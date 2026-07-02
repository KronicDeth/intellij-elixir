package org.elixir_lang.code_insight.completion.contributor

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import java.io.File

/**
 * Regression tests for BEAM (decompiled `.beam`) completion.
 *
 * Three fixed bugs:
 *
 * 1. **`Variants.execute(CallDefinitionImpl<*>)`** was a no-op: the always-false `element is Named`
 *    guard meant exported BEAM functions never appeared in unqualified scope-walk completion after
 *    `import SomeBeamModule`.  Tested by [testBeamExportedFunctionAppearsInUnqualifiedCompletion].
 *
 * 2. **`element_renderer.CallDefinitionClause`** had no branch for `CallDefinitionImpl`, so the
 *    tail text (arity) was missing from BEAM completion items.  Tested by
 *    [testBeamExportedFunctionRendersWithArity].
 *
 * 3. The same renderer set no icon for `CallDefinitionImpl`, so BEAM items rendered without the
 *    function/macro + visibility icon that source items get.  Tested by
 *    [testBeamExportedFunctionRendersWithIcon].
 *
 * Plus a guard, [testSourcePreferredOverBeamInUnqualifiedCompletion], that pins the
 * source-over-decompiled preference for unqualified `import` completion.
 */
class BeamCallDefinitionClauseTest : PlatformTestCase() {

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause/beam_function"

    /**
     * The shared light project module is reused across test methods (and other test classes).
     * Library dependencies added by [addBeamLibrary] / [addBeamLibraryWithSource] persist on the
     * module and leak into later tests (e.g. a lingering SOURCES root changes what `string_to_quoted`
     * resolves to).  Restore the module and project library table to a pristine state in tearDown.
     */
    @Throws(Exception::class)
    override fun tearDown() {
        runAll(
            { removeAddedLibrariesAndDependencies() },
            { super.tearDown() },
        )
    }

    private fun removeAddedLibrariesAndDependencies() {
        // Drop the library order entries this test added to the shared module.
        ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
            model.orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .forEach { model.removeOrderEntry(it) }
        }

        // Drop the project libraries this test created.
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        WriteAction.runAndWait<RuntimeException> {
            libraryTable.libraries
                .filter { (it.name ?: "").startsWith("beam-") }
                .forEach { libraryTable.removeLibrary(it) }
        }
    }

    /**
     * Adds this suite's own `ebin/Elixir.Code.beam` as a library CLASSES root.
     * `ebin/` contains only the `.beam` file (its companion `code.ex` lives in `lib/`, which is
     * NOT added here), so completion resolves through `CallDefinitionImpl` stubs rather than source
     * PSI.  Uses the test method name to produce a unique library name, avoiding collisions across
     * tests in the same light-project instance.
     */
    private fun addBeamLibrary(): String {
        val ebinDir = File(testDataPath, "ebin").absoluteFile

        val ebinVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ test data directory in VFS", ebinVirtualDir)

        // Include test name to guarantee uniqueness within a shared light project.
        val libraryName = "beam-$name"

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val library = WriteAction.computeAndWait<Library, RuntimeException> {
            val lib = libraryTable.createLibrary(libraryName)
            val model = lib.modifiableModel
            model.addRoot(ebinVirtualDir!!, OrderRootType.CLASSES)
            model.commit()
            lib
        }

        ModuleRootModificationUtil.addDependency(myFixture.module, library)
        return libraryName
    }

    /**
     * Adds this suite's own library with BOTH roots:
     *   - CLASSES root -> ebin/ (contains Elixir.Code.beam, the decompiled stubs)
     *   - SOURCES root -> lib/  (contains code.ex, the real source for the same Code module)
     *
     * This reproduces a real SDK layout where source and decompiled forms of the same module
     * coexist.  Used to assert that unqualified completion prefers source over decompiled,
     * mirroring the resolution behaviour verified by PreferSourceOverDecompiledTest.
     */
    private fun addBeamLibraryWithSource(): String {
        val ebinDir = File(testDataPath, "ebin").absoluteFile
        val libDir = File(testDataPath, "lib").absoluteFile

        val ebinVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ directory in VFS", ebinVirtualDir)
        val libVirtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(libDir)
        assertNotNull("Could not find lib/ directory in VFS", libVirtualDir)

        // Include test name to guarantee uniqueness within a shared light project.
        val libraryName = "beam-source-$name"

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val library = WriteAction.computeAndWait<Library, RuntimeException> {
            val lib = libraryTable.createLibrary(libraryName)
            val model = lib.modifiableModel
            model.addRoot(ebinVirtualDir!!, OrderRootType.CLASSES)
            model.addRoot(libVirtualDir!!, OrderRootType.SOURCES)
            model.commit()
            lib
        }

        ModuleRootModificationUtil.addDependency(myFixture.module, library)
        return libraryName
    }

    /**
     * When both the source (`code.ex`) and decompiled (`Elixir.Code.beam`) forms of the `Code`
     * module are in scope, UNQUALIFIED completion after `import Code` must NOT offer the BEAM
     * (`CallDefinitionImpl`) variants alongside the source clauses - exactly like resolution
     * prefers source over decompiled (see PreferSourceOverDecompiledTest).
     *
     * Regression guard for the source-over-decompiled preference at the *module-alias resolution*
     * layer, which is what dedups the unqualified `import` path (a different and earlier point than
     * the qualified `provider.CallDefinitionClause`, which filters BEAM stubs itself).  The `import`
     * scope walk only visits the modulars that `Import.modulars` returns, and `import Code` resolves
     * the `Code` alias via `QualifiableAlias.maybeModularNameToModulars` -> `reference.multiResolve`,
     * which returns only the source `defmodule Code` when source exists.  As a result the walk takes
     * the source `executeOnCallDefinitionClause(Call)` path and never reaches
     * `Variants.execute(CallDefinitionImpl<*>)` for `Code`, so no BEAM lookup elements are produced.
     *
     * This test should fail only if that alias-resolution preference regresses (e.g. the alias
     * starts resolving to both the source and BEAM modulars), which would let BEAM duplicates leak
     * into unqualified completion.
     */
    fun testSourcePreferredOverBeamInUnqualifiedCompletion() {
        addBeamLibraryWithSource()

        myFixture.configureByFiles("unqualified_usage.ex")
        val lookupElements: Array<LookupElement>? = myFixture.complete(CompletionType.BASIC, 1)
        assertNotNull(NO_POPUP_MESSAGE, lookupElements)

        val stringToQuotedElements = lookupElements!!.filter { it.lookupString == "string_to_quoted" }
        assertFalse(
            "Expected at least one 'string_to_quoted' completion from source code.ex",
            stringToQuotedElements.isEmpty()
        )

        val beamElements = stringToQuotedElements.filter { it.psiElement is CallDefinitionImpl<*> }
        assertTrue(
            "Unqualified completion offered BEAM-decompiled 'string_to_quoted' variants " +
                "(${beamElements.size}) even though source code.ex is in scope; source should be " +
                "preferred over decompiled, as it is during resolution",
            beamElements.isEmpty()
        )
    }

    /**
     * Exported functions from a BEAM module must appear in UNQUALIFIED completion after
     * `import Code`.  This exercises `Variants.execute(CallDefinitionImpl<*>)` - the method
     * that was previously a no-op due to the always-false `element is Named` guard.
     *
     * Without the fix, the scope walker visits each `CallDefinitionImpl` from the imported
     * BEAM module but returns `true` without adding it to the lookup, so no BEAM functions
     * appear in unqualified completion.  With the fix, exported functions are added.
     *
     * `Code.string_to_quoted` is a stable exported function present in every Elixir release.
     */
    fun testBeamExportedFunctionAppearsInUnqualifiedCompletion() {
        addBeamLibrary()

        myFixture.configureByFiles("unqualified_usage.ex")
        val lookupElements: Array<LookupElement>? = myFixture.complete(CompletionType.BASIC, 1)
        assertNotNull(NO_POPUP_MESSAGE, lookupElements)

        val strings = lookupElements!!.map { it.lookupString }
        assertTrue(
            "Expected 'string_to_quoted' (from Elixir.Code.beam) in unqualified completions " +
                "after `import Code` - BEAM functions missing from scope-walk Variants path",
            strings.contains("string_to_quoted")
        )
    }

    /**
     * The lookup element for a BEAM-decompiled function must render with `/arity` tail text.
     *
     * This verifies the fix in `element_renderer.CallDefinitionClause` that adds a
     * `renderCallDefinitionImpl` branch, since `CallDefinitionImpl` is not a `Call` and the
     * pre-fix code path left the tail text empty for BEAM elements.
     */
    fun testBeamExportedFunctionRendersWithArity() {
        addBeamLibrary()

        myFixture.configureByFiles("usage.ex")
        val lookupElements: Array<LookupElement>? = myFixture.complete(CompletionType.BASIC, 1)
        assertNotNull(NO_POPUP_MESSAGE, lookupElements)

        val element = lookupElements!!.firstOrNull { it.lookupString == "string_to_quoted" }
        assertNotNull("No lookup element for 'string_to_quoted' - BEAM exported functions missing from completion", element)

        val presentation = LookupElementPresentation()
        element!!.renderElement(presentation)

        assertEquals("string_to_quoted", presentation.itemText)
        // string_to_quoted/1 and string_to_quoted/2 are both exported; either renders as "/1" or "/2"
        val tailText = presentation.tailText
        assertNotNull("Expected '/arity' tail text for BEAM completion item, got null", tailText)
        assertTrue(
            "Expected tail text to start with '/' for BEAM arity, got: '$tailText'",
            tailText!!.startsWith("/")
        )
    }

    /**
     * The lookup element for a BEAM-decompiled function must render with an icon, just like a
     * source-defined function does (the source path forwards `ItemPresentation.getIcon` via
     * `renderItemPresentation`).  `renderCallDefinitionImpl` builds the equivalent RowIcon
     * (time/function-vs-macro, visibility, call-definition-clause) so the popup is visually
     * consistent and keeps those cues.
     */
    fun testBeamExportedFunctionRendersWithIcon() {
        addBeamLibrary()

        myFixture.configureByFiles("usage.ex")
        val lookupElements: Array<LookupElement>? = myFixture.complete(CompletionType.BASIC, 1)
        assertNotNull(NO_POPUP_MESSAGE, lookupElements)

        val element = lookupElements!!.firstOrNull { it.lookupString == "string_to_quoted" }
        assertNotNull("No lookup element for 'string_to_quoted' - BEAM exported functions missing from completion", element)

        val presentation = LookupElementPresentation()
        element!!.renderElement(presentation)

        assertNotNull(
            "Expected a non-null icon for BEAM completion item 'string_to_quoted' - " +
                "renderCallDefinitionImpl should set the same icon as the source render path",
            presentation.icon
        )
    }

    companion object {
        /**
         * `myFixture.complete()` returns `null` when the result set narrows to a single element and
         * the platform auto-inserts it instead of showing the popup.  These tests rely on >= 2
         * candidates surviving the prefix; if that ever stops holding, fail with a message that
         * points at auto-insertion rather than a misleading "no completions" diagnosis.
         */
        private const val NO_POPUP_MESSAGE =
            "No completion popup - a single match was auto-inserted; broaden the fixture/prefix so " +
                "at least two candidates remain"
    }
}
