package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Cons {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = AbstractCode.ifTag(term, TAG, ifTrue)
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it, scope) }

    fun `is`(term: OtpErlangObject?): Boolean = ifTo(term) { true } ?: false

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        headTailMacroStringDeclaredScope(term, scope).let { (headTailMacroString, headTailDeclaredScope) ->
            MacroStringDeclaredScope("[$headTailMacroString]", headTailDeclaredScope)
        }

    private const val TAG = "cons"

    private fun headMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toHead(term)
                    ?.let { headToMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_head", Scope.EMPTY)

    private fun headTailMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
        headTailMacroStringDeclaredScope(term, scope, Pair(StringBuilder(), Scope.EMPTY))

    private tailrec fun headTailMacroStringDeclaredScope(
            term: OtpErlangTuple,
            scope: Scope,
            acc: Pair<StringBuilder, Scope>
    ): MacroStringDeclaredScope {
        val (headMacroString, headDeclaredScope) = headMacroStringDeclaredScope(term, scope)
        val tail = toTail(term)

        return when {
            Nil.`is`(tail) -> {
                val (macroStringBuilder, accDeclaredScope) = acc
                macroStringBuilder.append(headMacroString)
                val declaredScopeUnion = accDeclaredScope.union(headDeclaredScope)

                MacroStringDeclaredScope(macroStringBuilder.toString(), declaredScopeUnion)
            }
            Cons.`is`(tail) -> {
                val (macroStringBuilder, accDeclaredScope) = acc
                macroStringBuilder.append(headMacroString).append(", ")
                val declaredScopeUnion = accDeclaredScope.union(headDeclaredScope)

                headTailMacroStringDeclaredScope(tail as OtpErlangTuple, scope, Pair(macroStringBuilder, declaredScopeUnion))
            }
            else -> {
                val (macroStringBuilder, accDeclaredScope) = acc
                macroStringBuilder.append(headMacroString).append(" | ")

                val (tailMacroString, tailDeclaredScope) = tailMacroStringDeclaredScope(term, scope)
                macroStringBuilder.append(tailMacroString)

                val declaredScopeUnion = accDeclaredScope.union(headDeclaredScope).union(tailDeclaredScope)

                MacroStringDeclaredScope(macroStringBuilder.toString(), declaredScopeUnion)
            }
        }
    }

    private fun headToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope) =
            AbstractCode.toMacroStringDeclaredScope(term, scope)

    private fun tailMacroStringDeclaredScope(term: OtpErlangTuple, scope : Scope) =
            toTail(term)
                    ?.let { tailToMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_tail", Scope.EMPTY)

    private fun tailToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope) =
            AbstractCode.toMacroStringDeclaredScope(term, scope)

    private fun toHead(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toTail(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
