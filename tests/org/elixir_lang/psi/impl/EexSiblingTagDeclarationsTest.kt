package org.elixir_lang.psi.impl

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirEex
import org.elixir_lang.psi.ElixirEexTag
import org.elixir_lang.psi.call.Call

/**
 * A template's tags answer for themselves: the walk over an `ElixirEex`'s earlier tags has to enter each one, or an
 * `alias` written in one is invisible to every processor but the variable resolver.
 */
class EexSiblingTagDeclarationsTest : PlatformTestCase() {
    fun testAnEarlierTagOffersItsContentsToAnyProcessor() {
        myFixture.configureByText("page.html.eex", "<% alias Foo.Bar %>\n<div>\n  <%= Bar.baz() %>\n</div>\n")

        val elixirRoot = checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage)) { "no Elixir root" }
        val eex = PsiTreeUtil.findChildOfType(elixirRoot, ElixirEex::class.java)
        assertNotNull("no ElixirEex in the Elixir root:\n" + elixirRoot.text, eex)
        val tags = PsiTreeUtil.getChildrenOfTypeAsList(eex, ElixirEexTag::class.java)
        assertTrue("expected two tags under the ElixirEex, got ${tags.size}", tags.size >= 2)

        val lastTag = tags.last()
        val place = PsiTreeUtil.findChildrenOfType(lastTag, Call::class.java).lastOrNull() ?: lastTag
        val seen = mutableListOf<PsiElement>()
        val recorder = object : PsiScopeProcessor {
            override fun execute(element: PsiElement, state: ResolveState): Boolean {
                seen.add(element)
                return true
            }

            override fun <T> getHint(hintKey: Key<T>): T? = null
            override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}
        }

        eex!!.processDeclarations(recorder, ResolveState.initial(), lastTag, place)

        val descriptions = seen.joinToString("\n") { "  " + it::class.java.simpleName + " :: " + it.text.trim() }
        assertTrue(
            "the earlier tag's `alias Foo.Bar` was never offered to the processor. Saw:\n$descriptions",
            seen.any { it is Call && it.text.trim().startsWith("alias ") }
        )
    }
}
