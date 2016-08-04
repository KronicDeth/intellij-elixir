package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnmatchedQualifiedNoArgumentsCall;
import org.elixir_lang.psi.impl.ElixirUnmatchedQualifiedNoArgumentsCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnmatchedQualifiedNoArgumentsCall extends Stub<org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall, ElixirUnmatchedQualifiedNoArgumentsCall> {
    /*
     * Constructors
     */

    public UnmatchedQualifiedNoArgumentsCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnmatchedQualifiedNoArgumentsCall createPsi(@NotNull org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall stub) {
        return new ElixirUnmatchedQualifiedNoArgumentsCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall createStub(@NotNull ElixirUnmatchedQualifiedNoArgumentsCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall(
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
    public org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall(
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
