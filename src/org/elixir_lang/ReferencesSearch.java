package org.elixir_lang;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import kotlin.collections.CollectionsKt;
import org.elixir_lang.psi.Implementation;
import org.elixir_lang.psi.Module;
import org.elixir_lang.psi.Protocol;
import org.elixir_lang.psi.call.Named;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class ReferencesSearch extends QueryExecutorBase<PsiPolyVariantReference, SearchParameters> {
    public void processQuery(@NotNull SearchParameters queryParameters,
                             @NotNull @SuppressWarnings("unused") Processor processor) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();

        if (elementToSearch instanceof Named elementToSearchNamed) {
            // PSI access (Implementation.is, Module.is, getName, getLanguage) requires a read
            // action. processQuery runs on a pooled thread without one.
            SearchWord searchWord = ReadAction.nonBlocking(() -> {
                if (Implementation.is(elementToSearchNamed)
                        || Module.is(elementToSearchNamed)
                        || Protocol.is(elementToSearchNamed)) {
                    String name = elementToSearchNamed.getName();

                    if (name != null) {
                        List<String> relativeNames = org.elixir_lang.Module.split(name);

                        if (relativeNames.size() > 1) {
                            return new SearchWord(
                                    CollectionsKt.last(relativeNames),
                                    elementToSearchNamed.getLanguage().isCaseSensitive()
                            );
                        }
                    }
                }
                return SearchWord.NONE;
            }).executeSynchronously();

            if (searchWord != SearchWord.NONE) {
                queryParameters.getOptimizer().searchWord(
                        searchWord.word,
                        queryParameters.getEffectiveSearchScope(),
                        searchWord.caseSensitive,
                        elementToSearchNamed
                );
            }
        }
    }

    private record SearchWord(String word, boolean caseSensitive) {
            private static final SearchWord NONE = new SearchWord(null, false);
    }
}
