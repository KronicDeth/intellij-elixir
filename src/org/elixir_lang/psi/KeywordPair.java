package org.elixir_lang.psi;

/**
 * A keyword key and value used in a keyword list
 *
 * Created by luke.imhoff on 3/15/15.
 */
public interface KeywordPair extends Quotable {
    public Quotable getKeywordKey();
    public Quotable getKeywordValue();
}
