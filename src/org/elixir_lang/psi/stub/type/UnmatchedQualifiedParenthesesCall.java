package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnmatchedQualifiedParenthesesCall;
import org.elixir_lang.psi.impl.ElixirUnmatchedQualifiedParenthesesCallImpl;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class UnmatchedQualifiedParenthesesCall extends Stub<org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall, ElixirUnmatchedQualifiedParenthesesCall> {
    /*
     * Constructors
     */

    public UnmatchedQualifiedParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnmatchedQualifiedParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall stub) {
        return new ElixirUnmatchedQualifiedParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall createStub(@NotNull ElixirUnmatchedQualifiedParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall(
                parentStub,
                this,
                psi.resolvedModuleName(),
                psi.functionName(),
                psi.resolvedFinalArity(),
                psi.hasDoBlockOrKeyword(),
                StringUtil.notNullize(psi.getName(), "?"),
                psi.canonicalNameSet()
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall deserialize(@NotNull StubInputStream dataStream,
                                                                                  @Nullable StubElement parentStub)
            throws IOException {
        Deserialized deserialized = Deserialized.deserialize(dataStream);
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedParenthesesCall(parentStub, this, deserialized);
    }
}
