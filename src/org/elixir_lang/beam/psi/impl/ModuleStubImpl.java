package org.elixir_lang.beam.psi.impl;

import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.beam.psi.stubs.ModuleStub;
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes;
import org.elixir_lang.psi.call.name.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.DEFMODULE;
import static org.elixir_lang.psi.call.name.Module.KERNEL;

/**
 * See {@link com.intellij.psi.impl.java.stubs.impl.PsiClassStubImpl}
 */
public class ModuleStubImpl<T extends Module> extends StubbicBase<T> implements ModuleStub<T> {
    public static final int RESOLVED_FINAL_ARITY = 2;
    public static final String RESOLVED_FUNCTION_NAME = DEFMODULE;
    public static final String RESOLVED_MODULE_NAME = KERNEL;

    public ModuleStubImpl(@NotNull StubElement parentStub,
                          @NotNull String name) {
        super(parentStub, ModuleStubElementTypes.MODULE, name);
    }

    /**
     * Arity of {@code defmodule .. do}.
     *
     * @return 2 (1 for name and 1 for do block)
     */
    @Override
    public Integer resolvedFinalArity() {
        return RESOLVED_FINAL_ARITY;
    }

    /**
     * @return {@link Function#DEFMODULE}
     */
    @Nullable
    @Override
    public String resolvedFunctionName() {
        return RESOLVED_FUNCTION_NAME;
    }

    /**
     * @return {@link org.elixir_lang.psi.call.name.Module#KERNEL}
     */
    @Nullable
    @Override
    public String resolvedModuleName() {
        return RESOLVED_MODULE_NAME;
    }
}
