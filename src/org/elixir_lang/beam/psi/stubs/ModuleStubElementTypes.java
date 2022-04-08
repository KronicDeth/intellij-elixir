package org.elixir_lang.beam.psi.stubs;

import org.elixir_lang.beam.psi.*;

public interface ModuleStubElementTypes {
    ModuleElementType<ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>, ModuleDefinition> MODULE_DEFINITION = ModuleDefinitionType.INSTANCE;
    ModuleElementType<TypeDefinitionStub<TypeDefinition>, TypeDefinition> TYPE_DEFINITION = TypeDefinitionType.INSTANCE;
    ModuleElementType<CallDefinitionStub<CallDefinition>, CallDefinition> CALL_DEFINITION = CallDefinitionType.INSTANCE;
}
