package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase

/** A tag's statements bind in order, so a later tag reads the last binding of a name in an earlier tag. */
class VariableResolvesInsideAnEexTagTest : PlatformTestCase() {
    fun testALaterTagReadsTheLastBindingInAnEarlierTag() {
        myFixture.configureByText("page.html.eex", "<% x = 1\nx = 2 %>\n<%= <caret>x %>\n")
        val root = checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage)) { "no Elixir root" }
        val reference = root.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(listOf("x = 2"), reference.multiResolve(false).mapNotNull { it.element?.parent?.text })
    }
}
