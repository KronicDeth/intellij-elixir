package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirMatchedQualifiedParenthesesCall;
import org.elixir_lang.psi.impl.ElixirMatchedQualifiedParenthesesCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MatchedQualifiedParenthesesCall extends Stub<org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall, ElixirMatchedQualifiedParenthesesCall> {
    /*
     * Constructors
     */

    public MatchedQualifiedParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirMatchedQualifiedParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall stub) {
        return new ElixirMatchedQualifiedParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall createStub(@NotNull ElixirMatchedQualifiedParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall(
                parentStub,
                this,
                psi.resolvedModuleName(),
                psi.resolvedFunctionName(),
                psi.resolvedFinalArity(),
                psi.hasDoBlockOrKeyword(),
                StringUtil.notNullize(psi.getName(), "?"),
                psi.canonicalNameCollection()
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall(
                parentStub,
                this,
                dataStream.readName(),
                dataStream.readName(),
                dataStream.readInt(),
                dataStream.readBoolean(),
                dataStream.readName(),
                readNameCollection(dataStream)
        );
    }
}
