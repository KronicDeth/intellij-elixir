package org.elixir_lang.injection

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.psi.SigilLine
import org.intellij.lang.regexp.RegExpLanguage
import org.elixir_lang.eex.Language as EexLanguage

private val LOG = logger<ElixirSigilInjector>()

internal class ElixirSigilInjector : MultiHostInjector {
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
        if (!sigilHeredoc.isValidHost || sigilHeredoc.heredocLineList.isEmpty() || !sigilHeredoc.isValid) {
            if (LOG.isDebugEnabled) {
                LOG.debug(
                    "handleSigilHeredoc: returning early: " +
                            "isValidHost=${sigilHeredoc.isValidHost}, " +
                            "heredocLineList.isEmpty=${sigilHeredoc.heredocLineList.isEmpty()}, " +
                            "isValid=${sigilHeredoc.isValid}"
                )
            }
            return
        }

        val lang = languageForSigil(sigilHeredoc.sigilName()) ?: return
        registrar.startInjecting(lang)
        if (LOG.isDebugEnabled) {
            LOG.debug("handleSigilHeredoc: injecting ${lang.displayName} into ${sigilHeredoc.heredocLineList.size} lines")
        }
        for (item in sigilHeredoc.heredocLineList) {
            if (item.isValid) {
                if (LOG.isDebugEnabled) {
                    LOG.debug("handleSigilHeredoc: injecting into heredocLine: ${item.text}")
                }
                registrar.addPlace(null, null, sigilHeredoc, item.textRangeInParent)
            } else if (LOG.isDebugEnabled) {
                LOG.debug("handleSigilHeredoc: skipping invalid heredocLine")
            }
        }
        if (LOG.isDebugEnabled) {
            LOG.debug("handleSigilHeredoc: done injecting into ${sigilHeredoc.heredocLineList.size} lines")
        }

        registrar.doneInjecting()
    }

    override fun elementsToInjectIn() = listOf(SigilHeredoc::class.java, SigilLine::class.java)

    private fun languageForSigil(sigilName: Char): Language? {
        if (LOG.isDebugEnabled) {
            LOG.debug("languageForSigil: sigilName='$sigilName'")
        }

        return when (sigilName) {
            'H' -> HTMLLanguage.INSTANCE
            'L' -> EexLanguage.INSTANCE
            'r' -> RegExpLanguage.INSTANCE
            else -> null
        }
    }
}
