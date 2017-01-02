package org.elixir_lang.psi.stub.type;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.NamedStubBase;
import org.elixir_lang.psi.stub.call.Stubbic;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class Named<S extends NamedStubBase<T>, T extends PsiNameIdentifierOwner> extends Element<S, T> {
    public Named(@NonNls @NotNull String debugName) {
        super(debugName);
    }

    public static <T extends Stubbic> void indexStubbic(@NotNull final T stubbic, @NotNull final IndexSink sink) {
        String name = stubbic.getName();

        if (name != null) {
            sink.occurrence(AllName.KEY, name);
        }

        @SuppressWarnings("unchecked") Set<String> canonicalNameSet = stubbic.canonicalNameSet();

        for (String canonicalName : canonicalNameSet) {
            if (!canonicalName.equals(name)) {
                sink.occurrence(AllName.KEY, canonicalName);
            }
        }
    }

    @Override
    public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {
        if (stub instanceof Stubbic) {
            indexStubbic((Stubbic) stub, sink);
        } else {
            String name = stub.getName();

            if (name != null) {
                sink.occurrence(AllName.KEY, name);
            }
        }
    }

    @NotNull
    public String getExternalId() {
        return "elixir." + super.toString();
    }
}
