package org.elixir_lang.psi.stub.type;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnmatchedQualifiedNoParenthesesCall;
import org.elixir_lang.psi.impl.ElixirUnmatchedQualifiedNoParenthesesCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnmatchedQualifiedNoParenthesesCall extends Stub<org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall, ElixirUnmatchedQualifiedNoParenthesesCall> {
    /*
     * Constructors
     */

    public UnmatchedQualifiedNoParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnmatchedQualifiedNoParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall stub) {
        return new ElixirUnmatchedQualifiedNoParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall createStub(@NotNull ElixirUnmatchedQualifiedNoParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall(
                parentStub,
                this,
                psi.resolvedModuleName(),
                psi.resolvedFunctionName(),
                psi.resolvedFinalArity(),
                psi.hasDoBlockOrKeyword(),
                psi.getName()
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedNoParenthesesCall(
                parentStub,
                this,
                dataStream.readName(),
                dataStream.readName(),
                dataStream.readInt(),
                dataStream.readBoolean(),
                dataStream.readName()
        );
    }
}
