package org.elixir_lang.beam.psi.stubs;

import org.elixir_lang.beam.psi.CallDefinition;
import org.elixir_lang.beam.psi.TypeDefinition;
import org.elixir_lang.beam.psi.Module;

public interface ModuleStubElementTypes {
    ModuleElementType<ModuleStub<?>, Module> MODULE = new ModuleType("MODULE");
    ModuleElementType<TypeDefinitionStub<?>, TypeDefinition> TYPE_DEFINITION = new TypeDefinitionType("TYPE_DEFINITION");
    ModuleElementType<CallDefinitionStub<?>, CallDefinition> CALL_DEFINITION = new CallDefinitionType("CALL_DEFINITION");
}
