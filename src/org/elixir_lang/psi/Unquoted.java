package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * A PsiElement that is quoted as nothing and should be skipped when a Quotable parent is enumerating it's children for
 * quoting.
 *
 * Created by luke.imhoff on 2/21/15.
 */
public interface Unquoted extends PsiElement {
}
