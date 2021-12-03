package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.NameArity
import org.elixir_lang.beam.Decompiler
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.decompiler.InfixOperator
import org.elixir_lang.beam.decompiler.PrefixOperator
import org.elixir_lang.beam.decompiler.Unquoted
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.code.Identifier.inspectAsFunction
import org.elixir_lang.toOtpErlangList

object Call {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val string = toString(term, scope)

        return MacroStringDeclaredScope(string, doBlock = false, Scope.EMPTY)
    }

    private const val TAG = "call"

    private fun anonymousFunctionCallToString(name: OtpErlangTuple, term: OtpErlangTuple): String {
        val nameString = Var.toMacroStringDeclaredScope(name, Scope.EMPTY).macroString.string
        val argumentsMacroString = argumentsString(term)

        return "${nameString}.($argumentsMacroString)"
    }

    private fun inlineAnonymousFunctionCallToMacroString(name: OtpErlangTuple, term: OtpErlangTuple, scope: Scope): String {
        val nameString = Fun.toMacroStringDeclaredScope(name, scope).macroString.group().string
        val argumentsString = argumentsString(term)

        return "(${nameString}).($argumentsString)"
    }

    private fun argumentsString(term: OtpErlangTuple): String =
            toArguments(term)
                    ?.let { argumentsToString(it) }
                    ?: "missing_arguments"

    private fun argumentsToString(term: OtpErlangList): String = Sequence.toCommaSeparatedString(term)

    private fun argumentsToString(term: OtpErlangObject?): String =
        when (term) {
            is OtpErlangList -> argumentsToString(term)
            else -> "unknown_arguments"
        }

    private fun localNamedFunctionCallToString(
            name: OtpErlangObject?,
            arguments: OtpErlangObject?
    ): String =
        if (name != null) {
            val elixirAtom = Atom.toElixirAtom(name)

            if (elixirAtom != null) {
                val atomValue = elixirAtom.atomValue()
                val argumentList = arguments?.toOtpErlangList()
                val argumentsMacroString = argumentsToString(arguments)

                if (argumentList != null) {
                    val nameArity = NameArity(atomValue, argumentList.arity())

                    when (Decompiler.decompiler("erlang", nameArity)) {
                        is PrefixOperator, is InfixOperator, is Unquoted -> {
                            val function = inspect(elixirAtom)

                            "apply(__MODULE__, ${function}, [${argumentsMacroString}])"
                        }
                        else -> {
                            val function = inspectAsFunction(atomValue, local = true)
                            "${function}(${argumentsMacroString})"
                        }
                    }
                } else {
                    val function = inspectAsFunction(atomValue, local = true)
                    "${function}(${argumentsMacroString})"
                }
            } else {
                val nameString = AbstractCode.toString(name)
                val argumentMacroString = argumentsToString(arguments)

                "${nameString}(${argumentMacroString})"
            }
        } else {
            val nameMacroString = "unknown_function"
            val argumentMacroString = argumentsToString(arguments)

            "${nameMacroString}(${argumentMacroString})"
        }

    private fun namedFunctionCallToString(term: OtpErlangTuple): String {
        val name = toName(term)
        val arguments = toArguments(term)

        return Remote.ifTo(name) { remoteName ->
            remoteNamedFunctionCallToString(remoteName, arguments)
        } ?: localNamedFunctionCallToString(name, arguments)
    }

    private fun remoteNamedFunctionCallToString(
            remoteName: OtpErlangTuple,
            arguments: OtpErlangObject?
    ): String {
        val remoteFunction = Remote.toFunction(remoteName)
        val argumentsString = argumentsToString(arguments)

        return if (Var.`is`(remoteFunction)) {
            val remoteModuleString = Remote.moduleString(remoteName)

            val remoteFunctionString =
                    Var.toMacroStringDeclaredScope(remoteFunction as OtpErlangTuple, Scope.EMPTY).macroString.string

            "apply($remoteModuleString, $remoteFunctionString, [$argumentsString])"
        } else {
            val remoteNameString = Remote.toMacroStringDeclaredScope(remoteName).macroString.string
            "$remoteNameString($argumentsString)"
        }
    }

    private fun toArguments(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)

    private fun toString(term: OtpErlangTuple, scope: Scope): String =
        toName(term).let { name ->
            Var.ifTo(name) {
                anonymousFunctionCallToString(it, term)
            } ?:
            Fun.ifTo(name) {
                inlineAnonymousFunctionCallToMacroString(it, term, scope)
            } ?:
            namedFunctionCallToString(term)
        }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
