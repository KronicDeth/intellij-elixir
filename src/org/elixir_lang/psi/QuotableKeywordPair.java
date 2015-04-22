package org.elixir_lang.psi;

/**
 * A keyword key and value that is Quotable.
 *
 * Created by luke.imhoff on 3/15/15.
 */
public interface QuotableKeywordPair extends Quotable {
    public Quotable getKeywordKey();
    public Quotable getKeywordValue();
}
