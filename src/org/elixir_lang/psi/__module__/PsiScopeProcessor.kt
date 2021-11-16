package org.elixir_lang.psi.__module__

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.beam.psi.impl.StubbicBase
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.psi.stub.type.call.Stub

class PsiScopeProcessor(val call: Call, val useCall: Call?) : PsiScopeProcessor {
    val resolveResults: Array<ResolveResult>
      get() = resolveResultList.toTypedArray()

    override fun execute(element: PsiElement, state: ResolveState): Boolean =
       when (element) {
           is Call -> execute(element, state)
           else -> true
       }

    private fun execute(call: Call, state: ResolveState): Boolean =
        if (call != this.call) {
            when {
                QuoteMacro.`is`(call) -> {
                    if (useCall != null) {
                        for (modular in Use.modulars(useCall)) {
                            resolveResultList.add(PsiElementResolveResult(modular))
                        }
                    } else {
                        val project = call.project

                        StubIndex
                                .getInstance()
                                .processElements(AllName.KEY, "__MODULE__", project, GlobalSearchScope.allScope(project), NamedElement::class.java) { namedElement ->
                                    if (namedElement is CallDefinitionImpl<*>) {
                                        namedElement.parent.let { it as? ModuleImpl<*> }?.let { module ->
                                            if (module.stub.let { it as StubbicBase }.name == "Kernel.SpecialForms" && namedElement.stub.callDefinitionClauseHeadArity() == 0) {
                                                resolveResultList.add(PsiElementResolveResult(namedElement))
                                            }
                                        }
                                    } else if (namedElement is Call) {
                                         if (CallDefinitionClause.`is`(namedElement) && CallDefinitionClause.nameArityInterval(namedElement, state)?.arityInterval?.contains(0) == true) {
                                             enclosingModularMacroCall(namedElement)?.let  { modularCall ->
                                                 if (Module.`is`(modularCall) && Module.name(modularCall) == "Kernel.SpecialForms") {
                                                     resolveResultList.add(PsiElementResolveResult(namedElement))
                                                 }
                                             }
                                         }
                                    }

                                    true
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
