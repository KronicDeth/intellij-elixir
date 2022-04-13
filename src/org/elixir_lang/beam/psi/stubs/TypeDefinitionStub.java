package org.elixir_lang.beam.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.TypeDefinition;
import org.jetbrains.annotations.NotNull;

public interface TypeDefinitionStub<T extends TypeDefinition> extends StubElement<T> {
    @NotNull
    String getName();
}
