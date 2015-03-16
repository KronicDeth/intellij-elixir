package org.elixir_lang.psi;

import java.util.List;

/**
 * A list of {@link org.elixir_lang.psi.KeywordPair}s.
 *
 * Created by luke.imhoff on 3/15/15.
 */
public interface KeywordList extends Quotable {
    public List<KeywordPair> getKeywordPairList();
}
