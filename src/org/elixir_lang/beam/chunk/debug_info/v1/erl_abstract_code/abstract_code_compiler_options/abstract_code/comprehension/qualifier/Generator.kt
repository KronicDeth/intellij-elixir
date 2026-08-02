package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

/**
 * Shared scaffolding for comprehension qualifier *generators* (`generate`, `b_generate`, `m_generate`).
 *
 * Every generator has the Erlang abstract form `{Tag, Anno, Pattern, Expression}` and renders as
 * `pattern <- expression`, with the expression's declared scope threaded into the pattern's scope (so a variable
 * bound by the expression is in scope for the pattern).  Subtypes supply only what differs: the [tags], the
 * [expressionDescription] used in error messages, how the pattern is rendered ([patternMacroStringDeclaredScope]),
 * and, when the surrounding syntax is not a plain `pattern <- expression`, how the two are combined ([macroString]).
 *
 * Each subtype matches more than one [tags] value because OTP 28 (EEP 70) added a *strict* variant of every
 * generator (`generate_strict`/`b_generate_strict`/`m_generate_strict`, written `<:-`/`<:=`), identical in
 * abstract shape to its non-strict counterpart.  Elixir has no strict-generator syntax, so both render the
 * same `<-` form - the strictness distinction (raise vs. skip on a non-matching element) is not expressible.
 */
abstract class Generator(private val tags: Set<String>, private val expressionDescription: String) {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, tags) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (expressionMacroString, expressionDeclaredScope) = expressionMacroStringDeclaredScope(term, scope)
        val patternScope = scope.union(expressionDeclaredScope)
        val (patternMacroString, patternDeclaredScope) = patternMacroStringDeclaredScope(term, patternScope)
        val string = macroString(patternMacroString, expressionMacroString)
        val declaredScope = patternScope.union(patternDeclaredScope)

        return MacroStringDeclaredScope(string, doBlock = false, declaredScope)
    }

    protected abstract fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope

    protected open fun macroString(patternMacroString: MacroString, expressionMacroString: MacroString): String =
            "${patternMacroString.string} <- ${expressionMacroString.group().string}"

    protected fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("expression", scope, expressionDescription, term)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
