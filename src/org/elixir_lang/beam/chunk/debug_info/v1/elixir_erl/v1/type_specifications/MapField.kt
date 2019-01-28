package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

sealed class MapField {
    companion object {
        fun from(mapArgument: OtpErlangObject): MapField? =
                if (mapArgument is OtpErlangTuple && mapArgument.arity() == 4 &&
                        mapArgument.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                    val typeType = mapArgument.elementAt(2)

                    if (typeType is OtpErlangAtom) {
                        when (typeType.atomValue()) {
                            "map_field_assoc" -> mapFieldOptionalFrom(mapArgument.elementAt(3))
                            "map_field_exact" -> mapFieldRequiredFrom(mapArgument.elementAt(3))
                            else -> null
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }

        private fun mapFieldOptionalFrom(nameValue: OtpErlangObject): MapFieldOptional? =
                if (nameValue is OtpErlangList && nameValue.arity() == 2) {
                    MapFieldOptional(nameValue.elementAt(0), nameValue.elementAt(1))
                } else {
                    null
                }

        private fun mapFieldRequiredFrom(nameValue: OtpErlangObject): MapField? =
                if (nameValue is OtpErlangList && nameValue.arity() == 2) {
                    mapFieldRequiredFrom(nameValue.elementAt(0), nameValue.elementAt(1))
                } else {
                    null
                }

        private fun mapFieldRequiredFrom(name: OtpErlangObject, value: OtpErlangObject): MapField =
                ifAtom(name) { nameAtom ->
                    if (nameAtom.atomValue() == "__struct__") {
                        ifAtom(value) { valueAtom ->
                            MapFieldStruct(valueAtom)
                        }
                    } else {
                        null
                    } ?: MapFieldRequiredAtom(nameAtom, value)
                } ?: MapFieldRequiredNonAtom(name, value)

        private fun <T> ifAtom(type: OtpErlangObject, whenTrue: (atom: OtpErlangAtom) -> T): T? =
                if (type is OtpErlangTuple && type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom") {
                    type
                            .elementAt(2)
                            .let { it as? OtpErlangAtom }
                            ?.let { whenTrue(it) }
                } else {
                    null
                }
    }
}

data class MapFieldRequiredAtom(val name: OtpErlangAtom, val value: OtpErlangObject) : MapField()
data class MapFieldStruct(val value: OtpErlangAtom) : MapField()
data class MapFieldRequiredNonAtom(val name: OtpErlangObject, val value: OtpErlangObject) : MapField() {
    init {
        assert(name !is OtpErlangAtom)
    }
}

data class MapFieldOptional(val name: OtpErlangObject, val value: OtpErlangObject) : MapField()
