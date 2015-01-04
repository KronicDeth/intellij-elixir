package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static org.elixir_lang.intellij_elixir.Quoter.elixirString;
import static org.elixir_lang.intellij_elixir.Quoter.javaString;

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
    public static OtpErlangObject quote(@NotNull final ElixirBinaryWholeNumber binaryWholeNumber) {
        ASTNode[] childNodes = binaryWholeNumber.getNode().getChildren(null);

        int validBinaryDigitsCount = 0;
        int invalidBinaryDigitsCount = 0;

        for (ASTNode childNode : childNodes) {
            IElementType elementType = childNode.getElementType();

            if (elementType == ElixirTypes.INVALID_BINARY_DIGITS) {
                invalidBinaryDigitsCount++;
            } else if (elementType == ElixirTypes.VALID_BINARY_DIGITS) {
                validBinaryDigitsCount++;
            }
        }

        List<ElixirBinaryDigits> binaryDigitsList = binaryWholeNumber.getBinaryDigitsList();
        OtpErlangObject quoted;

        if (invalidBinaryDigitsCount == 0 && validBinaryDigitsCount == 1) {
            ElixirBinaryDigits binaryDigits = binaryDigitsList.get(0);
            quoted = binaryDigits.quote();
        } else {
            /* 0 elements is invalid in native Elixir and can be emulated as String.to_integer("", 2) while
               2 elements implies at least one element is INVALID_BINARY_DIGITS which is invalid in native Elixir and
               can be emulated as String.to_integer(<all-digits>, 2) so that it raises an ArgumentError on the invalid
               binary digits */
            StringBuilder stringAccumulator = new StringBuilder();

            for (ElixirBinaryDigits binaryDigits : binaryDigitsList) {
                stringAccumulator.append(binaryDigits.getText());
            }

            quoted = quotedFunctionCall(
                    "String",
                    "to_integer",
                    metadata(binaryWholeNumber),
                    elixirString(stringAccumulator.toString()),
                    new OtpErlangLong(2)
            );
        }

        return quoted;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quote(@NotNull final ElixirCharList charList) {
        ElixirInterpolatedCharListBody interpolatedCharListBody = charList.getInterpolatedCharListBody();

        return interpolatedCharListBody.quote();
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
    public static OtpErlangObject quote(@NotNull final ElixirInterpolatedCharListBody interpolatedCharListBody) {
        ASTNode interpolatedStringBodyNode = interpolatedCharListBody.getNode();

        ASTNode[] children = interpolatedStringBodyNode.getChildren(null);
        OtpErlangObject quoted;

        if (children.length == 1) {
            ASTNode child = children[0];

            if (child.getElementType() == ElixirTypes.CHAR_LIST_FRAGMENT) {
                final String text = child.getText();
                quoted = new OtpErlangString(text);
            } else {
                throw new NotImplementedException("Can't quote ElixirInterpolatedCharListBody with one child that isn't a CHAR_LIST_FRAGMENT");
            }
        } else {
            OtpErlangList interpolatedCharListBodyMetadata = metadata(interpolatedCharListBody);
            List<OtpErlangObject> quotedCharListList = new LinkedList<OtpErlangObject>();
            StringBuilder stringAccumulator = null;

            for (ASTNode child : children) {
                IElementType elementType = child.getElementType();

                if (elementType == ElixirTypes.CHAR_LIST_FRAGMENT) {
                    if (stringAccumulator == null) {
                        stringAccumulator = new StringBuilder("");
                    }

                    stringAccumulator.append(child.getText());
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (stringAccumulator != null) {
                        quotedCharListList.add(elixirString(stringAccumulator.toString()));
                        stringAccumulator = null;
                    }

                    ElixirInterpolation childElement = (ElixirInterpolation) child.getPsi();
                    quotedCharListList.add(childElement.quote());
                } else {
                    throw new NotImplementedException("Can quote only CHAR_LIST_FRAGMENT and INTERPOLATION");
                }
            }

            if (stringAccumulator != null) {
                quotedCharListList.add(elixirString(stringAccumulator.toString()));
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
}
