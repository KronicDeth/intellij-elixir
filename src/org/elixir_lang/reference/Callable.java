package org.elixir_lang.reference;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.IncorrectOperationException;
import org.elixir_lang.annonator.Parameter;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.call.name.*;
import org.elixir_lang.psi.call.name.Module;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.psi.scope.variable.Variants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;

public class Callable extends PsiReferenceBase<Call> implements PsiPolyVariantReference {
    /*
     *
     * CONSTANTS
     *
     */

    /*
     * Public CONSTANTS
     */

    public static final Set<String> BIT_STRING_TYPES = Sets.newHashSet(
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

    /*
     * Private CONSTANTS
     */

    private static final Set<String> BIT_STRING_ENDIANNESS = Sets.newHashSet(
            "big",
            "little",
            "native"
    );
    private static final Set<String> BIT_STRING_SIGNEDNESS = Sets.newHashSet(
            "signed",
            "unsigned"
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

    public static String ignoredElementDescription(@SuppressWarnings("unused") Call call,
                                                   ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "ignored";
        }

        return elementDescription;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
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
        Parameter parameter = new Parameter(ancestor);
        return Parameter.putParameterized(parameter).type != null;
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

                if (defaulted != null && defaulted.isEquivalentTo(element)) {
                    isParameterWithDefault = isParameter(parentOperation);
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
                ancestor instanceof ElixirBlockItem ||
                ancestor instanceof ElixirBlockList ||
                ancestor instanceof ElixirBracketArguments ||
                ancestor instanceof ElixirContainerAssociationOperation ||
                ancestor instanceof ElixirDoBlock ||
                ancestor instanceof ElixirKeywordPair ||
                ancestor instanceof ElixirKeywords ||
                ancestor instanceof ElixirList ||
                ancestor instanceof ElixirMapArguments ||
                ancestor instanceof ElixirMapConstructionArguments ||
                ancestor instanceof ElixirMapOperation ||
                ancestor instanceof ElixirMapUpdateArguments ||
                /* parenthesesArguments can be used in @spec other type declarations, so may not be variable until
                   ancestor call is checked */
                ancestor instanceof ElixirMatchedParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesOneArgument ||
                ancestor instanceof ElixirNoParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesKeywordPair ||
                ancestor instanceof ElixirNoParenthesesKeywords ||
                /* ElixirNoParenthesesManyStrictNoParenthesesExpression and ElixirNoParenthesesStrict
                   indicates a syntax error, but it can also occur during typing, so try searching above the syntax
                   error to resolve whether a variable */
                ancestor instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression ||
                ancestor instanceof ElixirNoParenthesesStrict ||
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
            if (!(ancestor instanceof AtNonNumericOperation ||
                    ancestor instanceof BracketOperation ||
                    ancestor instanceof PsiFile ||
                    ancestor instanceof QualifiedMultipleAliases)) {
                error("Don't know how to check if variable", ancestor);
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

    public static String parameterWithDefaultElementDescription(@SuppressWarnings("unused") Call call,
                                                                ElementDescriptionLocation location) {
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

    private static void error(@NotNull String message, @NotNull PsiElement element) {
        Logger.error(Callable.class, message, element);
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
        } else if (call.isCalling(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DESTRUCTURE)) {
            isVariable = true;
        } else if (call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.FOR)) {
            isVariable = true;
        } else if (call.isCalling(Module.KERNEL, Function.VAR_BANG)) {
            isVariable = true;
        } else {
            PsiElement parent = call.getParent();
            isVariable = isVariable(parent);
        }

        return isVariable;
    }

    @Nullable
    private static <T> List<T> merge(@Nullable List<T> maybeFirst, @Nullable List<T> maybeSecond) {
        List<T> merged = null;

        if (maybeFirst != null && maybeSecond != null) {
            merged = new ArrayList<T>(maybeFirst);
            merged.addAll(maybeSecond);
        } else if (maybeFirst != null) {
            merged = maybeFirst;
        } else if (maybeSecond != null) {
            merged = maybeSecond;
        }

        return merged;
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
            @SuppressWarnings("unchecked")
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
        } else if (parent instanceof PsiFile) {
            useScope = followingSiblingsSearchScope(match);
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
                ancestor instanceof ElixirBlockItem ||
                ancestor instanceof ElixirBlockList ||
                ancestor instanceof ElixirContainerAssociationOperation ||
                ancestor instanceof ElixirDoBlock ||
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
        } else if (ancestor instanceof ElixirInterpolation) {
            /* no variable can be declared inside an interpolation, so this is a variable usage missing a declaration,
               so it has no use scope */
            useScope = LocalSearchScope.EMPTY;
        } else {
            error("Don't know how to find variable use scope", ancestor);
        }

        return useScope;
    }

    /*
     * Constructors
     */

    public Callable(@NotNull Call call) {
        super(call);
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
        List<LookupElement> variableLookupElementList = null;

        if (myElement instanceof UnqualifiedNoArgumentsCall) {
            variableLookupElementList = Variants.lookupElementList(myElement);
        }

        List<LookupElement> callDefinitionClauseLookupElementList =
                org.elixir_lang.psi.scope.call_definition_clause.Variants.lookupElementList(myElement);

        List<LookupElement> lookupElementList = merge(variableLookupElementList, callDefinitionClauseLookupElementList);

        Object[] variants;

        if (lookupElementList == null) {
            variants = new Object[0];
        } else {
            variants = lookupElementList.toArray(new Object[lookupElementList.size()]);
        }

        return variants;
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
        String name = myElement.getName();

        if (name != null) {
            int resolvedFinalArity = myElement.resolvedFinalArity();
            List<ResolveResult> variableResolveList = null;

            if (resolvedFinalArity == 0) {
                variableResolveList = org.elixir_lang.psi.scope.variable.MultiResolve.resolveResultList(
                        name,
                        incompleteCode,
                        myElement
                );
            }

            List<ResolveResult> callDefinitionClauseResolveResultList =
                    org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResultList(
                            name,
                            resolvedFinalArity,
                            incompleteCode,
                            myElement
                    );

            resolveResultList = merge(variableResolveList, callDefinitionClauseResolveResultList);
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

    /*
     * Protected Instance Methods
     */

    @Override
    protected TextRange calculateDefaultRangeInElement() {
        TextRange defaultRangeInElement = null;
        TextRange myElementRangeInDocument = myElement.getTextRange();
        int myElementStartOffset = myElementRangeInDocument.getStartOffset();

        if (myElement instanceof Named) {
            Named named = (Named) myElement;
            PsiElement nameIdentifier = named.getNameIdentifier();

            if (nameIdentifier != null) {
                TextRange nameIdentifierRangeInDocument = nameIdentifier.getTextRange();
                defaultRangeInElement = new TextRange(
                        nameIdentifierRangeInDocument.getStartOffset() - myElementStartOffset,
                        nameIdentifierRangeInDocument.getEndOffset() - myElementStartOffset
                );
            }
        }

        if (defaultRangeInElement == null) {
            defaultRangeInElement = new TextRange(
                    0,
                    myElementRangeInDocument.getEndOffset() - myElementStartOffset
            );
        }

        return defaultRangeInElement;
    }
}
