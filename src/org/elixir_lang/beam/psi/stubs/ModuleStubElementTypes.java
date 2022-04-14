package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.elixir_lang.beam.psi.*;
import org.elixir_lang.beam.psi.impl.*;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.type.Visibility;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.elixir_lang.psi.stub.call.Deserialized.readGuarded;
import static org.elixir_lang.psi.stub.call.Deserialized.writeGuarded;
import static org.elixir_lang.psi.stub.type.Named.indexStubbic;

// See com.intellij.psi.impl.java.stubs.JavaStubElementTypes
public interface ModuleStubElementTypes {
    /**
     * See {@link com.intellij.psi.impl.java.stubs.JavaStubElementTypes#CLASS}
     */
    ModuleElementType<ModuleStub<?>, org.elixir_lang.beam.psi.Module> MODULE = new ModuleElementType<ModuleStub<?>, org.elixir_lang.beam.psi.Module>("Module") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new ModuleElement(this);
        }

        @Override
        public org.elixir_lang.beam.psi.Module createPsi(@NotNull ModuleStub stub) {
            return new ModuleImpl<>(stub);
        }

        @Override
        public ModuleStub<?> createStub(@NotNull org.elixir_lang.beam.psi.Module psi, StubElement<?> parentStub) {
            return super.createStub(psi, parentStub);
        }

        @Override
        public void serialize(@NotNull ModuleStub<?> stub, @NotNull StubOutputStream stubOutputStream) throws IOException {
            Deserialized.serialize(stubOutputStream, stub);
        }

        @NotNull
        @Override
        public ModuleStub<?> deserialize(@NotNull StubInputStream stubInputStream,
                                         @NotNull StubElement parentStub) throws IOException {
            Deserialized deserialized = Deserialized.deserialize(stubInputStream);

            return new ModuleStubImpl(parentStub, deserialized);
        }

        @Override
        public void indexStub(@NotNull ModuleStub<?> stub, @NotNull IndexSink sink) {
            indexStubbic(stub, sink);
        }
    };

    ModuleElementType<TypeDefinitionStub<?>, TypeDefinition> TYPE_DEFINITION = new ModuleElementType<TypeDefinitionStub<?>, TypeDefinition>("TypeDefinition") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new TypeDefinitionElement(this);
        }

        @Override
        public TypeDefinition createPsi(@NotNull TypeDefinitionStub<?> stub) {
            return new TypeDefinitionImpl<>(stub);
        }

        @Override
        public TypeDefinitionStub<?> createStub(@NotNull TypeDefinition psi, StubElement<?> parentStub) {
            return super.createStub(psi, parentStub);
        }

        @NotNull
        @Override
        public TypeDefinitionStub<?> deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
            Visibility visibility = Visibility.valueOf(dataStream.readNameString());
            String name = dataStream.readNameString();
            int arity = dataStream.readVarInt();

            return new TypeDefinitionStubImpl((ModuleStubImpl<ModuleImpl<?>>) parentStub, visibility, name, arity);
        }

        @Override
        public void serialize(@NotNull TypeDefinitionStub<?> stub, @NotNull StubOutputStream stubOutputStream) throws IOException {
            stubOutputStream.writeName(stub.getVisibility().toString());
            stubOutputStream.writeName(stub.getName());
            stubOutputStream.writeVarInt(stub.getArity());
        }

        @Override
        public void indexStub(@NotNull TypeDefinitionStub<?> stub, @NotNull IndexSink sink) {
            sink.occurrence(AllName.KEY, stub.getName());
        }
    };

    ModuleElementType<CallDefinitionStub<?>, CallDefinition> CALL_DEFINITION = new ModuleElementType<CallDefinitionStub<?>, CallDefinition>("CallDefinition") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new CallDefinitionElement(this);
        }

        @Override
        public CallDefinition createPsi(@NotNull CallDefinitionStub stub) {
            return new CallDefinitionImpl<>(stub);
        }

        @NotNull
        @Override
        public CallDefinitionStub<?> deserialize(@NotNull StubInputStream stubInputStream,
                                                 @NotNull StubElement parentStub) throws IOException {
            Deserialized deserialized = Deserialized.deserialize(stubInputStream);
            int callDefinitionClauseArity = deserializeCallDefinitionClauseArity(stubInputStream);

            return new CallDefinitionStubImpl((ModuleStub<?>) parentStub, deserialized, callDefinitionClauseArity);
        }

        int deserializeCallDefinitionClauseArity(@NotNull StubInputStream stubInputStream) throws IOException {
            return readGuarded(stubInputStream, StubInputStream::readVarInt);
        }

        @Override
        public void serialize(@NotNull CallDefinitionStub stub,
                              @NotNull StubOutputStream stubOutputStream) throws IOException {
            Deserialized.serialize(stubOutputStream, stub);
            writeGuarded(
                    stubOutputStream,
                    guardedStubOutputStream -> guardedStubOutputStream.writeVarInt(stub.callDefinitionClauseHeadArity())
            );
        }

        @Override
        public void indexStub(@NotNull CallDefinitionStub<?> stub, @NotNull IndexSink sink) {
            indexStubbic(stub, sink);
        }
    };

}
