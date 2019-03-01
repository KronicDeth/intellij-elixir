package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

class Specifications(private val specificationList: List<Specification>) {
    operator fun get(index: Int) = specificationList.get(index)
    fun indexOf(specification: Specification) = specificationList.indexOf(specification)
    fun size() = specificationList.size
}
