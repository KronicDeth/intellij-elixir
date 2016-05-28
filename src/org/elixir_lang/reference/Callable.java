package org.elixir_lang.reference;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.name.*;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.psi.scope.variable.MultiResolve;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Delegation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;

public class Callable extends PsiReferenceBase<Call> implements PsiPolyVariantReference {
    /*
     * CONSTANTS
     */

    private static final Condition<ResolveResult> HAS_VALID_RESULT_CONDITION = new Condition<ResolveResult>() {
        @Override
        public boolean value(ResolveResult resolveResult) {
            return resolveResult.isValidResult();
        }
    };
    private static final Set<String> BIT_STRING_ENDIANNESS = Sets.newHashSet(
            "big",
            "little",
            "native"
    );
    private static final Set<String> BIT_STRING_SIGNEDNESS = Sets.newHashSet(
            "signed",
            "unsigned"
    );
    private static final Set<String> BIT_STRING_TYPES = Sets.newHashSet(
            "binary",
            "bits",
            "bitstring",
            "bytes",
            "float",
            "integer",
            "utf16",
            "utf32",
            "utf8"
    );
    public static final String IGNORED = "_";

    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    @Contract(pure = true)
    @Nullable
    public static String bitStringSegmentOptionElementDescription(@NotNull  Call call,
                                                                  @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;
        String name = call.getName();

        if (name != null) {
            if (BIT_STRING_ENDIANNESS.contains(name)) {
                elementDescription = bitStringEndiannessElementDescription(call, location);
            } else if (BIT_STRING_SIGNEDNESS.contains(name)) {
                elementDescription = bitStringSignednessElementDescription(call, location);
            } else if (BIT_STRING_TYPES.contains(name)) {
                elementDescription = bitStringTypeElementDescription(call, location);
            }
        }

        // getType is @NotNull, so must have fallback
        if (elementDescription == null && location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "bitstring segment option";
        }

        return elementDescription;
    }

    @Contract(pure = true)
    @Nullable
    private static String bitStringEndiannessElementDescription(@NotNull @SuppressWarnings("unused") PsiElement element,
                                                                @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "bitstring endianness";
        }

