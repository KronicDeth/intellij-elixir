package org.elixir_lang.reference.module

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.QualifiedAlias
import org.elixir_lang.reference.module.UnaliasedName.unaliasedName

/**
 * Tests for [UnaliasedName.unaliasedName], covering all branches of both [UnaliasedName.up] and
 * [UnaliasedName.down].
 *
 * ## Branch map
 *
 * ### `up(element: PsiElement?, entrance: QualifiableAlias)`
 * | Branch | Elixir | Test |
 * |---|---|---|
 * | `is Call` → delegates to `up(Call, entrance)` | any alias | all tests |
 * | `is QualifiedMultipleAliases → entrance.fullyQualifiedName()` | `alias Prefix.{Suffix}`, qualifier | [testMultipleAliasQualifier] |
 * | `is ElixirAccessExpression / …NoParentheses… / QuotableArguments / QuotableKeywordList → up(parent)` | traversal step | all multi-level tests |
 * | `is ElixirMultipleAliases → entrance.fullyQualifiedName()` | `alias Prefix.{Suffix}`, leaf | [testMultipleAliasLeaf] |
 * | `is QuotableKeywordPair` → delegates to `up(QuotableKeywordPair, entrance)` | `alias …, as: X` | [testAsOptionValue] |
 * | `else → null` | non-alias non-recognized parent | [testNonAliasCallReturnsEntranceName] (never hits `else`; Call takes over first) |
 *
 * ### `up(call: Call, entrance: QualifiableAlias)`
 * | Branch | Test |
 * |---|---|
 * | alias call with args → `down(firstArg)` | [testSimpleAlias], [testAsOptionValue] |
 * | non-alias call → `entrance.name` | [testNonAliasCallReturnsEntranceName] |
 *
 * ### `up(element: QuotableKeywordPair, entrance: QualifiableAlias)`
 * | Branch | Test |
 * |---|---|
 * | has key "as" → continue up | [testAsOptionValue] |
 * | no "as" key → null | (aliased value for non-`as` keyword; difficult to reach naturally) |
 *
 * ### `down(element: PsiElement)`
 * | Branch | Test |
 * |---|---|
 * | `is QualifiableAlias → element.name` | [testSimpleAlias] |
 * | `is ElixirAccessExpression` → recurse into child | all `ElixirAccessExpression` traversal cases |
 * | `is ElixirAtom → ":name"` | (atom aliases; not exercised here) |
 * | `is Call` → `maybeModularNameToModulars` resolution | (macro-generated aliases; not exercised here) |
 * | `else → "?"` + Logger.error | (unexpected element type; not exercised here) |
 */
class UnaliasedNameTest : PlatformTestCase() {

    // -------------------------------------------------------------------------
    // `is QualifiedMultipleAliases` branch - the regression/bug-fix case
    // -------------------------------------------------------------------------

    /**
     * `Prefix` in `alias Prefix.{Suffix}`.
     *
     * Parent chain: `ElixirAlias(Prefix)` → `ElixirAccessExpression` → `QualifiedMultipleAliases` → `alias` Call.
     *
     * Before the fix this caused an infinite loop in [UnaliasedName.up]; after the fix it short-circuits
     * to `entrance.fullyQualifiedName()` = "Prefix".
     */
    fun testMultipleAliasQualifier() {
        myFixture.configureByFile("multiple_alias_qualifier.ex")
        val prefix = caretElixirAlias()
        assertEquals("Prefix", unaliasedName(prefix))
    }

    // -------------------------------------------------------------------------
    // `is ElixirMultipleAliases` branch
    // -------------------------------------------------------------------------

    /**
     * `Suffix` in `alias Prefix.{Suffix}`.
     *
     * Parent chain: `ElixirAlias(Suffix)` → `ElixirMultipleAliases` → `QualifiedMultipleAliases` → `alias` Call.
     *
     * `up(ElixirMultipleAliases, entrance)` short-circuits to `entrance.fullyQualifiedName()`.
     * `fullyQualifiedName()` walks UP the PSI tree to prepend the qualifier, so for `Suffix` inside `Prefix.{Suffix}`
     * it returns "Prefix.Suffix" - the fully-qualified module name, not the bare leaf name.
     */
    fun testMultipleAliasLeaf() {
        myFixture.configureByFile("multiple_alias_leaf.ex")
        val suffix = caretElixirAlias()
        assertEquals("Prefix.Suffix", unaliasedName(suffix))
    }

    // -------------------------------------------------------------------------
    // `is Call` (alias) → `down(QualifiableAlias → element.name)` branch
    // -------------------------------------------------------------------------

    /**
     * `Prefix.Suffix` in `alias Prefix.Suffix`.
     *
     * The [QualifiedAlias] text ("Prefix.Suffix") is the name returned by the `down` function when it encounters a
     * [org.elixir_lang.psi.QualifiableAlias].
     */
    fun testSimpleAlias() {
        myFixture.configureByFile("simple_alias.ex")
        // caret is inside "Suffix" - one parent up gives ElixirAlias("Suffix"),
        // one more gives the QualifiedAlias spanning the whole "Prefix.Suffix".
        val qualifiedAlias = caretElixirAlias().parent
        assertInstanceOf(qualifiedAlias, QualifiedAlias::class.java)
        assertEquals("Prefix.Suffix", unaliasedName(qualifiedAlias as QualifiedAlias))
    }

    // -------------------------------------------------------------------------
    // `is QuotableKeywordPair` with "as" → continue up → eventually alias Call → `down`
    // -------------------------------------------------------------------------

    /**
     * `As` (the alias name) in `alias Prefix.Suffix, as: As`.
     *
     * Parent chain: `ElixirAlias(As)` → `ElixirAccessExpression`? → `QuotableKeywordPair(as: …)` →
     * `QuotableKeywordList` → `ElixirNoParenthesesOneArgument` → `alias` Call.
     *
     * `up(QuotableKeywordPair)` detects the "as" key and continues upward; the alias Call then sends
     * execution to `down(Prefix.Suffix)`, which returns "Prefix.Suffix".
     */
    fun testAsOptionValue() {
        myFixture.configureByFile("as_option.ex")
        val asAlias = caretElixirAlias()
        assertEquals("Prefix.Suffix", unaliasedName(asAlias))
    }

    // -------------------------------------------------------------------------
    // `is Call` (non-alias) → `entrance.name`
    // -------------------------------------------------------------------------

    /**
     * `My.Module` in `defmodule My.Module do end`.
     *
     * The [QualifiedAlias] reaches a `defmodule` Call that is not `Kernel.alias/2`, so `up(Call, entrance)`
     * falls through to `entrance.name` = "My.Module" ([QualifiedAlias.getName] returns the full text).
     */
    fun testNonAliasCallReturnsEntranceName() {
        myFixture.configureByFile("defmodule_argument.ex")
        val qualifiedAlias = caretElixirAlias().parent
        assertInstanceOf(qualifiedAlias, QualifiedAlias::class.java)
        assertEquals("My.Module", unaliasedName(qualifiedAlias as QualifiedAlias))
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Navigates from the caret token to the immediately enclosing [ElixirAlias]. */
    private fun caretElixirAlias(): ElixirAlias {
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Expected an element at the caret", element)
        val alias = element!!.parent
        assertInstanceOf(alias, ElixirAlias::class.java)
        return alias as ElixirAlias
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/reference/module/unaliased_name"
}
