package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.util.Processor
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import org.elixir_lang.navigation.item_presentation.Implementation
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.outerMostQualifiableAlias

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
        }
    }
}
