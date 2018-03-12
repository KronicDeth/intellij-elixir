package org.elixir_lang.psi.impl

import org.elixir_lang.psi.ElixirAtIdentifier

fun ElixirAtIdentifier.identifierName(): String {
    val node = node
    val identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET)

    assert(identifierNodes.size == 1)

    val identifierNode = identifierNodes[0]
    return identifierNode.text
}
