package org.elixir_lang.psi.stub.type.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Callback;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class Stub<Stub extends org.elixir_lang.psi.stub.call.Stub<Psi>,
                            Psi extends org.elixir_lang.psi.call.StubBased> extends org.elixir_lang.psi.stub.type.Named<Stub, Psi> {
    /*
     * Constructors
     */

    public Stub(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public void serialize(@NotNull Stub stub,
                          @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.resolvedModuleName());
        dataStream.writeName(stub.resolvedFunctionName());
        dataStream.writeInt(stub.resolvedFinalArity());
        dataStream.writeBoolean(stub.hasDoBlockOrKeyword());
        dataStream.writeName(stub.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        Call call = (Call) node.getPsi();

        // TODO do reset of isSuitable
        return (CallDefinitionClause.is(call) ||
                // skip CallDefinitionHead because it is covered by CallDefinitionClause
                Callback.is(call)) &&
                // if it doesn't have a name then it can't be searched for, so there's no reason to stub it.
                call.getName() != null;
    }
}
