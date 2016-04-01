package org.elixir_lang.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.Infix;
import org.elixir_lang.psi.operation.Operation;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.CallDefinitionHead;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Callable extends PsiReferenceBase<Call> implements PsiPolyVariantReference {
    /*
     * Static Methods
     */

    public static boolean isParameter(@NotNull Call call) {
        PsiElement parent = call.getParent();
        boolean isParameter = false;

        if (parent instanceof ElixirParenthesesArguments) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof ElixirMatchedParenthesesArguments) {
                PsiElement greatGrandParent = grandParent.getParent();

                if (greatGrandParent instanceof Call) {
                    Call greatGrandParentCall = (Call) greatGrandParent;

                    if (CallDefinitionHead.is(greatGrandParentCall)) {
                        PsiElement greatGreatGrandParent = greatGrandParentCall.getParent();

                        if (greatGreatGrandParent instanceof ElixirNoParenthesesOneArgument) {
                            PsiElement greatGreatGreatGrandParent = greatGreatGrandParent.getParent();

                            if (greatGreatGreatGrandParent instanceof Call) {
                                Call greatGreatGreatGrandParentCall = (Call) greatGreatGreatGrandParent;

                                if (CallDefinitionClause.is(greatGreatGreatGrandParentCall)) {
                                    isParameter = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return isParameter;
    }

    public static boolean isParameterWithDefault(@NotNull Call call) {
        PsiElement parent = call.getParent();
        boolean isParameterWithDefault = false;

        if (parent instanceof ElixirUnmatchedInMatchOperation) {
            Infix parentOperation = (ElixirUnmatchedInMatchOperation) parent;
            Operator operator = parentOperation.operator();
            String operatorText = operator.getText();

            if (operatorText.equals("\\\\")) {
                PsiElement defaulted = parentOperation.leftOperand();

                if (defaulted.isEquivalentTo(call)) {
                    isParameterWithDefault = isParameter((Call) parentOperation);
                }
            }
        }

        return isParameterWithDefault;
    }

    /*
     * Constructors
     */

    public Callable(@NotNull Call call) {
        super(call, TextRange.create(0, call.getTextLength()));
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
     * Returns the array of String, {@link PsiElement} and/or {@link com.intellij.codeInsight.lookup.LookupElement}
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
        List<ResolveResult> resolveResultList = new ArrayList<ResolveResult>();
        // ensure that a pipe isn't make a no argument call really a 1-arity call
        int resolvedFinalArity = myElement.resolvedFinalArity();

        if (resolvedFinalArity == 0) {
            resolveResultList.addAll(resolveVariable(myElement, incompleteCode));
        }

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
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

    private List<ResolveResult> resolveVariable(@NotNull Call call, boolean incompleteCode) {
        List<ResolveResult> resolveResultList = new ArrayList<ResolveResult>();

        if (isParameter(call)) {
            resolveResultList.add(new PsiElementResolveResult(call));
        } else if (isParameterWithDefault(call)) {
            resolveResultList.add(new PsiElementResolveResult(call));
        }

        return resolveResultList;
    }
}
