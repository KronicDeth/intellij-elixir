package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.util.Processor
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.*
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause

class DefinitionsScopedSearch : QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters>(/* readAction = */ true) {
    override fun processQuery(queryParameters: DefinitionsScopedSearch.SearchParameters, consumer: Processor<in PsiElement>) {
        when (val element = queryParameters.element) {
            is Call -> processQuery(element, consumer)
            is QualifiableAlias -> processQuery(element, consumer)
        }
    }

    private fun processQuery(qualifiableAlias: QualifiableAlias, consumer: Processor<in PsiElement>) {
        qualifiableAlias.outerMostQualifiableAlias().maybeModularNameToModulars(qualifiableAlias.containingFile).filterIsInstance<Protocol>().map { protocol ->
            processQuery(protocol, consumer)
        }
    }

    private fun processQuery(call: Call, consumer: Processor<in PsiElement>) {
        call.semantic?.let { semantic ->
            when (semantic) {
                is Protocol -> processQuery(semantic, consumer)
                is Clause -> semantic.nameArityInterval?.let { protocolNameArityInterval ->
                    val protocolName = protocolNameArityInterval.name
                    val protocolArityInterval = protocolNameArityInterval.arityInterval

                    semantic
                            .definition
                            .enclosingModular.let { it as? Protocol }
                            ?.implementations
                            ?.flatMap(Implementation::exportedCallDefinitions)?.filter { implementationCallDefinitionClause ->
                                implementationCallDefinitionClause.nameArityInterval?.let { implementationNameArityInterval ->
                                    implementationNameArityInterval.name == protocolName && implementationNameArityInterval.arityInterval.overlaps(protocolArityInterval)
                                } == true
                            }
                            ?.flatMap(Definition::clauses)
                            ?.map(Clause::psiElement)?.let { implementationCallDefinitionClausePsiElements ->
                                whileIn(implementationCallDefinitionClausePsiElements) {
                                    consumer.process(it)
                                }
                            }
                }
                else -> Unit
            }
        }
    }

    private fun processQuery(protocol: Protocol, consumer: Processor<in PsiElement>) {
        whileIn(protocol.implementations) { implementation ->
            consumer.process(implementation.psiElement)
        }
    }
}
