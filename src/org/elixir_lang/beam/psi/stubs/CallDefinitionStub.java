package org.elixir_lang.beam.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.CallDefinition;
import org.elixir_lang.psi.stub.call.Stubbic;
import org.jetbrains.annotations.NotNull;

public interface CallDefinitionStub<T extends CallDefinition> extends Stubbic, StubElement<T> {
    @NotNull
    String getName();

    int callDefinitionClauseHeadArity();
}
