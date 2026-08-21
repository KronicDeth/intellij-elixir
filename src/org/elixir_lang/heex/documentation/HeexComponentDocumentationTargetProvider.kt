package org.elixir_lang.heex.documentation

import com.intellij.lang.documentation.psi.createPsiDocumentationTarget
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.heex.isInHeex
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Quick Docs on a HEEx component tag resolves, like Go To Declaration, to the def's name
 * identifier, but `ElixirDocumentationProvider` only renders a `Call`. Widen the target to the
 * enclosing `def`/`defp` clause. An unresolved tag declares itself and has no `Call` ancestor, so
 * this returns `null` and the platform's default target applies.
 */
class HeexComponentDocumentationTargetProvider : PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        if (originalElement == null || !isHeexOrigin(originalElement)) {
            return null
        }

        val call = generateSequence(element) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
            ?: return null

        return createPsiDocumentationTarget(call, originalElement)
    }

    /**
     * In a `.heex` file the originating element's own view provider is HEEx. For a `~H` sigil,
     * Quick Docs computes `originalElement` from the host `.ex` file (`PsiFile.findElementAt` does
     * not enter injections), so the sigil host's injected files are checked instead.
     */
    private fun isHeexOrigin(originalElement: PsiElement): Boolean {
        if (originalElement.isInHeex()) return true
        if (!originalElement.language.isKindOf(ElixirLanguage)) return false

        val host = PsiTreeUtil.getParentOfType(originalElement, PsiLanguageInjectionHost::class.java, false)
            ?: return false
        return InjectedLanguageManager.getInstance(originalElement.project)
            .getInjectedPsiFiles(host)
            .orEmpty()
            .any { it.first.isInHeex() }
    }
}
