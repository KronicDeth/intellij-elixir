package org.elixir_lang.psi.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirMatchedQualifiedAlias;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.impl.ElixirMatchedQualifiedAliasImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MatchedQualifiedAlias extends Named<org.elixir_lang.psi.stub.MatchedQualifiedAlias, ElixirMatchedQualifiedAlias> {
    /*
     * Constructors
     */

    public MatchedQualifiedAlias(@NonNls @NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedAlias deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.MatchedQualifiedAlias(parentStub, this, dataStream.readName());
    }

    @Override
    public ElixirMatchedQualifiedAlias createPsi(@NotNull org.elixir_lang.psi.stub.MatchedQualifiedAlias stub) {
        return new ElixirMatchedQualifiedAliasImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedAlias createStub(ElixirMatchedQualifiedAlias psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedQualifiedAlias(
                parentStub,
                this,
                /* Use fully-qualified name as that is what matters for defmodule Lookup as `defmodule Foo do\nend` and
                   `defmodule Elixir.Foo do\nend` should both resolve to `Elixir.Foo`. */
                psi.fullyQualifiedName()
        );
    }

    @Override
    public void serialize(final @NotNull org.elixir_lang.psi.stub.MatchedQualifiedAlias stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        boolean shouldCreateStub = false;

        ASTNode parent = node.getTreeParent();
        IElementType parentElementType = parent.getElementType();

        if (parentElementType == ElixirTypes.NO_PARENTHESES_ONE_ARGUMENT) {
            ASTNode grandParent = parent.getTreeParent();
            IElementType grandParentElementType = grandParent.getElementType();

            if (grandParentElementType == ElixirTypes.UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL &&
                    grandParent.getFirstChildNode().getText().equals("defmodule")) {
                IElementType grandParentLastChildElementType = grandParent.getLastChildNode().getElementType();

                if (grandParentLastChildElementType == ElixirTypes.DO_BLOCK) {
                    shouldCreateStub = true;
                }
            }
        }

        return shouldCreateStub;
    }
}
