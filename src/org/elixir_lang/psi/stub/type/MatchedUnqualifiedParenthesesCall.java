package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirMatchedUnqualifiedParenthesesCall;
import org.elixir_lang.psi.impl.ElixirMatchedUnqualifiedParenthesesCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MatchedUnqualifiedParenthesesCall extends Stub<org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall, ElixirMatchedUnqualifiedParenthesesCall> {
    /*
     * Constructors
     */

    public MatchedUnqualifiedParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirMatchedUnqualifiedParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall stub) {
        return new ElixirMatchedUnqualifiedParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall createStub(@NotNull ElixirMatchedUnqualifiedParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall(
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
    public org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.MatchedUnqualifiedParenthesesCall(
                parentStub,
                this,
                dataStream.readName(),
                dataStream.readName(),
                dataStream.readInt(),
                dataStream.readBoolean(),
                dataStream.readName(),
                readNameSet(dataStream)
        );
    }
}
