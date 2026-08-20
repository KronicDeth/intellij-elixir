package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

/**
 * The `maybe ... [else ...] end` expression introduced in OTP 25 (EEP 49).  Its abstract form is
 * `{'maybe', Anno, Body}` or, with an else block, `{'maybe', Anno, Body, {'else', Anno, \[Clause...]}}`.
 *
 * It maps almost exactly onto Elixir's [`with`](https://hexdocs.pm/elixir/Kernel.SpecialForms.html#with/1):
 *   - a `maybe_match` step `Pattern ?= Expression` becomes a match clause `pattern <- expression`
 *     (a non-matching value short-circuits - to the `else` clauses if present, otherwise it is the
 *     value of the block), identical to `with`'s `<-`;
 *   - an ordinary `Pattern = Expression` (`match`) or a plain expression stays as-is, becoming a
 *     `with` clause that raises / merely evaluates, exactly as in `maybe`;
 *   - the last body expression is the block's value, so it becomes `with`'s `do` body;
 *   - the `else` clauses match the short-circuited value and are the same `{clause, ...}` tuples a
 *     `case`/`try` uses, so they render via [Clause] just like [Try]'s `else`.
 *
 * The strictness/raise-on-no-else-match behaviour of both constructs coincides, so the translation is
 * faithful rather than approximate.
 */
object Maybe {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            MacroStringDeclaredScope(toString(term, scope), doBlock = true, Scope.EMPTY)

    private const val TAG = "maybe"
    private const val MAYBE_MATCH_TAG = "maybe_match"
    private const val ELSE_TAG = "else"

    private fun toString(term: OtpErlangTuple, scope: Scope): String {
        val body = toBody(term)
        if (body !is OtpErlangList || body.arity() == 0) {
            return AbstractCode.missing("body", "maybe body", term)
        }

        val steps = body.elements().toList()
        // A trailing `Pattern ?= Expression` has no plain value expression to become the `do` body,
        // so it stays a clause and the block's value is its (now bound) pattern.
        val lastMatch = ifTag(steps.last(), MAYBE_MATCH_TAG) { it }
        val clauseSteps = if (lastMatch != null) steps else steps.dropLast(1)

        // `with` requires at least one clause; a single plain expression has none, so there is
        // nothing conditional to translate - emit the bare expression.
        if (clauseSteps.isEmpty()) {
            return AbstractCode.toString(steps.last(), scope)
        }

        var clauseScope = scope
        val clauseStrings = clauseSteps.map { step ->
            val (clauseMacroString, declaredScope) = clauseMacroStringDeclaredScope(step, clauseScope)
            clauseScope = clauseScope.union(declaredScope)
            // A do-block step (e.g. `x = case ... end` or a bare `for ... end`) must be parenthesized:
            // in the comma-separated `with` clause list its bare `end` would otherwise collide with the
            // following `,` or the `with`'s own `do`.
            clauseMacroString.group().string
        }

        val doBodyTerm = if (lastMatch != null) toPattern(lastMatch) else steps.last()
        val doBody = doBodyTerm
                ?.let { adjustNewLines(AbstractCode.toString(it, clauseScope), "\n  ") }
                ?: AbstractCode.missing("value", "maybe value", term)

        val macroStringBuilder = StringBuilder("with ")
                .append(clauseStrings.joinToString(",\n     "))
                .append(" do\n")
                .append("  ").append(doBody).append('\n')

        // `else` clauses see only the outer scope: `maybe` does not export its body's bindings to them.
        elseClausesString(term, scope)?.let { elseClausesString ->
            macroStringBuilder.append("else\n").append("  ").append(elseClausesString).append('\n')
        }

        return macroStringBuilder.append("end").toString()
    }

    /**
     * A `maybe_match` step becomes `pattern <- expression` (mirroring [Match], but `<-` for `=` so a
     * non-match short-circuits instead of raising); any other step renders as an ordinary expression.
     */
    private fun clauseMacroStringDeclaredScope(step: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            ifTag(step, MAYBE_MATCH_TAG) { maybeMatch ->
                val expressionMacroString = toExpression(maybeMatch)
                        ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                        ?: MacroString.error("missing_expression", "maybe match expression", maybeMatch)
                val (patternMacroString, patternDeclaredScope) = toPattern(maybeMatch)
                        ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope.copy(pinning = true)) }
                        ?: MacroStringDeclaredScope.missing("pattern", "maybe match pattern", maybeMatch)

                MacroStringDeclaredScope(
                        "${patternMacroString.string} <- ${expressionMacroString.group().string}",
                        doBlock = false,
                        patternDeclaredScope
                )
            } ?: AbstractCode.toMacroStringDeclaredScope(step, scope)

    private fun elseClausesString(term: OtpErlangTuple, scope: Scope): String? =
            toElse(term)?.let { elseTerm ->
                ifTag(elseTerm, ELSE_TAG) { elseTuple ->
                    toClauses(elseTuple)?.let { clausesToString(it, scope) }
                }
            }

    private fun clausesToString(clauses: OtpErlangObject, scope: Scope): String =
            when (clauses) {
                is OtpErlangList ->
                    clauses
                            .joinToString("\n") {
                                Clause.ifToString(it, scope) ?: AbstractCode.unknown("clause", "maybe else clause", it)
                            }
                            .let { adjustNewLines(it, "\n  ") }
                else -> AbstractCode.unknown("clauses", "maybe else clauses", clauses)
            }

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toElse(term: OtpErlangTuple): OtpErlangObject? =
            if (term.arity() > 3) term.elementAt(3) else null
    private fun toClauses(elseTuple: OtpErlangTuple): OtpErlangObject? = elseTuple.elementAt(2)
    private fun toPattern(maybeMatch: OtpErlangTuple): OtpErlangObject? = maybeMatch.elementAt(2)
    private fun toExpression(maybeMatch: OtpErlangTuple): OtpErlangObject? = maybeMatch.elementAt(3)
}
