package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

class ModuleFunctionArguments(val module: String, val function: String, val arguments: List<OtpErlangObject>) {
    operator fun component1(): String = module
    operator fun component2(): String = function
    operator fun component3(): List<OtpErlangObject> = arguments

    companion object {
        private const val ARITY = 3

        private val LOGGER = Logger.getInstance(ModuleFunctionArguments::class.java)

        fun from(term: OtpErlangObject): ModuleFunctionArguments? =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> {
                    LOGGER.error("ModuleFunctionArguments (${inspect(term)}) is not an OtpErlangTuple")

                    null
                }
            }

        // Private Functions

        private fun arguments(tuple: OtpErlangTuple): List<OtpErlangObject>? {
            val arguments = tuple.elementAt(2)

            return when (arguments) {
                is OtpErlangList ->
                    arguments.iterator().asSequence().toList()
                is OtpErlangString ->
                    OtpErlangString.stringToCodePoints(arguments.stringValue()).map { OtpErlangInt(it) }
                else -> {
                    LOGGER.error("Arguments (${inspect(arguments)}) is not an OtpErlangList")

                    null
                }

            }
        }

        private fun from(tuple: OtpErlangTuple): ModuleFunctionArguments? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                module(tuple)?.let { module ->
                    function(tuple)?.let { function ->
                        arguments(tuple)?.let { arguments ->
                            ModuleFunctionArguments(module, function, arguments)
                        }
                    }
                }
            } else {
                LOGGER.error(
                        "Tuple arity ($arity) differs from expected arity ($ARITY) in ModuleFunctionArgument " +
                                "(${inspect(tuple)})"
                )

                null
            }
        }

        private fun function(tuple: OtpErlangTuple): String? {
            val function = tuple.elementAt(1)

            return if (function is OtpErlangAtom) {
                function.atomValue()
            } else {
                LOGGER.error("Function (${inspect(function)}) is not an OtpErlangAtom")

                null
            }
        }

        private fun module(tuple: OtpErlangTuple): String? {
            val module = tuple.elementAt(0)

            return if (module is OtpErlangAtom) {
                module.atomValue()
            } else {
                LOGGER.error("Module (${inspect(module)}) is not an OtpErlangAtom")

                null
            }
        }
    }
}
