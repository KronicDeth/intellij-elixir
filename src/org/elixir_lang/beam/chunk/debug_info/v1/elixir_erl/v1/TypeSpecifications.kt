package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.*

class TypeSpecifications(private val typeSpecificationList: List<TypeSpecification>) {
    val callbacks: Callbacks by lazy {
        typeSpecificationList
                .mapNotNull { it as? Callback }
                .let { Callbacks(it) }
    }

    val optionalCallbacks: OptionalCallbacks by lazy {
        typeSpecificationList
                .mapNotNull { it as? OptionalCallback }
                .let { OptionalCallbacks(it) }
    }

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

    val opaques: Opaques by lazy {
        typeSpecificationList
                .mapNotNull { it as? Opaque }
                .let { Opaques(it) }
    }

    operator fun get(index: Int) = typeSpecificationList.get(index)
    fun indexOf(typeSpecification: TypeSpecification): Int = typeSpecificationList.indexOf(typeSpecification)
    fun size() = typeSpecificationList.size

    fun isExported(name: String, arity: Int): Boolean =
        typeSpecificationList.any { it is ExportType && it.name == name && it.arity == arity }

    companion object {
        fun from(term: OtpErlangObject?): TypeSpecifications? =
            if (term is OtpErlangList) {
                from(term)
            } else {
                null
            }

        private fun from(list: OtpErlangList): TypeSpecifications =
            TypeSpecifications(list.mapNotNull { TypeSpecification.from(it) })
    }
}
