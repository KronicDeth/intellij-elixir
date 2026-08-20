package org.elixir_lang.psi.impl.qualifiable_alias

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias

/**
 * Tests for [ItemPresentation].
 */
class ItemPresentationTest : PlatformTestCase() {

    /**
     * Regression test for an infinite loop in [org.elixir_lang.reference.module.UnaliasedName.up] when a
     * [org.elixir_lang.psi.QualifiableAlias] is used as the qualifier in a
     * [org.elixir_lang.psi.QualifiedMultipleAliases] expression (e.g. `alias Prefix.{SubModule}`).
     *
     * The qualifier `Prefix` has parent chain:
     *   `ElixirAlias` → `ElixirAccessExpression` → `QualifiedMultipleAliases` → `alias` Call
     *
     * Before the fix, `UnaliasedName.up(QualifiedMultipleAliases, entrance)` called itself with the same
     * `QualifiedMultipleAliases`, causing an infinite tail-recursive loop that froze IntelliJ.
     */
    fun testGetPresentableTextForQualifierInQualifiedMultipleAliases() {
        myFixture.configureByFile("qualifier_in_qualified_multiple_aliases.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Expected an element at the caret", elementAtCaret)

        // The token's parent is the ElixirAlias PSI element for `Prefix`
        val qualifiableAlias = elementAtCaret!!.parent
        assertInstanceOf(qualifiableAlias, ElixirAlias::class.java)
        val elixirAlias = qualifiableAlias as ElixirAlias

        // getPresentableText() must terminate (not infinite-loop) and return the alias presentation
        val presentation = elixirAlias.presentation
        assertNotNull("Expected a non-null ItemPresentation for ElixirAlias", presentation)
        val presentableText = presentation.presentableText
        assertEquals("alias Prefix", presentableText)
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/psi/impl/qualifiable_alias/item_presentation"
}
