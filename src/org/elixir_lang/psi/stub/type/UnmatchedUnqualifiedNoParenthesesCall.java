package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class UnmatchedUnqualifiedNoParenthesesCall extends Stub<org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall, ElixirUnmatchedUnqualifiedNoParenthesesCall> {
    /*
     * Constructors
     */

    public UnmatchedUnqualifiedNoParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnmatchedUnqualifiedNoParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall stub) {
        return new ElixirUnmatchedUnqualifiedNoParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall createStub(@NotNull ElixirUnmatchedUnqualifiedNoParenthesesCall psi, StubElement parentStub) {
        Collection<String> canonicalNameCollection = psi.canonicalNameSet();
        return new org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall(
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
    public org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall(
                parentStub,
                this,
                dataStream.readName(),
                dataStream.readName(),
                dataStream.readVarInt(),
                dataStream.readBoolean(),
                dataStream.readName(),
                readNameSet(dataStream)
        );
    }
}
