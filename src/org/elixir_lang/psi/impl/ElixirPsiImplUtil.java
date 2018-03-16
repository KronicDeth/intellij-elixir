package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.MaybeExported;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.call.arguments.star.NoParentheses;
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument;
import org.elixir_lang.psi.call.arguments.star.Parentheses;
import org.elixir_lang.psi.impl.call.CallImpl;
import org.elixir_lang.psi.impl.call.CallImplKt;
import org.elixir_lang.psi.impl.call.CanonicallyNamedImpl;
import org.elixir_lang.psi.impl.declarations.UseScopeImpl;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.psi.qualification.Qualified;
import org.elixir_lang.psi.qualification.Unqualified;
import org.elixir_lang.psi.stub.call.Stub;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.QualifiableAliasImplKt.toModular;
import static org.elixir_lang.psi.impl.QuotableImpl.*;
import static org.elixir_lang.psi.stub.type.call.Stub.isModular;
import static org.elixir_lang.reference.ModuleAttribute.isNonReferencing;

/**
 * Created by luke.imhoff on 12/29/14.
 */
public class ElixirPsiImplUtil {
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
        return ArgumentsImpl.arguments(noParenthesesOneArgument);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirNoParenthesesStrict noParenthesesStrict) {
        return ArgumentsImpl.arguments(noParenthesesStrict);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirParenthesesArguments parenthesesArguments) {
        return ArgumentsImpl.arguments(parenthesesArguments);
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

    public static boolean isModuleName(@NotNull final ElixirAccessExpression accessExpression) {
        return MaybeModuleNameImpl.isModuleName(accessExpression);
    }

    public static boolean isModuleName(@NotNull final QualifiableAlias qualifiableAlias) {
        return MaybeModuleNameImpl.isModuleName(qualifiableAlias);
    }

    public static boolean isModuleName(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument) {
        return MaybeModuleNameImpl.isModuleName(noParenthesesOneArgument);
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

    @NotNull
    @Contract(pure=true)
    static SearchScope getUseScope(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return UseScopeImpl.get(atUnqualifiedNoParenthesesCall);
    }

    @NotNull
    @Contract(pure=true)
    static SearchScope getUseScope(@NotNull UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        return UseScopeImpl.get(unqualifiedNoArgumentsCall);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(InterpolatedCharListHeredocLined interpolatedCharListHeredocLined) {
        return HeredocImpl.getHeredocLineList(interpolatedCharListHeredocLined);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(InterpolatedStringHeredocLined interpolatedStringHeredocLined) {
        return HeredocImpl.getHeredocLineList(interpolatedStringHeredocLined);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirCharListHeredoc charListHeredoc) {
        return HeredocImpl.getHeredocLineList(charListHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedRegexHeredoc interpolatedRegexHeredoc) {
        return HeredocImpl.getHeredocLineList(interpolatedRegexHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedSigilHeredoc interpolatedSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(interpolatedSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirInterpolatedWordsHeredoc interpolatedWordsHeredoc) {
        return HeredocImpl.getHeredocLineList(interpolatedWordsHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirLiteralCharListSigilHeredoc literalCharListSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(literalCharListSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirLiteralRegexHeredoc literalRegexSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(literalRegexSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirLiteralSigilHeredoc literalSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(literalSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirLiteralStringSigilHeredoc literalStringSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(literalStringSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirLiteralWordsHeredoc literalWordsSigilHeredoc) {
        return HeredocImpl.getHeredocLineList(literalWordsSigilHeredoc);
    }

    @NotNull
    public static List<HeredocLine> getHeredocLineList(ElixirStringHeredoc stringHeredoc) {
        return HeredocImpl.getHeredocLineList(stringHeredoc);
    }

    @NotNull
    public static Quotable getKeywordValue(ElixirKeywordPair keywordPair) {
        return QuotableKeywordPairImpl.getKeywordValue(keywordPair);
    }

    @NotNull
    public static Quotable getKeywordValue(ElixirNoParenthesesKeywordPair noParenthesesKeywordPair) {
        return QuotableKeywordPairImpl.getKeywordValue(noParenthesesKeywordPair);
    }

    @NotNull
    public static String getName(@NotNull ElixirAlias alias) {
        return PsiNamedElementImpl.getName(alias);
    }

    @NotNull
    public static String getName(@NotNull QualifiedAlias qualifiedAlias) {
        return PsiNamedElementImpl.getName(qualifiedAlias);
    }

    @Contract(pure = true)
    @Nullable
    public static String getName(@NotNull NamedElement namedElement) {
        return PsiNamedElementImpl.getName(namedElement);
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ElixirAlias alias) {
        return PsiNameIdentifierOwnerImpl.getNameIdentifier(alias);
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ElixirKeywordKey keywordKey) {
        return PsiNameIdentifierOwnerImpl.getNameIdentifier(keywordKey);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement getNameIdentifier(@NotNull ElixirVariable variable) {
        return PsiNameIdentifierOwnerImpl.getNameIdentifier(variable);
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull org.elixir_lang.psi.call.Named named) {
        return PsiNameIdentifierOwnerImpl.getNameIdentifier(named);
    }

    @Nullable
    public static PsiElement getNameIdentifier(@NotNull QualifiableAlias qualifiableAlias) {
        return PsiNameIdentifierOwnerImpl.getNameIdentifier(qualifiableAlias);
    }

    @Contract(pure = true)
    @NotNull
    public static ItemPresentation getPresentation(@NotNull final Call call) {
        return PresentationImpl.getPresentation(call);
    }

    @Contract(pure = true)
    @Nullable
    public static ItemPresentation getPresentation(@NotNull final ElixirIdentifier identifier) {
        return PresentationImpl.getPresentation(identifier);
    }

    @Nullable
    public static PsiReference getReference(@NotNull Call call) {
        return CallImplKt.computeReference(call);
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
        return QualifiableAliasImplKt.getReference(qualifiableAlias, qualifiableAlias.getContainingFile());
    }

    @Nullable
    public static PsiPolyVariantReference getReference(@NotNull QualifiableAlias qualifiableAlias,
                                                       @NotNull PsiElement maxScope) {
        return QualifiableAliasImplKt.getReference(qualifiableAlias, maxScope);
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
                PsiPolyVariantReference reference = QualifiableAliasImplKt.getReference(qualifiableAlias, maxScope);
                modular = toModular(qualifiableAlias, reference);
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
