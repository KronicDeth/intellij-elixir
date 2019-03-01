package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

class Opaques(private val opaqueList: List<Opaque>) {
    operator fun get(index: Int): Opaque = opaqueList.get(index)
    fun indexOf(opaque: Opaque): Int = opaqueList.indexOf(opaque)
    fun size(): Int = opaqueList.size
}
