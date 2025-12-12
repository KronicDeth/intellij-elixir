package org.elixir_lang.injection

import com.intellij.psi.PsiLanguageInjectionHost
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport
import org.jetbrains.annotations.NonNls

internal class ElixirSigilInjectionSupport : AbstractLanguageInjectionSupport() {
    override fun getId(): String {
        return ELIXIR_SUPPORT_ID
    }

    override fun getPatternClasses(): Array<Class<*>> {
        return arrayOf(ElixirSigilPatterns::class.java)
    }

    override fun isApplicableTo(host: PsiLanguageInjectionHost): Boolean {
        return true
    }

    override fun useDefaultInjector(host: PsiLanguageInjectionHost): Boolean {
        return true
    }

    companion object {
        const val ELIXIR_SUPPORT_ID: @NonNls String = "elixir"
    }
}
