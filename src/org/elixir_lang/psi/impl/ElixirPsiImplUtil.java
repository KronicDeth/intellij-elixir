package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by luke.imhoff on 12/29/14.
 */
public class ElixirPsiImplUtil {
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
                throw new NotImplementedException("Cannot quote StringList in atom yet");
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
}
