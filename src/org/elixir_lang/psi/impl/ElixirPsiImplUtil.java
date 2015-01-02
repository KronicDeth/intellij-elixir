package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.elixir_lang.intellij_elixir.Quoter.elixirString;
import static org.elixir_lang.intellij_elixir.Quoter.javaString;

/**
 * Created by luke.imhoff on 12/29/14.
 */
public class ElixirPsiImplUtil {

    public static final OtpErlangAtom NIL = new OtpErlangAtom("nil");

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
            throw new NotImplementedException("Cannot quote CharList in atom yet");
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
                            new OtpErlangAtom("utf8")
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

    public static OtpErlangObject quote(ElixirFile file) {
        final Deque<OtpErlangObject> quotedChildren = new LinkedList<OtpErlangObject>();

        file.acceptChildren(
                new PsiElementVisitor() {
                    public void visitAtom(@NotNull ElixirAtom atom) {
                        final OtpErlangObject quotedAtom = atom.quote();
                        quotedChildren.add(quotedAtom);
                    }

                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof ElixirAtom) {
                            visitAtom((ElixirAtom) element);
                        }

                        super.visitElement(element);
                    }
                }
        );

        return block(quotedChildren);
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
