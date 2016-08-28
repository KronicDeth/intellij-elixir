package org.elixir_lang.psi.stub.type.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Stub<Stub extends org.elixir_lang.psi.stub.call.Stub<Psi>,
        Psi extends org.elixir_lang.psi.call.StubBased> extends org.elixir_lang.psi.stub.type.Named<Stub, Psi> {

    /*
     * Static Methods
     */

    public static boolean isModular(Call call) {
        return Implementation.is(call) || Module.is(call) || Protocol.is(call);
    }

    /*
     * Protected Static Methods
     */

    protected static Collection<StringRef> readNameCollection(@NotNull StubInputStream dataStream) throws IOException {
        int nameCollectionSize = dataStream.readInt();

        Collection<StringRef> canonicalNameCollection = new ArrayList<StringRef>(nameCollectionSize);

        for (int i = 0; i < nameCollectionSize; i++) {
            canonicalNameCollection.add(dataStream.readName());
        }

        return canonicalNameCollection;
    }

    /*
     * Private Static Methods
     */

    private static void writeNameCollection(@NotNull StubOutputStream dataStream,
                                            @NotNull Collection<String> nameCollection) throws IOException {
        dataStream.writeInt(nameCollection.size());

        for (String name : nameCollection) {
            dataStream.writeName(name);
        }
    }

    /*
     * Constructors
     */

    public Stub(@NotNull String debugName) {
        super(debugName);
    }

    /*
     * Instance Methods
     */

    @Override
    public void serialize(@NotNull Stub stub,
                          @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.resolvedModuleName());
        dataStream.writeName(stub.resolvedFunctionName());
        dataStream.writeInt(stub.resolvedFinalArity());
        dataStream.writeBoolean(stub.hasDoBlockOrKeyword());
        dataStream.writeName(stub.getName());
        writeNameCollection(dataStream, stub.canonicalNameCollection());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        Call call = (Call) node.getPsi();

        return isNameable(call) && hasNameOrCanonicalNames(call);
    }

    /*
     * Private Instance Methods
     */

    private boolean hasCanonicalNames(Call call) {
        boolean hasCanonicalNames = false;

        if (call instanceof StubBased) {
            StubBased stubBased = (StubBased) call;

            hasCanonicalNames = stubBased.canonicalNameCollection().size() > 0;
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
        return CallDefinitionHead.is(call) && CallDefinitionHead.enclosingDelegationCall(call) != null;
    }

    private boolean isEnclosableByModular(Call call) {
        return CallDefinitionClause.is(call) ||
                /* skip CallDefinitionHead because there can be false positives the the ancestor calls need to be
                   checked */
                CallDefinitionSpecification.is(call) ||
                // skip CallDefinitionHead because it is covered by CallDefinitionClause
                Callback.is(call);
    }


    private boolean isNameable(Call call) {
        return isEnclosableByModular(call) || isDelegationCallDefinitionHead(call) || isModular(call);
    }
}
