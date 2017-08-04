package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.ElixirMatchedQualifiedNoArgumentsCall;
import org.elixir_lang.psi.impl.ElixirMatchedQualifiedNoArgumentsCallImpl;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MatchedQualifiedNoArgumentsCall extends Stub<org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall, ElixirMatchedQualifiedNoArgumentsCall> {
    /*
     * Constructors
     */

    public MatchedQualifiedNoArgumentsCall(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public ElixirMatchedQualifiedNoArgumentsCall createPsi(@NotNull org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall stub) {
        return new ElixirMatchedQualifiedNoArgumentsCallImpl(stub, this);
    }

    @Override
    public org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall createStub(@NotNull ElixirMatchedQualifiedNoArgumentsCall psi, StubElement parentStub) {
        return new org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall(
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
    public org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall(
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
