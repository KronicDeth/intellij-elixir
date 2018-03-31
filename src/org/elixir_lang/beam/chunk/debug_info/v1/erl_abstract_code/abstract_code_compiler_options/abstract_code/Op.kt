package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsFunction

object Op {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String =
            toOperator(term)
                    ?.let { operationToMacroString(it, toOperands(term)) }
                    ?: "missing_operator"

    private const val TAG = "op"

    private fun binaryOperationToMacroString(
            leftOperand: OtpErlangObject,
            operator: OtpErlangObject,
            rightOperand: OtpErlangObject
    ): String {
        val operatorMacroString = binaryOperatorToMacroString(operator)
        val leftOperandMacroString = AbstractCode.toMacroString(leftOperand)
        val rightOperandMacroString = AbstractCode.toMacroString(rightOperand)

        return "$leftOperandMacroString $operatorMacroString $rightOperandMacroString"
    }

    private fun binaryOperatorToMacroString(operator: OtpErlangAtom): String {
        val name = operator.atomValue()

        return when (name) {
            "band" -> "&&&"
            "bor" -> "|||"
            "bsl" -> "<<<"
            "bsr" -> ">>>"
            "bxor" -> "^^^"
            else -> name
        }
    }

    private fun binaryOperatorToMacroString(operator: OtpErlangObject): String =
            when (operator) {
                is OtpErlangAtom -> binaryOperatorToMacroString(operator)
                else -> "unknown_binary_operator"
            }

    private fun operationToMacroString(operator: OtpErlangObject, operands: List<OtpErlangObject>): String =
        when (operands.size) {
            1 -> unaryOperationToMacroString(operator, operands.single())
            2 -> binaryOperationToMacroString(operands.first(), operator, operands.last())
            else -> "unknown_operation_arity"
        }

    private fun toOperator(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toOperands(term: OtpErlangTuple): List<OtpErlangObject> = term.elements().drop(3)

    private fun unaryOperationToMacroString(operator: OtpErlangObject, operand: OtpErlangObject): String {
        val operatorMacroString = unaryOperatorToMacroString(operator)
        val operandMacroString = AbstractCode.toMacroString(operand)

        return "$operatorMacroString $operandMacroString"
    }

    private fun unaryOperatorToMacroString(operator: OtpErlangAtom): String = inspectAsFunction(operator)

    private fun unaryOperatorToMacroString(operator: OtpErlangObject): String =
            when (operator) {
                is OtpErlangAtom -> unaryOperatorToMacroString(operator)
                else -> "unknown_unary_operator"
            }
}
