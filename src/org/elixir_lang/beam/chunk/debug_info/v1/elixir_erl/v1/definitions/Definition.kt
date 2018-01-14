package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.chunk.Keyword
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import org.elixir_lang.beam.chunk.inspect

class Definition(
        val debugInfo: V1,
        nameArity: OtpErlangObject,
        macro: OtpErlangObject,
        metadata: OtpErlangObject,
        clauses: OtpErlangObject
) {
    private val nameArityTuple: OtpErlangTuple? = nameArityTuple(nameArity)

    val clauses: List<Clause>? by lazy { clauses(clauses, this) }
    val macro: String? = macro(macro)
    val metdata: Keyword? = org.elixir_lang.beam.chunk.from(metadata)

    val name: String? by lazy {
        nameArityTuple?.let {
            nameArityToName(it)
        }
    }

    val arity: Int? by lazy {
        nameArityTuple?.let {
            nameArityToArity(it)
        }
    }

    companion object {
        fun from(term: OtpErlangObject, debugInfo: V1): Definition? =
            if (term is OtpErlangTuple) {
                from(term, debugInfo)
            } else {
                logger.error("""
                             Definition is not a tuple

                             ## definition

                             ```elixir
                             ${inspect(term)}
                             ```
                             """.trimIndent())

                null
            }

        private fun clauses(list: OtpErlangList, definition: Definition): List<Clause> =
                list.mapNotNull { Clause.from(it, definition) }

        private fun clauses(term: OtpErlangObject, definition: Definition): List<Clause>? =
                if (term is OtpErlangList) {
                    clauses(term, definition)
                } else {
                    logger.error("""
                                 Clauses it not a list

                                 ## clauses

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun from(term: OtpErlangTuple, debugInfo: V1): Definition? {
            val arity = term.arity()

            return if (arity == 4) {
                val (nameArity, macro, metadata, quoted) = term
                Definition(debugInfo, nameArity, macro, metadata, quoted)
            } else {
                logger.error("""
                             Definition tuple arity ($arity) is not 4

                             ```elixir
                             ${inspect(term)}
                             ```
                             """.trimIndent())

                null
            }
        }

        private fun macro(macroObject: OtpErlangObject): String? =
                if (macroObject is OtpErlangAtom) {
                    macroObject.atomValue()
                } else {
                    logger.error("""
                                 macro is not an atom

                                 ## macro

                                 ```elixir
                                 ${inspect(macroObject)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun name(nameObject: OtpErlangObject): String? =
            if (nameObject is OtpErlangAtom) {
                nameObject.atomValue()
            } else {
                logger.error("""
                             name is not an atom

                             ## name

                             ```elixir
                             ${inspect(nameObject)}
                             ```
                             """.trimIndent())

                null
            }

        private fun nameArityToArity(nameArity: OtpErlangTuple): Int? {
            val arityObject = nameArity.elementAt(1)

            return if (arityObject is OtpErlangLong) {
                arityObject.intValue()
            } else {
                logger.error("""
                             arity (${arityObject.javaClass}) is not a long

                             # arity

                             ```elixir
                             ${inspect(arityObject)}
                             ```
                             """.trimIndent())

                null
            }
        }

        private fun nameArityToName(nameArity: OtpErlangTuple): String? {
            val nameObject = nameArity.elementAt(0)

            return if (nameObject is OtpErlangAtom) {
                name(nameObject)
            } else {
                logger.error("""
                             name is not an atom

                             ## name

                             ```elixir
                             ${inspect(nameObject)}
                             ```
                             """.trimIndent())

                null
            }
        }

        private fun nameArityTuple(nameArity: OtpErlangObject): OtpErlangTuple? =
                if (nameArity is OtpErlangTuple) {
                    nameArityTuple(nameArity)
                } else {
                    logger.error("""
                                 Definition nameArity is not a tuple

                                 ## nameArity

                                 ```elixir
                                 ${inspect(nameArity)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun nameArityTuple(nameArity: OtpErlangTuple): OtpErlangTuple? {
            val arity = nameArity.arity()

            return if (arity == 2) {
                nameArity
            } else {
                logger.error("""
                             nameArity tuple arity (${arity}) is not 2

                             ## nameArity tuple

                             ```elixir
                             ${inspect(nameArity)}
                             ```
                             """.trimIndent())

                null
            }
        }

    }
}

operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
