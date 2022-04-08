package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.*

class TypeSpecifications(list: OtpErlangList) {
    private val typeSpecificationList: List<TypeSpecification> = list.mapNotNull { TypeSpecification.from(this, it) }

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

    fun isExported(nameArity: NameArity): Boolean =
        typeSpecificationList.any { it is ExportType && it.nameArity == nameArity }

    companion object {
        fun from(term: OtpErlangObject?): TypeSpecifications? =
                if (term is OtpErlangList) {
                    TypeSpecifications(term)
                } else {
                    null
                }
    }
}
