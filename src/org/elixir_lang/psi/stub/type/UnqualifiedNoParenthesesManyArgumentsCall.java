package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnqualifiedNoParenthesesManyArgumentsCall;
import org.elixir_lang.psi.impl.ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnqualifiedNoParenthesesManyArgumentsCall extends Stub<org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall, ElixirUnqualifiedNoParenthesesManyArgumentsCall> {
    /*
     * Constructors
     */

    public UnqualifiedNoParenthesesManyArgumentsCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnqualifiedNoParenthesesManyArgumentsCall createPsi(@NotNull org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall stub) {
        return new ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall createStub(@NotNull ElixirUnqualifiedNoParenthesesManyArgumentsCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall(
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
    public org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall(
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
