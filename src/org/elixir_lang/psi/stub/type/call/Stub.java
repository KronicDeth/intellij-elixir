package org.elixir_lang.psi.stub.type.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.semantic.Semantic;
import org.elixir_lang.semantic.SemanticKt;
import org.elixir_lang.structure_view.element.call.definition.delegation.Head;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class Stub<Stub extends org.elixir_lang.psi.stub.call.Stub<Psi>,
        Psi extends org.elixir_lang.psi.call.StubBased> extends org.elixir_lang.psi.stub.type.Named<Stub, Psi> {
    /*
     * Static Methods
     */

    public Stub(@NotNull String debugName) {
        super(debugName);
    }

    private boolean hasCanonicalNames(Call call) {
        boolean hasCanonicalNames = false;

        if (call instanceof StubBased) {
            StubBased stubBased = (StubBased) call;

            hasCanonicalNames = stubBased.getCanonicalNameSet().size() > 0;
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
        return Head.Companion.is(call) && Head.Companion.enclosingDelegationCall(call) != null;
    }

    private boolean isEnclosableByModular(Call call) {
        Semantic semantic = SemanticKt.getSemantic(call);

        return semantic instanceof org.elixir_lang.semantic.call.definition.Clause ||
                semantic instanceof org.elixir_lang.semantic.type.definition.source.Specification ||
                semantic instanceof org.elixir_lang.semantic.type.definition.source.Callback;
    }

    private boolean isNameable(Call call) {
        return isEnclosableByModular(call) || isDelegationCallDefinitionHead(call) || isModular(call) || isQuoted(call);
    }

    private boolean isModular(Call call) {
        Semantic semantic = SemanticKt.getSemantic(call);

        return semantic instanceof org.elixir_lang.semantic.Modular;
    }

    private boolean isQuoted(Call call) {
        boolean isQuoted;

        Semantic semantic = SemanticKt.getSemantic(call);

        if ((semantic instanceof org.elixir_lang.semantic.module_attribute.definition.Literal ||
                semantic instanceof org.elixir_lang.semantic.module_attribute.definition.Dynamic)) {
            isQuoted = SemanticKt.getSemanticParent(call) instanceof org.elixir_lang.semantic.Quote;
        } else {
            isQuoted = semantic instanceof org.elixir_lang.semantic.variable.QuoteDeclared;
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
