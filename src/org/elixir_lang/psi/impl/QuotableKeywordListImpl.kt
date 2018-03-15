package org.elixir_lang.psi.impl

import org.elixir_lang.psi.Quotable
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.hasKeywordKey


/**
 * The value associated with the keyword value.
 *
 * @param this@keywordValue The keyword list to search for `keywordKeyText`.
 * @param keywordKeyText the text of the keyword value.
 * @return the `PsiElement` associated with `keywordKeyText`.
 */
fun QuotableKeywordList.keywordValue(keywordKeyText: String): Quotable? {
    var keywordValue: Quotable? = null

    for (quotableKeywordPair in quotableKeywordPairList()) {
        if (hasKeywordKey(quotableKeywordPair, keywordKeyText)) {
            keywordValue = quotableKeywordPair.keywordValue
        }
    }

    return keywordValue
}
