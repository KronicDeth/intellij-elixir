package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import com.intellij.util.Processor
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallList
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.outerMostQualifiableAlias

class DefinitionsScopedSearch :
    QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters>(/* readAction = */ true) {
    override fun processQuery(
        queryParameters: DefinitionsScopedSearch.SearchParameters,
        consumer: Processor<in PsiElement>
    ) {
        when (val element = queryParameters.element) {
            is Call -> processQuery(element, consumer)
            is QualifiableAlias -> processQuery(element, consumer)
        }
    }

    private fun processQuery(qualifiableAlias: QualifiableAlias, consumer: Processor<in PsiElement>) {
        qualifiableAlias.outerMostQualifiableAlias().maybeModularNameToModulars(qualifiableAlias.containingFile)
            .map { modular ->
                processQuery(modular, consumer)
            }
    }

    private fun processQuery(psiElement: PsiElement, consumer: Processor<in PsiElement>) {
        when (psiElement) {
            is Call -> processQuery(psiElement, consumer)
            is ModuleImpl<*> -> processQuery(psiElement, consumer)
            is CallDefinitionImpl<*> -> processQuery(psiElement, consumer)
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
                                    CallDefinitionClause.nameArityInterval(defimplChild, ResolveState.initial())
                                        ?.let { implNameArityInterval ->
                                            if (implNameArityInterval.name == protocolNameArityInterval.name &&
                                                implNameArityInterval.arityInterval.overlaps(protocolNameArityInterval.arityInterval)
                                            ) {
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

    private fun processQuery(moduleImpl: ModuleImpl<*>, consumer: Processor<in PsiElement>) {
        if (Protocol.`is`(moduleImpl)) {
            Protocol.processImplementations(moduleImpl, consumer)
        }
    }

    private fun processQuery(callDefinitionImpl: CallDefinitionImpl<*>, consumer: Processor<in PsiElement>) {
        val moduleImpl = callDefinitionImpl.parent

        if (Protocol.`is`(moduleImpl)) {
            val name = callDefinitionImpl.name
            val arity = callDefinitionImpl.exportedArity(ResolveState.initial())

            Protocol.processImplementations(moduleImpl) { defimpl ->
                when (defimpl) {
                    is Call -> {
                        for (defimplChild in defimpl.macroChildCallList()) {
                            if (CallDefinitionClause.`is`(defimplChild)) {
                                CallDefinitionClause.nameArityInterval(defimplChild, ResolveState.initial())
                                    ?.let { implNameArityInterval ->
                                        if (implNameArityInterval.name == name &&
                                            implNameArityInterval.arityInterval.contains(arity)
                                        ) {
                                            consumer.process(defimplChild)
                                        }
                                    }
                            }
                        }
                    }
                    is ModuleImpl<*> ->
                        for (callDefinition in defimpl.callDefinitions()) {
                            val implNameArityInterval = callDefinition.nameArityInterval

                            if (implNameArityInterval.name == name &&
                                implNameArityInterval.arityInterval.contains(arity)
                            ) {
                                consumer.process(callDefinition)
                            }
                        }
                }

                true
            }
        }
    }
}
