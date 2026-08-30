package org.elixir_lang.psi.scope.variable

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1793
 *
 * A variable bound in one EEx tag and used in a later one, both inside the same block-spanning
 * `<%= if ... do %>` ... `<% end %>`, resolves - and the `ElixirEex` node the grammar puts inside the
 * block's stab body is what the resolver has to walk past to do it. Shipped in v11.11.0
 * (2021-06-22), and until this test nothing asserted variable resolution in a template language, in
 * any of `.eex`, `.leex` or `.heex`.
 */
class Issue1793Test : PlatformTestCase() {
    /**
     * `total` is bound in `<% total = 1 %>` and used in a later `<%= total %>`, both inside the same
     * `<%= if @ok do %>` block. Resolution has to walk out of the using tag, across the `ElixirEex`
     * holding the block's template data, and into the binding tag.
     *
     * The `<p>a</p>` before the first nested tag is load-bearing, not decoration: without template
     * data between the opening tag and the tag that follows it, the block does not parse - the
     * `do` block is left unterminated and `<% end %>` falls out as a `PsiErrorElement`. Asserting a
     * clean parse here keeps this a resolution test rather than one that would pass for the wrong
     * reason.
     */
    fun testVariableBoundInOneTagResolvesFromALaterTag() {
        for (extension in listOf("eex", "leex", "heex")) {
            myFixture.configureByText(
                "block.html.$extension",
                """<%= if @ok do %><p>a</p><% total = 1 %><p><%= tot<caret>al %></p><% end %>"""
            )

            val elixirRoot = myFixture.file.viewProvider.allFiles.filterIsInstance<ElixirFile>().first()
            assertEmpty(
                ".$extension template did not parse cleanly, so resolution is not what is under test",
                PsiTreeUtil.findChildrenOfType(elixirRoot, com.intellij.psi.PsiErrorElement::class.java)
            )

            val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
            val call = generateSequence(elementAtCaret) { it.parent }.filterIsInstance<Call>().firstOrNull()
            assertNotNull("caret is not inside a Call in the .$extension template", call)

            val results = (call!!.reference as PsiPolyVariantReference).multiResolve(false)
            assertEquals(
                "`total` did not resolve to its binding in the .$extension template",
                1,
                results.size
            )

            val resolved = results.single().element
            assertEquals(
                "`total` resolved to something other than its `total = 1` binding in .$extension",
                "total",
                resolved?.text
            )
            assertEquals(
                "`total` resolved to the use rather than the binding in .$extension",
                "<% total = 1 %>",
                PsiTreeUtil.getParentOfType(resolved, org.elixir_lang.psi.ElixirEexTag::class.java)?.text
            )
        }
    }
}
