package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageViewUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.annotator.Parameter;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.MaybeExported;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.call.arguments.star.NoParentheses;
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument;
import org.elixir_lang.psi.call.arguments.star.Parentheses;
import org.elixir_lang.psi.call.name.Function;
import org.elixir_lang.psi.impl.call.CallImpl;
import org.elixir_lang.psi.impl.call.CanonicallyNamedImpl;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.psi.operation.infix.Position;
import org.elixir_lang.psi.operation.infix.Triple;
import org.elixir_lang.psi.qualification.Qualified;
import org.elixir_lang.psi.qualification.Unqualified;
import org.elixir_lang.psi.stub.call.Stub;
import org.elixir_lang.reference.Callable;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.CallDefinitionSpecification;
import org.elixir_lang.structure_view.element.Callback;
import org.elixir_lang.structure_view.element.Delegation;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.elixir_lang.errorreport.Logger.error;
import static org.elixir_lang.mix.importWizard.ImportedOtpAppKt.computeReadAction;
import static org.elixir_lang.psi.call.name.Function.*;
import static org.elixir_lang.psi.call.name.Module.*;
import static org.elixir_lang.psi.impl.QuotableImpl.*;
import static org.elixir_lang.psi.impl.QuotableImpl.NIL;
import static org.elixir_lang.psi.stub.type.call.Stub.isModular;
import static org.elixir_lang.reference.Callable.*;
import static org.elixir_lang.reference.ModuleAttribute.isNonReferencing;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;

/**
 * Created by luke.imhoff on 12/29/14.
 */
public class ElixirPsiImplUtil {
    public static final Class[] UNQUOTED_TYPES = new Class[]{
            ElixirEndOfExpression.class,
            PsiComment.class,
            PsiWhiteSpace.class
    };

    public static final OtpErlangAtom BLOCK = new OtpErlangAtom("__block__");
    public static final String DEFAULT_OPERATOR = "\\\\";
    public static final OtpErlangAtom DO = new OtpErlangAtom("do");
    public static final Key<PsiElement> ENTRANCE = new Key<PsiElement>("ENTRANCE");

    public static final OtpErlangAtom FALSE = new OtpErlangAtom("false");
    public static final OtpErlangAtom FN = new OtpErlangAtom("fn");

