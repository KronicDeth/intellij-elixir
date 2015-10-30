package org.elixir_lang.psi.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirAlias;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.impl.ElixirAliasImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Alias extends Named<org.elixir_lang.psi.stub.Alias, ElixirAlias> {
    /*
     * Constructors
     */

    public Alias(@NonNls @NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.Alias deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.Alias(parentStub, this, dataStream.readName());
    }

    @Override
    public ElixirAlias createPsi(@NotNull org.elixir_lang.psi.stub.Alias stub) {
        return new ElixirAliasImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.Alias createStub(ElixirAlias psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.Alias(
                parentStub,
                this,
                /* Use fully-qualified name as that is what matters for defmodule Lookup as `defmodule Foo do\nend` and
                   `defmodule Elixir.Foo do\nend` should both resolve to `Elixir.Foo`. */
                psi.fullyQualifiedName()
        );
    }

    @Override
    public void serialize(final @NotNull org.elixir_lang.psi.stub.Alias stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        // @see getNameIdentifier(ElixirAlias)
        boolean shouldCreateStub = false;

        ASTNode parent = node.getTreeParent();
        IElementType parentElementType = parent.getElementType();

        if (parentElementType == ElixirTypes.ACCESS_EXPRESSION) {
            ASTNode grandParent = parent.getTreeParent();
            IElementType grandParentElementType = grandParent.getElementType();

            if (grandParentElementType == ElixirTypes.NO_PARENTHESES_ONE_ARGUMENT) {
                ASTNode greatGrandParent = grandParent.getTreeParent();
                IElementType greatGrandParentElementType = greatGrandParent.getElementType();

                if (greatGrandParentElementType == ElixirTypes.UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL &&
                        greatGrandParent.getFirstChildNode().getText().equals("defmodule")) {
                    IElementType greatGrandParentLastChildElementType = greatGrandParent.getLastChildNode().getElementType();

                    if (greatGrandParentLastChildElementType == ElixirTypes.DO_BLOCK) {
                        shouldCreateStub = true;
                    }
                }
            }
        }

        return shouldCreateStub;
    }
}
