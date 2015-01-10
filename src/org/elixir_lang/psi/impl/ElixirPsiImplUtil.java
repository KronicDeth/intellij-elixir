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
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static final OtpErlangAtom NIL = new OtpErlangAtom("nil");
    public static final OtpErlangAtom UTF_8 = new OtpErlangAtom("utf8");

    @Contract(pure = true)
    @NotNull
    public static int base(@SuppressWarnings("unused") @NotNull final ElixirBinaryWholeNumber binaryWholeNumber) {
        return 2;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject block(@NotNull final Deque<OtpErlangObject> quotedChildren) {
        OtpErlangObject asBlock;
        final int size = quotedChildren.size();

        if (size == 1) {
            asBlock = quotedChildren.getFirst();
        } else {
            OtpErlangObject[] quotedArray = new OtpErlangObject[size];
            OtpErlangAtom blockFunction = new OtpErlangAtom("__block__");
            OtpErlangList blockMetadata = new OtpErlangList();
            OtpErlangList blockArguments = new OtpErlangList(quotedChildren.toArray(quotedArray));

            asBlock = new OtpErlangTuple(new OtpErlangObject[]{blockFunction, blockMetadata, blockArguments});
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

        if (invalidDigitsCount == 0 && validDigitsCount == 1) {
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

    public static OtpErlangList metadata(PsiElement element) {
        final OtpErlangObject[] keywordListElements = {
                keywordTuple(
                        "line",
                        // Elixir metadata lines are 1-indexed while getLineNumber is 0-indexed
                        lineNumber(element) + 1)
        };

        return new OtpErlangList(keywordListElements);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirAtom atom) {
        OtpErlangObject quoted;
        ElixirCharList charList = atom.getCharList();

        if (charList != null) {
            OtpErlangObject quotedCharList = charList.quote();

            if (quotedCharList instanceof OtpErlangString) {
                final String atomText = ((OtpErlangString) quotedCharList).stringValue();
                quoted = new OtpErlangAtom(atomText);
            } else {
                final OtpErlangTuple quotedStringToCharListCall = (OtpErlangTuple) quotedCharList;
                final OtpErlangList quotedStringToCharListArguments = (OtpErlangList) quotedStringToCharListCall.elementAt(2);
                final OtpErlangObject binaryConstruction = quotedStringToCharListArguments.getHead();

                quoted = quotedFunctionCall(
                        "erlang",
                        "binary_to_atom",
                        (OtpErlangList) quotedStringToCharListCall.elementAt(1),
                        binaryConstruction,
                        UTF_8
                );
            }
        } else {
            ElixirString string = atom.getString();

            if (string != null) {
                OtpErlangObject quotedString = string.quote();

                if (quotedString instanceof OtpErlangBinary) {
                    String atomText = javaString((OtpErlangBinary) quotedString);
                    quoted = new OtpErlangAtom(atomText);
                } else {
                    quoted = quotedFunctionCall(
                            "erlang",
                            "binary_to_atom",
                            metadata(string),
                            quotedString,
                            UTF_8
                    );
                }
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
    public static OtpErlangObject quote(@NotNull final ElixirBinaryDigits binaryDigits) {
        final String text = binaryDigits.getText();
        long value = Long.parseLong(text, 2);

        return new OtpErlangLong(value);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharList charList) {
        ElixirInterpolatedCharListBody interpolatedCharListBody = charList.getInterpolatedCharListBody();

        return interpolatedCharListBody.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharListHeredoc charListHeredoc) {
        ElixirCharListHeredocPrefix charListHeredocPrefix = charListHeredoc.getCharListHeredocPrefix();
        int prefixLength = charListHeredocPrefix.getTextLength();
        Deque<ASTNode> alignedNodeDeque = new LinkedList<ASTNode>();
        List<ElixirCharListHeredocLine> charListHeredocLineList = charListHeredoc.getCharListHeredocLineList();

        for (ElixirCharListHeredocLine line : charListHeredocLineList) {
            queueChildNodes(line, prefixLength, alignedNodeDeque);
        }

        Queue<ASTNode> mergedNodeQueue = mergeCharListFragments(alignedNodeDeque, charListHeredoc.getManager());
        ASTNode[] mergedNodes = new ASTNode[mergedNodeQueue.size()];
        mergedNodeQueue.toArray(mergedNodes);

        return quotedInterpolatedCharListBodyChildNodes(charListHeredoc, mergedNodes);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharListHeredocLine charListHeredocLine, int prefixLength) {
        ElixirCharListHeredocLineWhitespace charListHeredocLineWhitespace = charListHeredocLine.getCharListHeredocLineWhitespace();
        ASTNode excessWhitespace = charListHeredocLineWhitespace.excessWhitespace(prefixLength);
        ElixirInterpolatedCharListBody interpolatedCharListBody = charListHeredocLine.getInterpolatedCharListBody();
        ASTNode[] directChildNodes = childNodes(interpolatedCharListBody);
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

        return quotedInterpolatedCharListBodyChildNodes(interpolatedCharListBody, accumulatedChildNodes);
    }

    /* Returns a virtual PsiElement representing the spaces at the end of charListHeredocLineWhitespace that are not
     * consumed by prefixLength.
     *
     * @return null if prefixLength is greater than or equal to text length of charListHeredcoLineWhitespace.
     */
    @Contract(pure = true)
    @Nullable
    public static ASTNode excessWhitespace(@NotNull final ElixirCharListHeredocLineWhitespace charListHeredocLineWhitespace, int prefixLength) {
        int availableLength = charListHeredocLineWhitespace.getTextLength();
        int excessLength = availableLength - prefixLength;
        ASTNode excessWhitespaceASTNode = null;

        if (excessLength > 0) {
            char[] excessWhitespaceChars = new char[excessLength];
            Arrays.fill(excessWhitespaceChars, ' ');
            String excessWhitespaceString = new String(excessWhitespaceChars);
            excessWhitespaceASTNode = Factory.createSingleLeafElement(
                    ElixirTypes.CHAR_LIST_FRAGMENT,
                    excessWhitespaceString,
                    0,
                    excessLength,
                    null,
                    charListHeredocLineWhitespace.getManager()
            );
        }

        return excessWhitespaceASTNode;
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
            Digits digits = digitsList.get(0);
            quoted = digits.quote();
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
    public static OtpErlangObject quote(@NotNull final ElixirInterpolatedCharListBody interpolatedCharListBody) {
        return quotedInterpolatedCharListBodyChildNodes(interpolatedCharListBody);

    }

    protected static ASTNode[] childNodes(ElixirInterpolatedCharListBody interpolatedCharListBody) {
        ASTNode interpolatedStringBodyNode = interpolatedCharListBody.getNode();
        return interpolatedStringBodyNode.getChildren(null);
    }

    protected static OtpErlangObject quotedInterpolatedCharListBodyChildNodes(ElixirInterpolatedCharListBody interpolatedCharListBody) {
        return quotedInterpolatedCharListBodyChildNodes(
                interpolatedCharListBody,
                childNodes(interpolatedCharListBody)
        );
    }

    protected static OtpErlangObject quotedInterpolatedCharListBodyChildNodes(PsiElement anchor, ASTNode... children) {
        OtpErlangObject quoted;

        final int childCount = children.length;

        if (childCount == 0) {
            // an empty CharList is just an empty list
            quoted = new OtpErlangList();
        } else {
            OtpErlangList interpolatedCharListBodyMetadata = metadata(anchor);
            List<OtpErlangObject> quotedCharListList = new LinkedList<OtpErlangObject>();
            List<Integer> codePointList = null;

            for (ASTNode child : children) {
                IElementType elementType = child.getElementType();

                if (elementType == ElixirTypes.CHAR_LIST_FRAGMENT) {
                    if (codePointList == null) {
                        codePointList = new LinkedList<Integer>();
                    }

                    for (Integer codePoint : codePoints(child.getText())) {
                        codePointList.add(codePoint);
                    }
                } else if (elementType == ElixirTypes.ESCAPED_CHARACTER) {
                    if (codePointList == null) {
                        codePointList = new LinkedList<Integer>();
                    }

                    ElixirEscapedCharacter escapedCharacter = (ElixirEscapedCharacter) child.getPsi();
                    codePointList.add(
                            escapedCharacter.codePoint()
                    );
                } else if (elementType == ElixirTypes.HEXADECIMAL_ESCAPE_SEQUENCE) {
                    if (codePointList == null) {
                        codePointList = new LinkedList<Integer>();
                    }

                    ElixirHexadecimalEscapeSequence hexadecimalEscapeSequence = (ElixirHexadecimalEscapeSequence) child.getPsi();
                    codePointList.add(
                            hexadecimalEscapeSequence.codePoint()
                    );
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (codePointList != null) {
                        quotedCharListList.add(elixirString(codePointList));
                        codePointList = null;
                    }

                    ElixirInterpolation childElement = (ElixirInterpolation) child.getPsi();
                    quotedCharListList.add(childElement.quote());
                } else {
                    throw new NotImplementedException("Can quote only CHAR_LIST_FRAGMENT and INTERPOLATION");
                }
            }

            // can be represented as a pure Erlang string (Elixir CharList)
            if (codePointList != null && quotedCharListList.isEmpty()) {
                quoted = elixirCharList(codePointList);
            } else {
                if (codePointList != null) {
                    quotedCharListList.add(elixirString(codePointList));
                }

                OtpErlangObject[] quotedStringElements = new OtpErlangObject[quotedCharListList.size()];
                quotedCharListList.toArray(quotedStringElements);

                OtpErlangTuple binaryConstruction = quotedFunctionCall("<<>>", interpolatedCharListBodyMetadata, quotedStringElements);
                quoted = quotedFunctionCall(
                        "String",
                        "to_char_list",
                        interpolatedCharListBodyMetadata,
                        binaryConstruction
                );
            }
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirInterpolatedStringBody interpolatedStringBody) {
        ASTNode interpolatedStringBodyNode = interpolatedStringBody.getNode();

        ASTNode[] children = interpolatedStringBodyNode.getChildren(null);
        OtpErlangObject quoted;

        if (children.length == 1) {
            ASTNode child = children[0];

            if (child.getElementType() == ElixirTypes.STRING_FRAGMENT) {
                quoted = elixirString(child.getText());
            } else {
                throw new NotImplementedException("Can't quote ElixirInterpolatedStringBody with one child that isn't a STRING_FRAGMENT");
            }
        } else {
            OtpErlangList interpolatedStringBodyMetadata = metadata(interpolatedStringBody);
            List<OtpErlangObject> quotedStringList = new LinkedList<OtpErlangObject>();
            StringBuilder stringAccumulator = null;

            for (ASTNode child : children) {
                IElementType elementType = child.getElementType();

                if (elementType == ElixirTypes.STRING_FRAGMENT) {
                    if (stringAccumulator == null) {
                        stringAccumulator = new StringBuilder("");
                    }

                    stringAccumulator.append(child.getText());
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (stringAccumulator != null) {
                        quotedStringList.add(elixirString(stringAccumulator.toString()));
                        stringAccumulator = null;
                    }

                    ElixirInterpolation childElement = (ElixirInterpolation) child.getPsi();
                    quotedStringList.add(childElement.quote());
                } else {
                    throw new NotImplementedException("Can quote only STRING_FRAGMENT and INTERPOLATION");
                }
            }

            if (stringAccumulator != null) {
                quotedStringList.add(elixirString(stringAccumulator.toString()));
            }

            OtpErlangObject[] quotedStringElements = new OtpErlangObject[quotedStringList.size()];
            quotedStringList.toArray(quotedStringElements);

            quoted = quotedFunctionCall("<<>>", interpolatedStringBodyMetadata, quotedStringElements);
        }

        return quoted;
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
        OtpErlangObject quotedBinaryCall = quotedFunctionCall(
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
    public static OtpErlangObject quote(@NotNull final ElixirString string) {
        ElixirInterpolatedStringBody interpolatedStringBody = string.getInterpolatedStringBody();

        return interpolatedStringBody.quote();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(PsiElement[] children) {
        Quotable[] quotableChildren = new Quotable[children.length];

        for (int i = 0; i < children.length; i++) {
            quotableChildren[i] = (Quotable) children[i];
        }

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

    @NotNull
    public static OtpErlangObject quotedArguments(final OtpErlangObject... arguments) {
        OtpErlangObject quoted;

        if (arguments.length == 0) {
            quoted = NIL;
        } else {
            quoted = new OtpErlangList(arguments);
        }

        return quoted;
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
    public static OtpErlangTuple quotedFunctionCall(final OtpErlangObject quotedQualifiedIdentifier, final OtpErlangList metadata, final OtpErlangObject... arguments) {
        return new OtpErlangTuple(
                new OtpErlangObject[] {
                        quotedQualifiedIdentifier,
                        metadata,
                        quotedArguments(arguments)
                }
        );
    }

    @NotNull
    public static IElementType validElementType(@NotNull ElixirBinaryDigits binaryDigits) {
        return ElixirTypes.VALID_BINARY_DIGITS;
    }

    /*
     * Private static methods
     */

    @NotNull
    private static Queue<ASTNode> mergeCharListFragments(@NotNull Deque<ASTNode> unmergedNodes, @NotNull PsiManager manager) {
        Queue<ASTNode> mergedNodes = new LinkedList<ASTNode>();
        StringBuilder fragmentStringBuilder = null;

        for (ASTNode unmergedNode : unmergedNodes) {
            if (unmergedNode.getElementType() == ElixirTypes.CHAR_LIST_FRAGMENT) {
                if (fragmentStringBuilder == null) {
                    fragmentStringBuilder = new StringBuilder();
                }

                String fragment = unmergedNode.getText();
                fragmentStringBuilder.append(fragment);
            } else {
                addMergeCharListFragments(mergedNodes, fragmentStringBuilder, manager);
                fragmentStringBuilder = null;
                mergedNodes.add(unmergedNode);
            }
        }

        addMergeCharListFragments(mergedNodes, fragmentStringBuilder, manager);

        return mergedNodes;
    }

    private static void addMergeCharListFragments(@NotNull Queue<ASTNode> mergedNodes, StringBuilder fragmentStringBuilder, PsiManager manager) {
        if (fragmentStringBuilder != null) {
            ASTNode charListFragment = Factory.createSingleLeafElement(
                    ElixirTypes.CHAR_LIST_FRAGMENT,
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
    private static void queueChildNodes(@NotNull ElixirCharListHeredocLine line, int prefixLength, @NotNull Queue<ASTNode> heredocDescendentNodes) {
        ElixirCharListHeredocLineWhitespace charListHeredocLineWhitespace = line.getCharListHeredocLineWhitespace();

        ASTNode excessWhitespace = charListHeredocLineWhitespace.excessWhitespace(prefixLength);

        if (excessWhitespace != null) {
            heredocDescendentNodes.add(excessWhitespace);
        }

        ElixirInterpolatedCharListBody interpolatedCharListBody = line.getInterpolatedCharListBody();
        Collections.addAll(heredocDescendentNodes, childNodes(interpolatedCharListBody));

        ASTNode eolNode = Factory.createSingleLeafElement(
                ElixirTypes.CHAR_LIST_FRAGMENT,
                "\n",
                0,
                1,
                null,
                line.getManager()
        );
        heredocDescendentNodes.add(eolNode);
    }
}
