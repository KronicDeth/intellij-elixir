package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.elixir_lang.beam.psi.CallDefinition;
import org.elixir_lang.beam.psi.CallDefinitionElement;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.beam.psi.ModuleElement;
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl;
import org.elixir_lang.beam.psi.impl.CallDefinitionStubImpl;
import org.elixir_lang.beam.psi.impl.ModuleImpl;
import org.elixir_lang.beam.psi.impl.ModuleStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

import static org.elixir_lang.psi.stub.type.call.Stub.readNameSet;

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
            return new CallDefinitionImpl<CallDefinitionStub>(stub);
        }

        @NotNull
        @Override
        public CallDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
            assert dataStream.readName().toString().equals(CallDefinitionStubImpl.RESOLVED_MODULE_NAME);

            String macro = dataStream.readName().toString();

            assert dataStream.readInt() == CallDefinitionStubImpl.RESOLVED_FINAL_ARITY;
            assert dataStream.readBoolean() == CallDefinitionStubImpl.HAS_DO_BLOCK_OR_KEYWORD;

            StringRef nameRef = dataStream.readName();
            String name = nameRef.toString();

            Set<StringRef> canonicalNameRefSet = readNameSet(dataStream);

            assert canonicalNameRefSet.size() == 1;
            assert canonicalNameRefSet.iterator().next().toString().equals(name);

            int callDefinitionClauseArity = dataStream.readInt();

            return new CallDefinitionStubImpl((ModuleStub) parentStub, macro, name, callDefinitionClauseArity);
        }

        @Override
        public void serialize(@NotNull CallDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
            super.serialize(stub, dataStream);
            dataStream.writeInt(stub.callDefinitionClauseHeadArity());
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
            return new ModuleImpl<ModuleStub>(stub);
        }

        @NotNull
        @Override
        public ModuleStub deserialize(@NotNull StubInputStream dataStream,
                                      @NotNull StubElement parentStub) throws IOException {
            assert dataStream.readName().toString().equals(ModuleStubImpl.RESOLVED_MODULE_NAME);
            assert dataStream.readName().toString().equals(ModuleStubImpl.RESOLVED_FUNCTION_NAME);
            assert dataStream.readInt() == ModuleStubImpl.RESOLVED_FINAL_ARITY;
            assert dataStream.readBoolean() == ModuleStubImpl.HAS_DO_BLOCK_OR_KEYWORD;

            StringRef nameRef = dataStream.readName();
            String name = nameRef.toString();

            Set<StringRef> canonicalNameRefSet = readNameSet(dataStream);

            assert canonicalNameRefSet.size() == 1;
            assert canonicalNameRefSet.iterator().next().toString().equals(name);

            return new ModuleStubImpl(parentStub, name);
        }
    };
}
