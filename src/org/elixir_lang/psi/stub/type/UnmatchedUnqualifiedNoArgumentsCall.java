package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoArgumentsCall;
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoArgumentsCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnmatchedUnqualifiedNoArgumentsCall extends Stub<org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall, ElixirUnmatchedUnqualifiedNoArgumentsCall> {
    /*
     * Constructors
     */

    public UnmatchedUnqualifiedNoArgumentsCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirUnmatchedUnqualifiedNoArgumentsCall createPsi(@NotNull org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall stub) {
        return new ElixirUnmatchedUnqualifiedNoArgumentsCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall createStub(@NotNull ElixirUnmatchedUnqualifiedNoArgumentsCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall(
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
    public org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall(
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
