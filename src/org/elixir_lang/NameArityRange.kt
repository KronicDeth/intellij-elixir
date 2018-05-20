package org.elixir_lang

typealias ArityRange = IntRange

fun ArityRange.overlaps(other: ArityRange): Boolean =
        this.contains(other.start) || this.contains(other.endInclusive) || other.contains(this.start)

data class NameArityRange(val name: Name, val arityRange: ArityRange)
