package org.elixir_lang.psi.scope.module_attribute.implemetation

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.visitedElementSet
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.semantic

class For(private val validResult: Boolean) : PsiScopeProcessor {
    val resolveResultOrderedSet = ResolveResultOrderedSet()

    override fun execute(element: PsiElement, state: ResolveState): Boolean =
        element.semantic?.let { semantic ->
            execute(semantic, state)
        } ?: true

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    private fun execute(semantic: Semantic, state: ResolveState): Boolean =
        when (semantic) {
            is org.elixir_lang.semantic.implementation.Call -> {
                val call = semantic.call
                val forNamesElement = semantic.forNamesElement
                val element: PsiElement
                val name: String
                val validResult: Boolean

                if (forNamesElement != null) {
                    element = forNamesElement
                    name = forNamesElement.text
                    validResult = this.validResult
                } else {
                    when (val enclosingModular = semantic.enclosingModular) {
                        is org.elixir_lang.semantic.module.Call -> {
                            element = enclosingModular.nameIdentifier!!
                            name = element.text
                            validResult = this.validResult
                        }
                        null -> {
                            element = call
                            name = "impl"
                            validResult = false
                        }
                        else -> {
                            element = enclosingModular.psiElement
                            name = "modular"
                            validResult = false
                        }
                    }
                }

                resolveResultOrderedSet.add(element, name, validResult, state.visitedElementSet())

                false
            }
            else -> true
        }

    companion object {
        fun resolveResultOrderedSet(validResult: Boolean, entrance: PsiElement): ResolveResultOrderedSet {
            val scopeProcessor = For(validResult)

            PsiTreeUtil.treeWalkUp(
                scopeProcessor,
                entrance,
                entrance.containingFile,
                ResolveState.initial().put(ENTRANCE, entrance)
            )

            return scopeProcessor.resolveResultOrderedSet
        }
    }
}
