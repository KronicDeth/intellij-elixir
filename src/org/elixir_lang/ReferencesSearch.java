package org.elixir_lang;

import com.intellij.openapi.application.QueryExecutorBase;
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

public final class ReferencesSearch extends QueryExecutorBase<PsiPolyVariantReference, SearchParameters> {
    public void processQuery(@NotNull SearchParameters queryParameters,
                             /* Cannot use generics as
                                < 2018.2 needs `Processor<PsiPolyVariantReference>` while
                                >= 2018.2 needs `Processor<? super PsiPolyVariantReference>` */
                             @NotNull @SuppressWarnings("unused") Processor processor) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();

        if (elementToSearch instanceof Named) {
            Named elementToSearchNamed = (Named) elementToSearch;

            if (Implementation.is(elementToSearchNamed)
                    || Module.is(elementToSearchNamed)
                    || Protocol.is(elementToSearchNamed)) {
                String name = elementToSearchNamed.getName();

                if (name != null) {
                    List relativeNames = org.elixir_lang.Module.split(name);

                    if (relativeNames.size() > 1) {
                        queryParameters.getOptimizer().searchWord(
                                (String) CollectionsKt.last(relativeNames),
                                queryParameters.getEffectiveSearchScope(),
                                elementToSearchNamed.getLanguage().isCaseSensitive(),
                                elementToSearchNamed
                        );
                    }
                }
            }
        }
    }
}
