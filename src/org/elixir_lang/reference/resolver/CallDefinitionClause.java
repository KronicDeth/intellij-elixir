package org.elixir_lang.reference.resolver;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;
import static org.elixir_lang.structure_view.element.CallDefinitionSpecification.typeNameArity;

public class CallDefinitionClause implements ResolveCache.PolyVariantResolver<org.elixir_lang.reference.CallDefinitionClause> {
    public static final CallDefinitionClause INSTANCE = new CallDefinitionClause();

    @NotNull
    private static List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                           @NotNull Call call,
                                           boolean validResult) {
        @NotNull List<ResolveResult> nonNullResolveResultList;

        if (resolveResultList != null) {
            nonNullResolveResultList = resolveResultList;
        } else {
            nonNullResolveResultList = new ArrayList<>();
        }

        nonNullResolveResultList.add(new PsiElementResolveResult(call, validResult));

        return nonNullResolveResultList;
    }

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull org.elixir_lang.reference.CallDefinitionClause callDefinitionClause,
                                   boolean incompleteCode) {
        Call enclosingModularMacroCall = enclosingModularMacroCall(callDefinitionClause.moduleAttribute);
        List<ResolveResult> resolveResultList = null;

        if (enclosingModularMacroCall != null) {
            Call[] siblings = macroChildCalls(enclosingModularMacroCall);

            if (siblings != null && siblings.length > 0) {
                Pair<String, Integer> nameArity = typeNameArity(callDefinitionClause.getElement());
                String name = nameArity.first;
                int arity = nameArity.second;

                for (Call call : siblings) {
                    if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(call)) {
                        Pair<String, IntRange> callNameArityRange =
                                org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange(call);

                        if (callNameArityRange != null) {
                            String callName = callNameArityRange.first;

                            if (callName.equals(name)) {
                                IntRange callArityRange = callNameArityRange.second;

                                if (callArityRange.containsInteger(arity)) {
                                    resolveResultList = add(resolveResultList, call, true);
                                } else if (arity < callArityRange.getMaximumInteger()) {
                                    resolveResultList = add(resolveResultList, call, false);
                                }
                            } else if (incompleteCode && callName.startsWith(name)) {
                                IntRange callArityRange = callNameArityRange.second;

                                if (callArityRange.containsInteger(arity)) {
                                    resolveResultList = add(resolveResultList, call, false);
                                } else if (arity < callArityRange.getMaximumInteger()) {
                                    resolveResultList = add(resolveResultList, call, false);
                                }
                            }
                        }
                    }
                }
            }
        }

        ResolveResult[] resolveResults;

        if (resolveResultList != null) {
            resolveResults = resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
        } else {
            resolveResults = ResolveResult.EMPTY_ARRAY;
        }

        return resolveResults;
    }
}
