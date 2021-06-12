package org.elixir_lang.psi.__module__

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.elixir_lang.psi.QuoteMacro
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.type.call.Stub

class PsiScopeProcessor(val call: Call, val useCall: Call?) : PsiScopeProcessor {
    val resolveResults: Array<ResolveResult>
      get() = resolveResultList.toTypedArray()

    override fun execute(element: PsiElement, state: ResolveState): Boolean =
       when (element) {
           is Call -> execute(element)
           else -> true
       }

    private fun execute(call: Call): Boolean =
        if (call != this.call) {
            when {
                QuoteMacro.`is`(call) -> {
                    useCall
                            ?.let { Use.modulars(it) }
                            ?.run {
                                for (modular in this) {
                                    resolveResultList.add(PsiElementResolveResult(modular))
                                }
                            }

                    false
                }
                Stub.isModular(call) -> {
                    PsiElementResolveResult(call).let {
                        resolveResultList.add(it)
                    }

                    false
                }
                else -> true
            }
        } else {
            true
        }

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    private val resolveResultList: MutableList<ResolveResult> = mutableListOf()
}
