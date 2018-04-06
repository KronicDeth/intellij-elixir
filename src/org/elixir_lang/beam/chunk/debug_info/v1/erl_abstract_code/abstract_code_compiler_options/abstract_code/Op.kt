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
                    ?: MacroStringDeclaredScope("missing_operator", Scope.EMPTY)

    private const val TAG = "op"

    private fun binaryOperationToMacroStringDeclaredScope(
            leftOperand: OtpErlangObject,
            operator: OtpErlangObject,
            rightOperand: OtpErlangObject,
            scope: Scope
    ): MacroStringDeclaredScope {
        val operatorMacroString = binaryOperatorToMacroString(operator)
        val (leftOperandMacroString, leftOperandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(leftOperand, scope)
        val (rightOperandMacroString, rightOperandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(rightOperand, scope)

        val macroString = when (operatorMacroString) {
            "div", "rem", "send" ->
                "$operatorMacroString($leftOperandMacroString, $rightOperandMacroString)"
            else ->
                "$leftOperandMacroString $operatorMacroString $rightOperandMacroString"
        }

        return MacroStringDeclaredScope(macroString, leftOperandDeclaredScope.union(rightOperandDeclaredScope))
    }

    private fun binaryOperatorToMacroString(operator: OtpErlangAtom): String {
        val name = operator.atomValue()

        return when (name) {
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
    }

    private fun binaryOperatorToMacroString(operator: OtpErlangObject): String =
            when (operator) {
                is OtpErlangAtom -> binaryOperatorToMacroString(operator)
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
            else -> MacroStringDeclaredScope( "unknown_operation_arity", Scope.EMPTY)
        }

    private fun toOperator(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toOperands(term: OtpErlangTuple): List<OtpErlangObject> = term.elements().drop(3)

    private fun unaryOperationToMacroStringDeclaredScope(
            operator: OtpErlangObject,
            operand: OtpErlangObject,
            scope: Scope
    ): MacroStringDeclaredScope {
        val operatorMacroString = unaryOperatorToMacroString(operator)
        val (operandMacroString, operandDeclaredScope) = AbstractCode.toMacroStringDeclaredScope(operand, scope)

        return MacroStringDeclaredScope("$operatorMacroString $operandMacroString", operandDeclaredScope)
    }

    private fun unaryOperatorToMacroString(operator: OtpErlangAtom): MacroString = inspectAsFunction(operator)

    private fun unaryOperatorToMacroString(operator: OtpErlangObject) =
            when (operator) {
                is OtpErlangAtom -> unaryOperatorToMacroString(operator)
                else -> "unknown_unary_operator"
            }
}
