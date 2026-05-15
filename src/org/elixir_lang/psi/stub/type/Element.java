package org.elixir_lang.psi.stub.type;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class Element<S extends StubElement<T>, T extends PsiElement> extends IStubElementType<S, T> {
    public Element(@NonNls @NotNull String debugName) {
        super(debugName, ElixirLanguage.INSTANCE);
    }

    @Override
    public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {
    }

    @NotNull
    public String getExternalId() {
        return "elixir." + super.toString();
    }
}
