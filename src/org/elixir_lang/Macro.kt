package org.elixir_lang

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.Operand.LEFT
import org.elixir_lang.Operand.RIGHT
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.component1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.component2
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.component3
import org.elixir_lang.beam.chunk.inspect
import org.elixir_lang.psi.impl.ElixirPsiImplUtil

val binaryOps = arrayOf(
        "===",
        "!==",
        "==",
        "!=",
        "<=",
        ">=",
        "&&",
        "||",
        "<>",
        "++",
        "--",
        "\\",
        "::",
        "<-",
        "..",
        "|>",
        "=~",
        "<",
        ">",
        "->",
        "+",
        "-",
        "*",
        "/",
        "=",
        "|",
        ".",
        "and",
        "or",
        "when",
        "in",
        "~>>",
        "<<~",
        "~>",
        "<~",
        "<~>",
        "<|>",
        "<<<",
        ">>>",
        "|||",
        "&&&",
        "^^^",
        "~~~"
)

object Macro {
    val logger = Logger.getInstance(Macro.javaClass)

    fun callArguments(callExpression: OtpErlangTuple): OtpErlangList {
        return callExpression.elementAt(2) as OtpErlangList
    }

    fun isAliases(macro: OtpErlangObject): Boolean {
        var aliases = false

        if (isExpression(macro)) {
            val expression = macro as OtpErlangTuple

            val first = expression.elementAt(0)

            if (first is OtpErlangAtom) {

                if (first.atomValue() == "__aliases__") {
                    aliases = true
                }
            }
        }

        return aliases
    }

    /**
     * Return whether quoted form contains an Elixir keyword that is just an alias to an atom, such as `false`,
     * `true`, or `nil`.
     *
     * @param macro a quoted form from a `quote` method.
     * @return `true` if OtpErlangAtom containing one of the keywords.
     */
    fun isAtomKeyword(macro: OtpErlangObject): Boolean {
        var atomKeyword = false

        for (knownAtomKeyword in ElixirPsiImplUtil.ATOM_KEYWORDS) {
            if (macro == knownAtomKeyword) {
                atomKeyword = true

                break
            }
        }

        return atomKeyword
    }

    /**
     * Return whether the macro is a __block__ expression representing sequential lines.
     *
     * @param macro a quoted form from a `quote` method.
     * @return `true` if `{:__block__, _, _}`.
     */
    fun isBlock(macro: OtpErlangObject): Boolean {
        var block = false

        if (isExpression(macro)) {
            val expression = macro as OtpErlangTuple

            if (expression.elementAt(0) == ElixirPsiImplUtil.BLOCK) {
                block = true
            }
        }

        return block
    }

    /**
     * Return whether the macro is an Expr node: `expr :: {expr | atom, Keyword.t, atom | [t]}`.
     *
     * @param macro a quoted form from a `quote` method.
     * @return `true` if a tuple with 3 elements; `false` otherwise.
     */
    fun isExpression(macro: OtpErlangObject): Boolean {
        var expression = false

        if (macro is OtpErlangTuple) {

            if (macro.arity() == 3) {
                expression = true
            }
        }

        return expression
    }

    /**
     * Returns whether macro is a local call.
     *
     * @param macro a quoted form
     * @return `true` if local call; `false` otherwise.
     * @see [Macro.decompose_call/1](https://github.com/elixir-lang/elixir/blob/6151f2ab1af0189b9c8c526db196e2a65c609c64/lib/elixir/lib/macro.ex.L277-L281)
     */
    fun isLocalCall(macro: OtpErlangObject): Boolean {
        var localCall = false

        if (isExpression(macro)) {
            val expression = macro as OtpErlangTuple

            val first = expression.elementAt(0)

            if (first is OtpErlangAtom) {
                val last = expression.elementAt(2)

                /* OtpErlangString maps to CharList, which are list, so is_list in Elixir would be true for
                   OtpErlangList and OtpErlangString. */
                if (last is OtpErlangList || last is OtpErlangString) {
                    localCall = true
                }
            }
        }

        return localCall
    }

    /** Return whether macro is a local call expression with no arguments.
     *
     * @param macro
     * @return
     */
    fun isVariable(macro: OtpErlangObject): Boolean {
        var variable = false

        if (isExpression(macro)) {
            val expression = macro as OtpErlangTuple

            val first = expression.elementAt(0)

            if (first is OtpErlangAtom) {
                val last = expression.elementAt(2)

                if (last is OtpErlangAtom) {
                    variable = true
                }
            }
        }

        return variable
    }

