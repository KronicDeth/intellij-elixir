package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.impl.cache.RecordUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.io.StringRef;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.beam.psi.impl.ModuleStubImpl;
import org.elixir_lang.beam.psi.impl.ModuleImpl;
import org.elixir_lang.psi.stub.call.Stub;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

import static org.elixir_lang.beam.psi.impl.ModuleStubImpl.*;
import static org.elixir_lang.psi.stub.type.Named.indexStubbic;
import static org.elixir_lang.psi.stub.type.call.Stub.readNameSet;
import static org.elixir_lang.psi.stub.type.call.Stub.serializeStubbic;

public class ModuleElementType extends ModuleStubElementType<ModuleStub, Module> {
    public ModuleElementType(@NotNull String id) {
        super(id);
    }

    @Override
    public Module createPsi(@NotNull ASTNode node) {
        return new ModuleImpl(node);
    }

    @Override
    public ModuleStub createStub(LighterAST tree, LighterASTNode node, StubElement parentStub) {
        String name = null;

        for (final LighterASTNode child : tree.getChildren(node)) {
            final IElementType type = child.getTokenType();

            if (type == JavaTokenType.IDENTIFIER) {
                name = RecordUtil.intern(tree.getCharTable(), child);
            }
        }

        return new ModuleStubImpl(parentStub, name);
    }

    @Override
    public Module createPsi(@NotNull ModuleStub stub) {
        return new ModuleImpl<ModuleStub>(stub);
    }

    /**
     * @param stub
     * @param dataStream
     * @throws IOException
     */
    @Override
    public void serialize(@NotNull ModuleStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        serializeStubbic(stub, dataStream);
    }

    @NotNull
    @Override
    public ModuleStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        assert dataStream.readName().toString().equals(RESOLVED_MODULE_NAME);
        assert dataStream.readName().toString().equals(RESOLVED_FUNCTION_NAME);
        assert dataStream.readInt() == RESOLVED_FINAL_ARITY;
        assert dataStream.readBoolean() == HAS_DO_BLOCK_OR_KEYWORD;

        StringRef nameRef = dataStream.readName();
        String name = nameRef.toString();

        Set<StringRef> canonicalNameRefSet = readNameSet(dataStream);

        assert canonicalNameRefSet.size() == 1;
        assert canonicalNameRefSet.iterator().next().toString().equals(name);

        return new ModuleStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull ModuleStub stub, @NotNull IndexSink sink) {
        indexStubbic(stub, sink);
    }

    @NotNull
    @Override
    public ASTNode createCompositeNode() {
        return null;
    }
}
