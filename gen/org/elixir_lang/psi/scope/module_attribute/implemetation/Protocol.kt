package org.elixir_lang.psi.scope.module_attribute.implemetation

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.scope.ResolveResultOrderedSet

class Protocol(private val validResult: Boolean) : PsiScopeProcessor {
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
            val protocolNameElement = org.elixir_lang.structure_view.element.modular.Implementation.protocolNameElement(call)
            val element: PsiElement
            val name: String
            val validResult: Boolean

            if (protocolNameElement != null) {
                element = protocolNameElement
                name = protocolNameElement.text
                validResult = this.validResult
            } else {
                element = call
                name = "impl"
                validResult = false
            }

            resolveResultOrderedSet.add(element, name, validResult)

            false
        } else {
           true
        }

    companion object {
        fun resolveResultOrderedSet(validResult: Boolean, entrance: PsiElement): ResolveResultOrderedSet {
            val protocol = Protocol(validResult)

            PsiTreeUtil.treeWalkUp(
                    protocol,
                    entrance,
                    entrance.containingFile,
                    ResolveState.initial().put(ENTRANCE, entrance)
            )

            return protocol.resolveResultOrderedSet
        }
    }
}
