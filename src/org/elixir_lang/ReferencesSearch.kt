package org.elixir_lang

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import org.elixir_lang.psi.call.Named
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Protocol

class ReferencesSearch : QueryExecutorBase<PsiPolyVariantReference, ReferencesSearch.SearchParameters>() {
    override fun processQuery(
            searchParameters: ReferencesSearch.SearchParameters,
            processor: Processor<PsiPolyVariantReference>
    ) {
        searchParameters.elementToSearch.let { elementToSearch ->
            when (elementToSearch) {
                is Named -> {
                    if (Implementation.`is`(elementToSearch) || org.elixir_lang.structure_view.element.modular.Module.`is`(elementToSearch) || Protocol.`is`(elementToSearch)) {
                       elementToSearch.name?.let { name ->
                           val relativeNames = Module.split(name)

                           /* normal search handles the full name, so this only needs to handle the suffix that could be
                              aliased */
                           if (relativeNames.size > 1) {
                               searchParameters.optimizer.searchWord(
                                       relativeNames.last(),
                                       searchParameters.effectiveSearchScope,
                                       elementToSearch.language.isCaseSensitive,
                                       elementToSearch
                               )
                           }
                       }
                    }
                }
            }
        }
    }
}
