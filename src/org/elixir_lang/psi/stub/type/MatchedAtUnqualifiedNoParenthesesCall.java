package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirMatchedAtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.impl.ElixirMatchedAtUnqualifiedNoParenthesesCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MatchedAtUnqualifiedNoParenthesesCall extends Stub<org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall, ElixirMatchedAtUnqualifiedNoParenthesesCall> {
    /*
     * Constructors
     */

    public MatchedAtUnqualifiedNoParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirMatchedAtUnqualifiedNoParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall stub) {
        return new ElixirMatchedAtUnqualifiedNoParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall createStub(@NotNull ElixirMatchedAtUnqualifiedNoParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall(
                parentStub,
                this,
                psi.resolvedModuleName(),
                psi.resolvedFunctionName(),
                psi.resolvedFinalArity(),
                psi.hasDoBlockOrKeyword(),
                StringUtil.notNullize(psi.getName(), "?"),
                StringUtil.notNullize(psi.canonicalName(), "?")
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.MatchedAtUnqualifiedNoParenthesesCall(
                parentStub,
                this,
                dataStream.readName(),
                dataStream.readName(),
                dataStream.readInt(),
                dataStream.readBoolean(),
                dataStream.readName(),
                dataStream.readName()
        );
    }
}