        return elementDescription;
    }

    @Contract(pure = true)
    @Nullable
    private static String bitStringSignednessElementDescription(@NotNull @SuppressWarnings("unused") PsiElement element,
                                                                @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "bitstring signedness";
        }

        return elementDescription;
    }

    @Contract(pure = true)
    @Nullable
    private static String bitStringTypeElementDescription(@NotNull @SuppressWarnings("unused") PsiElement element,
                                                          @NotNull ElementDescriptionLocation location) {

        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "bitstring type";
        }

        return elementDescription;
    }

    public static String ignoredElementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "ignored";
        }

        return elementDescription;
    }

    @Contract(pure = true)
    public static boolean isBitStreamSegmentOption(@NotNull PsiElement element) {
        boolean isBitStreamSegmentOption = false;

        Type type = PsiTreeUtil.getContextOfType(element, Type.class);

        if (type != null) {
            PsiElement typeParent = type.getParent();

            if (typeParent instanceof ElixirBitString) {
                PsiElement rightOperand = type.rightOperand();

                if (PsiTreeUtil.isAncestor(rightOperand, element, false)) {
                    isBitStreamSegmentOption = isBitStreamSegmentOptionDown(rightOperand, element);
                }
            }
        }

        return isBitStreamSegmentOption;
    }

    /**
     * Checks if call is a ignored, a parameter, a parameter with default or a variable, in that order.
     * @param call that may be a variablic
     * @return if {@link #isIgnored(Call)}, {@link #isParameter(Call)}, {@link #isParameterWithDefault(Call)} or
     *   {@link #isVariable(Call)} is true
     */
    public static boolean isVariablic(@NotNull Call call) {
        return isIgnored(call) || isParameter(call) || isParameterWithDefault(call) || isVariable(call);
    }

    @Contract(pure = true)
    public static boolean isIgnored(@NotNull PsiElement element) {
        boolean isIgnored = false;

        if (element instanceof ElixirKeywordKey ||
                element instanceof UnqualifiedNoArgumentsCall) {
            PsiNamedElement psiNamedElement = (PsiNamedElement) element;
            String name = psiNamedElement.getName();

            if (name != null && name.equals(IGNORED)) {
                isIgnored = true;
            }
        }

        return isIgnored;
    }

    @Contract(pure = true)
    public static boolean isParameter(@NotNull PsiElement ancestor) {
        PsiElement parent = ancestor.getParent();
        boolean isParameter = false;

        if (parent instanceof Call) {
            isParameter = isParameter((Call) parent);
        } else if (parent instanceof AtNonNumericOperation ||
                parent instanceof ElixirAccessExpression ||
                parent instanceof ElixirAssociations ||
                parent instanceof ElixirAssociationsBase ||
                parent instanceof ElixirBitString ||
                parent instanceof ElixirContainerAssociationOperation ||
                parent instanceof ElixirKeywordPair ||
                parent instanceof ElixirKeywords ||
                parent instanceof ElixirList ||
                parent instanceof ElixirMapArguments ||
                parent instanceof ElixirMapConstructionArguments ||
                parent instanceof ElixirMapOperation ||
                parent instanceof ElixirMatchedParenthesesArguments ||
                parent instanceof ElixirNoParenthesesArguments ||
                parent instanceof ElixirNoParenthesesKeywordPair ||
                parent instanceof ElixirNoParenthesesKeywords ||
                parent instanceof ElixirNoParenthesesOneArgument ||
                parent instanceof ElixirParenthesesArguments ||
                parent instanceof ElixirParentheticalStab ||
                parent instanceof ElixirStab ||
                parent instanceof ElixirStabNoParenthesesSignature ||
                parent instanceof ElixirStabBody ||
                parent instanceof ElixirStabOperation ||
                parent instanceof ElixirStabParenthesesSignature ||
                parent instanceof ElixirStructOperation ||
                parent instanceof ElixirTuple) {
            isParameter = isParameter(parent);
        } else if (parent instanceof ElixirAnonymousFunction || parent instanceof InMatch) {
            isParameter = true;
        } else {
            if (!(parent instanceof ElixirBlockItem ||
                    parent instanceof ElixirDoBlock ||
                    parent instanceof ElixirMapUpdateArguments ||
                    parent instanceof ElixirQuoteStringBody)) {
                Logger.error(Callable.class, "Don't know how to check if parameter", parent);
            }
        }

        return isParameter;
    }

    public static boolean isParameterWithDefault(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        boolean isParameterWithDefault = false;

        if (parent instanceof InMatch) {
            Infix parentOperation = (InMatch) parent;
            Operator operator = parentOperation.operator();
            String operatorText = operator.getText();

            if (operatorText.equals(DEFAULT_OPERATOR)) {
                PsiElement defaulted = parentOperation.leftOperand();

                if (defaulted.isEquivalentTo(element)) {
                    isParameterWithDefault = isParameter((Call) parentOperation);
                }
            }
        }

        return isParameterWithDefault;
    }

    @Contract(pure = true)
    public static boolean isVariable(@NotNull PsiElement ancestor) {
        boolean isVariable = false;

        if (ancestor instanceof ElixirInterpolation ||
                // bound quoted variable name in {@code quote bind_quoted: [name: value] do ... end}
                ancestor instanceof ElixirKeywordKey ||
                ancestor instanceof ElixirStabNoParenthesesSignature ||
                /* if a StabOperation is encountered before
                   ElixirStabNoParenthesesSignature or
                   ElixirStabParenthesesSignature, then must have come from body */
                ancestor instanceof ElixirStabOperation ||
                ancestor instanceof ElixirStabParenthesesSignature ||
                ancestor instanceof InMatch ||
                ancestor instanceof Match) {
            isVariable = true;
        } else if (ancestor instanceof ElixirAccessExpression ||
                ancestor instanceof ElixirAssociations ||
                ancestor instanceof ElixirAssociationsBase ||
                ancestor instanceof ElixirBitString ||
                ancestor instanceof ElixirContainerAssociationOperation ||
                ancestor instanceof ElixirDoBlock ||
                ancestor instanceof ElixirKeywordPair ||
                ancestor instanceof ElixirKeywords ||
                ancestor instanceof ElixirList ||
                ancestor instanceof ElixirMapArguments ||
                ancestor instanceof ElixirMapConstructionArguments ||
                ancestor instanceof ElixirMapOperation ||
                /* parenthesesArguments can be used in @spec other type declarations, so may not be variable until
                   ancestor call is checked */
                ancestor instanceof ElixirMatchedParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesOneArgument ||
                ancestor instanceof ElixirNoParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesKeywordPair ||
                ancestor instanceof ElixirNoParenthesesKeywords ||
                ancestor instanceof ElixirParenthesesArguments ||
                ancestor instanceof ElixirParentheticalStab ||
                ancestor instanceof ElixirStab ||
                ancestor instanceof ElixirStabBody ||
                ancestor instanceof ElixirStructOperation ||
                ancestor instanceof ElixirTuple ||
                ancestor instanceof QualifiedAlias ||
                ancestor instanceof Type) {
            isVariable = isVariable(ancestor.getParent());
        } else if (ancestor instanceof Call) {
            // MUST be after any operations because operations also implement Call
            isVariable = isVariable((Call) ancestor);
        } else {
            if (!(ancestor instanceof AtNonNumericOperation || ancestor instanceof PsiFile)) {
                Logger.error(Callable.class, "Don't know how to check if variable", ancestor);
            }
        }

        return isVariable;
    }

    public static String parameterElementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewLongNameLocation.INSTANCE || location == UsageViewShortNameLocation.INSTANCE) {
            elementDescription = call.getName();
        } else if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "parameter";
        }

        return elementDescription;
    }

    public static String parameterWithDefaultElementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "parameter with default";
        }

        return elementDescription;
    }

    public static String variableElementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewLongNameLocation.INSTANCE || location == UsageViewShortNameLocation.INSTANCE) {
            elementDescription = call.getName();
        } else if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "variable";
        }

        return elementDescription;
    }

    public static LocalSearchScope variableUseScope(@NotNull UnqualifiedNoArgumentsCall match) {
        return variableUseScope((PsiElement) match);
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                           @NotNull String name,
                                           boolean incompleteCode,
                                           @NotNull PsiNamedElement match) {
        String matchName = match.getName();

        if (matchName != null) {
            if (matchName.equals(name)) {
                resolveResultList = add(resolveResultList, new PsiElementResolveResult(match, true));
            } else if (incompleteCode && matchName.startsWith(name)) {
                resolveResultList = add(resolveResultList, new PsiElementResolveResult(match, false));
            }
        }

        return resolveResultList;
    }

    /**
     * Adds {@code resolveResult} to {@code resolveResultList} if it exists; otherwise, create new
     * {@code List<ResolveList>}, add {@code resolveResult} to it.  Return the modified or new
     * {@code List<ResolveResult>}.
     *
     * @param resolveResultList The current accumulated {@link ResolveResult}s, or {@code null} if no results have been
     *                          found yet.
     * @param resolveResult     The element to add to {@code resolveResultList}
     * @return {@code resolveResult} if it is not {@code null}; otherwise, a new {@code List<ResolveResult>}.
     */
    @NotNull
    private static List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                           @NotNull ResolveResult resolveResult) {
        if (resolveResultList == null) {
            resolveResultList = new ArrayList<ResolveResult>();
        }

        resolveResultList.add(resolveResult);

        return resolveResultList;
    }

    /**
     * Adds {@code namedElement} to {@code lookupElementList} in a {@link LookupElement} using
     * {@link com.intellij.codeInsight.lookup.LookupElementBuilder#createWithSmartPointer(String, PsiElement)} if
     * {@code lookupElementList} exists; otherwise, create new {@code List<LookupElement>}, and then add
     * {@code namedElement}.
     *
     * @param lookupElementList The current accumulated {@link LookupElement}s for the {@link PsiElement}s that match
     *                          the type of {@link #myElement}.  So, if it is an {@link UnqualifiedNoArgumentsCall},
     *                          then this is a list of potential variables.
     * @param namedElement an element that matches the criteria for {@link #getVariants()}
     * @return the modified {@code lookupElementList} if it was not {@code null}; otherwise, a new
     *   {@code List<LookupElement>}
     */
    @NotNull
    private static List<LookupElement> add(@Nullable List<LookupElement> lookupElementList,
                                           @NotNull PsiNamedElement namedElement) {
        if (lookupElementList == null) {
            lookupElementList = new ArrayList<LookupElement>();
        }

        String lookupString = namedElement.getName();

        if (lookupString == null) {
            lookupString = namedElement.getText();
        }

        lookupElementList.add(
                LookupElementBuilder.createWithSmartPointer(
                        lookupString,
                        namedElement
                )
        );

        return lookupElementList;
    }

    /**
     * Whether the {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()}
     * is {@code true}.
     *
     * @param resolveResultList
     * @return {@code false} if {@code resolveResultList} is {@code null}; otherwise, {@code true} if the
     * {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()} is
     * {@code true}.
     */
    @Contract(pure = true)
    private static boolean hasValidResult(@Nullable List<ResolveResult> resolveResultList) {
        boolean hasValidResult = false;

        if (resolveResultList != null) {
            hasValidResult = ContainerUtil.exists(resolveResultList, HAS_VALID_RESULT_CONDITION);
        }

        return hasValidResult;
    }

    /**
     * Searches downward from {@code ancestor}, only returning true if {@code element} is a type, unit, size or
     * modifier.
     *
     * @return {@code true} if {@code element} is a type, unit, size, or modifier.  i.e. something separated by
     *   {@code -}. *   {@code false} if {@code element} is a variable used in {@code size(variable)}.
     */
    private static boolean isBitStreamSegmentOptionDown(@NotNull PsiElement ancestor, @NotNull  PsiElement element) {
        boolean is = false;

        if (ancestor.isEquivalentTo(element)) {
            is = true;
        } else if (ancestor instanceof Addition) {
            Addition ancestorAddition = (Addition) ancestor;

            PsiElement leftOperand = ancestorAddition.leftOperand();

            if (leftOperand != null) {
                is = isBitStreamSegmentOptionDown(leftOperand, element);
            }

            if (!is) {
                PsiElement rightOperand = ancestorAddition.rightOperand();

                if (rightOperand != null) {
                    is = isBitStreamSegmentOptionDown(rightOperand, element);
                }
            }
        } else if (ancestor instanceof UnqualifiedParenthesesCall) {
            Call ancestorCall = (Call) ancestor;
            PsiElement functionNameElement = ancestorCall.functionNameElement();

            if (functionNameElement != null && functionNameElement.isEquivalentTo(element)) {
                is = true;
            }
        }

        return is;
    }

    private static boolean isParameter(@NotNull Call call) {
        boolean isParameter;

        if (CallDefinitionClause.is(call) || Delegation.is(call)) {
            isParameter = true;
        } else {
            PsiElement parent = call.getParent();

            isParameter = parent != null && !(parent instanceof PsiFile) && isParameter(parent);
        }

        return isParameter;
    }

    @Contract(pure = true)
    private static boolean isVariable(@NotNull Call call) {
        boolean isVariable;

        if (call instanceof UnqualifiedNoArgumentsCall) {
            String name = call.getName();

            // _ is an "ignored" not a variable
            if (name == null || !name.equals(IGNORED)) {
                PsiElement parent = call.getParent();
                isVariable = isVariable(parent);
            } else {
                isVariable = false;
            }
        } else if (call instanceof AtUnqualifiedNoParenthesesCall) {
            // module attribute, so original may be a unqualified no argument type name
            isVariable = false;
        } else if (call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.FOR)) {
            isVariable = true;
        } else {
            PsiElement parent = call.getParent();
            isVariable = isVariable(parent);
        }

        return isVariable;
    }

    @NotNull
    private static LocalSearchScope variableUseScope(@NotNull Call call) {
        LocalSearchScope useScope = null;

        switch (useScopeSelector(call)) {
            case PARENT:
                useScope = variableUseScope(call.getParent());

                break;
            case SELF:
                useScope = new LocalSearchScope(call);

                break;
            case SELF_AND_FOLLOWING_SIBLINGS:
                List<PsiElement> selfAndFollowingSiblingList = new ArrayList<PsiElement>();
                PsiElement sibling = call;

                while (sibling != null) {
                    selfAndFollowingSiblingList.add(sibling);

                    sibling = sibling.getNextSibling();
                }

                useScope = new LocalSearchScope(
                        selfAndFollowingSiblingList.toArray(
                                new PsiElement[selfAndFollowingSiblingList.size()]
                        )
                );

                break;
        }

        return useScope;
    }

    private static LocalSearchScope variableUseScope(@NotNull Match match) {
        LocalSearchScope useScope;

        PsiElement parent = match.getParent();

        if (parent instanceof ElixirStabBody) {
            PsiElement ancestor = PsiTreeUtil.getContextOfType(
                    parent,
                    ElixirAnonymousFunction.class,
                    ElixirBlockItem.class,
                    ElixirDoBlock.class,
                    ElixirParentheticalStab.class,
                    ElixirStabOperation.class
            );

            if (ancestor instanceof ElixirParentheticalStab) {
                useScope = variableUseScope(parent);
            } else {
                /* all non-ElixirParentheticalStab are block-like and so could have multiple statements after the match
                   where the match variable is used */
                useScope = followingSiblingsSearchScope(match);
            }
        } else {
            useScope = variableUseScope(parent);
        }

        return useScope;
    }

    private static LocalSearchScope variableUseScope(@NotNull PsiElement ancestor) {
        LocalSearchScope useScope = null;

        if (ancestor instanceof ElixirAccessExpression ||
                ancestor instanceof ElixirAssociations ||
                ancestor instanceof ElixirAssociationsBase ||
                ancestor instanceof ElixirBitString ||
                ancestor instanceof ElixirContainerAssociationOperation ||
                ancestor instanceof ElixirKeywordPair ||
                ancestor instanceof ElixirKeywords ||
                ancestor instanceof ElixirList ||
                ancestor instanceof ElixirMapArguments ||
                ancestor instanceof ElixirMapConstructionArguments ||
                ancestor instanceof ElixirMapOperation ||
                ancestor instanceof ElixirMatchedParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesOneArgument ||
                ancestor instanceof ElixirNoParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesKeywordPair ||
                ancestor instanceof ElixirNoParenthesesKeywords ||
                ancestor instanceof ElixirParenthesesArguments ||
                ancestor instanceof ElixirParentheticalStab ||
                ancestor instanceof ElixirStab ||
                ancestor instanceof ElixirStabBody ||
                ancestor instanceof ElixirStabNoParenthesesSignature ||
                ancestor instanceof ElixirStabParenthesesSignature ||
                ancestor instanceof ElixirStructOperation ||
                ancestor instanceof ElixirTuple ||
                ancestor instanceof InMatch ||
                ancestor instanceof Type ||
                ancestor instanceof UnqualifiedNoArgumentsCall) {
            useScope = variableUseScope(ancestor.getParent());
        } else if (ancestor instanceof ElixirStabOperation ||
                ancestor instanceof QualifiedAlias) {
            useScope = new LocalSearchScope(ancestor);
        } else if (ancestor instanceof Match) {
            useScope = variableUseScope((Match) ancestor);
        } else if (ancestor instanceof Call) {
            useScope = variableUseScope((Call) ancestor);
        } else {
            Logger.error(Callable.class, "Don't know how to find variable use scope for ", ancestor);
        }

        return useScope;
    }

    private static List<LookupElement> variablesInElement(@NotNull PsiElement element,
                                                          @Nullable List<LookupElement> lookupElementList) {
        if (element instanceof UnqualifiedNoArgumentsCall) {
            lookupElementList = add(lookupElementList, (UnqualifiedNoArgumentsCall) element);
        } else {
            if (!(element instanceof ElixirStabBody ||
                    element instanceof ElixirEndOfExpression ||
                    element instanceof PsiWhiteSpace)) {
                Logger.error(Callable.class, "Don't know how to find variables in ", element);
            }
        }

        return lookupElementList;
    }

    @Nullable
    private static List<LookupElement> variablesInPreviousSiblings(@NotNull PsiElement lastSibling,
                                                                   @Nullable List<LookupElement> lookupElementList) {
        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            lookupElementList = variablesInElement(sibling, lookupElementList);
        }

        return lookupElementList;
    }

    /*
     * Constructors
     */

    public Callable(@NotNull Call call) {
        super(call, TextRange.create(0, call.getTextLength()));
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return ((NamedElement) myElement).setName(newElementName);
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
        List<ResolveResult> resolveResultList = null;

        /* to differentiate from UnqualifiedParenthesesCalls with no arguments in the parentheses that would have a
           final arity of 0 too */
        if (myElement instanceof UnqualifiedNoArgumentsCall) {
            // ensure that a pipe isn't making a no argument call really a 1-arity call
            int resolvedFinalArity = myElement.resolvedFinalArity();

            if (resolvedFinalArity == 0) {
                String name = myElement.getName();

                if (name != null) {
                    if (!isBitStreamSegmentOption(myElement)) {
                        resolveResultList = MultiResolve.resolveResultList(name, incompleteCode, myElement);
                    }
                }
            }
        }

        ResolveResult[] resolveResults;

        if (resolveResultList == null) {
            resolveResults = new ResolveResult[0];
        } else {
            resolveResults = resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
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

}
