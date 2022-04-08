package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import org.elixir_lang.Module.split
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.semantic

class ReferencesSearch : QueryExecutorBase<PsiPolyVariantReference?, ReferencesSearch.SearchParameters>() {
    override fun processQuery(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiPolyVariantReference?>
    ) {
        val elementToSearch = queryParameters.elementToSearch

        elementToSearch.semantic?.let { it as? Modular }?.name?.let { name ->
            split(name).lastOrNull()?.let { relativeName ->
                queryParameters.optimizer.searchWord(
                    relativeName,
                    queryParameters.effectiveSearchScope,
                    elementToSearch.language.isCaseSensitive,
                    elementToSearch
                )
            }
        }
    }
}
