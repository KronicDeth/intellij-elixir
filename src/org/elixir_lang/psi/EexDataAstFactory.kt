package org.elixir_lang.psi

import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl
import com.intellij.psi.tree.IElementType

/**
 * Makes an `EEx Data` leaf - template content with no Elixir code, in `.eex`/`.leex`/`.heex`
 * files and `~H` sigils - a genuine [com.intellij.psi.templateLanguages.OuterLanguageElement],
 * as the HTML root already does for its own outer spans.
 *
 * [com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider.findElementAt] skips outer
 * elements when several roots have a leaf at one offset; otherwise the winner follows
 * `getLanguages()` iteration order, which is unspecified and, for an injected fragment, a plain
 * `HashSet` - so caret-based lookups (Symbol rename, find usages) on a component tag in a `~H`
 * sigil were non-deterministic. Only the leaf class changes; the element type stays
 * [ElixirTypes.EEX_DATA], so no grammar or `gen/` change is involved.
 */class EexDataAstFactory : ASTFactory() {
    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? =
        if (type === ElixirTypes.EEX_DATA) OuterLanguageElementImpl(type, text) else null
}
