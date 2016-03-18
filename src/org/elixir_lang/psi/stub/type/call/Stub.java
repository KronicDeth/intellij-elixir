package org.elixir_lang.psi.stub.type.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
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

        return isNameable(call) && hasName(call);
    }

    /*
     * Private Instance Methods
     */

    private boolean hasName(Call call) {
        return call.getName() != null;
    }

    private boolean isDelegationCallDefinitionHead(Call call) {
        return CallDefinitionHead.is(call) && CallDefinitionHead.enclosingDelegationCall(call) != null;
    }

    private boolean isEnclosableByModular(Call call) {
        return CallDefinitionClause.is(call) ||
                /* skip CallDefinitionHead because there can be false positives the the ancestor calls need to be
                   checked */
                CallDefinitionSpecification.is(call) ||
                // skip CallDefinitionHead because it is covered by CallDefinitionClause
                Callback.is(call);
    }

    private boolean isModular(Call call) {
        return Implementation.is(call) || Module.is(call) || Protocol.is(call);
    }

    private boolean isNameable(Call call) {
        return isEnclosableByModular(call) || isDelegationCallDefinitionHead(call) || isModular(call);
    }
}