    fun metadata(expression: OtpErlangTuple): OtpErlangList {
        return expression.elementAt(1) as OtpErlangList
    }

    /**
     *
     * @param macro
     * @return
     * @see [Macro.to_string](https://github.com/elixir-lang/elixir/blob/a1b06e1e9067ae08826f91274fefcb68c2bbdd02/lib/elixir/lib/macro.ex.L461-L464)
     */
    fun toString(macro: OtpErlangObject): String =
            when (macro) {
                is OtpErlangTuple -> toString(macro)
                is OtpErlangList -> toString(macro)
                else -> otherToString(macro)
            }

    // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L838-L839
    fun otherToString(macro: OtpErlangObject): String {
        val inspected = inspect(macro)

        logger.error("""
                     Don't know how to convert ${macro.javaClass} in macro to string, so inspecting

                     ## macro

                     ```elixir
                     $inspected
                     ```
                     """)

        return inspected
    }

    fun toString(macro: OtpErlangList): String =
            // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L825-L835
            when {
                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L826-L827
                macro.arity() == 0 -> "[]"
                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L828-L830
                isPrintable(macro) -> "'${charListToString(macro)}'"
                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L828-L830
                isKeywordList(macro) -> "[${keywordListToString(macro)}]"
                // TODO https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L733-L736
                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L833-L834
                else -> "[${macro.joinToString(", ") { toString(it) }}]"
            }

    private fun keywordListToString(macro: OtpErlangList): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun charListToString(macro: OtpErlangList): String {
        TODO()
    }

    private fun isKeywordList(macro: OtpErlangList): Boolean {
        TODO()
    }

    private fun isPrintable(macro: OtpErlangList): Boolean {
        TODO()
    }

    fun toString(macro: OtpErlangTuple): String =
            when (macro.arity()) {
                3 -> {
                    // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L657-L816
                    val (first, _, last) = macro

                    if (last is OtpErlangAtom) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L658
                        if (first is OtpErlangAtom) {
                            first.atomValue() // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L659
                        } else {
                            otherToString(first)
                        }
                    } else if (first is OtpErlangAtom) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L663-L803
                        val firstAtomValue = first.atomValue()

                        if (firstAtomValue == "__aliases__") {
                            if (last is OtpErlangList) {
                                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L664
                                last.joinToString(".") { callToString(it) }
                            } else {
                                otherToString(last)
                            }
                        } else if (firstAtomValue == "__block___") { //https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L667-L675
                            if (last is OtpErlangList) {
                                if (last.arity() == 1) {
                                    // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L668
                                    toString(last.head)
                                } else {
                                    // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L673-L674
                                    val block = adjustNewLines(blockToString(macro), "\n  ")
                                    "(\n  ${block}\n)"
                                }
                            } else {
                                otherToString(last)
                            }
                        } else if (firstAtomValue == "<<>>") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L677-L692
                            if (isInterpolated(macro)) {
                                interpolate(macro)
                            } else {
                                // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L682-L690
                                if (last is OtpErlangList) {
                                    val result = last.joinToString(", ") { part ->
                                        val string = bitPartToString(part)

                                        if (string.startsWith('<') or string.endsWith('>')) {
                                            "(${string})"
                                        } else {
                                            string
                                        }
                                    }

                                    "<<$result>>"
                                } else {
                                    otherToString(macro)
                                }
                            }
                        } else if (firstAtomValue == "{}") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L694-L698
                            val argumentsString = if (last is OtpErlangList) {
                                last.joinToString(", ") { argument -> toString(argument) }
                            } else {
                                otherToString(last)
                            }

