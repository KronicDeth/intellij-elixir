package org.elixir_lang.psi.stub.type;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import org.elixir_lang.psi.Definition;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoArgumentsCall;
import org.elixir_lang.psi.QuoteMacro;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoArgumentsCallImpl;
import org.elixir_lang.psi.operation.Match;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.psi.stub.type.call.Stub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall;
import static org.elixir_lang.psi.DefinitionKt.definition;

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
                psi.canonicalNameSet(),
                definition(psi),
                psi.implementedProtocolName()
        );
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall deserialize(@NotNull StubInputStream dataStream,
                                                                                    @Nullable StubElement parentStub)
            throws IOException {
        Deserialized deserialized = Deserialized.deserialize(dataStream);
        return new org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall(parentStub, this, deserialized);
    }

    private @Nullable Definition definition(@NotNull ElixirUnmatchedUnqualifiedNoArgumentsCall psi) {
        Definition definition = null;

        if (psi.getParent() instanceof Match) {
            Call enclosingModularMacroCall = enclosingModularMacroCall(psi);

            if (enclosingModularMacroCall != null && QuoteMacro.is(enclosingModularMacroCall)) {
                definition = Definition.VARIABLE;
            }
        }

        return definition;
    }
}
