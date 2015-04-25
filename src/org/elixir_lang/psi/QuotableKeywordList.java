package org.elixir_lang.psi;

import java.util.List;

/**
 * A list of {@link QuotableKeywordPair}s.
 *
 * Created by luke.imhoff on 3/15/15.
 */
public interface QuotableKeywordList extends Quotable {
    public List<QuotableKeywordPair> quotableKeywordPairList();
}
