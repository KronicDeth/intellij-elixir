package org.elixir_lang.beam.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.stubs.ModuleElementType;
import org.elixir_lang.psi.stub.call.Stubbic;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public abstract class StubbicBase<T extends PsiElement> extends StubBase<T> implements Stubbic {
    /*
     * CONSTANTS
     */

    public static final boolean HAS_DO_BLOCK_OR_KEYWORD = true;

    /*
     * Fields
     */

    @NotNull
    private final Set<String> canonicalNameSet;
    @NotNull
    private final String name;

    /*
     * Constructors
     */

    public StubbicBase(@NotNull StubElement parentStub,
                       @NotNull ModuleElementType moduleElementType,
                       @NotNull String name) {
        super(parentStub, moduleElementType);
        this.canonicalNameSet = Collections.singleton(name);
        this.name = name;
    }

    /*
     * Instance Methods
     */

    /**
     * These names do not depend on aliases or nested modules.
     *
     * @return the canonical texts of the reference
     * @see PsiReference#getCanonicalText()
     */
    @Override
    public Set<String> canonicalNameSet() {
        return canonicalNameSet;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * Always have do blocks when decompiled
     *
     * @return {@code true}
     */
    @Override
    public boolean hasDoBlockOrKeyword() {
        return HAS_DO_BLOCK_OR_KEYWORD;
    }

}
