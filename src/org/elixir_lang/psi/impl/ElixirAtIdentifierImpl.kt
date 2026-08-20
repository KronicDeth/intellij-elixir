package org.elixir_lang.psi.impl

import com.intellij.openapi.util.TextRange
import org.elixir_lang.psi.ElixirAtIdentifier


fun ElixirAtIdentifier.identifierName(): String {
    val node = node
    val identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET)

    assert(identifierNodes.size == 1)

    val identifierNode = identifierNodes[0]
    return identifierNode.text
}

/**
 * The [TextRange] of the identifier following the `@` sigil (i.e. the renamable name only, excluding
 * the leading `@`). Renaming/highlighting a module attribute must target this range so the `@` sigil -
 * which is fixed punctuation, not part of the name - is preserved.
 */
fun ElixirAtIdentifier.identifierTextRange(): TextRange {
    val identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET)

    assert(identifierNodes.size == 1)

    return identifierNodes[0].textRange
}
