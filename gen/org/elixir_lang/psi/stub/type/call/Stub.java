package org.elixir_lang.psi.stub.type.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.ResolveState;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.module.PutAttribute;
import org.elixir_lang.module.RegisterAttribute;
import org.elixir_lang.psi.CallDefinitionClause;
import org.elixir_lang.psi.Implementation;
import org.elixir_lang.psi.Module;
import org.elixir_lang.psi.ModuleAttribute;
import org.elixir_lang.psi.Protocol;
import org.elixir_lang.psi.QuoteMacro;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.structure_view.element.CallDefinitionHead;
import org.elixir_lang.structure_view.element.CallDefinitionSpecification;
import org.elixir_lang.structure_view.element.Callback;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall;

public abstract class Stub<Stub extends org.elixir_lang.psi.stub.call.Stub<Psi>,
        Psi extends org.elixir_lang.psi.call.StubBased> extends org.elixir_lang.psi.stub.type.Named<Stub, Psi> {
    /*
     * Static Methods
     */

    public Stub(@NotNull String debugName) {
        super(debugName);
    }

    public static boolean isModular(Call call) {
        return Implementation.is(call) || Module.is(call) || Protocol.is(call);
    }

    private boolean hasCanonicalNames(Call call) {
        boolean hasCanonicalNames = false;

        if (call instanceof StubBased) {
            StubBased stubBased = (StubBased) call;

            hasCanonicalNames = stubBased.canonicalNameSet().size() > 0;
        }

        return hasCanonicalNames;
    }

    private boolean hasName(Call call) {
        return call.getName() != null;
    }

    private boolean hasNameOrCanonicalNames(Call call) {
        return hasName(call) || hasCanonicalNames(call);
    }

    private boolean isDelegationCallDefinitionHead(Call call) {
        return CallDefinitionHead.Companion.is(call) && CallDefinitionHead.Companion.enclosingDelegationCall(call) != null;
    }

    private boolean isEnclosableByModular(Call call) {
        return CallDefinitionClause.is(call) ||
                /* skip CallDefinitionHead because there can be false positives the the ancestor calls need to be
                   checked */
                CallDefinitionSpecification.Companion.is(call) ||
                // skip CallDefinitionHead because it is covered by CallDefinitionClause
                Callback.Companion.is(call);
    }

    private boolean isNameable(Call call) {
        return isEnclosableByModular(call) || isDelegationCallDefinitionHead(call) || isModular(call) || isQuoted(call);
    }

    private boolean isQuoted(Call call) {
        boolean isQuoted;

        if (ModuleAttribute.isDeclaration(call) || RegisterAttribute.is(call) || PutAttribute.is(call)) {
            Call enclosingModularMacroCall = enclosingModularMacroCall(call);

            if (enclosingModularMacroCall != null) {
                isQuoted = QuoteMacro.is(enclosingModularMacroCall);
            } else {
                isQuoted = false;
            }
        } else {
            isQuoted = false;
        }

        return isQuoted;
    }

    @Override
    public void serialize(@NotNull Stub stub, @NotNull StubOutputStream stubOutputStream) throws IOException {
        Deserialized.serialize(stubOutputStream, stub);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        Call call = (Call) node.getPsi();

        return isNameable(call) && hasNameOrCanonicalNames(call);
    }
}
