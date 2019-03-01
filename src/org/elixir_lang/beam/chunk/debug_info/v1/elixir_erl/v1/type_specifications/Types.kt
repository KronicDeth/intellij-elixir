package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

class Types(private val typeList: List<Type>) {
    operator fun get(index: Int) = typeList.get(index)
    fun indexOf(type: Type): Int = typeList.indexOf(type)
    fun size() = typeList.size
}
