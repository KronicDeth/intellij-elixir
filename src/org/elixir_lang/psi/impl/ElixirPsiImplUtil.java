package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.Macro;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.call.arguments.Parentheses;
import org.elixir_lang.psi.qualification.Qualified;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;

import static org.elixir_lang.intellij_elixir.Quoter.*;

/**
 * Created by luke.imhoff on 12/29/14.
 */
public class ElixirPsiImplUtil {

    public static final Class[] UNQUOTED_TYPES = new Class[]{
            ElixirEndOfExpression.class,
            PsiComment.class,
            PsiWhiteSpace.class
    };
    public static final OtpErlangObject ALIASES = new OtpErlangAtom("__aliases__");
    public static final OtpErlangAtom AMBIGUOUS_OP = new OtpErlangAtom("ambiguous_op");
    // Must be before AMBIGUOUS_OP_KEYWORD_PAIR where NIL is used
    public static final OtpErlangAtom NIL = new OtpErlangAtom("nil");
    public static final OtpErlangTuple AMBIGUOUS_OP_KEYWORD_PAIR = new OtpErlangTuple(
            new OtpErlangObject[]{
                    AMBIGUOUS_OP,
                    NIL
            }
    );
    public static final OtpErlangAtom BLOCK = new OtpErlangAtom("__block__");
    public static final OtpErlangAtom DO = new OtpErlangAtom("do");
    public static final OtpErlangAtom EXCLAMATION_POINT = new OtpErlangAtom("!");
    public static final OtpErlangAtom FALSE = new OtpErlangAtom("false");
    public static final OtpErlangAtom FN = new OtpErlangAtom("fn");
    public static final OtpErlangAtom MINUS = new OtpErlangAtom("-");
    public static final OtpErlangAtom NOT = new OtpErlangAtom("not");
    public static final OtpErlangAtom PLUS = new OtpErlangAtom("+");
    public static final OtpErlangAtom TRUE = new OtpErlangAtom("true");
    private static final OtpErlangAtom WHEN = new OtpErlangAtom("when");
    public static final OtpErlangAtom UNQUOTE_SPLICING = new OtpErlangAtom("unquote_splicing");
    public static final OtpErlangAtom[] ATOM_KEYWORDS = new OtpErlangAtom[]{
            FALSE,
            TRUE,
            NIL
    };
    public static final OtpErlangAtom[] REARRANGED_UNARY_OPERATORS = new OtpErlangAtom[]{
            EXCLAMATION_POINT,
            NOT
    };
    public static final OtpErlangAtom UTF_8 = new OtpErlangAtom("utf8");
    public static final int BINARY_BASE = 2;
    public static final int DECIMAL_BASE = 10;
    public static final int HEXADECIMAL_BASE = 16;
    public static final int OCTAL_BASE = 8;
    // NOTE: Unknown is all bases not 2, 8, 10, or 16, but 36 is used because all digits and letters are parsed.
    public static final int UNKNOWN_BASE = 36;
    public static final TokenSet IDENTIFIER_TOKEN_SET = TokenSet.create(ElixirTypes.IDENTIFIER);

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirMapConstructionArguments mapConstructionArguments) {
        return mapConstructionArguments.getChildren();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(ElixirNoParenthesesManyArguments noParenthesesManyArguments) {
        Arguments arguments = noParenthesesManyArguments.getNoParenthesesOnePositionalAndKeywordsArguments();

        if (arguments == null) {
            arguments = noParenthesesManyArguments.getNoParenthesesManyPositionalAndMaybeKeywordsArguments();
        }

        return arguments.arguments();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument) {
        PsiElement[] children = noParenthesesOneArgument.getChildren();

        assert children.length == 1;

        PsiElement child = children[0];
        PsiElement[] arguments;

        if (child instanceof Arguments) {
            Arguments childArguments = (Arguments) child;
            arguments = childArguments.arguments();
        } else {
            arguments = children;
        }

        return arguments;
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments noParenthesesManyPositionalAndMaybeKeywordsArguments) {
        return noParenthesesManyPositionalAndMaybeKeywordsArguments.getChildren();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] arguments(@NotNull final ElixirNoParenthesesOnePositionalAndKeywordsArguments noParenthesesOnePositionalAndKeywordsArguments) {
        PsiElement noParenthesesFirstPositional = noParenthesesOnePositionalAndKeywordsArguments.getNoParenthesesFirstPositional();
        PsiElement noParenthesesKeywords = noParenthesesOnePositionalAndKeywordsArguments.getNoParenthesesKeywords();

        return new PsiElement[]{
                noParenthesesFirstPositional,
                noParenthesesKeywords
        };
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
    @NotNull
    public static int base(@SuppressWarnings("unused") @NotNull final ElixirBinaryDigits binaryDigits) {
        return BINARY_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@SuppressWarnings("unused") @NotNull final ElixirBinaryWholeNumber binaryWholeNumber) {
        return BINARY_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirDecimalDigits decimalDigits) {
        return DECIMAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirDecimalWholeNumber decimalWholeNumber) {
        return DECIMAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirHexadecimalDigits hexadecimalDigits) {
        return HEXADECIMAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirHexadecimalWholeNumber hexadecimalWholeNumber) {
        return HEXADECIMAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirOctalDigits octalDigits) {
        return OCTAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirOctalWholeNumber octalWholeNumber) {
        return OCTAL_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirUnknownBaseDigits unknownBaseDigits) {
        return UNKNOWN_BASE;
    }

    @Contract(pure = true)
    @NotNull
    public static int base(@NotNull @SuppressWarnings("unused") final ElixirUnknownBaseWholeNumber unknownBaseWholeNumber) {
        return UNKNOWN_BASE;
    }

    /**
     * Converts group of separated expressions into a block or returns the single expression.
     *
     * See https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L724-L725
     */
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject toBlock(@NotNull final Deque<OtpErlangObject> quotedChildren) {
        OtpErlangObject asBlock;
        final int size = quotedChildren.size();

        if (size == 0) {
            asBlock = NIL;
        } else if (size == 1) {
            asBlock = quotedChildren.getFirst();
        } else {
            asBlock = blockFunctionCall(quotedChildren);
        }

        return asBlock;
    }

    /**
     * Builds a block for stab bodies.  Unlike `toBlock`, handles rearranging unary operations `not` and `!` and putting
     * solitary `unquote_splicing` calls in blocks.
     *
     * @param quotedChildren
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject buildBlock(@NotNull final Deque<OtpErlangObject> quotedChildren) {
        final int size = quotedChildren.size();
        OtpErlangObject builtBlock;

        if (size == 0) {
            builtBlock = NIL;
        } else if (size == 1) {
            OtpErlangObject quotedChild = quotedChildren.getFirst();
            builtBlock = quotedChild;

            // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L588
            if (Macro.isLocalCall(quotedChild)) {
                OtpErlangTuple childTuple = (OtpErlangTuple) quotedChild;
                OtpErlangObject quotedIdentifier = childTuple.elementAt(0);

                // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L547
                if (quotedIdentifier.equals(NOT) || quotedIdentifier.equals(EXCLAMATION_POINT) || quotedIdentifier.equals(UNQUOTE_SPLICING)) {
                    builtBlock = blockFunctionCall(quotedChildren);
                }
            }
        } else {
           builtBlock = blockFunctionCall(quotedChildren);
        }

        return builtBlock;
    }

    @Contract(pure = true)
    @NotNull
    private static OtpErlangTuple blockFunctionCall(@NotNull final Deque<OtpErlangObject> quotedChildren) {
        OtpErlangObject[] quotedArray = new OtpErlangObject[quotedChildren.size()];
        OtpErlangList blockMetadata = new OtpErlangList();
        quotedArray = quotedChildren.toArray(quotedArray);

        return quotedFunctionCall(
                BLOCK,
                blockMetadata,
                quotedArray
        );
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirEscapedCharacter escapedCharacter) {
        ASTNode[] escapedCharacterTokens = escapedCharacter
                .getNode()
                .getChildren(TokenSet.create(ElixirTypes.ESCAPED_CHARACTER_TOKEN));
        int parsedCodePoint = -1;

        if (escapedCharacterTokens.length == 1) {
            ASTNode escapedCharacterToken = escapedCharacterTokens[0];
            String formattedEscapedCharacter = escapedCharacterToken.getText();
            int formattedCodePoint = formattedEscapedCharacter.codePointAt(0);

            // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_interpolation.erl#L130-L142
            switch (formattedCodePoint) {
                case '0':
                    parsedCodePoint = 0;
                    break;
                case 'a':
                    parsedCodePoint = 7;
                    break;
                case 'b':
                    parsedCodePoint = 8;
                    break;
                case 'd':
                    parsedCodePoint = 127;
                    break;
                case 'e':
                    parsedCodePoint = 27;
                    break;
                case 'f':
                    parsedCodePoint = 12;
                    break;
                case 'n':
                    parsedCodePoint = 10;
                    break;
                case 'r':
                    parsedCodePoint = 13;
                    break;
                case 's':
                    parsedCodePoint = 32;
                    break;
                case 't':
                    parsedCodePoint = 9;
                    break;
                case 'v':
                    parsedCodePoint = 11;
                    break;
                default:
                    parsedCodePoint = formattedCodePoint;
            }
        }

        return parsedCodePoint;
    }

    @Contract(pure = true)
    public static int codePoint(@SuppressWarnings("unused") ElixirEscapedEOL escapedEOL) {
        return 10;
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull EscapedHexadecimalDigits hexadecimalEscapeSequence) {
        ASTNode[] validHexadecimalDigitsArray = hexadecimalEscapeSequence
                .getNode()
                .getChildren(
                        TokenSet.create(ElixirTypes.VALID_HEXADECIMAL_DIGITS)
                );
        int parsedCodePoint = -1;

        if (validHexadecimalDigitsArray.length == 1) {
            ASTNode validHexadecimalDigits = validHexadecimalDigitsArray[0];
            String formattedHexadecimalDigits = validHexadecimalDigits.getText();
            parsedCodePoint = Integer.parseInt(formattedHexadecimalDigits, 16);
        }

        return parsedCodePoint;
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirQuoteHexadecimalEscapeSequence quoteHexadecimalEscapeSequence) {
        EscapeSequence escapeSequence = quoteHexadecimalEscapeSequence.getEnclosedHexadecimalEscapeSequence();

        if (escapeSequence == null) {
            escapeSequence = quoteHexadecimalEscapeSequence.getOpenHexadecimalEscapeSequence();
        }

        int parsedCodePoint = -1;

        if (escapeSequence != null) {
            parsedCodePoint = escapeSequence.codePoint();
        }

        return parsedCodePoint;
    }

    public static int codePoint(@NotNull ElixirSigilHexadecimalEscapeSequence sigilHexadecimalEscapeSequence) {
        EscapeSequence escapeSequence = sigilHexadecimalEscapeSequence.getEnclosedHexadecimalEscapeSequence();

        if (escapeSequence == null) {
            escapeSequence = sigilHexadecimalEscapeSequence.getOpenHexadecimalEscapeSequence();
        }

        return escapeSequence.codePoint();
    }

    /*
     * @todo use String.codePoints in Java 8 when IntelliJ is using it
     * @see https://stackoverflow.com/questions/1527856/how-can-i-iterate-through-the-unicode-codepoints-of-a-java-string/21791059#21791059
     */
    public static Iterable<Integer> codePoints(final String string) {
        return new Iterable<Integer>() {
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    int nextIndex = 0;

                    public boolean hasNext() {
                        return nextIndex < string.length();
                    }

                    public Integer next() {
                        int result = string.codePointAt(nextIndex);
                        nextIndex += Character.charCount(result);
                        return result;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirBinaryWholeNumber binaryWholeNumber) {
        List<Digits> digitsList = new LinkedList<Digits>();

        digitsList.addAll(binaryWholeNumber.getBinaryDigitsList());

        return digitsList;
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirDecimalWholeNumber decimalWholeNumber) {
        List<Digits> digitsList = new LinkedList<Digits>();

        digitsList.addAll(decimalWholeNumber.getDecimalDigitsList());

        return digitsList;
    }

    @NotNull static List<Digits> digitsList(@NotNull ElixirHexadecimalWholeNumber hexadecimalWholeNumber) {
        List<Digits> digitsList = new LinkedList<Digits>();

        digitsList.addAll(hexadecimalWholeNumber.getHexadecimalDigitsList());

        return digitsList;
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirOctalWholeNumber octalWholeNumber) {
        List<Digits> digitsList = new LinkedList<Digits>();

        digitsList.addAll(octalWholeNumber.getOctalDigitsList());

        return digitsList;
    }

    @NotNull
    public static List<Digits> digitsList(@NotNull ElixirUnknownBaseWholeNumber unknownBaseWholeNumber) {
        List<Digits> digitsList = new LinkedList<Digits>();

        digitsList.addAll(unknownBaseWholeNumber.getUnknownBaseDigitsList());

        return digitsList;
    }

    public static Document document(PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();

        return fileViewProvider.getDocument();
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

    public static boolean inBase(@NotNull final Digits digits) {
        ASTNode child = digits.getNode().getFirstChildNode();
        boolean inBase = false;

        if (child.getElementType() == digits.validElementType()) {
            inBase = true;
        }

        return inBase;
    }

    public static boolean inBase(@NotNull final List<Digits> digitsList) {
        int validDigitsCount = 0;
        int invalidDigitsCount = 0;

        for (Digits digits : digitsList) {
            if (digits.inBase()) {
                validDigitsCount++;
            } else {
                invalidDigitsCount++;
            }
        }

        boolean valid = false;

        if (invalidDigitsCount < 1 && validDigitsCount > 0) {
            valid = true;
        }

        return valid;
    }

    /*
     * Whether this is a `defmodule <argument> do end` call.
     */
    public static boolean isDefmodule(@NotNull final ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall) {
        return (unmatchedUnqualifiedNoParenthesesCall.resolvedModuleName().equals("Elixir.Kernel") &&
                unmatchedUnqualifiedNoParenthesesCall.resolvedFunctionName().equals("defmodule") &&
                unmatchedUnqualifiedNoParenthesesCall.getDoBlock() != null);
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

            isModuleName = unmatchedUnqualifiedNoParenthesesCall.isDefmodule();
        }

        return isModuleName;
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
    @NotNull
    public static Quotable leftOperand(InfixOperation infixOperation) {
        PsiElement[] children = infixOperation.getChildren();

        assert children.length == 3;

        return (Quotable) children[0];
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

    public static OtpErlangList metadata(ASTNode node) {
        OtpErlangObject[] keywordListElements = {
                lineNumberKeywordTuple(node)
        };

        return new OtpErlangList(keywordListElements);
    }

    public static OtpErlangList metadata(Operator operator) {
        return metadata(operatorTokenNode(operator));
    }

    public static OtpErlangList metadata(PsiElement element) {
        return metadata(element.getNode());
    }

    /* TODO determine what counter means in Code.string_to_quoted("Foo")
       {:ok, {:__aliases__, [counter: 0, line: 1], [:Foo]}} */
    public static OtpErlangList metadata(PsiElement element, int counter) {
        /* QuotableKeywordList should be compared by sorting keys, but Elixir does counter first, so it's simpler to just use
           same order than detect a OtpErlangList is a QuotableKeywordList */
        final OtpErlangObject[] keywordListElements = {
                keywordTuple("counter", counter),
                lineNumberKeywordTuple(element.getNode())
        };

        return new OtpErlangList(keywordListElements);
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleAttributeName(@NotNull final AtNonNumericOperation atNonNumericOperation) {
        return atNonNumericOperation.getText();
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleAttributeName(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        ASTNode node = atUnqualifiedNoParenthesesCall.getNode();
        ASTNode[] identifierNodes = node.getChildren(IDENTIFIER_TOKEN_SET);

        assert identifierNodes.length == 1;

        return "@" + identifierNodes[0].getText();
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull @SuppressWarnings("unused") final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        // Always null because it's unqualified.
        return null;
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull @SuppressWarnings("unused") final DotCall dotCall) {
        // Always null because anonymous
        return null;
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String moduleName(@NotNull @SuppressWarnings("unused") final Unqualified unqualified) {
        // Always null because it's unqualified.
        return null;
    }

    @Contract(pure = true)
    @NotNull
    public static String moduleName(@NotNull final Qualified qualified) {
        // TODO handle more complex qualifiers besides Aliases
        return qualified.getFirstChild().getText();
    }

    @Contract(pure = true)
    @NotNull
    public static Operator operator(InfixOperation infixOperation) {
        PsiElement[] children = infixOperation.getChildren();

        assert children.length == 3;

        return (Operator) children[1];
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
        return TokenSet.create(ElixirTypes.DUAL_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAndInfixOperator andInfixOperator) {
        return TokenSet.create(ElixirTypes.AND_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirArrowInfixOperator arrowInfixOperator) {
        return TokenSet.create(ElixirTypes.ARROW_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAtPrefixOperator atPrefixOperator) {
        return TokenSet.create(ElixirTypes.AT_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirCapturePrefixOperator capturePrefixOperator) {
        return TokenSet.create(ElixirTypes.CAPTURE_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirComparisonInfixOperator comparisonInfixOperator) {
        return TokenSet.create(ElixirTypes.COMPARISON_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirDotInfixOperator dotInfixOperator) {
        return TokenSet.create(ElixirTypes.DOT_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMultiplicationInfixOperator multiplicationInfixOperator) {
        return TokenSet.create(ElixirTypes.MULTIPLICATION_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirInInfixOperator inInfixOperator) {
        return TokenSet.create(ElixirTypes.IN_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirInMatchInfixOperator inMatchInfixOperator) {
        return TokenSet.create(ElixirTypes.IN_MATCH_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMapPrefixOperator mapPrefixOperator) {
        return TokenSet.create(ElixirTypes.STRUCT_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirMatchInfixOperator matchInfixOperator) {
        return TokenSet.create(ElixirTypes.MATCH_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirOrInfixOperator orInfixOperator) {
        return TokenSet.create(ElixirTypes.OR_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirPipeInfixOperator pipeInfixOperator) {
        return TokenSet.create(ElixirTypes.PIPE_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirRelationalInfixOperator relationalInfixOperator) {
        return TokenSet.create(ElixirTypes.RELATIONAL_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirStabInfixOperator stabInfixOperator) {
        return TokenSet.create(ElixirTypes.STAB_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirTwoInfixOperator twoInfixOperator) {
        return TokenSet.create(ElixirTypes.TWO_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirTypeInfixOperator typeInfixOperator) {
        return TokenSet.create(ElixirTypes.TYPE_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirUnaryPrefixOperator unaryPrefixOperator) {
        return TokenSet.create(ElixirTypes.DUAL_OPERATOR, ElixirTypes.UNARY_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirWhenInfixOperator whenInfixOperator) {
        return TokenSet.create(ElixirTypes.WHEN_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final DotCall dotCall) {
        List<ElixirParenthesesArguments> parenthesesArgumentsList = dotCall.getParenthesesArgumentsList();

        ElixirParenthesesArguments primaryParenthesesArguments = parenthesesArgumentsList.get(0);
        return primaryParenthesesArguments.arguments();
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] primaryArguments(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        Arguments arguments = unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArguments();

        if (arguments == null) {
            arguments = unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesStrict();
        }

        assert arguments != null;

        return arguments.arguments();
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] primaryArguments(@NotNull @SuppressWarnings("unused") final None none) {
        return null;
    }


    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final NoParenthesesOneArgument noParenthesesOneArgument) {
        return noParenthesesOneArgument.getNoParenthesesOneArgument().arguments();
    }

    @Contract(pure = true)
    @NotNull
    public static PsiElement[] primaryArguments(@NotNull final Parentheses parentheses) {
        ElixirMatchedParenthesesArguments matchedParenthesesArguments = parentheses.getMatchedParenthesesArguments();
        List<ElixirParenthesesArguments> parenthesesArgumentsList = matchedParenthesesArguments.getParenthesesArgumentsList();

        ElixirParenthesesArguments primaryParenthesesArguments = parenthesesArgumentsList.get(0);
        return primaryParenthesesArguments.arguments();
    }

    public static boolean processDeclarations(@NotNull final ElixirAccessExpression accessExpression,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return processDeclarationsRecursively(
                accessExpression,
                processor,
                state,
                lastParent,
                place
        );
    }

    public static boolean processDeclarations(@NotNull final ElixirAlias alias,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return processDeclarationsRecursively(
                alias,
                processor,
                state,
                lastParent,
                place
        );
    }

    public static boolean processDeclarations(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return processDeclarationsRecursively(
                noParenthesesOneArgument,
                processor,
                state,
                lastParent,
                place
        );
    }

    public static boolean processDeclarations(@NotNull final ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return processDeclarationsRecursively(
                unmatchedUnqualifiedNoParenthesesCall,
                processor,
                state,
                lastParent,
                place
        );
    }

    public static boolean processDeclarations(@NotNull final QualifiedAlias qualifiedAlias,
                                              @NotNull PsiScopeProcessor processor,
                                              @NotNull ResolveState state,
                                              PsiElement lastParent,
                                              @NotNull PsiElement place) {
        return processor.execute(qualifiedAlias, state);
    }

    public static boolean processDeclarationsRecursively(@NotNull final PsiElement psiElement,
                                                         @NotNull PsiScopeProcessor processor,
                                                         @NotNull ResolveState state,
                                                         PsiElement lastParent,
                                                         @NotNull PsiElement place) {
        boolean keepProcessing = processor.execute(psiElement, state);

        if (keepProcessing) {
            PsiElement[] children = psiElement.getChildren();

            for (PsiElement child : children) {
                if (!child.processDeclarations(processor, state, lastParent, place)) {
                    keepProcessing = false;

                    break;
                }
            }
        }

        return keepProcessing;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final AssociationOperation associationOperation) {
        PsiElement[] children = associationOperation.getChildren();

        // associationInfixOperator is private so not a PsiElement
        assert children.length == 2;

        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];

        int i = 0;
        for (PsiElement child : children) {
            Quotable quotableChild = (Quotable) child;

            quotedChildren[i++] = quotableChild.quote();
        }

        return new OtpErlangTuple(quotedChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final InfixOperation infixOperation) {
        Quotable leftOperand = infixOperation.leftOperand();
        OtpErlangObject quotedLeftOperand = leftOperand.quote();

        Operator operator = infixOperation.operator();
        OtpErlangObject quotedOperator = operator.quote();

        Quotable rightOperand = infixOperation.rightOperand();
        OtpErlangObject quotedRightOperand = rightOperand.quote();

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedLeftOperand,
                quotedRightOperand
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirBitString bitString) {
        ASTNode node = bitString.getNode();
        ASTNode[] openingBits = node.getChildren(TokenSet.create(ElixirTypes.OPENING_BIT));

        assert openingBits.length == 1;

        ASTNode openingBit = openingBits[0];

        PsiElement[] children = bitString.getChildren();
        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];

        int i = 0;
        for (PsiElement child : children) {
            Quotable quotableChild = (Quotable) child;
            quotedChildren[i++] = quotableChild.quote();
        }

        return quotedFunctionCall(
                "<<>>",
                metadata(openingBit),
                quotedChildren
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirBlockIdentifier blockIdentifier) {
        return new OtpErlangAtom(blockIdentifier.getNode().getText());
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirBlockItem blockItem) {
        Quotable blockIdentifier = blockItem.getBlockIdentifier();
        Quotable stab = blockItem.getStab();
        OtpErlangObject quotedValue = NIL;

        if (stab != null) {
            quotedValue = stab.quote();
        }

        return new OtpErlangTuple(
                new OtpErlangObject[]{
                        blockIdentifier.quote(),
                        quotedValue
                }
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirBracketArguments bracketArguments) {
        PsiElement[] children = bracketArguments.getChildren();

        assert children.length == 1;

        Quotable child = (Quotable) children[0];

        return child.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final Digits digits) {
        final String text = digits.getText();
        final int base = digits.base();
        OtpErlangLong quoted;

        try {
            long value = Long.parseLong(text, base);
            quoted = new OtpErlangLong(value);
        } catch (NumberFormatException exception) {
            BigInteger value = new BigInteger(text, base);
            quoted = new OtpErlangLong(value);
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAccessExpression accessExpression) {
        PsiElement[] children = accessExpression.getChildren();

        if (children.length != 1) {
            throw new NotImplementedException("Expecting 1 child in accessExpression");
        }

        Quotable child = (Quotable) children[0];

        return child.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAlias alias) {
        return quotedFunctionCall(
                ALIASES,
                metadata(alias, 0),
                new OtpErlangAtom(alias.getText())
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAnonymousFunction anonymousFunction) {
        Quotable stab = anonymousFunction.getStab();
        OtpErlangObject quotedStab = stab.quote();

        OtpErlangList metadata = metadata(anonymousFunction);

        return new OtpErlangTuple(
                new OtpErlangObject[] {
                        FN,
                        metadata,
                        quotedStab
                }
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAssociations associations) {
        Quotable associationBase = associations.getAssociationsBase();

        return associationBase.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAssociationsBase associationsBase) {
        PsiElement[] children = associationsBase.getChildren();
        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];
        int i = 0;

        for (PsiElement child : children) {
            Quotable quotableChild = (Quotable) child;

            quotedChildren[i++] = quotableChild.quote();
        }

        return new OtpErlangList(quotedChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAtom atom) {
        OtpErlangObject quoted;
        ElixirCharListLine charListLine = atom.getCharListLine();

        if (charListLine != null) {
            quoted = charListLine.quoteAsAtom();
        } else {
            ElixirStringLine stringLine = atom.getStringLine();

            if (stringLine != null) {
                quoted = stringLine.quoteAsAtom();
            } else {
                ASTNode atomNode = atom.getNode();
                ASTNode atomFragmentNode = atomNode.getLastChildNode();

                assert atomFragmentNode.getElementType() == ElixirTypes.ATOM_FRAGMENT;

                quoted = new OtpErlangAtom(atomFragmentNode.getText());
            }
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAtomKeyword atomKeyword) {
        return new OtpErlangAtom(atomKeyword.getText());
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharListLine charListLine) {
        ElixirQuoteCharListBody quoteCharListBody = charListLine.getQuoteCharListBody();

        return quotedChildNodes(charListLine, childNodes(quoteCharListBody));
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharToken charToken) {
        ASTNode[] children = charToken.getNode().getChildren(null);

        if (children.length != 2) {
            throw new NotImplementedException("CharToken expected to be ?(<character>|<escape sequence>)");
        }

        final ASTNode tokenized = children[1];
        IElementType tokenizedElementType = tokenized.getElementType();
        int codePoint;

        if (tokenizedElementType == ElixirTypes.CHAR_LIST_FRAGMENT) {
            if (tokenized.getTextLength() != 1) {
                throw new NotImplementedException("Tokenized character expected to only be one character long");
            }

            String tokenizedString = tokenized.getText();
            codePoint = tokenizedString.codePointAt(0);
        } else {
            EscapeSequence escapeSequence = (EscapeSequence) tokenized.getPsi();
            codePoint = escapeSequence.codePoint();
        }

        return new OtpErlangLong(codePoint);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirContainerAssociationOperation containerAssociationOperation) {
        PsiElement[] children = containerAssociationOperation.getChildren();

        // associationInfixOperator is private so not a PsiElement
        assert children.length == 2;

        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];

        int i = 0;
        for (PsiElement child : children) {
            Quotable quotableChild = (Quotable) child;

            quotedChildren[i++] = quotableChild.quote();
        }

        return new OtpErlangTuple(quotedChildren);
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

    /**
     * Adds `Elixir.` to alias text.
     *
     * @param alias
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static String fullyQualifiedName(@NotNull final ElixirAlias alias) {
        return "Elixir." + alias.getName();
    }

    @Contract(pure = true)
    @Nullable
    public static String fullyQualifiedName(@NotNull final QualifiableAlias qualifiableAlias) {
        String fullyQualifiedName = null;
        PsiElement[] children = qualifiableAlias.getChildren();

        assert children.length == 3;

        PsiElement qualifier = children[0];
        String qualifierName = null;

        if (qualifier instanceof QualifiableAlias) {
            QualifiableAlias qualifiableQualifier = (QualifiableAlias) qualifier;

            qualifierName = qualifiableQualifier.fullyQualifiedName();
        } else if (qualifier instanceof ElixirAccessExpression) {
            PsiElement[] qualifierChildren = qualifier.getChildren();

            if (qualifierChildren.length == 1) {
                PsiElement qualifierChild = qualifierChildren[0];

                if (qualifierChild instanceof ElixirAlias) {
                    ElixirAlias childAlias = (ElixirAlias) qualifierChild;

                    qualifierName = childAlias.getName();
                }
            }
        }

        if (qualifierName != null) {
            ElixirAlias alias = (ElixirAlias) children[2];

            fullyQualifiedName = qualifierName + "." + alias.getName();
        }

        return fullyQualifiedName;
    }

    @Contract(pure = true)
    @Nullable
    public static String functionName(@NotNull final Call call) {
        String functionName = null;
        ASTNode node = call.functionNameNode();

        if (node != null) {
            functionName = node.getText();
        }

        return functionName;
    }

    /**
     *
     * @param atUnqualifiedNoParenthesesCall
     * @return `null` because the `IDENTIFIER`, `foo` in `@foo 1` is not the local name of a function, but the name of a
     *   Module attribute.
     */
    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static ASTNode functionNameNode(@NotNull @SuppressWarnings("unused") final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        return null;
    }

    /**
     *
     * @param dotCall
     * @return `null` because the expression before the `.` is a variable name and not a function name.
     */
    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static ASTNode functionNameNode(@NotNull @SuppressWarnings("unused") final DotCall dotCall) {
        return null;
    }

    public static ASTNode functionNameNode(@NotNull final Qualified qualified) {
        return qualified.getRelativeIdentifier().getNode();
    }

    @Contract(pure = true)
    @NotNull
    public static ASTNode functionNameNode(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        return unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArgumentsUnqualifiedIdentifier().getNode();
    }

    @Contract(pure = true)
    @NotNull
    public static ASTNode functionNameNode(@NotNull final Unqualified unqualified) {
        return unqualified.getNode().getFirstChildNode();
    }

    @Contract(pure = true)
    @NotNull
    public static QuotableArguments getArguments(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        QuotableArguments arguments = unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArguments();

        if (arguments == null) {
            arguments = unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesStrict();
        }

        return arguments;
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
    public static ElixirDoBlock getDoBlock(@NotNull @SuppressWarnings("unused") final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        return null;
    }

    @Nullable
    public static ElixirDoBlock getDoBlock(@SuppressWarnings("unused") MatchedCall matchedCall) {
        return null;
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

    @Contract(pure = true)
    @NotNull
    public static Quotable getIdentifier(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        return unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArgumentsUnqualifiedIdentifier();
    }

    public static Quotable getKeywordValue(ElixirKeywordPair keywordPair) {
        PsiElement[] children = keywordPair.getChildren();

        assert children.length == 2;

        return (Quotable) children[1];
    }

    public static Quotable getKeywordValue(ElixirNoParenthesesKeywordPair noParenthesesKeywordPair) {
        return noParenthesesKeywordPair.getNoParenthesesExpression();
    }

    @NotNull
    public static String getName(@NotNull ElixirAlias alias) {
        return alias.getText();
    }

    @NotNull
    public static String getName(@NotNull ElixirMatchedQualifiedAlias matchedQualifiedAlias) {
        return matchedQualifiedAlias.getText();
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

    @Nullable
    public static PsiReference getReference(@NotNull QualifiableAlias qualifiableAlias) {
        PsiElement parent = qualifiableAlias.getParent();
        PsiReference reference = null;

        /* prevents individual Aliases or tail qualified aliases of qualified chain from having reference separate
           reference from overall chain */
        if (!(parent instanceof QualifiableAlias)) {
            PsiElement grandParent = parent.getParent();

            // prevents first Alias of a qualified chain from having a separate reference from overall chain
            if (!(grandParent instanceof QualifiableAlias)) {
                reference = new org.elixir_lang.reference.Module(qualifiableAlias);
            }
        }

        return reference;
    }

    @Nullable
    public static PsiReference getReference(@NotNull final AtNonNumericOperation atNonNumericOperation) {
        return new org.elixir_lang.reference.ModuleAttribute(atNonNumericOperation);
    }

    public static List<QuotableKeywordPair> quotableKeywordPairList(ElixirKeywords keywords) {
        return new ArrayList<QuotableKeywordPair>(keywords.getKeywordPairList());
    }

    public static List<QuotableKeywordPair> quotableKeywordPairList(ElixirNoParenthesesKeywords noParenthesesKeywords) {
        return new ArrayList<QuotableKeywordPair>(noParenthesesKeywords.getNoParenthesesKeywordPairList());
    }

    @NotNull
    public static OtpErlangObject quote(@NotNull ElixirDecimalFloat decimalFloat) {
        OtpErlangObject quoted;

        ElixirDecimalFloatIntegral decimalFloatIntegral = decimalFloat.getDecimalFloatIntegral();
        ElixirDecimalWholeNumber integralDecimalWholeNumber = decimalFloatIntegral.getDecimalWholeNumber();
        List<Digits> integralDigitsList = integralDecimalWholeNumber.digitsList();
        String integralString = compactDigits(integralDigitsList);

        ElixirDecimalFloatFractional decimalFloatFractional = decimalFloat.getDecimalFloatFractional();
        ElixirDecimalWholeNumber fractionalDecimalWholeNumber = decimalFloatFractional.getDecimalWholeNumber();
        List<Digits> fractionalDigitsList = fractionalDecimalWholeNumber.digitsList();
        String fractionalString = compactDigits(fractionalDigitsList);

        ElixirDecimalFloatExponent decimalFloatExponent = decimalFloat.getDecimalFloatExponent();

        if (decimalFloatExponent != null) {
            ElixirDecimalWholeNumber exponentDecimalWholeNumber = decimalFloatExponent.getDecimalWholeNumber();
            List<Digits> exponentDigitsList = exponentDecimalWholeNumber.digitsList();
            String exponentSignString = decimalFloatExponent.getDecimalFloatExponentSign().getText();
            String exponentString = compactDigits(exponentDigitsList);

            String floatString = String.format(
                    "%s.%se%s%s",
                    integralString,
                    fractionalString,
                    exponentSignString,
                    exponentString
            );

            if (inBase(integralDigitsList) && inBase(fractionalDigitsList) && inBase(exponentDigitsList)) {
                Double parsedDouble = Double.parseDouble(floatString);
                quoted = new OtpErlangDouble(parsedDouble);
            } else {
                // Convert parser error to runtime ArgumentError
                quoted = quotedFunctionCall(
                        "String",
                        "to_float",
                        metadata(decimalFloat),
                        elixirString(floatString)
                );
            }
        } else {
            String floatString = String.format(
                    "%s.%s",
                    integralString,
                    fractionalString
            );

            if (inBase(integralDigitsList) && inBase(fractionalDigitsList)) {
                Double parsedDouble = Double.parseDouble(floatString);
                quoted = new OtpErlangDouble(parsedDouble);
            } else {
                // Convert parser error to runtime ArgumentError
                quoted = quotedFunctionCall(
                        "String",
                        "to_float",
                        metadata(decimalFloat),
                        elixirString(floatString)
                );
            }
        }

        return quoted;
    }

    public static OtpErlangObject quote(@SuppressWarnings("unused") ElixirEmptyParentheses emptyParentheses) {
        return new OtpErlangAtom("nil");
    }

    public static OtpErlangObject quote(ElixirFile file) {
        final Deque<OtpErlangObject> quotedChildren = new LinkedList<OtpErlangObject>();

        file.acceptChildren(
                new PsiElementVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof Quotable) {
                            visitQuotable((Quotable) element);
                        } else if (!isUnquoted(element)) {
                            throw new NotImplementedException("Don't know how to visit " + element);
                        }

                        super.visitElement(element);
                    }

                    public void visitQuotable(@NotNull Quotable child) {
                        final OtpErlangObject quotedChild = child.quote();
                        quotedChildren.add(quotedChild);
                    }
                }
        );

        // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L76-L79
        return toBlock(quotedChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final WholeNumber wholeNumber) {
        List<Digits> digitsList = wholeNumber.digitsList();

        OtpErlangObject quoted;

        if (inBase(digitsList)) {
            StringBuilder stringBuilder = new StringBuilder();

            for (Digits digits : digitsList) {
                stringBuilder.append(digits.getText());
            }

            quoted = quoteInBase(stringBuilder.toString(), wholeNumber.base());
        } else {
            /* 0 elements is invalid in native Elixir and can be emulated as `String.to_integer("", base)` while
               2 elements implies at least one element is invalidDigitsElementType which is invalid in native Elixir and
               can be emulated as String.to_integer(<all-digits>, base) so that it raises an ArgumentError on the invalid
               digits */
            StringBuilder stringBuilder = new StringBuilder();

            for (Digits digits : digitsList) {
                stringBuilder.append(digits.getText());
            }

            quoted = quotedFunctionCall(
                    "String",
                    "to_integer",
                    metadata(wholeNumber),
                    elixirString(
                            stringBuilder.toString()
                    ),
                    new OtpErlangLong(wholeNumber.base())
            );
        }

        return quoted;
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

    /* "#{a}" is transformed to "<<Kernel.to_string(a) :: binary>>" in
     * `"\"\#{a}\"" |> Code.string_to_quoted |> Macro.to_string`, so interpolation has to be represented as a type call
     * (`:::`) to binary of a call of `Kernel.to_string`
     */
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirInterpolation interpolation) {
        OtpErlangObject quotedChildren = quote(interpolation.getChildren());
        OtpErlangList interpolationMetadata = metadata(interpolation);

        OtpErlangObject quotedKernelToStringCall = quotedFunctionCall(
                "Elixir.Kernel",
                "to_string",
                interpolationMetadata,
                quotedChildren
        );
        OtpErlangObject quotedBinaryCall = quotedVariable(
                "binary",
                interpolationMetadata
        );
        OtpErlangObject quotedType = quotedFunctionCall(
                "::",
                interpolationMetadata,
                quotedKernelToStringCall,
                quotedBinaryCall
        );

        return quotedType;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirRelativeIdentifier relativeIdentifier) {
        PsiElement[] children = relativeIdentifier.getChildren();
        OtpErlangObject quotedRelativeIdentifier;

        // Only tokens
        if (children.length == 0) {
            ASTNode relativeIdentifierNode = relativeIdentifier.getNode();
            // take first node to avoid SIGNIFICANT_WHITE_SPACE after DUAL_OPERATOR
            ASTNode operatorNode = relativeIdentifierNode.getFirstChildNode();
            quotedRelativeIdentifier = new OtpErlangAtom(operatorNode.getText());
        } else {
            assert children.length == 1;

            PsiElement child = children[0];

            if (child instanceof Atomable) {
                Atomable atomable = (Atomable) child;
                quotedRelativeIdentifier = atomable.quoteAsAtom();
            } else {
                Quotable quotable = (Quotable) child;
                quotedRelativeIdentifier = quotable.quote();
            }
        }

        return quotedRelativeIdentifier;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirSigilModifiers sigilModifiers) {
        String sigilModifiersText = sigilModifiers.getText();
        List<Integer> codePoints = new ArrayList<Integer>(sigilModifiersText.length());
        OtpErlangObject quoted;

        for (int i = 0; i < sigilModifiersText.length(); i++) {
            int codePoint = sigilModifiersText.codePointAt(i);
            codePoints.add(codePoint);
        }

        if (codePoints.size() == 0) {
            quoted = new OtpErlangList();
        } else {
            quoted = elixirCharList(codePoints);
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirStab stab) {
        Deque<OtpErlangObject> quotedChildren = new ArrayDeque<OtpErlangObject>();

        ElixirStabBody stabBody = stab.getStabBody();
        OtpErlangObject quoted;

        if (stabBody != null) {
            // TODO port quote(ElixirStabExpression)'s unary handling
            quoted = stabBody.quote();
        } else {
            List<ElixirStabOperation> stabOperationList = stab.getStabOperationList();

            for (ElixirStabOperation stabOperation : stabOperationList) {
                quotedChildren.add(stabOperation.quote());
            }

            int size = quotedChildren.size();

            OtpErlangObject[] quotedArray = new OtpErlangObject[size];
            quotedArray = quotedChildren.toArray(quotedArray);
            quoted = new OtpErlangList(quotedArray);
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabBody stabBody) {
        PsiElement[] children = stabBody.getChildren();
        Deque<OtpErlangObject> quotedChildren = new ArrayDeque<OtpErlangObject>();

        for (PsiElement child : children) {
            // skip endOfExpression
            if (isUnquoted(child)) {
                continue;
            }

            Quotable quotableChild = (Quotable) child;
            OtpErlangObject quotedChild = quotableChild.quote();
            quotedChildren.add(quotedChild);
        }

        return buildBlock(quotedChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabNoParenthesesSignature stabNoParenthesesSignature) {
        QuotableArguments noParenthesesArguments = stabNoParenthesesSignature.getNoParenthesesArguments();
        OtpErlangObject[] quotedArguments = noParenthesesArguments.quoteArguments();

        // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L277
        OtpErlangObject[] unwrappedWhen = unwrapWhen(quotedArguments);
        OtpErlangList quotedArgumentList = new OtpErlangList(unwrappedWhen);

        return elixirCharList(quotedArgumentList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(ElixirStabOperation stabOperation) {
        Quotable leftOperand = stabOperation.getStabParenthesesSignature();

        if (leftOperand == null) {
            leftOperand = stabOperation.getStabNoParenthesesSignature();
        }

        OtpErlangObject quotedLeftOperand;

        if (leftOperand != null) {
            quotedLeftOperand = leftOperand.quote();
        } else {
            // when there is not signature before `->`.
            quotedLeftOperand = new OtpErlangList();
        }

        Operator operator = stabOperation.getStabInfixOperator();
        OtpErlangObject quotedOperator = operator.quote();

        Quotable rightOperand = stabOperation.getStabBody();
        OtpErlangObject quotedRightOperand;

        if (rightOperand != null) {
            quotedRightOperand = rightOperand.quote();
        } else {
            quotedRightOperand = NIL;
        }

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedLeftOperand,
                quotedRightOperand
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStabParenthesesSignature stabParenthesesSignature) {
        PsiElement[] children = stabParenthesesSignature.getChildren();
        OtpErlangObject[] quotedListElements;

        assert children.length >= 1;

        // stabParenthesesManyArguments ...
        QuotableArguments quotableArguments = (QuotableArguments) children[0];
        OtpErlangObject[] quotedArguments = quotableArguments.quoteArguments();

        // stabParenthesesManyArguments WHEN_OPERATOR expression
        if (children.length > 1) {
            assert children.length == 3;

            Operator operator = (Operator) children[1];
            OtpErlangObject quotedOperator = operator.quote();

            Quotable guard = (Quotable) children[2];
            OtpErlangObject quotedGuard = guard.quote();

            OtpErlangObject[] quotedWhenArguments = new OtpErlangObject[quotedArguments.length + 1];

            System.arraycopy(quotedArguments, 0, quotedWhenArguments, 0, quotedArguments.length);
            quotedWhenArguments[quotedWhenArguments.length - 1] = quotedGuard;

            OtpErlangObject quotedWhenOperation = quotedFunctionCall(
                    quotedOperator,
                    metadata(operator),
                    quotedWhenArguments
            );
            quotedListElements = new OtpErlangObject[]{
                    quotedWhenOperation
            };
        } else {
            quotedListElements = quotedArguments;
        }

        return new OtpErlangList(quotedListElements);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStringLine stringLine) {
        ElixirQuoteStringBody quoteStringBody = stringLine.getQuoteStringBody();
        return quotedChildNodes(stringLine, childNodes(quoteStringBody));
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirStructOperation structOperation) {
        PsiElement[] children = structOperation.getChildren();

        assert children.length == 3;

        Operator operator = (Operator) children[0];
        OtpErlangObject quotedOperator = operator.quote();

        Quotable name = (Quotable) children[1];
        OtpErlangObject quotedName = name.quote();

        Quotable mapArguments = (Quotable) children[2];
        OtpErlangObject quotedMapArguments = mapArguments.quote();

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedName,
                quotedMapArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirTuple tuple) {
        ASTNode node = tuple.getNode();
        ASTNode[] openingCurlies = node.getChildren(TokenSet.create(ElixirTypes.OPENING_CURLY));

        assert openingCurlies.length == 1;

        ASTNode openingCurly = openingCurlies[0];

        PsiElement[] children = tuple.getChildren();
        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];

        int i = 0;
        for (PsiElement child : children) {
            Quotable quotableChild = (Quotable) child;
            quotedChildren[i++] = quotableChild.quote();
        }

        OtpErlangObject quoted;

        // 2-tuples are literals in quoted form
        if (quotedChildren.length == 2) {
            quoted = new OtpErlangTuple(quotedChildren);
        } else {
            quoted = quotedFunctionCall(
                    "{}",
                    metadata(openingCurly),
                    quotedChildren
            );
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final Heredoc heredoc) {
        ElixirHeredocPrefix heredocPrefix = heredoc.getHeredocPrefix();
        int prefixLength = heredocPrefix.getTextLength();
        Deque<ASTNode> alignedNodeQueue = new LinkedList<ASTNode>();
        List<HeredocLine> heredocLineList = heredoc.getHeredocLineList();
        IElementType fragmentType = heredoc.getFragmentType();

        for (HeredocLine line  : heredocLineList) {
            queueChildNodes(line, fragmentType, prefixLength, alignedNodeQueue);
        }

        Queue<ASTNode> mergedNodeQueue = mergeFragments(
                alignedNodeQueue,
                heredoc.getFragmentType(),
                heredoc.getManager()
        );
        ASTNode[] mergedNodes = new ASTNode[mergedNodeQueue.size()];
        mergedNodeQueue.toArray(mergedNodes);

        return quotedChildNodes(heredoc, mergedNodes);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final HeredocLine heredocLine, @NotNull final Heredoc heredoc, int prefixLength) {
        ElixirHeredocLinePrefix heredocLinePrefix = heredocLine.getHeredocLinePrefix();
        ASTNode excessWhitespace = heredocLinePrefix.excessWhitespace(heredoc.getFragmentType(), prefixLength);
        Body body = heredocLine.getBody();
        ASTNode[] directChildNodes = childNodes(body);
        ASTNode[] accumulatedChildNodes;

        if (excessWhitespace != null) {
            accumulatedChildNodes = new ASTNode[directChildNodes.length + 1];
            accumulatedChildNodes[0] = excessWhitespace;

            for (int i = 0; i < directChildNodes.length; i++) {
                accumulatedChildNodes[i + 1] = directChildNodes[i];
            }
        } else {
            accumulatedChildNodes = directChildNodes;
        }

        return quotedChildNodes(
                heredoc,
                metadata(heredocLine),
                accumulatedChildNodes
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QuotableKeywordList quotableKeywordList) {
        List<QuotableKeywordPair> keywordPairList = quotableKeywordList.quotableKeywordPairList();
        List<OtpErlangObject> quotedKeywordPairList = new ArrayList<OtpErlangObject>(keywordPairList.size());

        for (QuotableKeywordPair quotableKeywordPair : keywordPairList) {
            quotedKeywordPairList.add(
                    quotableKeywordPair.quote()
            );
        }

        OtpErlangObject[] quotedKeywordPairs = new OtpErlangObject[quotedKeywordPairList.size()];
        quotedKeywordPairList.toArray(quotedKeywordPairs);

        return new OtpErlangList(quotedKeywordPairs);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QuotableKeywordPair quotableKeywordPair) {
        Quotable keywordKey = quotableKeywordPair.getKeywordKey();
        OtpErlangObject quotedKeywordKey = keywordKey.quote();

        Quotable keywordValue = quotableKeywordPair.getKeywordValue();
        OtpErlangObject quotedKeywordValue = keywordValue.quote();

        OtpErlangObject[] elements = {
                quotedKeywordKey,
                quotedKeywordValue
        };

        return new OtpErlangTuple(elements);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirKeywordKey keywordKey) {
        ElixirCharListLine charListLine = keywordKey.getCharListLine();
        OtpErlangObject quoted;

        if (charListLine != null) {
            quoted = charListLine.quoteAsAtom();
        } else {
            ElixirStringLine stringLine = keywordKey.getStringLine();

            if (stringLine != null) {
                quoted = stringLine.quoteAsAtom();
            } else {
                quoted = new OtpErlangAtom(keywordKey.getText());
            }
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirList list) {
        PsiElement[] listArguments = list.getChildren();
        List<OtpErlangObject> quotedListArgumentList = new ArrayList<OtpErlangObject>(listArguments.length);

        if (listArguments.length > 0) {
            for (int i = 0; i < listArguments.length - 1; i++) {
                Quotable listArgument = (Quotable) listArguments[i];
                quotedListArgumentList.add(listArgument.quote());
            }

            PsiElement lastListArgument = listArguments[listArguments.length - 1];

            if (lastListArgument instanceof ElixirKeywords) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) lastListArgument;

                for (Quotable keywordPair : quotableKeywordList.quotableKeywordPairList()) {
                    quotedListArgumentList.add(keywordPair.quote());
                }
            } else {
                Quotable quotable = (Quotable) lastListArgument;
                quotedListArgumentList.add(quotable.quote());
            }
        }

        OtpErlangObject[] quotedListArguments = new OtpErlangObject[quotedListArgumentList.size()];
        quotedListArgumentList.toArray(quotedListArguments);

        return elixirCharList(new OtpErlangList(quotedListArguments));
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapArguments mapArguments) {
        ASTNode node = mapArguments.getNode();
        ASTNode[] openingCurlies = node.getChildren(TokenSet.create(ElixirTypes.OPENING_CURLY));

        assert openingCurlies.length == 1;

        ASTNode openingCurly = openingCurlies[0];

        OtpErlangObject[] quotedArguments;

        ElixirMapUpdateArguments mapUpdateArguments = mapArguments.getMapUpdateArguments();

        if (mapUpdateArguments != null) {
            quotedArguments = new OtpErlangObject[]{
                    mapUpdateArguments.quote()
            };
        } else {
            ElixirMapConstructionArguments mapConstructionArguments = mapArguments.getMapConstructionArguments();

            if (mapConstructionArguments != null) {
                quotedArguments = mapConstructionArguments.quoteArguments();
            } else {
                quotedArguments = new OtpErlangObject[0];
            }
        }

        return quotedFunctionCall(
                "%{}",
                metadata(openingCurly),
                quotedArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapOperation mapOperation) {
        Quotable mapArguments = mapOperation.getMapArguments();

        return mapArguments.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMapUpdateArguments mapUpdateArguments) {
        PsiElement[] children = mapUpdateArguments.getChildren();

        assert children.length >= 3;

        Quotable currentMap = (Quotable) children[0];
        OtpErlangObject quotedCurrentMap = currentMap.quote();

        Operator pipeOperator = (Operator) children[1];
        OtpErlangObject quotedPipeOperator = pipeOperator.quote();

        List<OtpErlangObject> quotedRightOperandList = new ArrayList<OtpErlangObject>();

        for (int i = 2; i < children.length; i++) {
            Quotable child = (Quotable) children[i];
            OtpErlangObject quotedChild = child.quote();

            if (quotedChild instanceof OtpErlangList) {
                OtpErlangList quotedList = (OtpErlangList) quotedChild;

                for (OtpErlangObject quotedElement : quotedList.elements()) {
                    quotedRightOperandList.add(quotedElement);
                }
            } else {
                quotedRightOperandList.add(quotedChild);
            }
        }

        OtpErlangObject[] quotedRightOperands = new OtpErlangObject[quotedRightOperandList.size()];
        quotedRightOperands = quotedRightOperandList.toArray(quotedRightOperands);
        OtpErlangObject quotedMapUpdates = new OtpErlangList(quotedRightOperands);

        return quotedFunctionCall(
                quotedPipeOperator,
                metadata(pipeOperator),
                quotedCurrentMap,
                quotedMapUpdates
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final AtUnqualifiedBracketOperation atUnqualifiedBracketOperation) {
        Quotable operator = atUnqualifiedBracketOperation.getAtPrefixOperator();
        OtpErlangObject quotedOperator = operator.quote();

        ASTNode node = atUnqualifiedBracketOperation.getNode();
        ASTNode[] identifierNodes = node.getChildren(IDENTIFIER_TOKEN_SET);

        assert identifierNodes.length == 1;

        ASTNode identifierNode = identifierNodes[0];
        String identifier = identifierNode.getText();
        OtpErlangList metadata = metadata(atUnqualifiedBracketOperation);

        OtpErlangObject quotedOperand = quotedVariable(identifier, metadata);
        OtpErlangTuple quotedContainer = quotedFunctionCall(quotedOperator, metadata, quotedOperand);

        Quotable bracketArguments = atUnqualifiedBracketOperation.getBracketArguments();
        OtpErlangObject quotedBracketArguments = bracketArguments.quote();

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        Quotable operator = atUnqualifiedNoParenthesesCall.getAtPrefixOperator();
        OtpErlangObject quotedOperator = operator.quote();

        ASTNode node = atUnqualifiedNoParenthesesCall.getNode();
        ASTNode[] identifierNodes = node.getChildren(IDENTIFIER_TOKEN_SET);

        assert identifierNodes.length == 1;

        ASTNode identifierNode = identifierNodes[0];
        String identifier = identifierNode.getText();
        OtpErlangObject quotedIdentifier = new OtpErlangAtom(identifier);

        QuotableArguments noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        OtpErlangObject[] quotedArguments = noParenthesesOneArgument.quoteArguments();
        ElixirDoBlock doBlock = atUnqualifiedNoParenthesesCall.getDoBlock();

        OtpErlangObject quotedOperand =  quotedBlockCall(
                quotedIdentifier,
                metadata(operator),
                quotedArguments,
                doBlock
        );

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedOperand
        );
    }

    @NotNull
    public static OtpErlangObject quote(@NotNull final BracketOperation bracketOperation) {
        PsiElement[] children = bracketOperation.getChildren();

        assert children.length == 2;

        Quotable matchedExpression = (Quotable) children[0];
        OtpErlangObject quotedMatchedExpression = matchedExpression.quote();
        Quotable bracketArguments = (Quotable) children[1];
        OtpErlangObject quotedBracketArguments = bracketArguments.quote();

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedMatchedExpression,
                quotedBracketArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final DotCall dotCall) {
        Quotable leftOperand = (Quotable) dotCall.getFirstChild();
        OtpErlangObject quotedLeftOperand = leftOperand.quote();

        Quotable operator = dotCall.getDotInfixOperator();
        OtpErlangObject quotedOperator = operator.quote();
        OtpErlangList operatorMetadata = metadata(operator);

        OtpErlangTuple quotedIdentifier = quotedFunctionCall(
                quotedOperator,
                operatorMetadata,
                quotedLeftOperand
        );

        List<ElixirParenthesesArguments> parenthesesArgumentsList = dotCall.getParenthesesArgumentsList();
        ElixirDoBlock doBlock = dotCall.getDoBlock();

        return quotedParenthesesCall(
                quotedIdentifier,
                operatorMetadata,
                parenthesesArgumentsList,
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final InOperation inOperation) {
        PsiElement[] children = inOperation.getChildren();

        if (children.length != 3) {
            throw new NotImplementedException("BinaryOperation expected to have 3 children (left operand, operator, right operand");
        }

        Quotable leftOperand = (Quotable) children[0];
        OtpErlangObject quotedLeftOperand = leftOperand.quote();

        Quotable operator = (Quotable) children[1];
        OtpErlangObject quotedOperator = operator.quote();

        Quotable rightOperand = (Quotable) children[2];
        OtpErlangObject quotedRightOperand = rightOperand.quote();

        OtpErlangObject quoted = null;

        // @see https://github.com/elixir-lang/elixir/blob/6c288be7300509ff7b809002a3563c6a02dc13fa/lib/elixir/src/elixir_parser.yrl#L596-L597
        // @see https://github.com/elixir-lang/elixir/blob/6c288be7300509ff7b809002a3563c6a02dc13fa/lib/elixir/src/elixir_parser.yrl#L583
        if (Macro.isExpression(quotedLeftOperand)) {
            OtpErlangTuple leftExpression = (OtpErlangTuple) quotedLeftOperand;
            OtpErlangObject leftOperator = leftExpression.elementAt(0);

            for (OtpErlangAtom rearrangedUnaryOperator : REARRANGED_UNARY_OPERATORS) {
                /* build_op({_Kind, Line, 'in'}, {UOp, _, [Left]}, Right) when ?rearrange_uop(UOp) ->
                     {UOp, meta(Line), [{'in', meta(Line), [Left, Right]}]}; */
                if (leftOperator.equals(rearrangedUnaryOperator)) {
                    OtpErlangObject unaryOperatorArguments = leftExpression.elementAt(2);
                    OtpErlangObject originalUnaryOperand;


                    if (unaryOperatorArguments instanceof OtpErlangString) {
                        OtpErlangString unaryOperatorPrintableArguments = (OtpErlangString) unaryOperatorArguments;
                        int codePoint = unaryOperatorPrintableArguments.stringValue().codePointAt(0);
                        originalUnaryOperand = new OtpErlangLong(codePoint);
                    } else if (unaryOperatorArguments instanceof OtpErlangList) {
                        OtpErlangList unaryOperatorArgumentList = (OtpErlangList) unaryOperatorArguments;
                        originalUnaryOperand = unaryOperatorArgumentList.elementAt(0);
                    } else {
                        throw new NotImplementedException("Expected REARRANGED_UNARY_OPERATORS operand to be quoted as an OtpErlangString or OtpErlangList");
                    }

                    OtpErlangList operatorMetadata = metadata(operator);

                    quoted = quotedFunctionCall(
                            leftOperator,
                            operatorMetadata,
                            quotedFunctionCall(
                                    quotedOperator,
                                    operatorMetadata,
                                    originalUnaryOperand,
                                    quotedRightOperand
                            )
                    );
                }
            }
        }
if (quoted == null) {
            quoted = quotedFunctionCall(
                    quotedOperator,
                    metadata(operator),
                    quotedLeftOperand,
                    quotedRightOperand
            );
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QualifiedAlias qualifiedAlias) {
        PsiElement[] children = qualifiedAlias.getChildren();

        assert children.length == 3;

        Quotable alias = (Quotable) children[2];
        OtpErlangTuple quotedAlias = (OtpErlangTuple) alias.quote();

        Quotable matchedExpression = (Quotable) children[0];
        OtpErlangObject quotedMatchedExpression = matchedExpression.quote();

        /*
         * Use line from last alias, but drop `counter: 0`
         */
        OtpErlangList aliasMetadata = Macro.metadata(quotedAlias);
        OtpErlangTuple lineTuple = (OtpErlangTuple) org.elixir_lang.List.keyfind(
                aliasMetadata,
                new OtpErlangAtom("line"),
                0
        );
        OtpErlangList qualifiedAliasMetdata = new OtpErlangList(
                new OtpErlangObject[] {
                        lineTuple
                }
        );

        OtpErlangList lastAliasList = Macro.callArguments(quotedAlias);
        OtpErlangObject[] mergedArguments;
        int i = 0;

        /* if both aliases, then the counter: 0 needs to be removed from the metadata data and the arguments for
           each __aliases__ need to be combined */
        if (Macro.isAliases(quotedMatchedExpression)) {
            OtpErlangList firstAliasList = Macro.callArguments((OtpErlangTuple) quotedMatchedExpression);
            mergedArguments = new OtpErlangObject[firstAliasList.arity() + lastAliasList.arity()];

            for (OtpErlangObject firstAliasElement : firstAliasList) {
                mergedArguments[i++] = firstAliasElement;
            }
        } else {
            mergedArguments = new OtpErlangObject[1 + lastAliasList.arity()];

            mergedArguments[i++] = quotedMatchedExpression;
        }

        for (OtpErlangObject lastAliasElement : lastAliasList) {
            mergedArguments[i++] = lastAliasElement;
        }

        return quotedFunctionCall(
                ALIASES,
                qualifiedAliasMetdata,
                mergedArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QualifiedBracketOperation qualifiedBracketOperation) {
        Quotable matchedExpression = (Quotable) qualifiedBracketOperation.getFirstChild();
        OtpErlangObject quotedIdentifier = matchedExpression.quote();

        ElixirRelativeIdentifier relativeIdentifier = qualifiedBracketOperation.getRelativeIdentifier();
        OtpErlangObject quotedRelativeIdentifier = relativeIdentifier.quote();

        quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedIdentifier,
                quotedRelativeIdentifier
        );

        OtpErlangList callMetadata = Macro.metadata((OtpErlangTuple) quotedIdentifier);

        OtpErlangObject quotedContainer = quotedFunctionCall(
                quotedIdentifier,
                callMetadata
        );

        Quotable bracketArguments = qualifiedBracketOperation.getBracketArguments();
        OtpErlangObject quotedBracketArguments = bracketArguments.quote();

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QualifiedNoArgumentsCall qualifiedNoArgumentsCall) {
        Quotable qualifier = (Quotable) qualifiedNoArgumentsCall.getFirstChild();
        OtpErlangObject quotedQualifier = qualifier.quote();

        Quotable relativeIdentifier = qualifiedNoArgumentsCall.getRelativeIdentifier();
        OtpErlangObject quotedRelativeIdentifier = relativeIdentifier.quote();

        OtpErlangTuple quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedQualifier,
                quotedRelativeIdentifier
        );

        ElixirDoBlock doBlock = qualifiedNoArgumentsCall.getDoBlock();

        return quotedBlockCall(
                quotedIdentifier,
                metadata(relativeIdentifier),
                new OtpErlangObject[0],
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QualifiedNoParenthesesCall qualifiedNoParenthesesCall) {
        Quotable qualifier = (Quotable) qualifiedNoParenthesesCall.getFirstChild();
        OtpErlangObject quotedQualifier = qualifier.quote();

        Quotable relativeIdentifier = qualifiedNoParenthesesCall.getRelativeIdentifier();
        OtpErlangObject quotedRelativeIdentifier = relativeIdentifier.quote();

        OtpErlangTuple quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedQualifier,
                quotedRelativeIdentifier
        );

        ElixirNoParenthesesOneArgument noParenthesesOneArgument = qualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        OtpErlangObject[] quotedArguments = noParenthesesOneArgument.quoteArguments();
        ElixirDoBlock doBlock = qualifiedNoParenthesesCall.getDoBlock();

        return quotedBlockCall(
                quotedIdentifier,
                metadata(relativeIdentifier),
                quotedArguments,
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final QualifiedParenthesesCall qualifiedParenthesesCall) {
        Quotable qualifier = (Quotable) qualifiedParenthesesCall.getFirstChild();
        OtpErlangObject quotedQualifier = qualifier.quote();

        Quotable relativeIdentifier = qualifiedParenthesesCall.getRelativeIdentifier();
        OtpErlangObject quotedRelativeIdentifier = relativeIdentifier.quote();

        OtpErlangList metadata = metadata(relativeIdentifier);
        OtpErlangObject quotedIdentifier = quotedFunctionCall(
                ".",
                metadata,
                quotedQualifier,
                quotedRelativeIdentifier
        );

        ElixirMatchedParenthesesArguments matchedParenthesesArguments = qualifiedParenthesesCall.getMatchedParenthesesArguments();
        List<ElixirParenthesesArguments> parenthesesArgumentsList = matchedParenthesesArguments.getParenthesesArgumentsList();
        ElixirDoBlock doBlock = qualifiedParenthesesCall.getDoBlock();

        return quotedParenthesesCall(
                quotedIdentifier,
                metadata,
                parenthesesArgumentsList,
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedBracketOperation unqualifiedBracketOperation) {
        ASTNode node = unqualifiedBracketOperation.getNode();
        OtpErlangObject quotedIdentifier = new OtpErlangAtom(node.getFirstChildNode().getText());
        OtpErlangObject quotedContainer = quotedVariable(quotedIdentifier, metadata(unqualifiedBracketOperation));

        Quotable bracketArguments = unqualifiedBracketOperation.getBracketArguments();
        OtpErlangObject quotedBrackArguments = bracketArguments.quote();

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBrackArguments
        );
    }

    /* Replaces `nil` argument in variables with the quoted ElixirMatchdNotParenthesesArguments.
     *
     */
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        String identifier = unqualifiedNoParenthesesCall.functionName();
        OtpErlangObject quotedIdentifer = new OtpErlangAtom(identifier);

        ElixirNoParenthesesOneArgument noParenthesesOneArgument = unqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        OtpErlangObject[] quotedArguments = noParenthesesOneArgument.quoteArguments();

        OtpErlangList blockCallMetadata = metadata(unqualifiedNoParenthesesCall);

        // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b//lib/elixir/src/elixir_parser.yrl#L627-L628
        if (quotedArguments.length == 1) {
            OtpErlangObject quotedArgument = quotedArguments[0];

            if (Macro.isExpression(quotedArgument)) {
                OtpErlangTuple expression = (OtpErlangTuple) quotedArgument;

                OtpErlangObject receiver = expression.elementAt(0);

                if (receiver.equals(MINUS) || receiver.equals(PLUS)) {
                    OtpErlangList dualCallArguments = Macro.callArguments(expression);

                    // [Arg]
                    if (dualCallArguments.arity() == 1) {
                        /* @note getChildren[0] is NOT the same as getFirstChild().  getFirstChild() will get the
                             leaf node for identifier instead of the first compound, rule node for the argument. */
                        PsiElement argument = unqualifiedNoParenthesesCall.getChildren()[0].getFirstChild();

                        if (!(argument instanceof ElixirAccessExpression && argument.getFirstChild() instanceof ElixirParentheticalStab)) {
                            blockCallMetadata = new OtpErlangList(
                                    new OtpErlangObject[]{
                                            AMBIGUOUS_OP_KEYWORD_PAIR,
                                            blockCallMetadata.elementAt(0)
                                    }
                            );
                        }
                    }
                }
            }
        }

        ElixirDoBlock doBlock = unqualifiedNoParenthesesCall.getDoBlock();

        return quotedBlockCall(
                quotedIdentifer,
                blockCallMetadata,
                quotedArguments,
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        ElixirDoBlock doBlock = unqualifiedNoArgumentsCall.getDoBlock();
        OtpErlangObject quoted;
        ASTNode node = unqualifiedNoArgumentsCall.getNode();
        ASTNode[] identifierNodes = node.getChildren(IDENTIFIER_TOKEN_SET);

        assert identifierNodes.length == 1;

        ASTNode identifierNode = identifierNodes[0];
        String identifier = identifierNode.getText();
        OtpErlangList callMetadata = metadata(identifierNode);

        // if a variable has a `do` block is no longer a variable because the do block acts as keyword arguments.
        if (doBlock != null) {
            OtpErlangObject[] quotedBlockArguments = doBlock.quoteArguments();

            quoted = quotedFunctionCall(
                    identifier,
                    callMetadata,
                    quotedBlockArguments
            );
        } else {
            /* @note quotedFunctionCall cannot be used here because in the 3-tuple for function calls, the elements are
              {name, metadata, arguments}, while for an ambiguous call or variable, the elements are
              {name, metadata, context}.  Importantly, context is nil when there is no context while arguments are []
              when there are no arguments. */
            quoted = quotedVariable(
                    identifier,
                    callMetadata
            );
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final UnqualifiedParenthesesCall unqualifiedParenthesesCall) {
        ASTNode identifierNode = unqualifiedParenthesesCall.getNode().getFirstChildNode();
        OtpErlangList metadata = metadata(unqualifiedParenthesesCall);
        OtpErlangObject quotedIdentifier = new OtpErlangAtom(identifierNode.getText());

        ElixirMatchedParenthesesArguments matchedParenthesesArguments = unqualifiedParenthesesCall.getMatchedParenthesesArguments();
        List<ElixirParenthesesArguments> parenthesesArgumentsList = matchedParenthesesArguments.getParenthesesArgumentsList();
        ElixirDoBlock doBlock = unqualifiedParenthesesCall.getDoBlock();

        return quotedParenthesesCall(
                quotedIdentifier,
                metadata,
                parenthesesArgumentsList,
                doBlock
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirNoParenthesesExpression noParenthesesExpression) {
        PsiElement[] children = noParenthesesExpression.getChildren();
        OtpErlangObject quoted;

        if (children.length != 1) {
            throw new NotImplementedException("noParenthesesExpression expected to only have one child");
        }

        PsiElement child = children[0];
        Quotable quotable;

        if (child instanceof Quotable) {
            quotable = (Quotable) child;
        } else if (child instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression) {
            ElixirNoParenthesesManyStrictNoParenthesesExpression noParenthesesManyStrictNoParenthesesExpression = (ElixirNoParenthesesManyStrictNoParenthesesExpression) child;
            quotable = noParenthesesManyStrictNoParenthesesExpression.getUnqualifiedNoParenthesesManyArgumentsCall();
        } else {
            throw new NotImplementedException("Expected either Quotable or ");
        }

        return quotable.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirNoParenthesesFirstPositional noParenthesesFirstPositional) {
        PsiElement[] children = noParenthesesFirstPositional.getChildren();

        if (children.length != 1) {
            throw new NotImplementedException("noParenthesesFirstPositional expected to only have one child");
        }

        return ((Quotable) children[0]).quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier noParenthesesManyArgumentsUnqualifiedIdentifier) {
        return new OtpErlangAtom(noParenthesesManyArgumentsUnqualifiedIdentifier.getText());
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirParentheticalStab parentheticalStab) {
        Quotable stab = parentheticalStab.getStab();
        OtpErlangObject quoted;

        if (stab != null) {
            quoted = stab.quote();
        } else {
            // @note CANNOT use quotedFunctionCall because it requires metadata and gives nil instead of [] when no
            //   arguments are given while empty block is quoted as `{__block__, [], []}`
            quoted = new OtpErlangTuple(
                    new OtpErlangObject[]{
                            BLOCK,
                            new OtpErlangList(),
                            new OtpErlangList()
                    }
            );
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirVariable variable) {
        /* @note quotedFunctionCall cannot be used here because in the 3-tuple for function calls, the elements are
           {name, metadata, arguments}, while for an ambiguous call or variable, the elements are
           {name, metadata, context}.  Importantly, context is nil when there is no context while arguments are [] when
           there are no arguments. */
        return quotedVariable(variable);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(Operator operator) {
        ASTNode operatorTokenNode = operatorTokenNode(operator);

        return new OtpErlangAtom(operatorTokenNode.getText());
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PrefixOperation prefixOperation) {
        PsiElement[] children = prefixOperation.getChildren();

        if (children.length != 2) {
            throw new NotImplementedException("PrefixOperation expected to have 2 children (operator and operand");
        }

        Quotable operator = (Quotable) children[0];
        OtpErlangObject quotedOperator = operator.quote();

        Quotable operand = (Quotable) children[1];
        OtpErlangObject quotedOperand = operand.quote();

        return quotedFunctionCall(
                quotedOperator,
                metadata(prefixOperation),
                quotedOperand
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PsiElement[] children) {
        List<Quotable> quotableList = new LinkedList<Quotable>();

        for (int i = 0; i < children.length; i++) {
            PsiElement child = children[i];

            if (child instanceof Quotable) {
                quotableList.add((Quotable) child);
            } else if (child instanceof Unquoted) {
                continue;
            } else {
                throw new NotImplementedException("Child, " + child + ", must be Quotable or Unquoted");
            }
        }

        Quotable[] quotableChildren = new Quotable[quotableList.size()];
        quotableList.toArray(quotableChildren);

        return quote(quotableChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PsiFile file) {
        final FileViewProvider fileViewProvider = file.getViewProvider();
        final ElixirFile root = (ElixirFile) fileViewProvider.getPsi(ElixirLanguage.INSTANCE);

        return ElixirPsiImplUtil.quote(root);
    }

    @Contract(pure = true)
    @NotNull
    private static OtpErlangObject quote(Quotable[] children) {
        Deque<OtpErlangObject> quotedChildren = new ArrayDeque<OtpErlangObject>(children.length);

        for (int i = 0; i < children.length; i++) {
            quotedChildren.add(children[i].quote());
        }

        // Uses toBlock because this is for inside interpolation, which functions the same as an embedded file
        return toBlock(quotedChildren);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        OtpErlangObject quotedIdentifier = unqualifiedNoParenthesesManyArgumentsCall.quoteIdentifier();
        OtpErlangObject[] quotedArguments = unqualifiedNoParenthesesManyArgumentsCall.quoteArguments();
        return anchoredQuotedFunctionCall(unqualifiedNoParenthesesManyArgumentsCall, quotedIdentifier, quotedArguments);
    }

    @NotNull
    public static OtpErlangObject anchoredQuotedFunctionCall(PsiElement anchor, OtpErlangObject quotedIdentifier, OtpErlangObject... quotedArguments) {
        OtpErlangList metadata;

        if (Macro.isExpression(quotedIdentifier)) {
            OtpErlangTuple expression = (OtpErlangTuple) quotedIdentifier;
            /* Grab metadata from quotedIdentifier so line of quotedFunctionCall is line of identifier or `.` in
               identifier, which can differ from the line of quotable call when there are newlines on either side of
               `.`. */
            metadata = Macro.metadata(expression);
        } else {
            metadata = metadata(anchor);
        }

        return quotedFunctionCall(
                quotedIdentifier,
                metadata,
                quotedArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(SigilHeredoc sigilHeredoc) {
        OtpErlangObject quotedHeredoc = quote((Heredoc) sigilHeredoc);

        return quote(sigilHeredoc, quotedHeredoc);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(SigilLine sigilLine) {
        Body body = sigilLine.getBody();
        ASTNode[] bodyChildNodes = childNodes(body);
        OtpErlangObject quotedBody = quotedChildNodes(sigilLine, bodyChildNodes);

        return quote(sigilLine, quotedBody);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirBlockList blockList) {
        List<ElixirBlockItem> blockItemList = blockList.getBlockItemList();
        OtpErlangObject[] quotedBlockItems = new OtpErlangObject[blockItemList.size()];

        int i = 0;
        for (ElixirBlockItem blockItem : blockItemList) {
            quotedBlockItems[i++] = blockItem.quote();
        }

        return quotedBlockItems;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        return unqualifiedNoParenthesesManyArgumentsCall.getArguments().quoteArguments();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirDoBlock doBlock) {
        ElixirStab stab = doBlock.getStab();
        OtpErlangObject doValue = NIL;
        OtpErlangObject[] quotedKeywordPairs;

        if (stab != null) {
            doValue = stab.quote();
        }

        OtpErlangTuple quotedDoKeywordPair = new OtpErlangTuple(
                new OtpErlangObject[]{
                        DO,
                        doValue
                }
        );

        ElixirBlockList blockList = doBlock.getBlockList();

        if (blockList != null) {
            OtpErlangObject[] blockListQuotedArguments = blockList.quoteArguments();

            quotedKeywordPairs = new OtpErlangObject[1 + blockListQuotedArguments.length];

            int i = 0;
            quotedKeywordPairs[i++] = quotedDoKeywordPair;
            System.arraycopy(blockListQuotedArguments, 0, quotedKeywordPairs, i, blockListQuotedArguments.length);
        } else {
            quotedKeywordPairs = new OtpErlangObject[]{
                    quotedDoKeywordPair
            };
        }

        return new OtpErlangObject[]{
                new OtpErlangList(quotedKeywordPairs)
        };
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirMapConstructionArguments mapConstructionArguments) {
        PsiElement[] arguments = mapConstructionArguments.arguments();
        List<OtpErlangObject> quotedArgumentList = new ArrayList<OtpErlangObject>();

        for (PsiElement argument : arguments) {
            Quotable quotableArgument = (Quotable) argument;
            OtpErlangList quotedArgument = (OtpErlangList) quotableArgument.quote();

            for (OtpErlangObject element : quotedArgument.elements()) {
                quotedArgumentList.add(element);
            }
        }

        OtpErlangObject[] quotedArguments = new OtpErlangObject[quotedArgumentList.size()];

        return quotedArgumentList.toArray(quotedArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirNoParenthesesArguments noParenthesesArguments) {
        PsiElement[] children = noParenthesesArguments.getChildren();

        assert children.length == 1;

        QuotableArguments quotableArguments = (QuotableArguments) children[0];
        return quotableArguments.quoteArguments();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirNoParenthesesManyArguments noParenthesesManyArguments) {
        QuotableArguments quotableArguments;

        quotableArguments = noParenthesesManyArguments.getNoParenthesesOnePositionalAndKeywordsArguments();

        if (quotableArguments == null) {
            quotableArguments = noParenthesesManyArguments.getNoParenthesesManyPositionalAndMaybeKeywordsArguments();
        }

        return quotableArguments.quoteArguments();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments noParenthesesManyPositionalAndMaybeKeywordsArguments) {
        PsiElement[] children = noParenthesesManyPositionalAndMaybeKeywordsArguments.getChildren();
        OtpErlangObject[] quotedChildren = new OtpErlangObject[children.length];

        int i = 0;
        for (PsiElement child : children) {
            if (child instanceof Quotable) {
                Quotable quotable = (Quotable) child;
                quotedChildren[i++] = quotable.quote();
            } else {
                throw new NotImplementedException("Expected all children to be Quotable");
            }
        }

        return quotedChildren;
    }


    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirNoParenthesesOneArgument noParenthesesOneArgument) {
        PsiElement[] noParenthesesOneArgumentChildren = noParenthesesOneArgument.getChildren();

        assert noParenthesesOneArgumentChildren.length == 1;

        PsiElement noParenthesesOneArgumentChild = noParenthesesOneArgumentChildren[0];
        OtpErlangObject[] quotedArguments;

        if (noParenthesesOneArgumentChild instanceof Quotable) {
            Quotable quotable = (Quotable) noParenthesesOneArgumentChild;
            quotedArguments = new OtpErlangObject[]{
                    quotable.quote()
            };
        } else {
            QuotableArguments quotableArguments = (QuotableArguments) noParenthesesOneArgumentChild;
            quotedArguments = quotableArguments.quoteArguments();
        }

        return quotedArguments;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirNoParenthesesOnePositionalAndKeywordsArguments noParenthesesOnePositionalAndKeywordsArguments) {
        Quotable noParenthesesFirstPositional = noParenthesesOnePositionalAndKeywordsArguments.getNoParenthesesFirstPositional();
        OtpErlangObject quotedFirstArgument = noParenthesesFirstPositional.quote();

        Quotable noParenthesesKeywords = noParenthesesOnePositionalAndKeywordsArguments.getNoParenthesesKeywords();
        OtpErlangObject quotedKeywordArguments = noParenthesesKeywords.quote();

        return new OtpErlangObject[]{
                quotedFirstArgument,
                quotedKeywordArguments
        };
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirNoParenthesesStrict noParenthesesStrict) {
        OtpErlangObject[] quotedArguments = null;

        PsiElement[] children = noParenthesesStrict.getChildren();

        if (children.length == 1) {
            PsiElement child = children[0];

            if (child instanceof QuotableArguments) {
                QuotableArguments quotableArguments = (QuotableArguments) child;

                quotedArguments = quotableArguments.quoteArguments();
            }
        }

        if (quotedArguments == null) {
            quotedArguments = new OtpErlangObject[children.length];

            for (int i = 0; i < children.length; i++) {
                Quotable quotable = (Quotable) children[i];
                quotedArguments[i] = quotable.quote();
            }
        }

        return quotedArguments;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirParenthesesArguments parenthesesArguments) {
        PsiElement[] arguments = parenthesesArguments.arguments();

        OtpErlangObject[] quotedArguments = new OtpErlangObject[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            Quotable quotableChild = (Quotable) arguments[i];
            OtpErlangObject quotedChild = quotableChild.quote();
            quotedArguments[i] = quotedChild;
        }

        return quotedArguments;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(InterpolatedCharList interpolatedCharList, OtpErlangTuple binary) {
        return quotedFunctionCall(
                "Elixir.String",
                "to_char_list",
                metadata(interpolatedCharList),
                binary
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(@SuppressWarnings("unused") InterpolatedString interpolatedString, OtpErlangTuple binary) {
        return binary;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(@SuppressWarnings("unused") Sigil sigil, OtpErlangTuple binary) {
        return binary;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") InterpolatedCharList interpolatedCharList) {
        return new OtpErlangList();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") InterpolatedString interpolatedString) {
        return elixirString("");
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") Sigil sigil) {
        return elixirString("");
    }

    public static OtpErlangObject quoteIdentifier(@NotNull ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        Quotable identifier = unqualifiedNoParenthesesManyArgumentsCall.getIdentifier();
        OtpErlangObject quotedIdentifier;

        if (identifier instanceof ElixirVariable) {
            quotedIdentifier = new OtpErlangAtom(identifier.getText());
        } else {
            quotedIdentifier = identifier.quote();
        }
        return quotedIdentifier;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") InterpolatedCharList interpolatedCharList, List<Integer> codePointList) {
        return elixirCharList(codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") InterpolatedString interpolatedString, List<Integer> codePointList) {
        return elixirString(codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") Sigil sigil, List<Integer> codePointList) {
        return elixirString(codePointList);
    }

    @NotNull
    public static OtpErlangObject quotedFunctionArguments(final OtpErlangObject... arguments) {
        /*
         * Erlang will automatically stringify a list that is just a list of LATIN-1 printable code
         * points.
         * OtpErlangString and OtpErlangList are not equal when they have the same content, so to check against
         * Elixir.Code.string_to_quoted, this code must determine if Erlang would return an OtpErlangString instead
         * of OtpErlangList and do the same.
         */
        return elixirCharList(
                new OtpErlangList(arguments)
        );
    }

    @NotNull
    public static OtpErlangTuple quotedFunctionCall(final String identifier, final OtpErlangList metadata, final OtpErlangObject... arguments) {
        return quotedFunctionCall(
                new OtpErlangAtom(identifier),
                metadata,
                arguments
        );
    }

    @NotNull
    public static OtpErlangTuple quotedFunctionCall(final String module, final String identifier, final OtpErlangList metadata, final OtpErlangObject... arguments) {
        OtpErlangObject quotedQualifiedIdentifier = quotedFunctionCall(
                ".",
                metadata,
                new OtpErlangAtom(module),
                new OtpErlangAtom(identifier)
        );
        return quotedFunctionCall(
                quotedQualifiedIdentifier,
                metadata,
                arguments
        );
    }

    @Contract(pure = true)
    @NotNull
    private static OtpErlangObject quotedBlockCall(
            @NotNull OtpErlangObject quotedIdentifier,
            @NotNull final OtpErlangList callMetadata,
            @NotNull final OtpErlangObject[] quotedArguments,
            @Nullable final ElixirDoBlock doBlock) {
        OtpErlangObject[] quotedCombinedArguments = quotedArguments;

        if (doBlock != null) {
            OtpErlangObject[] quotedBlockArguments = doBlock.quoteArguments();
            quotedCombinedArguments = new OtpErlangObject[
                    quotedArguments.length + quotedBlockArguments.length
                    ];

            System.arraycopy(
                    quotedArguments,
                    0,
                    quotedCombinedArguments,
                    0,
                    quotedArguments.length
            );
            System.arraycopy(
                    quotedBlockArguments,
                    0,
                    quotedCombinedArguments,
                    quotedArguments.length,
                    quotedBlockArguments.length
            );
        }

        return quotedFunctionCall(
                quotedIdentifier,
                callMetadata,
                quotedCombinedArguments
        );
    }

    @Contract(pure = true)
    @NotNull
    private static OtpErlangObject quotedParenthesesCall(
            @NotNull OtpErlangObject quotedIdentifier,
            @NotNull final OtpErlangList identifierMetadata,
            @NotNull final List<ElixirParenthesesArguments> parenthesesArgumentsList,
            @Nullable final ElixirDoBlock doBlock) {
        OtpErlangObject quoted = null;

        int parenthesesArgumentsListSize = parenthesesArgumentsList.size();

        assert parenthesesArgumentsListSize > 0;

        int i = 0;
        for (ElixirParenthesesArguments parenthesesArguments : parenthesesArgumentsList) {
            OtpErlangObject[] quotedParenthesesArguments = parenthesesArguments.quoteArguments();

            if (i == parenthesesArgumentsListSize - 1) {
                quoted = quotedBlockCall(
                        quotedIdentifier,
                        identifierMetadata,
                        quotedParenthesesArguments,
                        doBlock
                );
            } else {
                quoted = quotedFunctionCall(
                        quotedIdentifier,
                        identifierMetadata,
                        quotedParenthesesArguments
                );
            }
            // function call is identifier for second call
            quotedIdentifier = quoted;

            i++;
        }

        return quoted;
    }

    @NotNull
    @Contract(pure = true)
    public static OtpErlangObject quotedEmpty(@SuppressWarnings("unused") ElixirCharListHeredoc charListHeredoc) {
        // an empty CharList is just an empty list
        return new OtpErlangList();
    }

    @NotNull
    public static OtpErlangTuple quotedFunctionCall(final OtpErlangObject quotedQualifiedIdentifier, final OtpErlangList metadata, final OtpErlangObject... arguments) {
        return new OtpErlangTuple(
                new OtpErlangObject[] {
                        quotedQualifiedIdentifier,
                        metadata,
                        quotedFunctionArguments(arguments)
                }
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quotedVariable(@NotNull final PsiElement variable) {
        return quotedVariable(
                variable.getText(),
                metadata(variable)
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quotedVariable(@NotNull final String identifier, @NotNull final OtpErlangList metadata) {
        return quotedVariable(
                new OtpErlangAtom(identifier),
                metadata
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quotedVariable(@NotNull final OtpErlangObject quotedIdentifier, @NotNull final OtpErlangList metadata) {
      return quotedVariable(
              quotedIdentifier,
              metadata,
              NIL
      );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quotedVariable(@NotNull final OtpErlangObject quotedIdentifier, @NotNull final OtpErlangList metadata, @NotNull final OtpErlangObject context) {
        return new OtpErlangTuple(
                new OtpErlangObject[] {
                        quotedIdentifier,
                        metadata,
                        context
                }
        );
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
     * Similar to {@link functionName}, but takes into account `import`s.
     *
     * @return
     */
    @Contract(pure = true)
    @NotNull
    public static String resolvedFunctionName(@NotNull final Call call) {
        // TODO handle `import`s
        return call.functionName();
    }

    /**
     * Similar to {@link functionName}, but takes into account `import`s.
     *
     * @return
     */
    @NotNull
    public static String resolvedFunctionName(@NotNull final UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        // TODO handle `import`s and determine whether actually local variable
        return unqualifiedNoArgumentsCall.functionName();
    }

    /**
     * Similar to {@link moduleName}, but takes into account `alias`es and `import`s.
     *
     * @param element
     * @param newName
     * @return
     */
    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String resolvedModuleName(@NotNull @SuppressWarnings("unused") final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        // TODO handle resolving module name from module attribute's declaration
        return null;
    }

    /**
     * Similar to {@link moduleName}, but takes into account `alias`es and `import`s.
     *
     * @param element
     * @param newName
     * @return
     */
    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static String resolvedModuleName(@NotNull @SuppressWarnings("unused") final DotCall dotCall) {
        // TODO handle resolving module name from any capture from variable declaration
        return null;
    }

    /**
     * Similar to {@link moduleName}, but takes into account `alias`es and `import`s.
     *
     * @param qualifiedNoArgumentsCall
     * @return
     */
    @NotNull
    public static String resolvedModuleName(@NotNull @SuppressWarnings("unused") final org.elixir_lang.psi.call.qualification.Qualified qualified) {
        // TODO handle `alias`es and `import`s
        String moduleName = qualified.moduleName();
        String resolvedModuleName = moduleName;

        if (!moduleName.startsWith("Elixir.")) {
            resolvedModuleName = "Elixir." + moduleName;
        }

        return resolvedModuleName;
    }

    /**
     * Similar to {@link moduleName}, but takes into account `alias`es and `import`s.
     *
     * @param element
     * @param newName
     * @return
     */
    @NotNull
    public static String resolvedModuleName(@NotNull @SuppressWarnings("unused") final Unqualified unqualified) {
        // TODO handle `import`s
        return "Elixir.Kernel";
    }

    /**
     * Similar to {@link moduleName}, but takes into account `alias`es and `import`s.
     *
     * @param element
     * @param newName
     * @return
     */
    @NotNull
    public static String resolvedModuleName(@NotNull @SuppressWarnings("unused") final UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        // TODO handle `import`s and determine whether actually a local variable
        return "Elixir.Kernel";
    }

    @Contract(pure = true)
    @NotNull
    public static Quotable rightOperand(InfixOperation infixOperation) {
        PsiElement[] children = infixOperation.getChildren();

        assert children.length == 3;

        return (Quotable) children[2];
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull final DotCall dotCall) {
        List<ElixirParenthesesArguments> parenthesesArgumentsList = dotCall.getParenthesesArgumentsList();
        PsiElement[] arguments;

        if (parenthesesArgumentsList.size() < 2) {
            arguments = null;
        } else {
            ElixirParenthesesArguments parenthesesArguments = parenthesesArgumentsList.get(1);
            arguments = parenthesesArguments.arguments();
        }

        return arguments;
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull @SuppressWarnings("unused") final None none) {
        return null;
    }

    @Contract(pure = true, value = "_ -> null")
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull @SuppressWarnings("unused") final NoParentheses noParentheses) {
        return null;
    }

    @Contract(pure = true)
    @Nullable
    public static PsiElement[] secondaryArguments(@NotNull final Parentheses parentheses) {
        ElixirMatchedParenthesesArguments matchedParenthesesArguments = parentheses.getMatchedParenthesesArguments();
        List<ElixirParenthesesArguments> parenthesesArgumentsList = matchedParenthesesArguments.getParenthesesArgumentsList();
        PsiElement[] arguments;

        if (parenthesesArgumentsList.size() < 2) {
            arguments = null;
        } else {
            ElixirParenthesesArguments parenthesesArguments = parenthesesArgumentsList.get(1);
            arguments = parenthesesArguments.arguments();
        }

        return arguments;
    }

    @NotNull
    public static PsiElement setName(@NotNull PsiElement element, @NotNull String newName) {
        return null;
    }

    public static char sigilName(@NotNull org.elixir_lang.psi.Sigil sigil) {
        ASTNode sigilNode = sigil.getNode();
        ASTNode[] childNodes = sigilNode.getChildren(null);
        ASTNode nameNode = childNodes[1];
        CharSequence chars = nameNode.getChars();
        return chars.charAt(0);
    }

    public static char terminator(@NotNull SigilLine sigilLine) {
        ASTNode node = sigilLine.getNode();
        ASTNode[] childNodes = node.getChildren(null);
        ASTNode terminatorNode = childNodes[4];
        CharSequence chars = terminatorNode.getChars();

        return chars.charAt(0);
    }

    @NotNull
    public static IElementType validElementType(@SuppressWarnings("unused") @NotNull ElixirBinaryDigits binaryDigits) {
        return ElixirTypes.VALID_BINARY_DIGITS;
    }

    @Contract(pure = true)
    @NotNull
    public static IElementType validElementType(@NotNull @SuppressWarnings("unused") ElixirDecimalDigits decimalDigits) {
        return ElixirTypes.VALID_DECIMAL_DIGITS;
    }

    @NotNull
    public static IElementType validElementType(@SuppressWarnings("unused") @NotNull ElixirHexadecimalDigits hexadecimalDigits) {
        return ElixirTypes.VALID_HEXADECIMAL_DIGITS;
    }

    @NotNull
    public static IElementType validElementType(@NotNull @SuppressWarnings("unused") ElixirOctalDigits octalDigits) {
        return ElixirTypes.VALID_OCTAL_DIGITS;
    }

    @Nullable
    public static IElementType validElementType(@NotNull @SuppressWarnings("unused") ElixirUnknownBaseDigits unknownBaseDigits) {
        return null;
    }

    /*
     * Private static methods
     */

    private static ASTNode[] childNodes(PsiElement parentElement) {
        ASTNode parentNode = parentElement.getNode();
        return parentNode.getChildren(null);
    }

    private static void addMergedFragments(@NotNull Queue<ASTNode> mergedNodes, IElementType fragmentType, StringBuilder fragmentStringBuilder, PsiManager manager) {
        if (fragmentStringBuilder != null) {
            ASTNode charListFragment = Factory.createSingleLeafElement(
                    fragmentType,
                    fragmentStringBuilder.toString(),
                    0,
                    fragmentStringBuilder.length(),
                    null,
                    manager
            );
            mergedNodes.add(charListFragment);
        }
    }

    @NotNull
    @Contract(pure = true)
    private static Queue<ASTNode> mergeFragments(@NotNull Deque<ASTNode> unmergedNodes, IElementType fragmentType, @NotNull PsiManager manager) {
        Queue<ASTNode> mergedNodes = new LinkedList<ASTNode>();
        StringBuilder fragmentStringBuilder = null;

        for (ASTNode unmergedNode : unmergedNodes) {
            if (unmergedNode.getElementType() == fragmentType) {
                if (fragmentStringBuilder == null) {
                    fragmentStringBuilder = new StringBuilder();
                }

                String fragment = unmergedNode.getText();
                fragmentStringBuilder.append(fragment);
            } else {
                addMergedFragments(mergedNodes, fragmentType, fragmentStringBuilder, manager);
                fragmentStringBuilder = null;
                mergedNodes.add(unmergedNode);
            }
        }

        addMergedFragments(mergedNodes, fragmentType, fragmentStringBuilder, manager);

        return mergedNodes;
    }

    @NotNull
    private static void queueChildNodes(@NotNull HeredocLine line, IElementType fragmentType, int prefixLength, @NotNull Queue<ASTNode> heredocDescendantNodes) {
        ElixirHeredocLinePrefix heredocLinePrefix = line.getHeredocLinePrefix();

        ASTNode excessWhitespace = heredocLinePrefix.excessWhitespace(fragmentType, prefixLength);

        if (excessWhitespace != null) {
            heredocDescendantNodes.add(excessWhitespace);
        }

        Body body = line.getBody();
        Collections.addAll(heredocDescendantNodes, childNodes(body));

        ASTNode eolNode = Factory.createSingleLeafElement(
                fragmentType,
                "\n",
                0,
                1,
                null,
                line.getManager()
        );
        heredocDescendantNodes.add(eolNode);
    }

    @Contract(pure = true)
    @NotNull
    private static OtpErlangObject quote(@NotNull Sigil sigil, @NotNull OtpErlangObject quotedContent) {
        char sigilName = sigil.sigilName();

        OtpErlangList sigilMetadata = metadata(sigil);
        OtpErlangTuple sigilBinary;

        if (quotedContent instanceof OtpErlangTuple) {
            sigilBinary = (OtpErlangTuple) quotedContent;
        } else {
            sigilBinary = quotedFunctionCall("<<>>", sigilMetadata, quotedContent);
        }

        ElixirSigilModifiers sigilModifiers = sigil.getSigilModifiers();
        OtpErlangObject quotedModifiers = sigilModifiers.quote();

        return quotedFunctionCall(
                "sigil_" + sigilName,
                sigilMetadata,
                sigilBinary,
                quotedModifiers
        );
    }

    @NotNull
    private static List<Integer> ensureCodePointList(@Nullable List<Integer> codePointList) {
        if (codePointList == null) {
            codePointList = new LinkedList<Integer>();
        }

        return codePointList;
    }

    @NotNull
    private static List<Integer> addChildTextCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        return addStringCodePoints(codePointList, child.getText());
    }

    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull @SuppressWarnings("unused") Quote parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        codePointList = ensureCodePointList(codePointList);

        ElixirEscapedCharacter escapedCharacter = (ElixirEscapedCharacter) child.getPsi();

        codePointList.add(
                escapedCharacter.codePoint()
        );

        return codePointList;
    }

    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull @SuppressWarnings("unused") Sigil parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        String childText = child.getText();

        // Not sure, why, but \ gets stripped in front of # when quoting using Quoter.
        if (childText.equals("\\#")) {
            childText = "#";
        } else if (parent instanceof SigilLine) {
            SigilLine sigilLine = (SigilLine) parent;

            char terminator = sigilLine.terminator();

            if (childText.equals("\\" + terminator)) {
                childText = new String(
                        new char[] {
                                terminator
                        }
                );
            }
        }

        return addStringCodePoints(codePointList, childText);
    }

    @NotNull
    public static List<Integer> addFragmentCodePoints(@NotNull @SuppressWarnings("unused") Parent parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        return addChildTextCodePoints(codePointList, child);
    }

    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull @SuppressWarnings("unused") Quote parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        codePointList = ensureCodePointList(codePointList);

        ElixirQuoteHexadecimalEscapeSequence hexadecimalEscapeSequence = (ElixirQuoteHexadecimalEscapeSequence) child.getPsi();

        codePointList.add(hexadecimalEscapeSequence.codePoint());

        return codePointList;
    }

    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull @SuppressWarnings("unused") Sigil parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        return addChildTextCodePoints(codePointList, child);
    }

    @NotNull
    private static String filterEscapedEOL(String unfiltered) {
        return unfiltered.replace("\\\n", "");
    }

    @NotNull
    private static List<Integer> addStringCodePoints(@Nullable List<Integer> codePointList, @NotNull String string) {
        codePointList = ensureCodePointList(codePointList);
        String filteredString = filterEscapedEOL(string);

        for (Integer codePoint : codePoints(filteredString)) {
            codePointList.add(codePoint);
        }

        return codePointList;
    }

    @NotNull
    private static OtpErlangObject quotedChildNodes(@NotNull Parent parent, @NotNull ASTNode... children) {
        return quotedChildNodes(parent, metadata(parent), children);
    }

    private static OtpErlangObject quotedChildNodes(@NotNull Parent parent, @NotNull OtpErlangList metadata, @NotNull ASTNode... children) {
        OtpErlangObject quoted;

        final int childCount = children.length;

        if (childCount == 0) {
            quoted = parent.quoteEmpty();
        } else {
            List<OtpErlangObject> quotedParentList = new LinkedList<OtpErlangObject>();
            List<Integer> codePointList = null;

            for (ASTNode child : children) {
                IElementType elementType = child.getElementType();

                if (elementType == parent.getFragmentType()) {
                    codePointList = parent.addFragmentCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.ESCAPED_CHARACTER) {
                    codePointList = parent.addEscapedCharacterCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.ESCAPED_EOL) {
                    continue;
                } else if (elementType == ElixirTypes.HEXADECIMAL_ESCAPE_PREFIX) {
                    codePointList = addChildTextCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (codePointList != null) {
                        quotedParentList.add(elixirString(codePointList));
                        codePointList = null;
                    }

                    ElixirInterpolation childElement = (ElixirInterpolation) child.getPsi();
                    quotedParentList.add(childElement.quote());
                } else if (elementType == ElixirTypes.QUOTE_HEXADECIMAL_ESCAPE_SEQUENCE ||
                           elementType == ElixirTypes.SIGIL_HEXADECIMAL_ESCAPE_SEQUENCE) {
                    codePointList = parent.addHexadecimalEscapeSequenceCodePoints(codePointList, child);
                } else {
                    throw new NotImplementedException("Can't quote " + child);
                }
            }

            if (codePointList !=  null && quotedParentList.isEmpty()) {
                quoted = parent.quoteLiteral(codePointList);
            } else {
                if (codePointList != null) {
                    quotedParentList.add(elixirString(codePointList));
                }

                OtpErlangObject[] quotedStringElements = new OtpErlangObject[quotedParentList.size()];
                quotedParentList.toArray(quotedStringElements);

                OtpErlangTuple binaryConstruction = quotedFunctionCall("<<>>", metadata, quotedStringElements);
                quoted = parent.quoteBinary(binaryConstruction);
            }
        }

        return quoted;
    }

    private static OtpErlangObject quoteInBase(@NotNull String string, int base) {
        BigInteger parsedInteger = new BigInteger(string, base);

        return new OtpErlangLong(parsedInteger);
    }

    private static String compactDigits(List<Digits> digitsList) {
        StringBuilder builtString = new StringBuilder();

        for (Digits digits : digitsList) {
            builtString.append(digits.getText());
        }

        return builtString.toString();
    }

    /**
     * Unwraps <code>when</code> from left end of noParenthesesArguments in stabNoParenthesesSignature so that the when acts as a guard for the signature instead of a guard on the last positional argument.
     *
     * @param
     * @return
     * @see <a href="https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L276-L277"><code>unwrap_when</code> in <code>stab_expr</code></a>
     * @see <a href="https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L716-L722"><code>unwrap_when</code></a>
     */
    private static OtpErlangObject[] unwrapWhen(OtpErlangObject[] quotedArguments) {
        // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L717
        OtpErlangObject last = quotedArguments[quotedArguments.length - 1];
        // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L720-L721
        OtpErlangObject[] unwrapped = quotedArguments;

        // { _, _, _}
        if (Macro.isExpression(last)) {
            OtpErlangTuple expression = (OtpErlangTuple) last;
            OtpErlangObject receiver = expression.elementAt(0);

            // {'when', _, _ }
            if (receiver.equals(WHEN)) {
                OtpErlangObject operands = expression.elementAt(2);

                // is_list(End)
                if (operands instanceof OtpErlangList) {
                    OtpErlangList operandList = (OtpErlangList) operands;

                    // Have to check for two element so that unwrap_when doesn't happen recursively as the unwrapped version of when will have more than 2 arguments, which is only seen in stabSignatures.
                    // [_, _] = End
                    if (operandList.arity() == 2) {
                        OtpErlangObject[] unwrappedArguments = new OtpErlangObject[quotedArguments.length - 1 + 2];

                        // Start
                        System.arraycopy(quotedArguments, 0, unwrappedArguments, 0, quotedArguments.length - 1);
                        // ++ End
                        System.arraycopy(operandList.elements(), 0, unwrappedArguments, quotedArguments.length - 1, 2);

                        unwrapped = new OtpErlangObject[1];

                        unwrapped[0] = quotedFunctionCall(
                                receiver,
                                Macro.metadata(expression),
                                unwrappedArguments
                        );
                    }
                }
            }
        }

        return unwrapped;
    }
}
