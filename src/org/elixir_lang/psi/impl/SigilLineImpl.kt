package org.elixir_lang.psi.impl

import org.elixir_lang.psi.SigilLine

fun terminator(sigilLine: SigilLine): Char = sigilLine.node.getChildren(null)[4].chars[0]