                            "{$argumentsString}"
                        } else if (firstAtomValue == "%{}") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L700-L704
                            "%{${mapToString(last)}}"
                        } else if (firstAtomValue == "%") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L706-L710
                            if (last is OtpErlangList && last.arity() == 2) {
                                val (structName, map) = last

                                "%${toString(structName)}{${mapToString(map)}}"
                            } else {
                                otherToString(macro)
                            }
                        } else if (firstAtomValue == "fn") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L712-L725
                            if (last is OtpErlangList && last.arity() == 1) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L713-L720
                                val (only) = last

                                if (only is OtpErlangTuple && only.arity() == 3) {
                                    val (function, meta, arguments) = only

                                    if (function is OtpErlangAtom && function.atomValue() == "->") {
                                        if (arguments is OtpErlangList && arguments.arity() == 2) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L713
                                            val (_, tuple) = arguments

                                            // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L714
                                            if (tuple !is OtpErlangTuple || (tuple.elementAt(0) as? OtpErlangAtom)?.atomValue() != "__block__") {
                                                "fn ${arrowToString(last)} end"
                                            } else {
                                                "fn ${blockToString(last)}\nend"
                                            }
                                        } else {
                                            "fn ${blockToString(last)}\nend"
                                        }
                                    } else {
                                        val block = adjustNewLines(blockToString(last), "\n  ")
                                        "fn\n $block\nend"
                                    }
                                } else {
                                    val block = adjustNewLines(blockToString(last), "\n  ")
                                    "fn\n $block\nend"
                                }
                            } else { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L722-L725
                                val block = adjustNewLines(blockToString(last), "\n  ")
                                "fn\n $block\nend"
                            }
                        } else if (firstAtomValue == "..") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L727-L731
                            if (last is OtpErlangList) {
                                last.joinToString("..") { argument -> toString(argument) }
                            } else {
                                otherToString(macro)
                            }
                        } else if (firstAtomValue == "when") {
                            if (last is OtpErlangList) {
                                if (last.arity() == 2) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L738-L748
                                    val (left, right) = last

                                    val rightString = if (right is OtpErlangList && isEmpty(right) && isKeyword(right)) {
                                        keywordListToString(right)
                                    } else {
                                        operationToString(right, "when", RIGHT)
                                    }

                                    "${operationToString(left, "when", LEFT)} when $rightString"
                                } else { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L755-L759
                                    val (left, right) = splitLast(last)

                                    if (left is OtpErlangList) {
                                        "(${left.joinToString(", ") { argument -> toString(argument) }}) when ${toString(right)}"
                                    } else {
                                        otherToString(macro)
                                    }
                                }
                            } else {
                                otherToString(macro)
                            }
                        } else if (firstAtomValue in binaryOps) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L751
                            if (last is OtpErlangList && last.arity() == 2) {
                                val (left, right) = last

                                "${operationToString(left, firstAtomValue, LEFT)} ${firstAtomValue} ${operationToString(right, firstAtomValue, RIGHT)}"
                            } else {
                                otherToString(macro)
                            }
                        } else if (firstAtomValue == "&") { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L761-L774
                            if (last is OtpErlangList && last.arity() == 1) {
                                val (only) = last

                                if (only is OtpErlangTuple && only.arity() == 3) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L762-L770
                                    val (onlyFunction, _, onlyArguments) = only
                                    TODO()



                                } else if (only !is OtpErlangLong) { // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L772-L774
                                    otherToString(macro)
                                } else {
                                    TODO()
                                }
                            } else {
                                otherToString(macro)
                            }
                        } else {
                            otherToString(macro)
                        }
                    } else {
                        otherToString(first)
                    }
                }
                2 ->
                    // https://github.com/elixir-lang/elixir/blob/v1.5.3/lib/elixir/lib/macro.ex#L818-L821
                    toString(
                            OtpErlangTuple(
                                    arrayOf(OtpErlangAtom("{}"), OtpErlangList(), OtpErlangList(macro.elements()))
                            )
                    )
                else ->
                    otherToString(macro)
        }

    private fun splitLast(list: OtpErlangList): Pair<OtpErlangList, OtpErlangObject> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun operationToString(right: OtpErlangObject, s: String, right1: Any): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun arrowToString(last: OtpErlangList): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun bitPartToString(part: OtpErlangObject?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun mapToString(last: OtpErlangObject): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun interpolate(macro: OtpErlangTuple): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isInterpolated(macro: OtpErlangTuple): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun blockToString(macro: OtpErlangObject): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun adjustNewLines(blockToString: Any, s: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun callToString(it: OtpErlangObject?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isEmpty(list: OtpErlangList): Boolean = list.arity() == 0

    private fun isKeyword(term: OtpErlangObject): Boolean {
        TODO("not implemented")
    }
}

enum class Operand {
    LEFT,
    RIGHT
}

private operator fun OtpErlangList.component1(): OtpErlangObject = this.elementAt(0)
private operator fun OtpErlangList.component2(): OtpErlangObject = this.elementAt(1)
