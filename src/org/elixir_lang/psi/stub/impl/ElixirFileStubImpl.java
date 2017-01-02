package org.elixir_lang.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.util.ArrayFactory;
import org.elixir_lang.beam.psi.stubs.ElixirFileStub;
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.call.CanonicallyNamed;
import org.jetbrains.annotations.NotNull;

public class ElixirFileStubImpl extends PsiFileStubImpl<ElixirFile> implements ElixirFileStub {
    public ElixirFileStubImpl() {
        super(null);
    }

    @NotNull
    @Override
    public CanonicallyNamed[] modulars() {
        return getChildrenByType(ModuleStubElementTypes.MODULE, new ArrayFactory<CanonicallyNamed>() {
            @NotNull
            @Override
            public CanonicallyNamed[] create(int count) {
                return new CanonicallyNamed[count];
            }
        });
    }
}
