package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.util.Processor
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallList
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall

class DefinitionsScopedSearch : QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters>(/* readAction = */ true) {
    override fun processQuery(queryParameters: DefinitionsScopedSearch.SearchParameters, consumer: Processor<in PsiElement>) {
        when (val element = queryParameters.element) {
            is Call -> processQuery(element, consumer)
            is QualifiableAlias -> processQuery(element, consumer)
        }
    }

    private fun processQuery(qualifiableAlias: QualifiableAlias, consumer: Processor<in PsiElement>) {
        qualifiableAlias.outerMostQualifiableAlias().maybeModularNameToModulars(qualifiableAlias.containingFile).map { modular ->
            processQuery(modular, consumer)
        }
    }

    private fun processQuery(call: Call, consumer: Processor<in PsiElement>) {
        if (Protocol.`is`(call)) {
            Protocol.processImplementations(call, consumer)
        } else if (CallDefinitionClause.`is`(call)) {
            enclosingModularMacroCall(call)?.let { modularCall ->
                CallDefinitionClause.nameArityInterval(call, ResolveState.initial())?.let { protocolNameArityInterval ->
                    if (Protocol.`is`(modularCall)) {
                        Protocol.processImplementations(modularCall) { defimpl ->
                            for (defimplChild in (defimpl as Call).macroChildCallList()) {
                                if (CallDefinitionClause.`is`(defimplChild)) {
                                    CallDefinitionClause.nameArityInterval(defimplChild, ResolveState.initial())?.let { implNameArityInterval ->
                                        if (implNameArityInterval.name == protocolNameArityInterval.name &&
                                                implNameArityInterval.arityInterval.overlaps(protocolNameArityInterval.arityInterval)) {
                                            consumer.process(defimplChild)
                                        }
                                    }
                                }
                            }

                            true
                        }
                    }
                }
            }
        }
    }
}
