package org.elixir_lang.psi.stub.type;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.NamedStubBase;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class Named<S extends NamedStubBase<T>, T extends PsiNameIdentifierOwner> extends Element<S, T> {
    public Named(@NonNls @NotNull String debugName) {
        super(debugName);
    }

    @Override
    public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {
        sink.occurrence(AllName.KEY, stub.getName());
    }

    @NotNull
    public String getExternalId() {
        return "elixir." + super.toString();
    }
}
