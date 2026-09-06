package org.elixir_lang.model.psi.module

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertNoNavigationAtCaret
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret

/**
 * Behavior-level tests for module Find/Show Usages.
 *
 * Each test describes what the user experiences: which caret position routes to Show Usages
 * and how many references appear in the popup for a given source layout.
 *
 * The pipeline is real caret → [com.intellij.find.usages.impl.searchTargets] → [com.intellij.find.usages.impl.buildQuery] → [com.intellij.find.usages.api.PsiUsage]s, identical to
 * what the Find Usages action invokes. No implementation classes (declaration providers,
 * usage mappers, symbol constructors) are called directly.
 */
class ModuleFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    // ── Ctrl-click routing ──────────────────────────────────────────────────────────────────────

    fun testCtrlClickOnUnqualifiedModuleDeclarationShowsUsages() {
        myFixture.configureByFiles("usages_alias_use_import.ex")
        myFixture.assertShowUsagesChosenAtCaret()
    }

    fun testCtrlClickOnQualifiedModuleLastSegmentShowsUsages() {
        myFixture.configureByFiles("usages_qualified_alias_last_segment.ex")
        myFixture.assertShowUsagesChosenAtCaret()
    }

    fun testCtrlClickOnQualifiedModuleQualifierShowsUsages() {
        myFixture.configureByFiles("usages_qualified_alias_use_import.ex")
        myFixture.assertShowUsagesChosenAtCaret()
    }

    fun testCtrlClickOnDefmoduleKeywordDoesNothing() {
        myFixture.configureByFiles("usages_defmodule_keyword.ex")
        myFixture.assertNoNavigationAtCaret()
    }

    // ── Find Usages popup content ───────────────────────────────────────────────────────────────

    /** `alias Target`, `use Target`, `import Target` all appear as usages of `defmodule Target`. */
    fun testFindUsagesOnUnqualifiedModuleFindsAliasUseImportSites() {
        assertEquals(3, nonDeclarationUsageCount("usages_alias_use_import.ex"))
    }

    /** Caret on the last segment of a qualified name still finds all reference sites. */
    fun testFindUsagesFromQualifiedModuleLastSegmentFindsReferences() {
        assertEquals(3, nonDeclarationUsageCount("usages_qualified_alias_last_segment.ex"))
    }

    /** Caret on the qualifier segment of a qualified name still finds all reference sites. */
    fun testFindUsagesFromQualifiedModuleQualifierFindsReferences() {
        assertEquals(3, nonDeclarationUsageCount("usages_qualified_alias_use_import.ex"))
    }

    /** `alias MyApp.{Module, AnotherModule}` multi-alias syntax counts as a usage. */
    fun testFindUsagesFindsMultiAliasSite() {
        assertEquals(1, nonDeclarationUsageCount("usages_multi_alias.ex"))
    }

    /** `MyApp.Module` appearing as a value in general code (e.g. a list literal) counts as a usage. */
    fun testFindUsagesFindsGeneralCodeReference() {
        assertEquals(1, nonDeclarationUsageCount("usages_general_reference.ex"))
    }

    /**
     * A bare `Module` reference where `alias MyApp.Module` is in lexical scope counts as a usage
     * of `MyApp.Module`. Both the alias site and the bare code reference appear in Find Usages.
     */
    fun testFindUsagesFindsAliasedShortNameUsageInCode() {
        assertEquals(2, nonDeclarationUsageCount("usages_aliased_short_name.ex"))
    }

    /**
     * `defmodule Declaration do alias Declaration end` - a module aliased to itself - reports the one
     * alias site once, not twice.
     *
     * The alias is both a reference to the module and lexically inside it, so a search that reaches it
     * by both routes can report the same range twice. `Issue354Test`'s sibling in the old
     * `FindUsagesTest` asserted exactly that duplicate: two usages, both at offset 33, both `ALIAS`.
     * That test was deleted when Find Usages moved to the symbol pipeline and nothing replaced it, so
     * the fixtures it used were left orphaned in `testData/org/elixir_lang/find_usages`.
     */
    fun testFindUsagesOnSelfAliasedModuleReportsTheAliasOnce() {
        assertEquals(1, nonDeclarationUsageCount("usages_self_alias_from_declaration.ex"))
    }

    /** Same file, caret on the alias rather than the declaration: still one usage, not two. */
    fun testFindUsagesFromSelfAliasSiteReportsTheAliasOnce() {
        assertEquals(1, nonDeclarationUsageCount("usages_self_alias_from_alias.ex"))
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Configures [files], resolves the search target at the caret (the same entry point the Find
     * Usages action uses), runs the query, and returns the count of non-declaration usages.
     */
    private fun nonDeclarationUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        return myFixture.nonDeclarationUsageCountAtCaret(project)
    }
}
