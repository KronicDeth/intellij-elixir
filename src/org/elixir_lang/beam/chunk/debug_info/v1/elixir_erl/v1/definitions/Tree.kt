package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions

import com.ericsson.otp.erlang.*
import org.elixir_lang.Macro
import org.elixir_lang.Macro.ifTagged3TupleTo
import org.elixir_lang.Macro.ifTupleTo
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import javax.swing.JTree
import javax.swing.tree.TreeModel

class Tree(model: TreeModel): JTree(model) {
    override fun convertValueToText(
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
    ): String =
        when (value) {
            is V1 -> value.module?.atomValue() ?: "?"
            is Definition -> "${value.name ?: '?'}/${value.arity ?: '?'}"
            is Clause -> "${value.definition.name}(${argumentsToText(value.arguments)})${guardsToText(value.guards)}"
            else -> super.convertValueToText(value, selected, expanded, leaf, row, hasFocus)
        }

    private fun guardsToText(guards: OtpErlangList?): String =
        if (guards != null) {
            if (guards.arity() == 0) {
                ""
            } else {
                assert (guards.arity() == 1)

                " when ${Macro.toString(rewriteGuard(guards.elementAt(0)))}"
            }
        } else {
            " when ?"
        }

    private fun argumentsToText(arguments: OtpErlangList?): String =
            arguments?.joinToString(", ") { argument -> Macro.toString(argument) } ?: ""

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
                        ifTagged3TupleTo(macro, ".") { tuple ->
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
