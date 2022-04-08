package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.semantic

abstract class ModuleAttribute : PsiScopeProcessor {
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
        when (val semantic = element.semantic) {
            is org.elixir_lang.semantic.module_attribute.definition.Literal -> execute(semantic, state)
            is org.elixir_lang.semantic.Use -> execute(semantic, state)
            else -> true
        }

    private fun execute(semantic: org.elixir_lang.semantic.Use, state: ResolveState): Boolean =
        whileIn(semantic.moduleAttributes) {
            execute(it, state)
        }

    protected abstract fun execute(
        semantic: org.elixir_lang.semantic.module_attribute.definition.Literal,
        state: ResolveState
    ): Boolean
}
