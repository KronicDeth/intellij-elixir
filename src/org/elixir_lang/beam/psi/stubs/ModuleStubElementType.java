package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.ICompositeElementType;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class ModuleStubElementType<S extends StubElement, P extends PsiElement>
        extends ILightStubElementType<S, P> implements ICompositeElementType {
    protected ModuleStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, ElixirLanguage.INSTANCE);
    }

    @SuppressWarnings("MethodOverloadsMethodOfSuperclass")
    public abstract P createPsi(@NotNull ASTNode node);

    @Override
    public S createStub(@NotNull P psi, StubElement parentStub) {
        final String message = "Should not be called. Element=" + psi +
                "; class" + psi.getClass() +
                "; file=" + (psi.isValid() ? psi.getContainingFile() : "-");

        throw new UnsupportedOperationException(message);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "beam." + toString();
    }
}
