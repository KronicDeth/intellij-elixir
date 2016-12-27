package org.elixir_lang.beam.psi.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.beam.psi.stubs.ModuleStub;
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes;
import org.jetbrains.annotations.NotNull;

/**
 * See {@link com.intellij.psi.impl.java.stubs.impl.PsiClassStubImpl}
 */
public class ModuleStubImpl<T extends Module> extends StubBase<T> implements ModuleStub<T> {
    @NotNull
    private final String name;

    public ModuleStubImpl(@NotNull StubElement parentStub,
                          @NotNull String name) {
        super(parentStub, ModuleStubElementTypes.MODULE);
        this.name = name;
    }
}
