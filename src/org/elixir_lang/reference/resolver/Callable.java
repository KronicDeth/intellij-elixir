package org.elixir_lang.reference.resolver;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.elixir_lang.psi.Modular;
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.qualifiedToModular;

public class Callable implements ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Callable> {
    public static final Callable INSTANCE = new Callable();

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull org.elixir_lang.reference.Callable callable, boolean incompleteCode) {
        final List<ResolveResult> resolveResultList = new ArrayList<ResolveResult>();
        Call element = callable.getElement();
        int resolvedFinalArity = element.resolvedFinalArity();

        if (element instanceof org.elixir_lang.psi.call.qualification.Qualified) {
            Call modular = qualifiedToModular((org.elixir_lang.psi.call.qualification.Qualified) element);

            /* If modular cannot be found then it means that either the qualifier has a typo or its part of
               .beam-only Module.  Since .beam-only Modules aren't resolvable at this time, assume typo and mark all
                ResolveResults with `validResult` `false.  Finally, it could also be a variable. */
            if (modular != null) {
                Modular.forEachCallDefinitionClauseNameIdentifier(
                        modular,
                        element.functionName(),
                        resolvedFinalArity,
                        new com.intellij.util.Function<PsiElement, Boolean>() {
                            @Override
                            public Boolean fun(PsiElement nameIdentifier) {
                                resolveResultList.add(new PsiElementResolveResult(nameIdentifier, true));

                                return true;
                            }
                        }
                );
            }
        } else {
            /* DO NOT use `getName()` as it will return the NameIdentifier's text, which for `defmodule` is the Alias,
               not `defmodule` */
            String name = element.functionName();

            if (name != null) {
                // UnqualifiedNorArgumentsCall prevents `foo()` from being treated as a variable.
                // resolvedFinalArity prevents `|> foo` from being counted as 0-arity
                if (element instanceof UnqualifiedNoArgumentsCall && resolvedFinalArity == 0) {
                    List<ResolveResult> variableResolveList =
                            org.elixir_lang.psi.scope.variable.MultiResolve.resolveResultList(
                                    name,
                                    incompleteCode,
                                    element
                            );

                    if (variableResolveList != null) {
                        resolveResultList.addAll(variableResolveList);
                    }
                }

                List<ResolveResult> callDefinitionClauseResolveResultList =
                        org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResultList(
                                name,
                                resolvedFinalArity,
                                incompleteCode,
                                element
                        );

                if (callDefinitionClauseResolveResultList != null) {
                    resolveResultList.addAll(callDefinitionClauseResolveResultList);
                }
            }

        }

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }
}
