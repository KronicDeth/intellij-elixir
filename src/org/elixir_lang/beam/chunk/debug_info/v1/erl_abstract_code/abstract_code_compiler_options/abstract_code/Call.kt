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
import org.elixir_lang.code.Identifier.inspectAsFunction
import org.elixir_lang.toOtpErlangList

object Call {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val macroString = toMacroString(term, scope)

        return MacroStringDeclaredScope(macroString, Scope.EMPTY)
    }

    private const val TAG = "call"

    private fun anonymousFunctionCallToMacroString(name: OtpErlangTuple, term: OtpErlangTuple): MacroString {
        val nameMacroString = Var.toMacroStringDeclaredScope(name, Scope.EMPTY).macroString
        val argumentsMacroString = argumentsMacroString(term)

        return "$nameMacroString.($argumentsMacroString)"
    }

    private fun inlineAnonymousFunctionCallToMacroString(name: OtpErlangTuple, term: OtpErlangTuple, scope: Scope): MacroString {
        val nameMacroString = Fun.toMacroStringDeclaredScope(name, scope).macroString
        val argumentsMacroString = argumentsMacroString(term)

        return "(${nameMacroString}).($argumentsMacroString)"
    }

    private fun argumentsMacroString(term: OtpErlangTuple): String =
            toArguments(term)
                    ?.let { argumentsToMacroString(it) }
                    ?: "missing_arguments"

    private fun argumentsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }

    private fun argumentsToMacroString(term: OtpErlangObject?): String =
        when (term) {
            is OtpErlangList -> argumentsToMacroString(term)
            else -> "unknown_arguments"
        }

    private fun localNamedFunctionCallToMacroString(
            name: OtpErlangObject?,
            arguments: OtpErlangObject?
    ): MacroString =
        if (name != null) {
            val elixirAtom = Atom.toElixirAtom(name)

            if (elixirAtom != null) {
                val function = inspectAsFunction(elixirAtom.atomValue(), local = true)
                val argumentList = arguments?.toOtpErlangList()
                val argumentsMacroString = argumentsToMacroString(arguments)

                if (argumentList != null) {
                    val nameArity = NameArity(elixirAtom.atomValue(), argumentList.arity())

                    when (Decompiler.decompiler("erlang", nameArity)) {
                        is PrefixOperator, is InfixOperator, is Unquoted -> {
                            "apply(__MODULE__, :${function}, [${argumentsMacroString}])"
                        }
                        else -> {
                            "${function}(${argumentsMacroString})"
                        }
                    }
                } else {
                    "${function}(${argumentsMacroString})"
                }
            } else {
                val nameMacroString = AbstractCode.toMacroStringDeclaredScope(name, Scope.EMPTY).macroString
                val argumentMacroString = argumentsToMacroString(arguments)

                "${nameMacroString}(${argumentMacroString})"
            }
        } else {
            val nameMacroString = "unknown_function"
            val argumentMacroString = argumentsToMacroString(arguments)

            "${nameMacroString}(${argumentMacroString})"
        }

    private fun namedFunctionCallToMacroString(term: OtpErlangTuple): MacroString {
        val name = toName(term)
        val arguments = toArguments(term)

        return Remote.ifTo(name) { remoteName ->
            remoteNamedFunctionCallToMacroString(remoteName, arguments)
        } ?: localNamedFunctionCallToMacroString(name, arguments)
    }

    private fun remoteNamedFunctionCallToMacroString(
            remoteName: OtpErlangTuple,
            arguments: OtpErlangObject?
    ): MacroString {
        val remoteFunction = Remote.toFunction(remoteName)
        val argumentsMacroString = argumentsToMacroString(arguments)

        return if (Var.`is`(remoteFunction)) {
            val remoteModuleMacroString = Remote.moduleMacroString(remoteName)

            val remoteFunctionMacroString =
                    Var.toMacroStringDeclaredScope(remoteFunction as OtpErlangTuple, Scope.EMPTY).macroString

            "apply($remoteModuleMacroString, $remoteFunctionMacroString, [$argumentsMacroString])"
        } else {
            val remoteNameMacroString = Remote.toMacroStringDeclaredScope(remoteName).macroString
            "$remoteNameMacroString($argumentsMacroString)"
        }
    }

    private fun toArguments(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): String =
        toName(term).let { name ->
            Var.ifTo(name) {
                anonymousFunctionCallToMacroString(it, term)
            } ?:
            Fun.ifTo(name) {
                inlineAnonymousFunctionCallToMacroString(it, term, scope)
            } ?:
            namedFunctionCallToMacroString(term)
        }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
