package org.elixir_lang.psi.__module__

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

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
            when (call.semantic) {
                is org.elixir_lang.semantic.quote.Call -> {
                    if (useCall != null) {
                        for (modular in Use.modulars(useCall)) {
                            resolveResultList.add(PsiElementResolveResult(modular.psiElement))
                        }
                    } else {
                        val project = call.project

                        StubIndex
                            .getInstance()
                            .processElements(
                                AllName.KEY,
                                "__MODULE__",
                                project,
                                GlobalSearchScope.allScope(project),
                                NamedElement::class.java
                            ) { namedElement ->
                                namedElement.semantic?.let { it as? Clause }?.takeIf { clause ->
                                    clause.definition.nameArityInterval?.arityInterval?.contains(0) == true &&
                                            clause.enclosingModular.canonicalName == "Kernel.SpecialForms"
                                }?.let {
                                    resolveResultList.add(PsiElementResolveResult(namedElement))
                                }

                                true
                            }
                    }

                    false
                }
                is Modular -> {
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
