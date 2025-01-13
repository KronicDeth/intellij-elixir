package org.elixir_lang.injection

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.psi.SigilLine
import org.intellij.lang.regexp.RegExpLanguage
import org.elixir_lang.eex.Language as EexLanguage

class ElixirSigilInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        when (context) {
            is SigilLine -> handleSigilLine(registrar, context)
            is SigilHeredoc -> handleSigilHeredoc(registrar, context)
            else -> return
        }
    }

    private fun handleSigilLine(registrar: MultiHostRegistrar, sigilLine: SigilLine) {
        if (!sigilLine.isValidHost) return
        val lang = languageForSigil(sigilLine.sigilName()) ?: return

        sigilLine.body?.let { lineBody ->
            registrar
                .startInjecting(lang)
                .addPlace(null, null, sigilLine, lineBody.textRangeInParent)
                .doneInjecting()
        }
    }

    private fun handleSigilHeredoc(registrar: MultiHostRegistrar, sigilHeredoc: SigilHeredoc) {
        if (!sigilHeredoc.isValidHost || sigilHeredoc.heredocLineList.isEmpty() || !sigilHeredoc.isValid) return

        val lang = languageForSigil(sigilHeredoc.sigilName()) ?: return
        registrar.startInjecting(lang)
        for (item in sigilHeredoc.heredocLineList) {
            if (item.isValid) {
                registrar.addPlace(null, null, sigilHeredoc, item.textRangeInParent)
            }
        }

        registrar.doneInjecting()
    }

    override fun elementsToInjectIn() = listOf(SigilHeredoc::class.java, SigilLine::class.java)

    private fun languageForSigil(sigilName: Char): Language? {
        return when (sigilName) {
            'H' -> HTMLLanguage.INSTANCE
            'L' -> EexLanguage.INSTANCE
            'r' -> RegExpLanguage.INSTANCE
            else -> null
        }
    }
}
