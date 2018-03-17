package org.elixir_lang.psi.impl

object SigilImpl {
    @JvmStatic
    fun sigilName(sigil: org.elixir_lang.psi.Sigil): Char = sigil.node.getChildren(null)[1].chars[0]
}
