package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.Macro;
import org.elixir_lang.psi.*;
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
    public static final OtpErlangAtom BLOCK = new OtpErlangAtom("__block__");
    public static final OtpErlangAtom NIL = new OtpErlangAtom("nil");
    public static final OtpErlangAtom UTF_8 = new OtpErlangAtom("utf8");
    public static final int BINARY_BASE = 2;
    public static final int DECIMAL_BASE = 10;
    public static final int HEXADECIMAL_BASE = 16;
    public static final int OCTAL_BASE = 8;
    // NOTE: Unknown is all bases not 2, 8, 10, or 16, but 36 is used because all digits and letters are parsed.
    public static final int UNKNOWN_BASE = 36;

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

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject block(@NotNull final Deque<OtpErlangObject> quotedChildren) {
        OtpErlangObject asBlock;
        final int size = quotedChildren.size();

        if (size == 0) {
            asBlock = NIL;
        } else if (size == 1) {
            asBlock = quotedChildren.getFirst();
        } else {
            OtpErlangObject[] quotedArray = new OtpErlangObject[size];
            OtpErlangList blockMetadata = new OtpErlangList();

            asBlock = quotedFunctionCall(
                    BLOCK,
                    blockMetadata,
                    quotedChildren.toArray(quotedArray)
            );
        }

        return asBlock;
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
    public static int codePoint(@NotNull ElixirHexadecimalEscapeSequence hexadecimalEscapeSequence) {
        EscapedHexadecimalDigits escapedHexadecimalDigits = hexadecimalEscapeSequence.getEnclosedHexadecimalEscapeSequence();
        int parsedCodePoint = -1;

        if (escapedHexadecimalDigits == null) {
            escapedHexadecimalDigits = hexadecimalEscapeSequence.getOpenHexadecimalEscapeSequence();
        }

        if (escapedHexadecimalDigits != null) {
            parsedCodePoint = escapedHexadecimalDigits.codePoint();
        }

        return parsedCodePoint;
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

    /* Returns the 0-indexed line number for the element */
    public static int lineNumber(PsiElement element) {
        int textOffset = element.getTextOffset();

        return document(element).getLineNumber(textOffset);
    }

    public static OtpErlangTuple lineNumberKeywordTuple(PsiElement element) {
        return keywordTuple(
                "line",
                // Elixir metadata lines are 1-indexed while getLineNumber is 0-indexed
                lineNumber(element) + 1
        );
    }

    public static OtpErlangList metadata(PsiElement element) {
        final OtpErlangObject[] keywordListElements = {
                lineNumberKeywordTuple(element)
        };

        return new OtpErlangList(keywordListElements);
    }

    /* TODO determine what counter means in Code.string_to_quoted("Foo")
       {:ok, {:__aliases__, [counter: 0, line: 1], [:Foo]}} */
    public static OtpErlangList metadata(PsiElement element, int counter) {
        /* KeywordList should be compared by sorting keys, but Elixir does counter first, so it's simpler to just use
           same order than detect a OtpErlangList is a KeywordList */
        final OtpErlangObject[] keywordListElements = {
                keywordTuple("counter", counter),
                lineNumberKeywordTuple(element)
        };

        return new OtpErlangList(keywordListElements);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirAdditionInfixOperator additionInfixOperator) {
        return TokenSet.create(ElixirTypes.DUAL_OPERATOR);
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
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirHatInfixOperator hatInfixOperator) {
        return TokenSet.create(ElixirTypes.HAT_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static TokenSet operatorTokenSet(@SuppressWarnings("unused") final ElixirUnaryPrefixOperator unaryPrefixOperator) {
        return TokenSet.create(ElixirTypes.DUAL_OPERATOR, ElixirTypes.UNARY_OPERATOR);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final InfixOperation infixOperation) {
        PsiElement[] children = infixOperation.getChildren();

        if (children.length != 3) {
            throw new NotImplementedException("BinaryOperation expected to have 3 children (left operand, operator, right operand");
        }

        Quotable leftOperand = (Quotable) children[0];
        OtpErlangObject quotedLeftOperand = leftOperand.quote();

        Quotable operator = (Quotable) children[1];
        OtpErlangObject quotedOperator = operator.quote();

        Quotable rightOperand = (Quotable) children[2];
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
    public static OtpErlangObject quote(@NotNull final Call call) {
        Quotable identifier = call.getIdentifier();
        OtpErlangObject quotedIdentifier;

        if (identifier instanceof ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariableImpl) {
            quotedIdentifier = new OtpErlangAtom(identifier.getText());
        } else {
            quotedIdentifier = identifier.quote();
        }

        QuotableArguments arguments = call.getArguments();
        OtpErlangObject[] quotedArguments = arguments.quoteArguments();

        return quotedFunctionCall(
                quotedIdentifier,
                metadata(call),
                quotedArguments
        );
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
    public static OtpErlangObject quote(@NotNull final ElixirAlias alias) {
        return quotedFunctionCall(
                ALIASES,
                metadata(alias, 0),
                new OtpErlangAtom(alias.getText())
        );
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
        ElixirInterpolatedCharListBody interpolatedCharListBody = charListLine.getInterpolatedCharListBody();

        return quotedChildNodes(charListLine, childNodes(interpolatedCharListBody));
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
    @NotNull
    public static QuotableArguments getArguments(@NotNull final ElixirMatchedCallOperation matchedCallOperation) {
        return matchedCallOperation.getNoParenthesesManyArguments();
    }

    @Contract(pure = true)
    @NotNull
    public static QuotableArguments getArguments(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
      return unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArguments();
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
        List<ElixirInterpolatedStringHeredocLine> stringHeredocLineList = stringHeredoc.getInterpolatedStringHeredocLineList();
        List<HeredocLine> heredocLineList = new ArrayList<HeredocLine>(stringHeredocLineList.size());

        for (HeredocLine heredocLine : stringHeredocLineList) {
            heredocLineList.add(heredocLine);
        }

        return heredocLineList;
    }

    @Contract(pure = true)
    @NotNull
    public static Quotable getIdentifier(@NotNull final ElixirMatchedCallOperation matchedCallOperation) {
        return (Quotable) matchedCallOperation.getFirstChild();
    }

    @Contract(pure = true)
    @NotNull
    public static Quotable getIdentifier(@NotNull final ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        return unqualifiedNoParenthesesManyArgumentsCall.getNoParenthesesManyArgumentsUnqualifiedIdentifier();
    }

    public static List<KeywordPair> getKeywordPairList(ElixirList list) {
        List<ElixirListKeywordPair> listKeywordPairList = list.getListKeywordPairList();
        List<KeywordPair> keywordPairList = new ArrayList<KeywordPair>(listKeywordPairList.size());

        keywordPairList.addAll(listKeywordPairList);

        return keywordPairList;
    }

    public static List<KeywordPair> getKeywordPairList(ElixirNoParenthesesKeywords noParenthesesKeywords) {
        return new ArrayList<KeywordPair>(noParenthesesKeywords.getNoParenthesesKeywordPairList());
    }

    public static Quotable getKeywordValue(ElixirNoParenthesesKeywordPair noParenthesesKeywordPair) {
        return noParenthesesKeywordPair.getNoParenthesesExpression();
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

    public static OtpErlangObject quote(@SuppressWarnings("unused") ElixirEmptyBlock emptyBlock) {
        // @note CANNOT use quotedFunctionCall because it requires metadata and gives nil instead of [] when no
        //   arguments are given while empty block is quoted as `{__block__, [], []}`
        return new OtpErlangTuple(
                new OtpErlangObject[]{
                        BLOCK,
                        new OtpErlangList(),
                        new OtpErlangList()
                }
        );
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

        return block(quotedChildren);
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
    public static OtpErlangObject quote(@NotNull final ElixirStringLine stringLine) {
        ElixirInterpolatedStringBody interpolatedStringBody = stringLine.getInterpolatedStringBody();
        return quotedChildNodes(stringLine, childNodes(interpolatedStringBody));
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
    public static OtpErlangObject quote(@NotNull final KeywordList keywordList) {
        List<KeywordPair> keywordPairList = keywordList.getKeywordPairList();
        List<OtpErlangObject> quotedKeywordPairList = new ArrayList<OtpErlangObject>(keywordPairList.size());

        for (KeywordPair keywordPair : keywordPairList) {
            quotedKeywordPairList.add(
                    keywordPair.quote()
            );
        }

        OtpErlangObject[] quotedKeywordPairs = new OtpErlangObject[quotedKeywordPairList.size()];
        quotedKeywordPairList.toArray(quotedKeywordPairs);

        return new OtpErlangList(quotedKeywordPairs);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final KeywordPair keywordPair) {
        Quotable keywordKey = keywordPair.getKeywordKey();
        OtpErlangObject quotedKeywordKey = keywordKey.quote();

        Quotable keywordValue = keywordPair.getKeywordValue();
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
    public static OtpErlangObject quote(@NotNull final ElixirKeywordValue keywordValue) {
        ElixirEmptyParentheses emptyParentheses = keywordValue.getEmptyParentheses();

        return emptyParentheses.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirMatchedDotOperation matchedDotOperation) {
        // get basic quoting of aliases, etc
        OtpErlangTuple quotedInfixOperation = (OtpErlangTuple) quote((InfixOperation) matchedDotOperation);
        // by default, no specialization
        OtpErlangObject specializedQuoted = quotedInfixOperation;

        OtpErlangList quotedArgumentList = Macro.callArguments((OtpErlangTuple) quotedInfixOperation);
        OtpErlangObject lastQuotedArgument = quotedArgumentList.elementAt(1);

        // The final arguments determines whether the whole thing is an alias, so check if first.
        if (Macro.isAliases(lastQuotedArgument)) {
            OtpErlangObject firstQuotedArgument = quotedArgumentList.elementAt(0);

            /* if both aliases, then the counter: 0 needs to be removed from the metadata data and the arguments for
               each __aliases__ need to be combined */
            if (Macro.isAliases(firstQuotedArgument)) {
                /*
                 * Use line from last alias, but drop `counter: 0`
                 */
                OtpErlangList lastMetadata = Macro.metadata((OtpErlangTuple) lastQuotedArgument);
                OtpErlangTuple lineTuple = (OtpErlangTuple) org.elixir_lang.List.keyfind(lastMetadata, new OtpErlangAtom("line"), 0);
                OtpErlangList newMetdata = new OtpErlangList(
                        new OtpErlangObject[] {
                                lineTuple
                        }
                );

                /*
                 * Merge alias names
                 */

                OtpErlangList firstAliasList = Macro.callArguments((OtpErlangTuple) firstQuotedArgument);
                OtpErlangList lastAliasList = Macro.callArguments((OtpErlangTuple) lastQuotedArgument);

                OtpErlangObject[] mergedArguments = new OtpErlangObject[firstAliasList.arity() + lastAliasList.arity()];

                int i = 0;
                for (OtpErlangObject alias : firstAliasList) {
                    mergedArguments[i++] = alias;
                }

                for (OtpErlangObject alias : lastAliasList) {
                    mergedArguments[i++] = alias;
                }

                specializedQuoted = quotedFunctionCall(
                        ALIASES,
                        newMetdata,
                        mergedArguments
                );
            }
        } else if (Macro.isLocalCall(lastQuotedArgument)) {
            OtpErlangTuple quotedLocalCall = (OtpErlangTuple) lastQuotedArgument;
            // lastQuotedArgument = {quotedIdentifier, callMetadata, arguments}
            OtpErlangObject quotedIdentifier = quotedLocalCall.elementAt(0);

            // quotedInfixOperation = {:., dotMetadata, [firstQuotedArgument, lastQuotedArgument]}
            OtpErlangObject firstQuotedArgument = quotedArgumentList.elementAt(0);

            // {:., dotMetadata, [firstQuotedArgument, quotedIdentifier]}
            final OtpErlangObject newMetadata = quotedInfixOperation.elementAt(1);
            OtpErlangTuple quotedRemoteFunction = new OtpErlangTuple(
                    new OtpErlangObject[] {
                            quotedInfixOperation.elementAt(0),
                            newMetadata,
                            new OtpErlangList(
                                    new OtpErlangObject[]{
                                            firstQuotedArgument,
                                            quotedIdentifier
                                    }
                            )
                    }
            );

            specializedQuoted = new OtpErlangTuple(
                    new OtpErlangObject[] {
                            quotedRemoteFunction,
                            newMetadata,
                            quotedLocalCall.elementAt(2)
                    }
            );
        } else if (Macro.isVariable(lastQuotedArgument)) {
            OtpErlangTuple lastExpression = (OtpErlangTuple) lastQuotedArgument;
            OtpErlangObject context = lastExpression.elementAt(2);

            // Variables are turned into remote calls and only the atom is used as the second argument to `.`.
            if (context.equals(NIL)) {
                OtpErlangObject identifier = lastExpression.elementAt(0);

                OtpErlangObject[] mergedArguments = new OtpErlangObject[]{
                        quotedArgumentList.elementAt(0),
                        identifier
                };
                OtpErlangList mergedArgumentList = new OtpErlangList(mergedArguments);

                OtpErlangTuple quotedRemoteFunction = new OtpErlangTuple(
                        new OtpErlangObject[]{
                                quotedInfixOperation.elementAt(0),
                                quotedInfixOperation.elementAt(1),
                                mergedArgumentList
                        }
                );

                /* The quotedRemoteFunction is not a complete call yet because a call is the {function, metadata, args}
                   where quotedRemoteFunction is function */

                specializedQuoted = quotedFunctionCall(
                        quotedRemoteFunction,
                        (OtpErlangList) quotedRemoteFunction.elementAt(1)
                );
            }
        }

        return specializedQuoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirNoParenthesesExpression noParenthesesExpression) {
        PsiElement[] children = noParenthesesExpression.getChildren();

        if (children.length != 1) {
            throw new NotImplementedException("noParenthesesExpression expected to only have one child");
        }

        return ((Quotable) children[0]).quote();
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
    public static OtpErlangObject quote(@NotNull final ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable noParenthesesNoArgumentsUnqualifiedCallOrVariable) {
        /* @note quotedFunctionCall cannot be used here because in the 3-tuple for function calls, the elements are
           {name, metadata, arguments}, while for an ambiguous call or variable, the elements are
           {name, metadata, context}.  Importantly, context is nil when there is no context while arguments are [] when
           there are no arguments. */
        return quotedVariable(
                noParenthesesNoArgumentsUnqualifiedCallOrVariable.getText(),
                metadata(noParenthesesNoArgumentsUnqualifiedCallOrVariable)
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(Operator operator) {
        ASTNode operator1 = operator
                .getNode()
                .getChildren(
                        operator.operatorTokenSet()
                )[0];

        return new OtpErlangAtom(operator1.getText());
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

        return block(quotedChildren);
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
        return null;
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

    public static char sigilName(@NotNull org.elixir_lang.psi.Sigil sigil) {
        ASTNode sigilNode = sigil.getNode();
        ASTNode[] childNodes = sigilNode.getChildren(null);
        ASTNode nameNode = childNodes[1];
        CharSequence chars = nameNode.getChars();
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

        ElixirHexadecimalEscapeSequence hexadecimalEscapeSequence = (ElixirHexadecimalEscapeSequence) child.getPsi();
        codePointList.add(
                hexadecimalEscapeSequence.codePoint()
        );

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
                } else if (elementType == ElixirTypes.HEXADECIMAL_ESCAPE_SEQUENCE) {
                    codePointList = parent.addHexadecimalEscapeSequenceCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (codePointList != null) {
                        quotedParentList.add(elixirString(codePointList));
                        codePointList = null;
                    }

                    ElixirInterpolation childElement = (ElixirInterpolation) child.getPsi();
                    quotedParentList.add(childElement.quote());
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
}
