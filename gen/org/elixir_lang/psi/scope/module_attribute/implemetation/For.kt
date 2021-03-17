package org.elixir_lang.psi.scope.module_attribute.implemetation

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.DEFMODULE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.elixir_lang.structure_view.element.modular.Module

class For(private val validResult: Boolean) : PsiScopeProcessor {
    val resolveResultOrderedSet = ResolveResultOrderedSet()

    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                is Call -> execute(element)
                else -> true
            }

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    private fun execute(call: Call): Boolean =
            if (org.elixir_lang.structure_view.element.modular.Implementation.`is`(call)) {
                val forNameElement = org.elixir_lang.structure_view.element.modular.Implementation.forNameElement(call)
                val element: PsiElement
                val name: String
                val validResult: Boolean

                if (forNameElement != null) {
                    element = forNameElement
                    name = forNameElement.text
                    validResult = this.validResult
                } else {
                    val enclosingModularMacroCall = enclosingModularMacroCall(call)

                    if (enclosingModularMacroCall != null) {
                        if (enclosingModularMacroCall.isCalling(KERNEL, DEFMODULE)) {
                            element = Module.nameIdentifier(enclosingModularMacroCall)!!
                            name = element.text
                            validResult = this.validResult
                        } else {
                            element = enclosingModularMacroCall
                            name = "modular"
                            validResult = false
                        }
                    } else {
                        element = call
                        name = "impl"
                        validResult = false
                    }
                }

                resolveResultOrderedSet.add(element, name, validResult)

                false
            } else {
                true
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
