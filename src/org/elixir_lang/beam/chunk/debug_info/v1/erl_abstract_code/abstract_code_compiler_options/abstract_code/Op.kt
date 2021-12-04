package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction

object Op {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toOperator(term)
                    ?.let { operationToMacroStringDeclaredScope(it, toOperands(term), scope) }
                    ?: MacroStringDeclaredScope.error("missing_operator")

    private const val TAG = "op"

    private fun binaryOperationToMacroStringDeclaredScope(
            leftOperand: OtpErlangObject,
            operator: OtpErlangObject,
            rightOperand: OtpErlangObject,
            scope: Scope
    ): MacroStringDeclaredScope {
        val operatorString = binaryOperatorToString(operator)
        val (leftOperandMacroString, leftOperandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(leftOperand, scope)
        val leftOperandString = leftOperandMacroString.group().string
        val (rightOperandMacroString, rightOperandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(rightOperand, scope)
        val rightOperandString = rightOperandMacroString.group().string

        val string = when (operatorString) {
            "div", "rem", "send" ->
                "$operatorString($leftOperandString, $rightOperandString)"
            "xor" ->
                ":erlang.xor($leftOperandString, $rightOperandString)"
            else ->
                "$leftOperandString $operatorString $rightOperandString"
        }

        return MacroStringDeclaredScope(string, doBlock = false, leftOperandDeclaredScope.union(rightOperandDeclaredScope))
    }

    private fun binaryOperatorToString(operator: OtpErlangAtom): String = when (val name = operator.atomValue()) {
        "!" -> "send"
        "/=" -> "!="
        "=/=" -> "!=="
        "=:=" -> "==="
        "=<" -> "<="
        "andalso" -> "and"
        "band" -> "&&&"
        "bor" -> "|||"
        "bsl" -> "<<<"
        "bsr" -> ">>>"
        "bxor" -> "^^^"
        "orelse" -> "or"
        else -> name
    }

    private fun binaryOperatorToString(operator: OtpErlangObject): String =
            when (operator) {
                is OtpErlangAtom -> binaryOperatorToString(operator)
                else -> "unknown_binary_operator"
            }

    private fun operationToMacroStringDeclaredScope(
            operator: OtpErlangObject,
            operands: List<OtpErlangObject>,
            scope: Scope
    ): MacroStringDeclaredScope =
        when (operands.size) {
            1 -> unaryOperationToMacroStringDeclaredScope(operator, operands.single(), scope)
            2 -> binaryOperationToMacroStringDeclaredScope(operands.first(), operator, operands.last(), scope)
            else -> MacroStringDeclaredScope.error( "unknown_operation_arity")
        }

    private fun toOperator(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toOperands(term: OtpErlangTuple): List<OtpErlangObject> = term.elements().drop(3)

    private fun unaryOperationToMacroStringDeclaredScope(
            operator: OtpErlangObject,
            operand: OtpErlangObject,
            scope: Scope
    ): MacroStringDeclaredScope {
        val operatorString = unaryOperatorToString(operator)
        val stringBuilder = StringBuilder().append(operatorString)

        val (operandMacroString, operandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(operand, scope)
        val operandString = operandMacroString.string

        if (operandString == "not") {
            stringBuilder.append(' ')
        }

        stringBuilder.append(operandString)

        return MacroStringDeclaredScope(stringBuilder.toString(), doBlock = false, operandDeclaredScope)
    }

    private fun unaryOperatorToString(operator: OtpErlangAtom): String =
            when (val inspectedAsFunction = inspectAsFunction(operator)) {
                "bnot" -> "~~~"
                else -> inspectedAsFunction
            }

    private fun unaryOperatorToString(operator: OtpErlangObject) =
            when (operator) {
                is OtpErlangAtom -> unaryOperatorToString(operator)
                else -> "unknown_unary_operator"
            }
}