    public static final OtpErlangAtom TRUE = new OtpErlangAtom("true");
    public static final TokenSet ADDITION_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.DUAL_OPERATOR);
    public static final TokenSet AND_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirTypes.AND_WORD_OPERATOR);
    public static final TokenSet AT_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.AT_OPERATOR);
    public static final TokenSet CAPTURE_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.CAPTURE_OPERATOR);
    public static final TokenSet COMPARISON_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.COMPARISON_OPERATOR);
    public static final TokenSet DOT_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.DOT_OPERATOR);
    public static final TokenSet IN_MATCH_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.IN_MATCH_OPERATOR);
    public static final TokenSet IN_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.IN_OPERATOR);
    public static final TokenSet MATCH_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.MATCH_OPERATOR);
    public static final TokenSet NOT_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.NOT_OPERATOR);
    public static final TokenSet MULTIPLICATIVE_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.DIVISION_OPERATOR, ElixirTypes.MULTIPLICATION_OPERATOR);
    public static final TokenSet OR_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.OR_SYMBOL_OPERATOR, ElixirTypes.OR_WORD_OPERATOR);
    public static final TokenSet PIPE_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.PIPE_OPERATOR);
    public static final TokenSet RELATIONAL_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.RELATIONAL_OPERATOR);
    public static final TokenSet STAB_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.STAB_OPERATOR);
    public static final TokenSet STRUCT_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.STRUCT_OPERATOR);
    public static final TokenSet THREE_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.THREE_OPERATOR);
    public static final TokenSet TWO_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.RANGE_OPERATOR, ElixirTypes.TWO_OPERATOR);
    public static final TokenSet TYPE_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.TYPE_OPERATOR);
    public static final TokenSet UNARY_OPERATOR_TOKEN_SET = TokenSet.create(
            ElixirTypes.DUAL_OPERATOR, ElixirTypes.NOT_OPERATOR, ElixirTypes.SIGN_OPERATOR, ElixirTypes.UNARY_OPERATOR
    );
    public static final OtpErlangAtom UNQUOTE_SPLICING = new OtpErlangAtom("unquote_splicing");
    public static final OtpErlangAtom[] ATOM_KEYWORDS = new OtpErlangAtom[]{
            FALSE,
            TRUE,
            NIL
    };
    public static final OtpErlangAtom UTF_8 = new OtpErlangAtom("utf8");
    public static final TokenSet IDENTIFIER_TOKEN_SET = TokenSet.create(ElixirTypes.IDENTIFIER_TOKEN);
    public static final com.intellij.util.Function<PsiElement, PsiElement> NEXT_SIBLING = new com.intellij.util.Function<PsiElement, PsiElement>() {
                @Override
                public PsiElement fun(PsiElement element) {
                    return element.getNextSibling();
                }
            };
    public static final com.intellij.util.Function<PsiElement, PsiElement> PREVIOUS_SIBLING = new com.intellij.util.Function<PsiElement, PsiElement>() {
                @Override
                public PsiElement fun(PsiElement element) {
                    return element.getPrevSibling();
                }
            };
    public static final TokenSet ARROW_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.ARROW_OPERATOR);
    public static final TokenSet WHEN_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.WHEN_OPERATOR);

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirMapConstructionArguments mapConstructionArguments) {
        return mapConstructionArguments.getChildren();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument) {
        PsiElement[] children = noParenthesesOneArgument.getChildren();
        PsiElement[] arguments = children;

        if (children.length == 1) {
            PsiElement child = children[0];

            if (child instanceof Arguments) {
                Arguments childArguments = (Arguments) child;
                arguments = childArguments.arguments();
            }
        }

        return arguments;
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirNoParenthesesStrict noParenthesesStrict) {
        return noParenthesesStrict.getChildren();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirParenthesesArguments parenthesesArguments) {
        return parenthesesArguments.getChildren();
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirBinaryDigits binaryDigits) {
        return DigitsImpl.base(binaryDigits);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirBinaryWholeNumber binaryWholeNumber) {
        return WholeNumberImpl.base(binaryWholeNumber);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirDecimalDigits decimalDigits) {
        return DigitsImpl.base(decimalDigits);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirDecimalWholeNumber decimalWholeNumber) {
        return WholeNumberImpl.base(decimalWholeNumber);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirHexadecimalDigits hexadecimalDigits) {
        return DigitsImpl.base(hexadecimalDigits);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirHexadecimalWholeNumber hexadecimalWholeNumber) {
        return WholeNumberImpl.base(hexadecimalWholeNumber);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirOctalDigits octalDigits) {
        return DigitsImpl.base(octalDigits);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirOctalWholeNumber octalWholeNumber) {
        return WholeNumberImpl.base(octalWholeNumber);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirUnknownBaseDigits unknownBaseDigits) {
        return DigitsImpl.base(unknownBaseDigits);
    }

    @Contract(pure = true)
    public static int base(@NotNull final ElixirUnknownBaseWholeNumber unknownBaseWholeNumber) {
        return WholeNumberImpl.base(unknownBaseWholeNumber);
    }

    @NotNull
    public static String javaString(@NotNull OtpErlangBinary elixirString) {
        byte[] bytes = elixirString.binaryValue();
        return new String(bytes, Charset.forName("UTF-8"));
    }

    @Nullable
    public static String canonicalName(@NotNull StubBased stubBased) {
       return CanonicallyNamedImpl.canonicalName(stubBased);
    }

    @NotNull
    public static Set<String> canonicalNameSet(@NotNull StubBased stubBased) {
        return CanonicallyNamedImpl.canonicalNameSet(stubBased);
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirEscapedCharacter escapedCharacter) {
        return EscapeSequenceImpl.codePoint(escapedCharacter);
    }

    @Contract(pure = true)
    public static int codePoint(ElixirEscapedEOL escapedEOL) {
        return EscapeSequenceImpl.codePoint(escapedEOL);
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull EscapedHexadecimalDigits hexadecimalEscapeSequence) {
        return EscapeSequenceImpl.codePoint(hexadecimalEscapeSequence);
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirQuoteHexadecimalEscapeSequence quoteHexadecimalEscapeSequence) {
        return EscapeSequenceImpl.codePoint(quoteHexadecimalEscapeSequence);
    }

    public static int codePoint(@NotNull ElixirSigilHexadecimalEscapeSequence sigilHexadecimalEscapeSequence) {
        return EscapeSequenceImpl.codePoint(sigilHexadecimalEscapeSequence);
    }

    @Nullable
    public static String definedModuleName(@NotNull final ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall) {
        PsiElement[] arguments = unmatchedUnqualifiedNoParenthesesCall.primaryArguments();
        String definedModuleName = null;

        if (arguments.length > 1) {
            PsiElement argument = arguments[0];
        }

        return definedModuleName;
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirBinaryWholeNumber binaryWholeNumber) {
        return WholeNumberImpl.digitsList(binaryWholeNumber);
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirDecimalWholeNumber decimalWholeNumber) {
        return WholeNumberImpl.digitsList(decimalWholeNumber);
    }

    @NotNull static List<Digits> digitsList(@NotNull ElixirHexadecimalWholeNumber hexadecimalWholeNumber) {
        return WholeNumberImpl.digitsList(hexadecimalWholeNumber);
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirOctalWholeNumber octalWholeNumber) {
        return WholeNumberImpl.digitsList(octalWholeNumber);
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirUnknownBaseWholeNumber unknownBaseWholeNumber) {
        return WholeNumberImpl.digitsList(unknownBaseWholeNumber);
    }

    public static Document document(PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();

        return fileViewProvider.getDocument();
    }

    @NotNull
    public static String identifierName(ElixirAtIdentifier atIdentifier) {
        ASTNode node = atIdentifier.getNode();
        ASTNode[] identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET);

        assert identifierNodes.length == 1;

        ASTNode identifierNode = identifierNodes[0];
        return identifierNode.getText();
    }

    /**
     * The keyword arguments for {@code call}.
     * @param call call to search for keyword arguments.
     * @return the final element of the {@link ElixirPsiImplUtil#finalArguments(Call)} of {@code} if they are a
     *   {@link QuotableKeywordList}; otherwise, {@code null}.
     */
    @Nullable
    public static QuotableKeywordList keywordArguments(@NotNull final Call call) {
        PsiElement[] finalArguments = finalArguments(call);
        QuotableKeywordList keywordArguments = null;

        if (finalArguments != null) {
            int finalArgumentCount = finalArguments.length;

            if (finalArgumentCount > 0) {
                PsiElement potentialKeywords = finalArguments[finalArgumentCount - 1];

                if (potentialKeywords instanceof QuotableKeywordList) {
                    keywordArguments = (QuotableKeywordList) potentialKeywords;
                } else if (potentialKeywords instanceof ElixirAccessExpression) {
                    PsiElement accessExpressionChild = stripAccessExpression(potentialKeywords);

                    if (accessExpressionChild instanceof ElixirList) {
                        ElixirList list = (ElixirList) accessExpressionChild;
                        PsiElement[] listChildren = list.getChildren();

                        if (listChildren.length == 1) {
                            PsiElement listChild = listChildren[0];

                            if (listChild instanceof QuotableKeywordList) {
                                keywordArguments = (QuotableKeywordList) listChild;
                            }
                        }
                    }
                }
            }
        }

        return keywordArguments;
    }

    /**
     * The value of the keyword argument with the given keywordKeyText.
     *
     * @param call call to seach for the keyword argument.
     * @param keywordKeyText the text of the key, such as {@code "do"}
     * @return the keyword value {@code PsiElement} if {@code call} has {@link ElixirPsiImplUtil#keywordArguments(Call)}
     *   and there is a {@link ElixirPsiImplUtil.keywordValue(QuotableKeywordList)} for {@code keywordKeyText}.
     */
    @Nullable
    public static PsiElement keywordArgument(@NotNull final Call call, String keywordKeyText) {
        QuotableKeywordList keywordArguments = keywordArguments(call);
        PsiElement keywordValue = null;

        if (keywordArguments != null) {
            keywordValue = keywordValue(keywordArguments, keywordKeyText);
        }

        return keywordValue;
    }

    public static OtpErlangTuple keywordTuple(String key, int value) {
        final OtpErlangAtom keyAtom = new OtpErlangAtom(key);
        final OtpErlangInt valueInt = new OtpErlangInt(value);
        final OtpErlangObject[] elements = {
                keyAtom,
                valueInt
        };

        return new OtpErlangTuple(elements);
    }

    /**
     * The value associated with the keyword value.
     *
     * @param keywordList The keyword list to search for {@code keywordKeyText}.
     * @param keywordKeyText the text of the keyword value.
     * @return the {@code PsiElement} associated with {@code keywordKeyText}.
     */
    @Nullable
    public static Quotable keywordValue(QuotableKeywordList keywordList, String keywordKeyText) {
        Quotable keywordValue = null;

        for (QuotableKeywordPair quotableKeywordPair : keywordList.quotableKeywordPairList()) {
            if (hasKeywordKey(quotableKeywordPair, keywordKeyText)) {
                keywordValue = quotableKeywordPair.getKeywordValue();
            }
        }

        return keywordValue;
    }

    public static boolean inBase(@NotNull final Digits digits) {
        return DigitsImpl.inBase(digits);
    }

    public static boolean isCalling(@NotNull final Call call,
                                    @NotNull final String resolvedModuleName,
                                    @NotNull final String functionName) {
        return CallImpl.isCalling(call, resolvedModuleName, functionName);
    }

    public static boolean isCalling(@NotNull final Call call,
                                    @NotNull final String resolvedModuleName,
                                    @NotNull final String functionName,
                                    final int resolvedFinalArity) {
        return CallImpl.isCalling(call, resolvedModuleName, functionName, resolvedFinalArity);
    }

    public static boolean isCallingMacro(@NotNull final Call call,
                                         @NotNull final String resolvedModuleName,
                                         @NotNull final String functionName) {
        return CallImpl.isCallingMacro(call, resolvedModuleName, functionName);
    }

    @Contract(pure = true)
    public static boolean isCallingMacro(@NotNull final Call call,
                                         @NotNull final String resolvedModuleName,
                                         @NotNull final  String functionName,
                                         final int resolvedFinalArity) {
        return CallImpl.isCallingMacro(call, resolvedModuleName, functionName, resolvedFinalArity);
    }

    public static boolean isExported(@NotNull final UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        return CallDefinitionClause.isPublicFunction(unqualifiedNoParenthesesCall) ||
                CallDefinitionClause.isPublicMacro(unqualifiedNoParenthesesCall);
    }

    /**
     * Whether the {@code arrow} is a pipe operation.
     *
     * @param arrow the parent (or futher ancestor of a {@link Call} that may be piped.
     * @return {@code} true if {@code arrow} is using the {@code "|>"} operator token.
     */
    public static boolean isPipe(@NotNull Arrow arrow) {
        Operator operator = arrow.operator();
        ASTNode[] arrowOperatorChildren = operator.getNode().getChildren(ARROW_OPERATOR_TOKEN_SET);
        boolean isPipe = false;

        if (arrowOperatorChildren.length == 1) {
            ASTNode arrowOperatorChild = arrowOperatorChildren[0];

            if (arrowOperatorChild.getText().equals("|>")) {
                isPipe = true;
            }
        }

        return isPipe;
    }

    /**
     * Whether the {@code callAncestor} is a pipe operation.
     *
     * @param callAncestor the parent (or further ancestor) of a {@link Call} that may be piped
     * @return {@code} true if {@code callAncestor} is an {@link Arrow} using the {@code "|>"} operator token.
     */
    public static boolean isPipe(@NotNull PsiElement callAncestor) {
        boolean isPipe = false;

        if (callAncestor instanceof Arrow) {
            isPipe = isPipe((Arrow) callAncestor);
        }

        return isPipe;
    }

    /*
     * Whether this is an argument in `defmodule <argument> do end` call.
     */
    public static boolean isModuleName(@NotNull final ElixirAccessExpression accessExpression) {
        PsiElement parent = accessExpression.getParent();
        boolean isModuleName = false;

        if (parent instanceof ElixirNoParenthesesOneArgument) {
            ElixirNoParenthesesOneArgument noParenthesesOneArgument = (ElixirNoParenthesesOneArgument) parent;

            isModuleName = noParenthesesOneArgument.isModuleName();
        }

        return isModuleName;
    }

    public static boolean isModuleName(@NotNull final QualifiableAlias qualifiableAlias) {
        PsiElement parent = qualifiableAlias.getParent();
        int siblingCount = parent.getChildren().length - 1;
        boolean isModuleName = false;

        /* check that this qualifiableAlias is the only child so subsections of alias chains don't say they are module
           names. */
        if (siblingCount == 0 && parent instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) parent;

            isModuleName = maybeModuleName.isModuleName();
        }

        return isModuleName;
    }

    /*
     * Whether this is an argument in `defmodule <argument> do end` call.
     */
    public static boolean isModuleName(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument) {
        PsiElement parent = noParenthesesOneArgument.getParent();
        boolean isModuleName = false;

        if (parent instanceof ElixirUnmatchedUnqualifiedNoParenthesesCall) {
            ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall = (ElixirUnmatchedUnqualifiedNoParenthesesCall) parent;

            isModuleName = unmatchedUnqualifiedNoParenthesesCall.isCallingMacro(KERNEL, DEFMODULE, 2);
        }

        return isModuleName;
    }

    @Contract(pure = true)
    public static boolean isOutermostQualifiableAlias(@NotNull QualifiableAlias qualifiableAlias) {
        PsiElement parent = qualifiableAlias.getParent();
        boolean outermost = false;

        /* prevents individual Aliases or tail qualified aliases of qualified chain from having reference separate
           reference from overall chain */
        if (!(parent instanceof QualifiableAlias)) {
            PsiElement grandParent = parent.getParent();

            // prevents first Alias of a qualified chain from having a separate reference from overall chain
            if (!(grandParent instanceof QualifiableAlias)) {
                outermost = true;
            }
        }

        return outermost;
    }

    /*
     * @return {@code true} if {@code element} should not have {@code quote} called on it because Elixir natively
     *   ignores such tokens.  {@code false} if {@code element} should have {@code quote} called on it.
     */
    public static boolean isUnquoted(PsiElement element) {
        boolean unquoted = false;

        for (Class unquotedType : UNQUOTED_TYPES) {
            if (unquotedType.isInstance(element)) {
                unquoted = true;
                break;
            }
        }

        return unquoted;
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull final ElixirStabOperation stabOperation) {
        Quotable leftOperand = stabOperation.getStabParenthesesSignature();

        if (leftOperand == null) {
            leftOperand = stabOperation.getStabNoParenthesesSignature();
        }

        return leftOperand;
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(Infix infix) {
        return org.elixir_lang.psi.operation.infix.Normalized.leftOperand(infix);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable leftOperand(@NotNull NotIn notIn) {
        return org.elixir_lang.psi.operation.not_in.Normalized.leftOperand(notIn);
    }

    /* Returns the 0-indexed line number for the element */
    public static int lineNumber(ASTNode node) {
        return document(node.getPsi()).getLineNumber(node.getStartOffset());
    }

    public static OtpErlangTuple lineNumberKeywordTuple(ASTNode node) {
        return keywordTuple(
                "line",
                lineNumber(node) + 1
        );
    }

    @NotNull
    public static Call[] macroChildCalls(Call macro) {
        List<Call> childCallList = macroChildCallList(macro);

        return childCallList.toArray(new Call[childCallList.size()]);
    }

    @NotNull
    public static List<Call> macroChildCallList(@NotNull Call macro) {
        List<Call> childCallList = null;
        ElixirDoBlock doBlock = macro.getDoBlock();

        if (doBlock != null) {
            ElixirStab stab = doBlock.getStab();

            if (stab != null) {
                PsiElement[] stabChildren = stab.getChildren();

                if (stabChildren.length == 1) {
                    PsiElement stabChild = stabChildren[0];

                    if (stabChild instanceof ElixirStabBody) {
                        childCallList = macroChildCallList(stabChild);
                    }
                }
            }
        } else { // one liner version with `do:` keyword argument
            PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(macro);

            assert finalArguments != null;
            assert finalArguments.length > 0;

            PsiElement potentialKeywords = finalArguments[finalArguments.length - 1];

            if (potentialKeywords instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) potentialKeywords;
                List<QuotableKeywordPair> quotableKeywordPairList = quotableKeywordList.quotableKeywordPairList();
                QuotableKeywordPair firstQuotableKeywordPair = quotableKeywordPairList.get(0);
                Quotable keywordKey = firstQuotableKeywordPair.getKeywordKey();

                if (keywordKey.getText().equals("do")) {
                    Quotable keywordValue = firstQuotableKeywordPair.getKeywordValue();

                    if (keywordValue instanceof Call) {
                        Call childCall = (Call) keywordValue;
                        childCallList = Collections.singletonList(childCall);
                    }
                }
            }
        }

        if (childCallList == null) {
            childCallList = Collections.emptyList();
        }

        return childCallList;
    }

    @NotNull
    private static List<Call> macroChildCallList(@NotNull PsiElement element) {
        List<Call> callList;

        if (element instanceof ElixirAccessExpression) {
            callList = macroChildCallList(element.getFirstChild());
        } else if (element instanceof ElixirList || element instanceof ElixirStabBody) {
            callList = new ArrayList<>();

            for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child instanceof Call) {
                    Call childCall = (Call) child;
                    callList.add(childCall);
                } else if (child instanceof ElixirAccessExpression) {
                    callList.addAll(macroChildCallList(child));
                }
            }
        } else {
            callList = Collections.emptyList();
        }

        return callList;
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleAttributeName(@NotNull final AtNonNumericOperation atNonNumericOperation) {
        return atNonNumericOperation.getText();
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleAttributeName(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return atUnqualifiedNoParenthesesCall.getAtIdentifier().getText();
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return CallImpl.moduleName(atUnqualifiedNoParenthesesCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull DotCall dotCall) {
        return CallImpl.moduleName(dotCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull NotIn notIn) {
        return CallImpl.moduleName(notIn);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull Operation operation) {
        return CallImpl.moduleName(operation);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull Unqualified unqualified) {
        return CallImpl.moduleName(unqualified);
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleName(@NotNull final Qualified qualified) {
        return CallImpl.moduleName(qualified);
    }

    private static GlobalSearchScope moduleWithDependentsScope(PsiElement element) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        Project project = element.getProject();
        com.intellij.openapi.module.Module module = ModuleUtilCore.findModuleForFile(
                virtualFile,
                project
        );

        GlobalSearchScope globalSearchScope;

        // module can be null for scratch files
        if (module != null) {
            globalSearchScope = GlobalSearchScope.moduleWithDependentsScope(module);
        } else {
            globalSearchScope = GlobalSearchScope.allScope(project);
        }

        return globalSearchScope;
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement nextSiblingExpression(@NotNull PsiElement element) {
        return siblingExpression(element, NEXT_SIBLING);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable operand(Prefix prefix) {
        return org.elixir_lang.psi.operation.prefix.Normalized.operand(prefix);
    }

    @Contract(pure = true)
    @NotNull
    public static Operator operator(@NotNull final ElixirStabOperation stabOperation) {
        return stabOperation.getStabInfixOperator();
    }

    @Contract(pure = true)
    @NotNull
    public static Operator operator(Infix infix) {
        return Normalized.operator(infix);
    }

    @Contract(pure = true)
    @NotNull
    public static Operator operator(Prefix prefix) {
        return Normalized.operator(prefix);
    }

    @Contract(pure = true)
    @NotNull
    public static ASTNode operatorTokenNode(@NotNull Operator operator) {
        return operator
                    .getNode()
                    .getChildren(
                            operator.operatorTokenSet()
                    )[0];
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAdditionInfixOperator additionInfixOperator) {
        return ADDITION_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAndInfixOperator andInfixOperator) {
        return AND_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirArrowInfixOperator arrowInfixOperator) {
        return ARROW_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAtPrefixOperator atPrefixOperator) {
        return AT_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirCapturePrefixOperator capturePrefixOperator) {
        return CAPTURE_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirComparisonInfixOperator comparisonInfixOperator) {
        return COMPARISON_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirDotInfixOperator dotInfixOperator) {
        return DOT_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMultiplicationInfixOperator multiplicationInfixOperator) {
        return MULTIPLICATIVE_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirInInfixOperator inInfixOperator) {
        return IN_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirInMatchInfixOperator inMatchInfixOperator) {
        return IN_MATCH_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMapPrefixOperator mapPrefixOperator) {
        return STRUCT_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMatchInfixOperator matchInfixOperator) {
        return MATCH_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirNotInfixOperator notInfixOperator) {
        return NOT_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirOrInfixOperator orInfixOperator) {
        return OR_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirPipeInfixOperator pipeInfixOperator) {
        return PIPE_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirRelationalInfixOperator relationalInfixOperator) {
        return RELATIONAL_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirStabInfixOperator stabInfixOperator) {
        return STAB_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirThreeInfixOperator threeInfixOperator) {
        return THREE_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirTwoInfixOperator twoInfixOperator) {
        return TWO_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirTypeInfixOperator typeInfixOperator) {
        return TYPE_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirUnaryPrefixOperator unaryPrefixOperator) {
        return UNARY_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirWhenInfixOperator whenInfixOperator) {
        return WHEN_OPERATOR_TOKEN_SET;
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement previousSiblingExpression(@NotNull PsiElement element) {
        return siblingExpression(element, PREVIOUS_SIBLING);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final DotCall dotCall) {
        return CallImpl.primaryArguments(dotCall);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final Infix infix) {
        return CallImpl.primaryArguments(infix);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(
            @NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall
    ) {
        return CallImpl.primaryArguments(unqualifiedNoParenthesesManyArgumentsCall);
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] primaryArguments(@NotNull None none) {
        return CallImpl.primaryArguments(none);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final NotIn notIn) {
        return CallImpl.primaryArguments(notIn);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final NoParenthesesOneArgument noParenthesesOneArgument) {
        return CallImpl.primaryArguments(noParenthesesOneArgument);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final Parentheses parentheses) {
        return CallImpl.primaryArguments(parentheses);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final Prefix prefix) {
        return CallImpl.primaryArguments(prefix);
    }

    @Contract(pure = true)
    @Nullable
    public static Integer primaryArity(@NotNull final Call call) {
        return CallImpl.primaryArity(call);
    }

    public static boolean processDeclarations(@NotNull final And and,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(and, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final Call call,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(call, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final ElixirAlias alias,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(alias, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final ElixirMultipleAliases multipleAliases,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement entrance) {
        return ProcessDeclarationsImpl.processDeclarations(multipleAliases, processor, state, lastParent, entrance);
    }

    public static boolean processDeclarations(@NotNull final ElixirStabBody scope,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull @SuppressWarnings("unused") PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(scope, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final ElixirStabOperation stabOperation,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(stabOperation, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final Match match,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(match, processor, state, lastParent, place);
    }

    public static boolean processDeclarations(@NotNull final QualifiedAlias qualifiedAlias,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return ProcessDeclarationsImpl.processDeclarations(qualifiedAlias, processor, state, lastParent, place);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull AssociationOperation associationOperation) {
        return QuotableImpl.quote(associationOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull Infix infix) {
        return QuotableImpl.quote(infix);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull NotIn notIn) {
        return QuotableImpl.quote(notIn);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirBitString bitString) {
        return QuotableImpl.quote(bitString);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirBlockIdentifier blockIdentifier) {
        return QuotableImpl.quote(blockIdentifier);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirBlockItem blockItem) {
        return QuotableImpl.quote(blockItem);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirBracketArguments bracketArguments) {
        return QuotableImpl.quote(bracketArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull Digits digits) {
        return QuotableImpl.quote(digits);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAccessExpression accessExpression) {
        return QuotableImpl.quote(accessExpression);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAlias alias) {
        return QuotableImpl.quote(alias);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAnonymousFunction anonymousFunction) {
        return QuotableImpl.quote(anonymousFunction);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAssociations associations) {
        return QuotableImpl.quote(associations);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAssociationsBase associationsBase) {
        return QuotableImpl.quote(associationsBase);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAtom atom) {
        return QuotableImpl.quote(atom);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirAtomKeyword atomKeyword) {
        return QuotableImpl.quote(atomKeyword);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirCharListLine charListLine) {
        return QuotableImpl.quote(charListLine);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirCharToken charToken) {
        return QuotableImpl.quote(charToken);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirContainerAssociationOperation containerAssociationOperation) {
        return QuotableImpl.quote(containerAssociationOperation);
    }

    /**
     *
     * @param call
     * @return {@code null} if call is at top-level
     */
    @Contract(pure = true)
    @Nullable
    public static Call enclosingMacroCall(PsiElement element) {
        Call enclosingMacroCall = null;
        PsiElement parent = element.getParent();

        if (parent instanceof ElixirDoBlock) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof Call) {
                enclosingMacroCall = (Call) grandParent;
            }
        } else if (parent instanceof ElixirStabBody) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof ElixirStab) {
                PsiElement greatGrandParent = grandParent.getParent();

                if (greatGrandParent instanceof ElixirBlockItem) {
                    PsiElement greatGreatGrandParent = greatGrandParent.getParent();

                    if (greatGreatGrandParent instanceof ElixirBlockList) {
                        PsiElement greatGreatGreatGrandParent = greatGreatGrandParent.getParent();

                        if (greatGreatGreatGrandParent instanceof ElixirDoBlock) {
                            PsiElement greatGreatGreatGreatGrandParent = greatGreatGreatGrandParent.getParent();

                            if (greatGreatGreatGreatGrandParent instanceof Call) {
                                enclosingMacroCall = (Call) greatGreatGreatGreatGrandParent;
                            }
                        }
                    }
                } else if (greatGrandParent instanceof ElixirDoBlock) {
                    PsiElement greatGreatGrandParent = greatGrandParent.getParent();

                    if (greatGreatGrandParent instanceof Call) {
                        enclosingMacroCall = (Call) greatGreatGrandParent;
                    }
                } else if (greatGrandParent instanceof ElixirParentheticalStab) {
                    PsiElement greatGreatGrandParent = greatGrandParent.getParent();

                    if (greatGreatGrandParent instanceof ElixirAccessExpression) {
                        enclosingMacroCall = enclosingMacroCall(greatGreatGrandParent);
                    }
                }
            } else if (grandParent instanceof ElixirStabOperation) {
                PsiElement stabOperationParent = grandParent.getParent();

                if (stabOperationParent instanceof ElixirStab) {
                    PsiElement stabParent = stabOperationParent.getParent();

                    if (stabParent instanceof ElixirAnonymousFunction) {
                        PsiElement anonymousFunctionParent = stabParent.getParent();

                        if (anonymousFunctionParent instanceof ElixirAccessExpression) {
                            PsiElement accessExpressionParent = anonymousFunctionParent.getParent();

                            if (accessExpressionParent instanceof Arguments) {
                                PsiElement argumentsParent = accessExpressionParent.getParent();

                                if (argumentsParent instanceof ElixirMatchedParenthesesArguments) {
                                    PsiElement matchedParenthesesArgumentsParent = argumentsParent.getParent();

                                    if (matchedParenthesesArgumentsParent instanceof Call) {
                                        Call matchedParenthesesArgumentsParentCall = (Call) matchedParenthesesArgumentsParent;

                                        if (matchedParenthesesArgumentsParentCall.isCalling("Enum", "map") ||
                                                matchedParenthesesArgumentsParentCall.isCalling("Enum", "each")) {
                                            enclosingMacroCall = enclosingMacroCall(matchedParenthesesArgumentsParentCall);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (parent instanceof Arguments ||
                // See https://github.com/elixir-lang/elixir/blob/v1.5/lib/elixir/lib/protocol.ex#L633
                parent instanceof AtUnqualifiedNoParenthesesCall ||
                // See https://github.com/phoenixframework/phoenix/blob/v1.2.4/lib/phoenix/template.ex#L380-L392
                parent instanceof ElixirAccessExpression ||
                // See https://github.com/absinthe-graphql/absinthe/blob/v1.3.0/lib/absinthe/schema/notation/writer.ex#L24-L44
                parent instanceof ElixirList ||
                parent instanceof ElixirMatchedParenthesesArguments ||
                // See https://github.com/absinthe-graphql/absinthe/blob/v1.3.0/lib/absinthe/schema/notation/writer.ex#L96
                parent instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression ||
                parent instanceof ElixirTuple ||
                parent instanceof Match ||
                parent instanceof QualifiedAlias ||
                parent instanceof QualifiedMultipleAliases) {
            enclosingMacroCall = enclosingMacroCall(parent);
        } else if (parent instanceof Call) {
            Call parentCall = (Call) parent;

            if (parentCall.isCalling(KERNEL, ALIAS)) {
                enclosingMacroCall = parentCall;
            } else if (parentCall.isCalling(org.elixir_lang.psi.call.name.Module.MODULE, CREATE, 3)) {
                enclosingMacroCall = parentCall;
            }
        } else if (parent instanceof QuotableKeywordPair) {
            QuotableKeywordPair parentKeywordPair = (QuotableKeywordPair) parent;

            if (hasKeywordKey(parentKeywordPair, "do")) {
                PsiElement grandParent = parent.getParent();

                if (grandParent instanceof QuotableKeywordList) {
                    PsiElement greatGrandParent = grandParent.getParent();

                    if (greatGrandParent instanceof ElixirNoParenthesesOneArgument) {
                        PsiElement greatGreatGrandParent = greatGrandParent.getParent();

                        if (greatGreatGrandParent instanceof Call) {
                            enclosingMacroCall = (Call) greatGreatGrandParent;
                        }
                    }
                }
            }
        }

        return enclosingMacroCall;
    }

    /* Returns a virtual PsiElement representing the spaces at the end of charListHeredocLineWhitespace that are not
     * consumed by prefixLength.
     *
     * @return null if prefixLength is greater than or equal to text length of charListHeredcoLineWhitespace.
     */
    @Contract(pure = true)
    @Nullable
    public static ASTNode excessWhitespace(@NotNull final ElixirHeredocLinePrefix heredocLinePrefix, @NotNull final IElementType fragmentType, final int prefixLength) {
        int availableLength = heredocLinePrefix.getTextLength();
        int excessLength = availableLength - prefixLength;
        ASTNode excessWhitespaceASTNode = null;

        if (excessLength > 0) {
            char[] excessWhitespaceChars = new char[excessLength];
            Arrays.fill(excessWhitespaceChars, ' ');
            String excessWhitespaceString = new String(excessWhitespaceChars);
            excessWhitespaceASTNode = Factory.createSingleLeafElement(
                    fragmentType,
                    excessWhitespaceString,
                    0,
                    excessLength,
                    null,
                    heredocLinePrefix.getManager()
            );
        }

        return excessWhitespaceASTNode;
    }

    @Contract(pure = true)
    public static int exportedArity(@NotNull final UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        int arity = MaybeExported.UNEXPORTED_ARITY;

        if (isExported(unqualifiedNoParenthesesCall)) {
            Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(unqualifiedNoParenthesesCall);

            if (nameArityRange != null) {
                IntRange arityRange = nameArityRange.second;

                if (arityRange != null) {
                    int minimumArity = arityRange.getMinimumInteger();
                    int maximumArity = arityRange.getMaximumInteger();

                    if (minimumArity == maximumArity) {
                        arity = minimumArity;
                    }
                }
            }
        }

        return arity;
    }

    @Contract(pure = true)
    @Nullable
    public static String exportedName(@NotNull final UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        String name = null;

        if (isExported(unqualifiedNoParenthesesCall)) {
            Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(unqualifiedNoParenthesesCall);

            if (nameArityRange != null) {
                name = nameArityRange.first;
            }
        }

        return name;
    }

    /**
     * The outer most arguments
     *
     * @return {@link Call#primaryArguments}
     */
    @Nullable
    public static PsiElement[] finalArguments(@NotNull final Call call) {
        PsiElement[] finalArguments = call.secondaryArguments();

        if (finalArguments == null) {
            finalArguments = call.primaryArguments();
        }

        return finalArguments;
    }

    public static LocalSearchScope followingSiblingsSearchScope(PsiElement element) {
        List<PsiElement> followingSiblingList = new ArrayList<PsiElement>();
        PsiElement previousSibling = element;
        PsiElement followingSibling = previousSibling.getNextSibling();

        while (followingSibling != null) {
            followingSiblingList.add(followingSibling);

            previousSibling = followingSibling;
            followingSibling = previousSibling.getNextSibling();
        }

        return new LocalSearchScope(followingSiblingList.toArray(new PsiElement[followingSiblingList.size()]));
    }

    @Contract(pure = true)
    @NotNull
    public static String fullyQualifiedName(@NotNull final ElixirAlias alias) {
        return QualifiableAliasImpl.fullyQualifiedName(alias);
    }

    @Contract(pure = true)
    @Nullable
    public static String fullyQualifiedName(@NotNull final QualifiableAlias qualifiableAlias) {
        return QualifiableAliasImpl.fullyQualifiedName(qualifiableAlias);
    }

    @NotNull
    public static PsiElement fullyResolveAlias(@NotNull QualifiableAlias alias,
                                               @Nullable PsiReference startingReference) {
        PsiElement fullyResolved;
        PsiElement currentResolved = alias;
        PsiReference reference = startingReference;

        do {
            if (reference == null) {
                reference = currentResolved.getReference();
            }

            if (reference != null) {
                if (reference instanceof PsiPolyVariantReference) {
                    PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;
                    ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);
                    int resolveResultCount = resolveResults.length;

                    if (resolveResultCount == 0) {
                        fullyResolved = currentResolved;

                        break;
                    } else if (resolveResultCount == 1) {
                        ResolveResult resolveResult = resolveResults[0];

                        PsiElement nextResolved = resolveResult.getElement();

                        if (nextResolved != null && nextResolved instanceof Call && isModular((Call) nextResolved)) {
                            fullyResolved = nextResolved;
                            break;
                        }

                        if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                            fullyResolved = currentResolved;
                            break;
                        } else {
                            currentResolved = nextResolved;
                        }
                    } else {
                        PsiElement nextResolved = null;

                        for (ResolveResult resolveResult : resolveResults) {
                            PsiElement resolveResultElement = resolveResult.getElement();

                            if (resolveResultElement != null &&
                                    resolveResultElement instanceof Call &&
                                    isModular((Call) resolveResultElement)) {
                                nextResolved = resolveResultElement;

                                break;
                            }
                        }

                        if (nextResolved == null) {
                            fullyResolved = currentResolved;
                        } else {
                            fullyResolved = nextResolved;
                        }

                        break;
                    }
                } else {
                    PsiElement nextResolved = reference.resolve();

                    if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                        fullyResolved = currentResolved;
                        break;
                    } else {
                        currentResolved = nextResolved;
                    }
                }
            } else {
                fullyResolved = currentResolved;

                break;
            }

            reference = null;
        } while (true);

        return fullyResolved;
    }

    @Contract(pure = true)
    @Nullable
    public static String functionName(@NotNull final Call call) {
        return CallImpl.functionName(call);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement functionNameElement(
            @NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall
    ) {
        return CallImpl.functionNameElement(atUnqualifiedNoParenthesesCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement functionNameElement(@NotNull final DotCall dotCall) {
        return CallImpl.functionNameElement(dotCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement functionNameElement(@NotNull final NotIn notIn) {
        return CallImpl.functionNameElement(notIn);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement functionNameElement(@NotNull final Operation operation) {
        return CallImpl.functionNameElement(operation);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement functionNameElement(@NotNull final Qualified qualified) {
        return CallImpl.functionNameElement(qualified);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement functionNameElement(@NotNull final Unqualified unqualified) {
        return CallImpl.functionNameElement(unqualified);
    }

    public static Body getBody(ElixirCharListHeredocLine charListHeredocLine) {
        return charListHeredocLine.getQuoteCharListBody();
    }

    @NotNull
    public static Body getBody(@NotNull final ElixirCharListLine charListLine) {
        return charListLine.getQuoteCharListBody();
    }

    public static Body getBody(ElixirInterpolatedCharListHeredocLine interpolatedCharListHeredocLine) {
        return interpolatedCharListHeredocLine.getInterpolatedCharListBody();
    }

    public static Body getBody(ElixirInterpolatedCharListSigilLine interpolatedCharListSigilLine) {
        return interpolatedCharListSigilLine.getInterpolatedCharListBody();
    }

    public static Body getBody(ElixirInterpolatedRegexHeredocLine interpolatedRegexHeredocLine) {
        return interpolatedRegexHeredocLine.getInterpolatedRegexBody();
    }

    public static Body getBody(ElixirInterpolatedRegexLine interpolatedRegexLine) {
        return interpolatedRegexLine.getInterpolatedRegexBody();
    }

    public static Body getBody(ElixirInterpolatedSigilHeredocLine sigilHeredocLine) {
        return sigilHeredocLine.getInterpolatedSigilBody();
    }

    public static Body getBody(ElixirInterpolatedSigilLine interpolatedSigilLine) {
        return interpolatedSigilLine.getInterpolatedSigilBody();
    }

    public static Body getBody(ElixirInterpolatedStringHeredocLine stringHeredocLine) {
        return stringHeredocLine.getInterpolatedStringBody();
    }

    public static Body getBody(ElixirInterpolatedStringSigilLine interpolatedStringSigilLine) {
        return interpolatedStringSigilLine.getInterpolatedStringBody();
    }

    public static Body getBody(ElixirInterpolatedWordsHeredocLine wordsHeredocLine) {
        return wordsHeredocLine.getInterpolatedWordsBody();
    }

    public static Body getBody(ElixirInterpolatedWordsLine interpolatedWordsLine) {
        return interpolatedWordsLine.getInterpolatedWordsBody();
    }

    public static Body getBody(ElixirLiteralCharListHeredocLine charListHeredocLine) {
        return charListHeredocLine.getLiteralCharListBody();
    }

    public static Body getBody(ElixirLiteralCharListSigilLine literalCharListLine) {
        return literalCharListLine.getLiteralCharListBody();
    }

    public static Body getBody(ElixirLiteralRegexHeredocLine literalRegexHeredocLine) {
        return literalRegexHeredocLine.getLiteralRegexBody();
    }

    public static Body getBody(ElixirLiteralRegexLine literalRegexLine) {
        return literalRegexLine.getLiteralRegexBody();
    }

    public static Body getBody(ElixirLiteralSigilHeredocLine literalSigilHeredocLine) {
        return literalSigilHeredocLine.getLiteralSigilBody();
    }

    public static Body getBody(ElixirLiteralSigilLine literalSigilLine) {
        return literalSigilLine.getLiteralSigilBody();
    }

    public static Body getBody(ElixirLiteralStringHeredocLine literalStringSigilHeredocLine) {
        return literalStringSigilHeredocLine.getLiteralStringBody();
    }

    public static Body getBody(ElixirLiteralStringSigilLine literalStringSigilLine) {
        return literalStringSigilLine.getLiteralStringBody();
    }

    public static Body getBody(ElixirLiteralWordsHeredocLine literalWordsHeredocLine) {
        return literalWordsHeredocLine.getLiteralWordsBody();
    }

    public static Body getBody(ElixirLiteralWordsLine literalWordsLine) {
        return literalWordsLine.getLiteralWordsBody();
    }

    public static Body getBody(ElixirStringHeredocLine stringHeredocLine) {
        return stringHeredocLine.getQuoteStringBody();
    }

    public static Body getBody(@NotNull final ElixirStringLine stringLine) {
        return stringLine.getQuoteStringBody();
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static ElixirDoBlock getDoBlock(
            @NotNull ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall
    ) {
        return CallImpl.getDoBlock(unqualifiedNoParenthesesManyArgumentsCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static ElixirDoBlock getDoBlock(@NotNull NotIn notIn) {
        return CallImpl.getDoBlock(notIn);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static ElixirDoBlock getDoBlock(@NotNull Operation operation) {
        return CallImpl.getDoBlock(operation);
    }

    @Nullable
    public static ElixirDoBlock getDoBlock(@NotNull MatchedCall matchedCall) {
        return CallImpl.getDoBlock(matchedCall);
    }

    public static IElementType getFragmentType(@SuppressWarnings("unused") CharListFragmented charListFragmented) {
        return ElixirTypes.CHAR_LIST_FRAGMENT;
    }

    public static IElementType getFragmentType(@SuppressWarnings("unused") RegexFragmented regexFragmented) {
        return ElixirTypes.REGEX_FRAGMENT;
    }

    public static IElementType getFragmentType(@SuppressWarnings("unused") SigilFragmented sigilFragmented) {
        return ElixirTypes.SIGIL_FRAGMENT;
    }

    public static IElementType getFragmentType(@SuppressWarnings("unused") StringFragmented stringFragmented) {
        return ElixirTypes.STRING_FRAGMENT;
    }

    public static IElementType getFragmentType(@SuppressWarnings("unused") WordsFragmented wordsFragmented) {
        return ElixirTypes.WORDS_FRAGMENT;
    }

    /**
     * Returns the scope in which references to this element are searched.
     *
     * @return the search scope instance.
     * @see {@link com.intellij.psi.search.PsiSearchHelper#getUseScope(PsiElement)}
     */
    @NotNull
    @Contract(pure=true)
    static SearchScope getUseScope(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        SearchScope useScope;

        if (isNonReferencing(atUnqualifiedNoParenthesesCall.getAtIdentifier())) {
            useScope = moduleWithDependentsScope(atUnqualifiedNoParenthesesCall);
        } else {
            useScope = followingSiblingsSearchScope(atUnqualifiedNoParenthesesCall);
        }

        return useScope;
    }

    /**
     * Returns the scope in which references to this element are searched.
     *
     * @return the search scope instance.
     * @see {@link com.intellij.psi.search.PsiSearchHelper#getUseScope(PsiElement)}
     */
    @NotNull
    @Contract(pure=true)
    static SearchScope getUseScope(UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        SearchScope useScope;

        if (isBitStreamSegmentOption(unqualifiedNoArgumentsCall)) {
            // Bit Stream Segment Options aren't variables or even real functions, so no use scope
            useScope = LocalSearchScope.EMPTY;
        } else if (isParameter(unqualifiedNoArgumentsCall) || isParameterWithDefault(unqualifiedNoArgumentsCall)) {
            PsiElement ancestor = unqualifiedNoArgumentsCall.getParent();

            while (true) {
                if (ancestor instanceof Call) {
                    Call ancestorCall = (Call) ancestor;

                    if (CallDefinitionClause.is(ancestorCall)) {
                        PsiElement macroDefinitionClause = macroDefinitionClauseForArgument(ancestorCall);

                        if (macroDefinitionClause != null) {
                            ancestor = macroDefinitionClause;
                        }

                        break;
                    } else if (Delegation.is(ancestorCall)) {
                        break;
                    } else if (ancestorCall.hasDoBlockOrKeyword()) {
                        break;
                    }
                } else if (ancestor instanceof ElixirStabOperation) {
                    break;
                } else if (ancestor instanceof PsiFile) {
                    error(
                            UnqualifiedNoArgumentsCall.class,
                            "Use scope for parameter not found before reaching file scope",
                            unqualifiedNoArgumentsCall
                    );
                    break;
                }

                ancestor = ancestor.getParent();
            }

            useScope = new LocalSearchScope(ancestor);
        } else if (isVariable(unqualifiedNoArgumentsCall)) {
            useScope = variableUseScope(unqualifiedNoArgumentsCall);
        } else {
            // if the type of callable isn't known, fallback to default scope
            useScope = moduleWithDependentsScope(unqualifiedNoArgumentsCall);
        }

        return useScope;
    }

    public static List<HeredocLine> getHeredocLineList(InterpolatedCharListHeredocLined interpolatedCharListHeredocLined) {
        List<ElixirInterpolatedCharListHeredocLine> interpolatedCharListHeredocLineList = interpolatedCharListHeredocLined.getInterpolatedCharListHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(interpolatedCharListHeredocLineList.size());

        for (HeredocLine heredocLine : interpolatedCharListHeredocLineList) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(InterpolatedStringHeredocLined interpolatedStringHeredocLined) {
        List<ElixirInterpolatedStringHeredocLine> interpolatedStringHeredocLineList = interpolatedStringHeredocLined.getInterpolatedStringHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(interpolatedStringHeredocLineList.size());

        for (HeredocLine heredocLine : interpolatedStringHeredocLineList) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirCharListHeredoc charListHeredoc) {
        List<ElixirCharListHeredocLine> charListHeredocLines = charListHeredoc.getCharListHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(charListHeredocLines.size());

        for (HeredocLine heredocLine : charListHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedRegexHeredoc interpolatedRegexHeredoc) {
        List<ElixirInterpolatedRegexHeredocLine> interpolatedRegexHeredocLines = interpolatedRegexHeredoc.getInterpolatedRegexHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(interpolatedRegexHeredocLines.size());

        for (HeredocLine heredocLine : interpolatedRegexHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedSigilHeredoc interpolatedSigilHeredoc) {
        List<ElixirInterpolatedSigilHeredocLine> interpolatedSigilHeredocLines = interpolatedSigilHeredoc.getInterpolatedSigilHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(interpolatedSigilHeredocLines.size());

        for (HeredocLine heredocLine : interpolatedSigilHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedWordsHeredoc interpolatedWordsHeredoc) {
        List<ElixirInterpolatedWordsHeredocLine> interpolatedWordsHeredocLines = interpolatedWordsHeredoc.getInterpolatedWordsHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(interpolatedWordsHeredocLines.size());

        for (HeredocLine heredocLine : interpolatedWordsHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirLiteralCharListSigilHeredoc literalCharListSigilHeredoc) {
        List<ElixirLiteralCharListHeredocLine> literalCharListHeredocLines = literalCharListSigilHeredoc.getLiteralCharListHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(literalCharListHeredocLines.size());

        for (HeredocLine heredocLine : literalCharListHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirLiteralRegexHeredoc literalRegexSigilHeredoc) {
        List<ElixirLiteralRegexHeredocLine> literalRegexHeredocLines = literalRegexSigilHeredoc.getLiteralRegexHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(literalRegexHeredocLines.size());

        for (HeredocLine heredocLine : literalRegexHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirLiteralSigilHeredoc literalSigilHeredoc) {
        List<ElixirLiteralSigilHeredocLine> literalSigilHeredocLines = literalSigilHeredoc.getLiteralSigilHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(literalSigilHeredocLines.size());

        for (HeredocLine heredocLine : literalSigilHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirLiteralStringSigilHeredoc literalStringSigilHeredoc) {
        List<ElixirLiteralStringHeredocLine> literalStringHeredocLines = literalStringSigilHeredoc.getLiteralStringHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(literalStringHeredocLines.size());

        for (HeredocLine heredocLine : literalStringHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirLiteralWordsHeredoc literalWordsSigilHeredoc) {
        List<ElixirLiteralWordsHeredocLine> literalWordsHeredocLines = literalWordsSigilHeredoc.getLiteralWordsHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(literalWordsHeredocLines.size());

        for (HeredocLine heredocLine : literalWordsHeredocLines) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static List<HeredocLine> getHeredocLineList(ElixirStringHeredoc stringHeredoc) {
        List<ElixirStringHeredocLine> stringHeredocLineList = stringHeredoc.getStringHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(stringHeredocLineList.size());

        for (HeredocLine heredocLine : stringHeredocLineList) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    public static Quotable getKeywordValue(ElixirKeywordPair keywordPair) {
        PsiElement[] children = keywordPair.getChildren();

        assert children.length == 2;

        return (Quotable) children[1];
    }

    public static Quotable getKeywordValue(ElixirNoParenthesesKeywordPair noParenthesesKeywordPair) {
        PsiElement[] children = noParenthesesKeywordPair.getChildren();

        assert children.length == 2;

        return (Quotable) children[1];
    }

    @NotNull
    public static String getName(@NotNull ElixirAlias alias) {
        return alias.getText();
    }

    @NotNull
    public static String getName(@NotNull QualifiedAlias qualifiedAlias) {
        return qualifiedAlias.getText();
    }

    @Contract(pure = true)
    @Nullable
    public static String getName(@NotNull NamedElement namedElement) {
        PsiElement nameIdentifier = namedElement.getNameIdentifier();
        String name = null;

        if (nameIdentifier != null) {
            name = unquoteName(namedElement, nameIdentifier.getText());
        } else {
            if (namedElement instanceof Call) {
                Call call = (Call) namedElement;

                /* The name of the module defined by {@code defimpl PROTOCOL[ for: MODULE]} is derived by combining the
                   PROTOCOL and MODULE name into PROTOCOL.MODULE.  Neither piece is really the "name" or
                   "nameIdentifier" element of the implementation because changing the PROTOCOL make the implementation
                   just for that different Protocol and changing the MODULE makes the implementation for a different
                   MODULE.  If `for:` isn't given, it's really the enclosing {@code defmodule MODULE} whose name should
                   be changed. */
                if (Implementation.is(call)) {
                    name = Implementation.name(call);
                }
            }
        }

        return name;
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ElixirAlias alias) {
        PsiElement parent = alias.getParent();
        PsiElement nameIdentifier = null;

        if (parent instanceof ElixirAccessExpression) {
            PsiElement grandParent = parent.getParent();

            // alias is first alias in chain of qualifiers
            if (grandParent instanceof QualifiableAlias) {
                QualifiableAlias grandParentQualifiableAlias = (QualifiableAlias) grandParent;
                nameIdentifier = grandParentQualifiableAlias.getNameIdentifier();
            } else {
                /* NamedStubbedPsiElementBase#getTextOffset assumes getNameIdentifier is null when the NameIdentifier is
                   the element itself. */
                // alias is single, unqualified alias
                nameIdentifier = null;
            }
        } else if (parent instanceof QualifiableAlias) {
            // alias is last in chain of qualifiers
            QualifiableAlias parentQualifiableAlias = (QualifiableAlias) parent;
            nameIdentifier = parentQualifiableAlias.getNameIdentifier();
        }

        return nameIdentifier;
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ElixirKeywordKey keywordKey) {
        ElixirCharListLine charListLine = keywordKey.getCharListLine();
        PsiElement nameIdentifier;

        if (charListLine != null) {
            nameIdentifier = null;
        } else {
            ElixirStringLine stringLine = keywordKey.getStringLine();

            if (stringLine != null) {
                nameIdentifier = null;
            } else {
                nameIdentifier = keywordKey;
            }
        }

        return nameIdentifier;
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ElixirVariable variable) {
        return variable;
    }

    public static PsiElement getNameIdentifier(
            @NotNull org.elixir_lang.psi.call.Named named
    ) {
        PsiElement nameIdentifier;

        /* can't be a {@code public static PsiElement getNameIdentifier(@NotNull Operation operation)} because it leads
           to "reference to getNameIdentifier is ambiguous" */
        if (named instanceof Operation) {
            Operation operation = (Operation) named;
            nameIdentifier = operation.operator();
        } else if (CallDefinitionClause.is(named)) {
            nameIdentifier = CallDefinitionClause.nameIdentifier(named);
        } else if (CallDefinitionSpecification.is(named)) {
            nameIdentifier = CallDefinitionSpecification.nameIdentifier(named);
        } else if (Callback.is(named)) {
            nameIdentifier = Callback.nameIdentifier(named);
        } else if (Implementation.is(named)) {
            /* have to set to null so that {@code else} clause doesn't return the {@code defimpl} element as the name
               identifier */
            nameIdentifier = null;
        } else if (Module.is(named)) {
            nameIdentifier = Module.nameIdentifier(named);
        } else if (Protocol.is(named)) {
            nameIdentifier = Protocol.nameIdentifier(named);
        } else if (named instanceof AtUnqualifiedNoParenthesesCall) { // module attribute
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) named;
            nameIdentifier = atUnqualifiedNoParenthesesCall.getAtIdentifier();
        } else {
            nameIdentifier = named.functionNameElement();
        }

        return nameIdentifier;
    }

    /**
     *
     * @param qualifiableAlias
     * @return null if qualifiableAlias is its own name identifier
     * @return PsiElement if qualifiableAlias is a subalias of a bigger qualified alias
     */
    @Nullable
    public static PsiElement getNameIdentifier(@NotNull QualifiableAlias qualifiableAlias) {
        PsiElement parent = qualifiableAlias.getParent();
        PsiElement nameIdentifier;

        if (parent instanceof QualifiableAlias) {
            QualifiableAlias parentQualifiedAlias = (QualifiableAlias) parent;

            nameIdentifier = parentQualifiedAlias.getNameIdentifier();
        } else {
            nameIdentifier = null;
        }

        return nameIdentifier;
    }

    @Contract(pure = true)
    @NotNull
    public static ItemPresentation getPresentation(@NotNull final Call call) {
        final String text = UsageViewUtil.createNodeText(call);

        return new ItemPresentation() {
            @Nullable
            public String getPresentableText() {
                return text;
            }

            @Nullable
            public String getLocationString() {
                return call.getContainingFile().getName();
            }

            @Nullable
            public Icon getIcon(boolean b) {
                return call.getIcon(0);
            }
        };
    }

    @Contract(pure = true)
    @Nullable
    public static ItemPresentation getPresentation(@NotNull final ElixirIdentifier identifier) {
        Parameter parameter = new Parameter(identifier);
        Parameter parameterizedParameter = Parameter.putParameterized(parameter);
        ItemPresentation itemPresentation = null;

        if ((parameterizedParameter.type == Parameter.Type.FUNCTION_NAME ||
                parameterizedParameter.type == Parameter.Type.MACRO_NAME) &&
                parameterizedParameter.parameterized != null) {
            final NavigatablePsiElement parameterized = parameterizedParameter.parameterized;

            if (parameterized instanceof Call) {
                CallDefinitionClause callDefinitionClause = CallDefinitionClause.fromCall((Call) parameterized);

                if (callDefinitionClause != null) {
                    itemPresentation = callDefinitionClause.getPresentation();
                }
            }
        }

        return itemPresentation;
    }

    @Nullable
    private static PsiReference computeReference(@NotNull Call call) {
        PsiReference reference = null;

        /* if the call is just the identifier for a module attribute reference, then don't return a Callable reference,
           and instead let {@link #getReference(AtNonNumbericOperation) handle it */
        if (!(call instanceof UnqualifiedNoArgumentsCall && call.getParent() instanceof AtNonNumericOperation) &&
                // if a bitstring segment option then the option is a pseudo-function
                !isBitStreamSegmentOption(call)) {
            PsiElement parent = call.getParent();

            if (parent instanceof Type) {
                PsiElement grandParent = parent.getParent();
                AtUnqualifiedNoParenthesesCall moduleAttribute = null;
                PsiElement maybeArgument = grandParent;

                if (grandParent instanceof When) {
                    maybeArgument = grandParent.getParent();
                }

                if (maybeArgument instanceof ElixirNoParenthesesOneArgument) {
                    PsiElement maybeModuleAttribute = maybeArgument.getParent();

                    if (maybeModuleAttribute instanceof AtUnqualifiedNoParenthesesCall) {
                        moduleAttribute = (AtUnqualifiedNoParenthesesCall) maybeModuleAttribute;
                    }

                    if (moduleAttribute != null) {
                        String name = moduleAttributeName(moduleAttribute);

                        if (name.equals("@spec")) {
                            reference = new org.elixir_lang.reference.CallDefinitionClause(call, moduleAttribute);
                        }
                    }
                }
            }

            if (reference == null) {
                if (CallDefinitionClause.is(call) || Implementation.is(call) || Module.is(call) || Protocol.is(call)) {
                    reference = Callable.definer(call);
                } else {
                    reference = new Callable(call);
                }
            }
        }

        return reference;
    }

    @Nullable
    public static PsiReference getReference(@NotNull Call call) {
        return CachedValuesManager.getCachedValue(
                call,
                () -> CachedValueProvider.Result.create(computeReference(call), call)
        );
    }

    @Nullable
    private static PsiPolyVariantReference computeReference(@NotNull QualifiableAlias qualifiableAlias,
                                                            @NotNull PsiElement maxScope) {
        PsiPolyVariantReference reference = null;

        if (isOutermostQualifiableAlias(qualifiableAlias)) {
            reference = new org.elixir_lang.reference.Module(qualifiableAlias, maxScope);
        }

        return reference;
    }

    @Nullable
    public static PsiReference getReference(@NotNull ElixirAtom atom) {
        return CachedValuesManager.getCachedValue(
                atom,
                () -> CachedValueProvider.Result.create(computeReference(atom), atom)
        );
    }

    @NotNull
    private static PsiReference computeReference(@NotNull ElixirAtom atom) {
        return new org.elixir_lang.reference.Atom(atom);
    }

    @Nullable
    public static PsiReference getReference(@NotNull QualifiableAlias qualifiableAlias) {
        return getReference(qualifiableAlias, qualifiableAlias.getContainingFile());
    }

    @Nullable
    public static PsiPolyVariantReference getReference(@NotNull QualifiableAlias qualifiableAlias,
                                                       @NotNull PsiElement maxScope) {
        return CachedValuesManager.getCachedValue(
                qualifiableAlias,
                () -> CachedValueProvider.Result.create(computeReference(qualifiableAlias, maxScope), qualifiableAlias)
        );
    }

    @NotNull
    private static PsiReference computeReference(@NotNull final ElixirAtIdentifier atIdentifier) {
        return new org.elixir_lang.reference.ModuleAttribute(atIdentifier);
    }

    /**
     * <blockquote>
     *     The PSI element at the cursor (the direct tree parent of the token at the cursor position) must be either a
     *     PsiNamedElement or <em>a PsiReference which resolves to a PsiNamedElement.</em>
     * </blockquote>
     * @param atIdentifier
     * @return
     * @see <a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/find_usages.html?search=PsiNameIdentifierOwner">IntelliJ Platform SDK DevGuide | Find Usages</a>
     */
    @Contract(pure = true)
    @NotNull
    public static PsiReference getReference(@NotNull final ElixirAtIdentifier atIdentifier) {
        return CachedValuesManager.getCachedValue(
                atIdentifier,
                () -> CachedValueProvider.Result.create(computeReference(atIdentifier), atIdentifier)
        );
    }

    @Nullable
    private static PsiReference computeReference(@NotNull final AtNonNumericOperation atNonNumericOperation) {
        PsiReference reference = null;

        if (!isNonReferencing(atNonNumericOperation)) {
            reference = new org.elixir_lang.reference.ModuleAttribute(atNonNumericOperation);
        }

        return reference;
    }

    @Nullable
    public static PsiReference getReference(@NotNull final AtNonNumericOperation atNonNumericOperation) {
        return CachedValuesManager.getCachedValue(
                atNonNumericOperation,
                () -> CachedValueProvider.Result.create(computeReference(atNonNumericOperation), atNonNumericOperation)
        );
    }

    public static boolean hasDoBlockOrKeyword(@NotNull final Call call) {
        return CallImpl.hasDoBlockOrKeyword(call);
    }

    public static boolean hasDoBlockOrKeyword(@NotNull final StubBased<Stub> stubBased) {
        return CallImpl.hasDoBlockOrKeyword(stubBased);
    }

    public static boolean hasKeywordKey(@NotNull QuotableKeywordPair quotableKeywordPair, @NotNull String keywordKeyText) {
        Quotable keywordKey = quotableKeywordPair.getKeywordKey();
        boolean has = false;

        if (computeReadAction(keywordKey::getText).equals(keywordKeyText)) {
            has = true;
        } else {
            OtpErlangObject quotedKeywordKey = keywordKey.quote();

            if (quotedKeywordKey instanceof OtpErlangAtom) {
                OtpErlangAtom keywordKeyAtom = (OtpErlangAtom) quotedKeywordKey;

                if (keywordKeyAtom.atomValue().equals(keywordKeyText)) {
                    has = true;
                }
            }
        }

        return has;
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement qualifier(@NotNull final Qualified qualified) {
        return qualified.getFirstChild();
    }

    public static List<QuotableKeywordPair> quotableKeywordPairList(ElixirKeywords keywords) {
        return new ArrayList<QuotableKeywordPair>(keywords.getKeywordPairList());
    }

    public static List<QuotableKeywordPair> quotableKeywordPairList(ElixirNoParenthesesKeywords noParenthesesKeywords) {
        return new ArrayList<QuotableKeywordPair>(noParenthesesKeywords.getNoParenthesesKeywordPairList());
    }

    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirDecimalFloat decimalFloat) {
       return QuotableImpl.quote(decimalFloat);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirEmptyParentheses emptyParentheses) {
        return QuotableImpl.quote(emptyParentheses);
    }

    public static OtpErlangObject quote(@NotNull ElixirFile file) {
        return QuotableImpl.quote(file);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final WholeNumber wholeNumber) {
        return QuotableImpl.quote(wholeNumber);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteAsAtom(@NotNull final ElixirCharListLine charListLine) {
        OtpErlangObject quoted = charListLine.quote();
        OtpErlangObject quotedAsAtom;

        if (quoted instanceof OtpErlangString) {
            final String atomText = ((OtpErlangString) quoted).stringValue();
            quotedAsAtom = new OtpErlangAtom(atomText);
        } else {
            final OtpErlangTuple quotedStringToCharListCall = (OtpErlangTuple) quoted;
            final OtpErlangList quotedStringToCharListArguments = (OtpErlangList) quotedStringToCharListCall.elementAt(2);
            final OtpErlangObject binaryConstruction = quotedStringToCharListArguments.getHead();

            quotedAsAtom = quotedFunctionCall(
                    "erlang",
                    "binary_to_atom",
                    (OtpErlangList) quotedStringToCharListCall.elementAt(1),
                    binaryConstruction,
                    UTF_8
            );
        }

        return quotedAsAtom;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteAsAtom(@NotNull final ElixirStringLine stringLine) {
        OtpErlangObject quoted = stringLine.quote();
        OtpErlangObject quotedAsAtom;

        if (quoted instanceof OtpErlangBinary) {
            String atomText = javaString((OtpErlangBinary) quoted);
            quotedAsAtom = new OtpErlangAtom(atomText);
        } else if (quoted instanceof OtpErlangString) {
            String atomText = ((OtpErlangString) quoted).stringValue();
            quotedAsAtom = new OtpErlangAtom(atomText);
        } else {
            quotedAsAtom = quotedFunctionCall(
                    "erlang",
                    "binary_to_atom",
                    metadata(stringLine),
                    quoted,
                    UTF_8
            );
        }

        return quotedAsAtom;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirIdentifier identifier) {
        return QuotableImpl.quote(identifier);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirInterpolation interpolation) {
        return QuotableImpl.quote(interpolation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirRelativeIdentifier relativeIdentifier) {
        return QuotableImpl.quote(relativeIdentifier);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirSigilModifiers sigilModifiers) {
        return QuotableImpl.quote(sigilModifiers);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirStab stab) {
        return QuotableImpl.quote(stab);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabBody stabBody) {
        return QuotableImpl.quote(stabBody);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabNoParenthesesSignature stabNoParenthesesSignature) {
        return QuotableImpl.quote(stabNoParenthesesSignature);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirStabOperation stabOperation) {
        return QuotableImpl.quote(stabOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabParenthesesSignature stabParenthesesSignature) {
        return QuotableImpl.quote(stabParenthesesSignature);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStringLine stringLine) {
        return QuotableImpl.quote(stringLine);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStructOperation structOperation) {
        return QuotableImpl.quote(structOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirTuple tuple) {
        return QuotableImpl.quote(tuple);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final Heredoc heredoc) {
        return QuotableImpl.quote(heredoc);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final HeredocLine heredocLine, @NotNull final Heredoc heredoc, int prefixLength) {
        return QuotableImpl.quote(heredocLine, heredoc, prefixLength);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QuotableKeywordList quotableKeywordList) {
        return QuotableImpl.quote(quotableKeywordList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QuotableKeywordPair quotableKeywordPair) {
        return QuotableImpl.quote(quotableKeywordPair);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirKeywordKey keywordKey) {
        return QuotableImpl.quote(keywordKey);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirList list) {
        return QuotableImpl.quote(list);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapArguments mapArguments) {
        return QuotableImpl.quote(mapArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapOperation mapOperation) {
        return QuotableImpl.quote(mapOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapUpdateArguments mapUpdateArguments) {
        return QuotableImpl.quote(mapUpdateArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirMultipleAliases multipleAliases) {
        return QuotableImpl.quote(multipleAliases);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(
            @NotNull
                    ElixirNoParenthesesManyStrictNoParenthesesExpression noParenthesesManyStrictNoParenthesesExpression
    ) {
        return QuotableImpl.quote(noParenthesesManyStrictNoParenthesesExpression);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull AtNumericBracketOperation atUnqualifiedBracketOperation) {
        return QuotableImpl.quote(atUnqualifiedBracketOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull AtUnqualifiedBracketOperation atUnqualifiedBracketOperation) {
        return QuotableImpl.quote(atUnqualifiedBracketOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return QuotableImpl.quote(atUnqualifiedNoParenthesesCall);
    }

    @NotNull
    public static OtpErlangObject quote(@NotNull BracketOperation bracketOperation) {
        return QuotableImpl.quote(bracketOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull DotCall dotCall) {
        return QuotableImpl.quote(dotCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull In in) {
        return QuotableImpl.quote(in);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedAlias qualifiedAlias) {
        return QuotableImpl.quote(qualifiedAlias);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedMultipleAliases qualifiedMultipleAliases) {
        return QuotableImpl.quote(qualifiedMultipleAliases);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedBracketOperation qualifiedBracketOperation) {
        return QuotableImpl.quote(qualifiedBracketOperation);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedNoArgumentsCall qualifiedNoArgumentsCall) {
        return QuotableImpl.quote(qualifiedNoArgumentsCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedNoParenthesesCall qualifiedNoParenthesesCall) {
        return QuotableImpl.quote(qualifiedNoParenthesesCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull QualifiedParenthesesCall qualifiedParenthesesCall) {
        return QuotableImpl.quote(qualifiedParenthesesCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull UnqualifiedBracketOperation unqualifiedBracketOperation) {
        return QuotableImpl.quote(unqualifiedBracketOperation);
    }

    /* Replaces `nil` argument in variables with the quoted ElixirMatchdNotParenthesesArguments.
     *
     */
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        return QuotableImpl.quote(unqualifiedNoParenthesesCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        return QuotableImpl.quote(unqualifiedNoArgumentsCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedParenthesesCall unqualifiedParenthesesCall) {
        return QuotableImpl.quote(unqualifiedParenthesesCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirParentheticalStab parentheticalStab) {
        return QuotableImpl.quote(parentheticalStab);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirVariable variable) {
        return QuotableImpl.quote(variable);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(Operator operator) {
        return QuotableImpl.quote(operator);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(Prefix prefix) {
        return QuotableImpl.quote(prefix);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PsiElement[] children) {
        return QuotableImpl.quote(children);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PsiFile file) {
        return QuotableImpl.quote(file);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(
            @NotNull
                    ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall
    ) {
        return QuotableImpl.quote(unqualifiedNoParenthesesManyArgumentsCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(SigilHeredoc sigilHeredoc) {
        return QuotableImpl.quote(sigilHeredoc);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(SigilLine sigilLine) {
        return QuotableImpl.quote(sigilLine);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final Arguments arguments) {
        return QuotableArgumentsImpl.quoteArguments(arguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirBlockList blockList) {
        return QuotableArgumentsImpl.quoteArguments(blockList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(
            @NotNull ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall
    ) {
        return QuotableArgumentsImpl.quoteArguments(unqualifiedNoParenthesesManyArgumentsCall);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirDoBlock doBlock) {
        return QuotableArgumentsImpl.quoteArguments(doBlock);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(
            @NotNull final ElixirMapConstructionArguments mapConstructionArguments
    ) {
        return QuotableArgumentsImpl.quoteArguments(mapConstructionArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirNoParenthesesArguments noParenthesesArguments) {
        return QuotableArgumentsImpl.quoteArguments(noParenthesesArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirParenthesesArguments parenthesesArguments) {
        return QuotableArgumentsImpl.quoteArguments(parenthesesArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(InterpolatedCharList interpolatedCharList, OtpErlangTuple binary) {
        return ParentImpl.quoteBinary(interpolatedCharList, binary);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(InterpolatedString interpolatedString, OtpErlangTuple binary) {
        return ParentImpl.quoteBinary(interpolatedString, binary);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(Sigil sigil, OtpErlangTuple binary) {
        return ParentImpl.quoteBinary(sigil, binary);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(InterpolatedCharList interpolatedCharList) {
        return ParentImpl.quoteEmpty(interpolatedCharList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(InterpolatedString interpolatedString) {
        return ParentImpl.quoteEmpty(interpolatedString);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(Sigil sigil) {
        return ParentImpl.quoteEmpty(sigil);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(InterpolatedCharList interpolatedCharList, List<Integer> codePointList) {
        return ParentImpl.quoteLiteral(interpolatedCharList, codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(InterpolatedString interpolatedString, List<Integer> codePointList) {
        return ParentImpl.quoteLiteral(interpolatedString, codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(Sigil sigil, List<Integer> codePointList) {
        return ParentImpl.quoteLiteral(sigil, codePointList);
    }

    @NotNull
    @Contract(pure = true)
    public static OtpErlangObject quotedEmpty(@SuppressWarnings("unused") ElixirCharListHeredoc charListHeredoc) {
        // an empty CharList is just an empty list
        return new OtpErlangList();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quotedRightOperand(@NotNull final ElixirStabOperation stabOperation) {
        PsiElement rightOperand = stabOperation.rightOperand();
        OtpErlangObject quotedRightOperand;

        if (rightOperand != null && rightOperand instanceof Quotable) {
            Quotable quotableRightOperand = (Quotable) rightOperand;
            quotedRightOperand = quotableRightOperand.quote();
        } else {
            quotedRightOperand = NIL;
        }

        return quotedRightOperand;
    }

    @Contract(pure = true)
    @NotNull
    public static boolean recursiveKernelImport(@NotNull QualifiableAlias qualifiableAlias,
                                                @NotNull PsiElement maxScope) {
        boolean recursiveKernelImport = false;

        if (maxScope instanceof ElixirFile) {
            ElixirFile elixirFile = (ElixirFile) maxScope;

            if (elixirFile.getName().equals("kernel.ex")) {
                String qualifiableAliasName = qualifiableAlias.getName();

                recursiveKernelImport = qualifiableAliasName != null && qualifiableAliasName.equals(KERNEL);
            }
        }

        return recursiveKernelImport;
    }

    @Contract(pure = true)
    @NotNull
    public static int resolvedFinalArity(@NotNull Call call) {
        return CallImpl.resolvedFinalArity(call);
    }

    @Contract(pure = true)
    @NotNull
    public static int resolvedFinalArity(@NotNull org.elixir_lang.psi.call.StubBased<Stub> stubBased) {
        return CallImpl.resolvedFinalArity(stubBased);
    }

    @Contract(pure = true)
    @NotNull
    public static IntRange resolvedFinalArityRange(@NotNull Call call) {
        return CallImpl.resolvedFinalArityRange(call);
    }

    /**
     * Similar to {@link functionName}, but takes into account `import`s.
     *
     * @return
     */
    @Nullable
    public static String resolvedFunctionName(@NotNull @SuppressWarnings("unused") final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        // TODO handle resolving function name from module attribute's declaration
        return null;
    }

    /**
     * Similar to {@link functionName}, but takes into account `import`s.
     *
     * @return
     */
    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String resolvedFunctionName(@NotNull @SuppressWarnings("unused") final DotCall dotCall) {
        // TODO handle resolving function name from potential capture when declaring variable
        return null;
    }

    /**
     * Similar to {@link #functionName(Call)}}, but takes into account `import`s.
     *
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static String resolvedFunctionName(@NotNull final Call call) {
        // TODO handle `import`s
        return call.functionName();
    }

    @Contract(pure = true)
    @NotNull
    public static String resolvedFunctionName(@NotNull final org.elixir_lang.psi.call.StubBased<Stub> stubBased) {
        Stub stub = stubBased.getStub();
        String resolvedFunctionName;

        if (stub != null) {
            resolvedFunctionName = stub.resolvedFunctionName();
        } else {
            resolvedFunctionName = resolvedFunctionName((Call) stubBased);
        }

        //noinspection ConstantConditions
        return resolvedFunctionName;
    }

    @Nullable
    public static String resolvedFunctionName(@NotNull final UnqualifiedNoArgumentsCall<Stub> unqualifiedNoArgumentsCall) {
        Stub stub = unqualifiedNoArgumentsCall.getStub();
        String resolvedFunctionName;

        if (stub != null) {
            resolvedFunctionName = stub.resolvedFunctionName();
        } else {
            // TODO handle `import`s and determine whether actually local variable
            resolvedFunctionName = unqualifiedNoArgumentsCall.functionName();
        }

        return resolvedFunctionName;
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String resolvedModuleName(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return CallImpl.resolvedModuleName(atUnqualifiedNoParenthesesCall);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String resolvedModuleName(@NotNull DotCall dotCall) {
        return CallImpl.resolvedModuleName(dotCall);
    }

    @Contract(pure = true)
    @NotNull
    public static String resolvedModuleName(@NotNull Infix infix) {
        return CallImpl.resolvedModuleName(infix);
    }

    @Contract(pure = true)
    @NotNull
    public static String resolvedModuleName(@NotNull NotIn notIn) {
        return CallImpl.resolvedModuleName(notIn);
    }

    @Contract(pure = true)
    @NotNull
    public static String resolvedModuleName(@NotNull Prefix prefix) {
        return CallImpl.resolvedModuleName(prefix);
    }

    @NotNull
    public static String resolvedModuleName(@NotNull org.elixir_lang.psi.call.qualification.Qualified qualified) {
        return CallImpl.resolvedModuleName(qualified);
    }

    @NotNull
    public static String resolvedModuleName(@NotNull Unqualified unqualified) {
        return CallImpl.resolvedModuleName(unqualified);
    }

    @Contract(pure = true)
    @NotNull
    public static String resolvedModuleName(@NotNull UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        return CallImpl.resolvedModuleName(unqualifiedNoArgumentsCall);
    }

    @Contract(pure = true)
    @Nullable
    public static Integer resolvedPrimaryArity(@NotNull Call call) {
        return CallImpl.resolvedPrimaryArity(call);
    }

    @Contract(pure = true)
    @Nullable
    public static Integer resolvedSecondaryArity(@NotNull Call call) {
        return CallImpl.resolvedSecondaryArity(call);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(ElixirStabOperation stabOperation) {
        return stabOperation.getStabBody();
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(Infix infix) {
        return org.elixir_lang.psi.operation.infix.Normalized.rightOperand(infix);
    }

    @Contract(pure = true)
    @Nullable
    public static Quotable rightOperand(@NotNull NotIn notIn) {
        return org.elixir_lang.psi.operation.not_in.Normalized.rightOperand(notIn);
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull DotCall dotCall) {
        return CallImpl.secondaryArguments(dotCall);
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull Infix infix) {
        return CallImpl.secondaryArguments(infix);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull None none) {
        return CallImpl.secondaryArguments(none);
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull NotIn notIn) {
        return CallImpl.secondaryArguments(notIn);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull NoParentheses noParentheses) {
        return CallImpl.secondaryArguments(noParentheses);
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull Parentheses parentheses) {
        return CallImpl.secondaryArguments(parentheses);
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull Prefix prefix) {
        return CallImpl.secondaryArguments(prefix);
    }

    @Contract(pure = true)
    @Nullable
    public static Integer secondaryArity(@NotNull Call call) {
        return CallImpl.secondaryArity(call);
    }

    @NotNull
    public static PsiElement setName(@NotNull PsiElement element, @NotNull String newName) {
        return null;
    }

    @NotNull
    public static PsiElement setName(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                     @NotNull final String newName) {
        AtUnqualifiedNoParenthesesCall newAtUnqualifiedNoParenthesesCall = ElementFactory.createModuleAttributeDeclaration(
                atUnqualifiedNoParenthesesCall.getProject(),
                newName,
                "dummy_value"
        );

        ASTNode nameNode = atUnqualifiedNoParenthesesCall.getAtIdentifier().getNode();
        ASTNode newNameNode = newAtUnqualifiedNoParenthesesCall.getAtIdentifier().getNode();

        ASTNode node = atUnqualifiedNoParenthesesCall.getNode();
        node.replaceChild(nameNode, newNameNode);

        return atUnqualifiedNoParenthesesCall;
    }

    @NotNull
    public static PsiElement setName(@NotNull ElixirVariable variable, @NotNull String newName) {
        return null;
    }

    @NotNull
    public static PsiElement setName(@NotNull final org.elixir_lang.psi.call.Named named,
                                     @NotNull final String newName) {
        PsiElement functionNameElement = named.functionNameElement();
        Call newFunctionNameElementCall = ElementFactory.createUnqualifiedNoArgumentsCall(named.getProject(), newName);

        ASTNode nameNode = functionNameElement.getNode();
        ASTNode newNameNode = newFunctionNameElementCall.functionNameElement().getNode();

        ASTNode node = nameNode.getTreeParent();
        node.replaceChild(nameNode, newNameNode);

        return named;
    }

    public static char sigilName(@NotNull org.elixir_lang.psi.Sigil sigil) {
        ASTNode sigilNode = sigil.getNode();
        ASTNode[] childNodes = sigilNode.getChildren(null);
        ASTNode nameNode = childNodes[1];
        CharSequence chars = nameNode.getChars();
        return chars.charAt(0);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement stripAccessExpression(@NotNull PsiElement maybeWrapped) {
        PsiElement unwrapped = maybeWrapped;

        if (maybeWrapped instanceof ElixirAccessExpression) {
            unwrapped = stripOnlyChildParent(maybeWrapped);
        }

        return unwrapped;
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement stripOnlyChildParent(@NotNull PsiElement parent) {
        PsiElement unwrapped = parent;
        PsiElement[] children = parent.getChildren();

        if (children.length == 1) {
            unwrapped = children[0];
        }

        return unwrapped;
    }

    public static char terminator(@NotNull SigilLine sigilLine) {
        ASTNode node = sigilLine.getNode();
        ASTNode[] childNodes = node.getChildren(null);
        ASTNode terminatorNode = childNodes[4];
        CharSequence chars = terminatorNode.getChars();

        return chars.charAt(0);
    }

    @NotNull
    public static IElementType validElementType(@NotNull ElixirBinaryDigits binaryDigits) {
        return DigitsImpl.validElementType(binaryDigits);
    }

    @Contract(pure = true)
    @NotNull
    public static IElementType validElementType(@NotNull ElixirDecimalDigits decimalDigits) {
        return DigitsImpl.validElementType(decimalDigits);
    }

    @NotNull
    public static IElementType validElementType(@NotNull ElixirHexadecimalDigits hexadecimalDigits) {
        return DigitsImpl.validElementType(hexadecimalDigits);
    }

    @NotNull
    public static IElementType validElementType(@NotNull ElixirOctalDigits octalDigits) {
        return DigitsImpl.validElementType(octalDigits);
    }

    @Nullable
    public static IElementType validElementType(@NotNull ElixirUnknownBaseDigits unknownBaseDigits) {
        return DigitsImpl.validElementType(unknownBaseDigits);
    }

    /*
     * Private static methods
     */

    @Contract(pure = true)
    @Nullable
    private static Call macroDefinitionClauseForArgument(Call callDefinitionClause) {
        Call macroDefinitionClause = null;
        PsiElement parent = callDefinitionClause.getParent();

        if (parent instanceof ElixirMatchedWhenOperation) {
            PsiElement grandParent =  parent.getParent();

            if (grandParent instanceof ElixirNoParenthesesOneArgument) {
                PsiElement greatGrandParent = grandParent.getParent();

                if (greatGrandParent instanceof Call) {
                    Call greatGrandParentCall = (Call) greatGrandParent;

                    if (CallDefinitionClause.isMacro(greatGrandParentCall)) {
                        macroDefinitionClause = greatGrandParentCall;
                    }
                }
            }
        }

        return macroDefinitionClause;
    }

    /**
     * Finds modular ({@code defmodule}, {@code defimpl}, or {@code defprotocol}) for the qualifier of
     * {@code qualified}.
     *
     * @param qualified a qualified expression
     * @return {@code null} if the modular cannot be resolved, such as when the qualifying Alias is invalid or is an
     *   unparsed module like {@code Kernel} or {@code Enum} OR if the qualified isn't an Alias.
     */
    @Contract(pure = true)
    @Nullable
    public static Call qualifiedToModular(@NotNull final org.elixir_lang.psi.call.qualification.Qualified qualified) {
        return maybeModularNameToModular(qualified.qualifier(), qualified.getContainingFile());
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull Sigil sigil, @NotNull OtpErlangObject quotedContent) {
        return QuotableImpl.quote(sigil, quotedContent);
    }

    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull Quote parent,
                                                              @Nullable List<Integer> codePointList,
                                                              @NotNull ASTNode child) {
        return ParentImpl.addEscapedCharacterCodePoints(parent, codePointList, child);
    }

    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull Sigil parent,
                                                              @Nullable List<Integer> codePointList,
                                                              @NotNull ASTNode child) {
        return ParentImpl.addEscapedCharacterCodePoints(parent, codePointList, child);
    }

    @NotNull
    public static List<Integer> addEscapedEOL(@NotNull Parent parent,
                                              @Nullable List<Integer> maybeCodePointList,
                                              @NotNull ASTNode child) {
        return ParentImpl.addEscapedEOL(parent, maybeCodePointList, child);
    }

    @NotNull
    public static List<Integer> addFragmentCodePoints(@NotNull Parent parent,
                                                      @Nullable List<Integer> codePointList,
                                                      @NotNull ASTNode child) {
        return ParentImpl.addFragmentCodePoints(parent, codePointList, child);
    }

    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull Quote parent,
                                                                       @Nullable List<Integer> codePointList,
                                                                       @NotNull ASTNode child) {
        return ParentImpl.addHexadecimalEscapeSequenceCodePoints(parent, codePointList, child);
    }

    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull Sigil parent,
                                                                       @Nullable List<Integer> codePointList,
                                                                       @NotNull ASTNode child) {
        return ParentImpl.addHexadecimalEscapeSequenceCodePoints(parent, codePointList, child);
    }

    /**
     * @return {@link Call} for the {@code defmodule}, {@code defimpl}, or {@code defprotocol} that defines
     *   {@code maybeAlias} after it is resolved through any {@code alias}es.
     */
    @Contract(pure = true)
    @Nullable
    public static Call maybeModularNameToModular(@NotNull final PsiElement maybeModularName, @NotNull PsiElement maxScope) {
        PsiElement strippedMaybeModuleName = stripAccessExpression(maybeModularName);

        Call modular = null;

        if (strippedMaybeModuleName instanceof ElixirAtom) {
            ElixirAtom atom = (ElixirAtom) strippedMaybeModuleName;
            PsiReference reference = atom.getReference();

            if (reference != null) {
                final PsiElement resolved = reference.resolve();

                if (resolved != null && resolved instanceof Call) {
                    Call call = (Call) resolved;

                    if (isModular(call)) {
                        modular = call;
                    }
                }
            }
        } else if (strippedMaybeModuleName instanceof QualifiableAlias) {
            QualifiableAlias qualifiableAlias = (QualifiableAlias) strippedMaybeModuleName;

            if (!recursiveKernelImport(qualifiableAlias, maxScope)) {
                /* need to construct reference directly as qualified aliases don't return a reference except for the
                   outermost */
                PsiPolyVariantReference reference = getReference(qualifiableAlias, maxScope);
                modular = aliasToModular(qualifiableAlias, reference);
            }
        }

        return modular;
    }

    @Contract(pure = true)
    @Nullable
    private static Call aliasToModular(@NotNull QualifiableAlias alias,
                                       @NotNull PsiReference startingReference) {
        PsiElement fullyResolvedAlias = fullyResolveAlias(alias, startingReference);
        Call modular = null;

        if (fullyResolvedAlias instanceof Call) {
           Call fullyResolvedAliasCall = (Call) fullyResolvedAlias;

           if (isModular(fullyResolvedAliasCall)) {
               modular = fullyResolvedAliasCall;
           }
        }

        return modular;
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement siblingExpression(@NotNull PsiElement element,
                                                @NotNull com.intellij.util.Function<PsiElement, PsiElement> function) {
        PsiElement expression = element;

        do {
            expression = function.fun(expression);
        } while (expression instanceof ElixirEndOfExpression ||
                expression instanceof LeafPsiElement ||
                expression instanceof PsiComment ||
                expression instanceof PsiWhiteSpace);

        return expression;
    }

    /**
     * If {@code name} is {@code "unquote"} then the {@link Call#primaryArguments()} single argument is added to the
     * name.
     */
    @Nullable
    public static String unquoteName(@NotNull PsiElement named, @Nullable String name) {
        String unquotedName = name;

        if (named instanceof Call && UNQUOTE.equals(name)) {
            Call namedElementCall = (Call) named;

            PsiElement[] primaryArguments = namedElementCall.primaryArguments();

            if (primaryArguments != null && primaryArguments.length == 1) {
                unquotedName += "(" + primaryArguments[0].getText() + ")";
            }
        }

        return unquotedName;
    }

    @Nullable
    public static String getModuleName(PsiElement elem) {
        final Predicate<PsiElement> isModuleName = (PsiElement c) ->
          c instanceof MaybeModuleName && ((MaybeModuleName) c).isModuleName();

        PsiElement moduleDefinition = PsiTreeUtil.findFirstParent(elem, e ->
          Stream.of(e.getChildren()).anyMatch(isModuleName)
        );
        if (moduleDefinition != null) {
            Optional<PsiElement> moduleName =
              Stream.of(moduleDefinition.getChildren())
                .filter(isModuleName)
                .findAny();
            if (moduleName.isPresent()) {
                String parentModuleName = getModuleName(moduleDefinition.getParent());
                if (parentModuleName != null) {
                    return parentModuleName + "." + moduleName.get().getText();
                } else {
                    return moduleName.get().getText();
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
