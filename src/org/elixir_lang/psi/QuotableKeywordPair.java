package org.elixir_lang.psi;

import com.intellij.psi.NavigatablePsiElement;

/**
 * A keyword key and value that is Quotable.
 *
 * Created by luke.imhoff on 3/15/15.
 */
public interface QuotableKeywordPair extends NavigatablePsiElement, Quotable {
    Quotable getKeywordKey();
    Quotable getKeywordValue();
}
