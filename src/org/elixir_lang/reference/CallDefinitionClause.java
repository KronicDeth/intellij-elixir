package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;
import static org.elixir_lang.structure_view.element.CallDefinitionSpecification.typeNameArity;

public class CallDefinitionClause extends PsiReferenceBase<Call> implements PsiPolyVariantReference {
    /*
     *
     * Fields
     *
     */

    @NotNull
    private final AtUnqualifiedNoParenthesesCall moduleAttribute;

    /*
     *
     * Constructors
     *
     */

    /**
     * @param call {@code foo(arg1, ...) :: return1} in {@code @spec foo(arg1, ...) :: return1}
     * @param moduleAttribute {@code @spec foo(arg1, ...) ... return1}, so that the tree doesn't have to be walked up
     *   again if there is a {@code when}.
     */
    public CallDefinitionClause(@NotNull Call call, @NotNull AtUnqualifiedNoParenthesesCall moduleAttribute) {
        super(call, TextRange.create(0, call.getTextLength()));
        this.moduleAttribute = moduleAttribute;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     *                       being resolved is considered incomplete, and the method may return additional
     *                       invalid results.
     * @return the array of results for resolving the reference.
     */
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Call enclosingModularMacroCall = enclosingModularMacroCall(moduleAttribute);
        List<ResolveResult> resolveResultList = null;

        if (enclosingModularMacroCall != null) {
            Call[] siblings = macroChildCalls(enclosingModularMacroCall);

            if (siblings != null && siblings.length > 0) {
                Pair<String, Integer> nameArity = typeNameArity(myElement);
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
            resolveResults = new ResolveResult[0];
        }

        return resolveResults;
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or null if it was not possible to resolve the reference to a valid target.
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    /*
     * Private Instance Methods
     */

    @NotNull
    private List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                    @NotNull Call call,
                                    boolean validResult) {
        if (resolveResultList == null) {
            resolveResultList = new ArrayList<ResolveResult>();
        }

        resolveResultList.add(new PsiElementResolveResult(call, validResult));

        return resolveResultList;
    }
}
