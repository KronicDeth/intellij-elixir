package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.code.Identifier.inspectAsFunction

object Type {
    private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(Type.javaClass)

    fun toMacroString(term: OtpErlangObject?): String? =
        when (term) {
            is OtpErlangTuple -> toMacroString(term)
            else -> null
        }

    fun toMacroString(term: OtpErlangTuple): String? {
        val arity = term.arity()

        return when (arity) {
            3 -> {
                val tag = (term.elementAt(0) as? OtpErlangAtom)?.atomValue()

                when (tag) {
                    "atom" -> atomToMacroString(term.elementAt(2))
                    "remote_type" -> remoteTypeToMacroString(term.elementAt(2))
                    "var" -> Var.nameToMacroString(term.elementAt(2))
                    else -> {
                        logger.error("""
                                 Type (${inspect(term)}) tag ($tag) is unrecognized
                                 """.trimIndent())

                        null
                    }
                }
            }
            4 -> {
                val tag = (term.elementAt(0) as? OtpErlangAtom)?.atomValue()

                when (tag) {
                    "type" -> {
                        val kind = (term.elementAt(2) as? OtpErlangAtom)?.atomValue()

                        when (kind) {
                            // built-ins
                            "any", "atom", "binary", "char", "iodata", "non_neg_integer", "pid" -> "$kind()"
                            "fun" -> anonymousFunctionToMacroString(term.elementAt(3))
                            "list" -> listToMacroString(term.elementAt(3))
                            "product" -> productToMacroString(term.elementAt(3))
                            "tuple" -> tupleToMacroString(term.elementAt(3))
                            "union" -> unionToMacroString(term.elementAt(3))
                            else -> {
                                logger.error("""
                                     Type kind (${kind}) not recognized
                                     """.trimIndent())

                                null
                            }
                        }
                    }
                    "user_type" -> userTypeToMacroString(term)
                    else -> null
                }
            }
            else -> {
                logger.error("""
                             Arity ($arity) of type (${inspect(term)}) is unrecognized
                             """.trimIndent())

                null
            }
        }
    }

    private fun anonymousFunctionToMacroString(term: OtpErlangList): String =
            if (term.arity() == 2) {
                "(${toMacroString(term.elementAt(0))} -> ${toMacroString(term.elementAt(1))})"
            } else {
                "(... -> ...)"
            }

    private fun anonymousFunctionToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> anonymousFunctionToMacroString(term)
                else -> "(... -> ...)"
            }

    private fun argumentsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { toMacroString(it) ?: "..." }

    private fun argumentsToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> argumentsToMacroString(term)
                else -> "..."
            }

    private fun atomToMacroString(term: OtpErlangAtom): String = inspect(term)

    private fun atomToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> atomToMacroString(term)
                else -> "..."
            }

    private fun functionToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> functionToMacroString(term)
                else -> "unknown_function"
            }

    private fun functionToMacroString(term: OtpErlangTuple): String =
        if ((term.elementAt(0) as? OtpErlangAtom)?.atomValue() == "atom") {
            (term.elementAt(2) as? OtpErlangAtom)?.let { inspectAsFunction(it) }
        } else {
            null
        } ?: "unknown_function"

    private fun listToMacroString(term: OtpErlangList): String =
            "[${term.joinToString(", ") { toMacroString(it) ?: "..." }}]"

    private fun listToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> listToMacroString(term)
                else -> "[...]"
            }

    private fun moduleToMacroString(term: OtpErlangObject): String =
           when (term) {
               is OtpErlangTuple -> moduleToMacroString(term)
               else -> "UNKNOWN_MODULE"
           }

    private fun moduleToMacroString(term: OtpErlangTuple): String =
        if ((term.elementAt(0) as? OtpErlangAtom)?.atomValue() == "atom") {
            (term.elementAt(2) as? OtpErlangAtom)?.let { inspect(it) }
        } else {
            null
        } ?: "UNKNOWN_MODULE"

    private fun productToMacroString(term: OtpErlangObject): String? =
        when (term) {
            is OtpErlangList -> productToMacroString(term)
            else -> null
        }

    private fun productToMacroString(term: OtpErlangList): String =
        term.joinToString(", ") { toMacroString(it) ?: "..." }

    private fun remoteTypeToMacroString(term: OtpErlangList): String? =
        if (term.arity() == 3) {
            val moduleMacroString = term.elementAt(0).let { moduleToMacroString(it) }
            val functionMacroString = term.elementAt(1).let { functionToMacroString(it) }
            val argumentsMacroString = term.elementAt(2).let { argumentsToMacroString(it) }

            "$moduleMacroString.$functionMacroString($argumentsMacroString)"
        } else {
            null
        }

    private fun remoteTypeToMacroString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangList -> remoteTypeToMacroString(term)
                else -> null
            }

    private fun tupleToMacroString(term: OtpErlangList): String =
            "{${term.joinToString(", ") { toMacroString(it) ?: "..." }}}"

    private fun tupleToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> tupleToMacroString(term)
                else -> "{...}"
            }

    private fun unionToMacroString(term: OtpErlangObject): String? =
        when (term) {
            is OtpErlangList -> unionToMacroString(term)
            else -> null
        }

    private fun unionToMacroString(term: OtpErlangList): String =
        term.joinToString(" | ") { toMacroString(it) ?: "..." }

    private fun userTypeToMacroString(term: OtpErlangTuple): String {
        val nameMacroString = (term.elementAt(2) as? OtpErlangAtom)?.atomValue() ?: "unknown_user_type"
        val argumentsMacroString = term.elementAt(3).let { argumentsToMacroString(it) }

        return "$nameMacroString($argumentsMacroString)"
    }
}

