package org.elixir_lang.psi.impl

object SigilHeredocImpl {
    @JvmStatic
    fun indentation(sigilHeredoc: org.elixir_lang.psi.SigilHeredocLiteral): Int = sigilHeredoc.heredocPrefix.textLength
}
