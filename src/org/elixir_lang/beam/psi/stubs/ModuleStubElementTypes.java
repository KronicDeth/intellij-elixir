package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.beam.psi.CallDefinition;
import org.elixir_lang.beam.psi.CallDefinitionElement;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.beam.psi.ModuleElement;
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl;
import org.elixir_lang.beam.psi.impl.CallDefinitionStubImpl;
import org.elixir_lang.beam.psi.impl.ModuleImpl;
import org.elixir_lang.beam.psi.impl.ModuleStubImpl;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.elixir_lang.psi.stub.call.Deserialized.readGuarded;
import static org.elixir_lang.psi.stub.call.Deserialized.writeGuarded;

// See com.intellij.psi.impl.java.stubs.JavaStubElementTypes
public interface ModuleStubElementTypes {
    ModuleElementType CALL_DEFINITION = new ModuleElementType<CallDefinitionStub, CallDefinition>("CallDefinition") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new CallDefinitionElement(this);
        }

        @Override
        public CallDefinition createPsi(@NotNull CallDefinitionStub stub) {
            return new CallDefinitionImpl(stub);
        }

        @NotNull
        @Override
        public CallDefinitionStub deserialize(@NotNull StubInputStream stubInputStream,
                                              @NotNull StubElement parentStub) throws IOException {
            Deserialized deserialized = Deserialized.deserialize(stubInputStream);
            int callDefinitionClauseArity = deserializeCallDefinitionClauseArity(stubInputStream);

            return new CallDefinitionStubImpl((ModuleStub) parentStub, deserialized, callDefinitionClauseArity);
        }

        int deserializeCallDefinitionClauseArity(@NotNull StubInputStream stubInputStream) throws IOException {
            return readGuarded(stubInputStream, StubInputStream::readVarInt);
        }

        @Override
        public void serialize(@NotNull CallDefinitionStub stub,
                              @NotNull StubOutputStream stubOutputStream) throws IOException {
            super.serialize(stub, stubOutputStream);
            writeGuarded(
                    stubOutputStream,
                    guardedStubOutputStream -> guardedStubOutputStream.writeVarInt(stub.callDefinitionClauseHeadArity())
            );
        }
    };

    /**
     * See {@link com.intellij.psi.impl.java.stubs.JavaStubElementTypes#CLASS}
     */
    ModuleElementType MODULE = new ModuleElementType<ModuleStub, Module>("Module") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new ModuleElement(this);
        }

        @Override
        public Module createPsi(@NotNull ModuleStub stub) {
            return new ModuleImpl<>(stub);
        }

        @NotNull
        @Override
        public ModuleStub deserialize(@NotNull StubInputStream stubInputStream,
                                      @NotNull StubElement parentStub) throws IOException {
            Deserialized deserialized = Deserialized.deserialize(stubInputStream);

            return new ModuleStubImpl(parentStub, deserialized);
        }
    };
}
