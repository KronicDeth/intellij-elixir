package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.*

class TypeSpecifications(private val typeSpecificationList: List<TypeSpecification>) {
    val specifications: Specifications by lazy {
        typeSpecificationList
                .mapNotNull { it as? Specification }
                .let { Specifications(it) }
    }

    val types: Types by lazy {
        typeSpecificationList
                .mapNotNull { it as? Type }
                .filter { isExported(it.name, it.arity) }
                .let { Types(it) }
    }

    operator fun get(index: Int) = typeSpecificationList.get(index)
    fun indexOf(typeSpecification: TypeSpecification): Int = typeSpecificationList.indexOf(typeSpecification)
    fun size() = typeSpecificationList.size

    fun isExported(name: String, arity: Int): Boolean =
        typeSpecificationList.any { it is ExportType && it.name == name && it.arity == arity }

    companion object {
        fun from(term: OtpErlangObject?, debugInfo: V1): TypeSpecifications? =
            if (term is OtpErlangList) {
                from(term, debugInfo)
            } else {
                null
            }

        private fun from(list: OtpErlangList, debugInfo: V1): TypeSpecifications =
            TypeSpecifications(list.mapNotNull { TypeSpecification.from(it) })
    }
}
