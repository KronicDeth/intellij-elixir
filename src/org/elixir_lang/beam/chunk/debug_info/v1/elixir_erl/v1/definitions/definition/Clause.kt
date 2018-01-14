package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition

import com.ericsson.otp.erlang.*
import org.elixir_lang.Macro
import org.elixir_lang.Macro.ifTagged3TupleTo
import org.elixir_lang.Macro.ifTupleTo
import org.elixir_lang.beam.chunk.Keyword
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Definition
import org.elixir_lang.beam.chunk.inspect


class Clause(
        val definition: Definition,
        metadata: OtpErlangObject,
        arguments: OtpErlangObject,
        guards: OtpErlangObject,
        val block: OtpErlangObject
) {
    val metadata: Keyword? = org.elixir_lang.beam.chunk.from(metadata)
    val arguments: OtpErlangList? = arguments as? OtpErlangList
    private val guards: OtpErlangList? = guards as? OtpErlangList
    val head by lazy {
        "${definition.name}(${argumentsToString(this.arguments)})${guardsToString(this.guards)}"
    }

    companion object {
        fun from(term: OtpErlangObject, definition: Definition): Clause? =
                if (term is OtpErlangTuple) {
                    from(term, definition)
                } else {
                    logger.error("""
                                 Clause is not a tuple

                                 ## clause

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun argumentsToString(arguments: OtpErlangList?): String =
                arguments?.joinToString(", ") { argument -> Macro.toString(argument) } ?: ""

        const val expectedArity = 4

        private fun from(tuple: OtpErlangTuple, definition: Definition): Clause? {
            val arity = tuple.arity()

            return if (arity == expectedArity) {
                // https://github.com/elixir-lang/elixir/blob/8c05bb078d715504a9d34343ad807275909474ed/lib/elixir/lib/exception.ex#L231
                val (metadata, arguments, guards, block) = tuple

                Clause(definition, metadata, arguments, guards, block)
            } else {
                logger.error("""
                             Clause arity (${arity}) is not ${expectedArity}

                             ## clause

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """.trimIndent())

                null
            }
        }

        private fun guardsToString(guards: OtpErlangList?): String =
                if (guards != null) {
                    if (guards.arity() == 0) {
                        ""
                    } else {
                        assert(guards.arity() == 1)

                        " when ${Macro.toString(rewriteGuard(guards.elementAt(0)))}"
                    }
                } else {
                    " when ?"
                }

    private inline fun <T> ifCallConvertArgumentsTo(
            term: OtpErlangObject,
            module: String,
            name: String,
            crossinline argumentsTo: (OtpErlangList) -> T?
    ): T? =
        ifTupleTo(term, 3) { tuple ->
            ifTagged3TupleTo(tuple.elementAt(0), ".") { function ->
                (function.elementAt(2) as? OtpErlangList)?.let { functionArguments ->
                    if (functionArguments.arity() == 2 &&
                            functionArguments.elementAt(0) == OtpErlangAtom(module) &&
                            functionArguments.elementAt(1) == OtpErlangAtom(name)) {
                        (tuple.elementAt(2) as? OtpErlangList)?.let { arguments ->
                            argumentsTo(arguments)
                        }
                    } else {
                        null
                    }
                }
            }
        }

    private inline fun <T> ifElementCallConvertArgumentsTo(
            term: OtpErlangObject,
            crossinline argumentsTo: (OtpErlangObject, OtpErlangObject) -> T?
    ): T? =
        ifCallConvertArgumentsTo(term, "erlang", "element") { arguments ->
            if (arguments.arity() == 2) {
                argumentsTo(arguments.elementAt(0), arguments.elementAt(1))
            } else {
                null
            }
        }


        // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L302-L316
        private fun rewriteGuard(guard: OtpErlangObject): OtpErlangObject =
                Macro.prewalk(guard) { macro ->
                    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L304-L308
                    ifElementCallConvertArgumentsTo(macro) { first, second ->
                        // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L307-L308
                        if (first is OtpErlangLong) {
                            OtpErlangTuple(arrayOf(
                                    OtpErlangAtom("elem"),
                                    OtpErlangList(),
                                    OtpErlangList(arrayOf(
                                            second,
                                            OtpErlangLong(first.longValue() - 1)
                                    ))
                            ))
                        } else {
                            // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L304-L305
                            ifCallConvertArgumentsTo(first, "erlang", "+") { plusArguments ->
                                if (plusArguments.arity() == 2 && plusArguments.elementAt(1) == OtpErlangLong(1)) {
                                    OtpErlangTuple(arrayOf(
                                            OtpErlangAtom("elem"),
                                            OtpErlangList(),
                                            OtpErlangList(arrayOf(
                                                    second,
                                                    plusArguments.elementAt(0)
                                            ))
                                    ))
                                } else {
                                    null
                                }
                            }
                        }
                    } ?:
                            // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L310-L311
                            Macro.ifTagged3TupleTo(macro, ".") { tuple ->
                                (tuple.elementAt(2) as? OtpErlangList)?.let { arguments ->
                                    if (arguments.arity() == 2 && arguments.elementAt(0) == OtpErlangAtom("erlang")) {
                                        rewriteGuardCall(arguments.elementAt(1))
                                    } else {
                                        null
                                    }
                                }
                            } ?:
                            macro
                }

        // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/exception.ex#L318-L329
        private fun rewriteGuardCall(operator: OtpErlangAtom): OtpErlangObject =
                when (operator.atomValue()) {
                    "orelse" -> OtpErlangAtom("or")
                    "andalso" -> OtpErlangAtom("and")
                    "=<" -> OtpErlangAtom("<=")
                    "/=" -> OtpErlangAtom("!=")
                    "=:=" -> OtpErlangAtom("===")
                    "=/=" -> OtpErlangAtom("!==")
                    "band", "bor", "bnot", "bsl", "bsr", "bxor" ->
                        OtpErlangTuple(arrayOf(
                                OtpErlangAtom("."),
                                OtpErlangList(),
                                OtpErlangList(arrayOf(
                                        OtpErlangAtom("Elixir.Bitwise"), operator
                                ))
                        ))
                    "xor", "element", "size" ->
                        OtpErlangTuple(arrayOf(
                                OtpErlangAtom("."),
                                OtpErlangList(),
                                OtpErlangList(arrayOf(
                                        OtpErlangAtom("erlang"), operator
                                ))
                        ))
                    else -> operator
                }

        private fun rewriteGuardCall(guardCall: OtpErlangObject): OtpErlangObject =
                when (guardCall) {
                    is OtpErlangAtom -> rewriteGuardCall(guardCall)
                    else -> guardCall
                }

    }
}

private operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
private operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
private operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
