package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

public interface MaybeModuleName extends PsiElement {
    /**
     * @return true if this element functions as a Module name in a `defmodule <element> do end` call.
     */
    boolean isModuleName();
}
