package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirMatchedQualifiedNoParenthesesCall;
import org.elixir_lang.psi.impl.ElixirMatchedQualifiedNoParenthesesCallImpl;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MatchedQualifiedNoParenthesesCall extends Stub<org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall, ElixirMatchedQualifiedNoParenthesesCall> {
    /*
     * Constructors
     */

    public MatchedQualifiedNoParenthesesCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirMatchedQualifiedNoParenthesesCall createPsi(@NotNull org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall stub) {
        return new ElixirMatchedQualifiedNoParenthesesCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall createStub(@NotNull ElixirMatchedQualifiedNoParenthesesCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall(
                parentStub,
                this,
                psi.resolvedModuleName(),
                psi.functionName(),
                psi.resolvedFinalArity(),
                psi.hasDoBlockOrKeyword(),
                StringUtil.notNullize(psi.getName(), "?"),
                psi.canonicalNameSet(),
                psi.implementedProtocolName()
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall deserialize(@NotNull StubInputStream dataStream,
                                                                                  @Nullable StubElement parentStub)
            throws IOException {
        Deserialized deserialized = Deserialized.deserialize(dataStream);
        return new org.elixir_lang.psi.stub.MatchedQualifiedNoParenthesesCall(parentStub, this, deserialized);
    }
}
