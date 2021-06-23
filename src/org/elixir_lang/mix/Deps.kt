package org.elixir_lang.mix

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirTuple
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.impl.stripAccessExpression

object Deps {
    fun from(depsListElement: PsiElement): Sequence<Dep> =
            when (depsListElement) {
                is ElixirTuple -> fromTuple(depsListElement)
                is Call -> fromCall(depsListElement)
                else -> emptySequence()
            }


    // `ecto_dep()` in `ecto_sql` `deps`
    private fun fromCall(depsListElement: Call): Sequence<Dep> =
            depsListElement
                    .reference?.let { it as PsiPolyVariantReference }
                    ?.multiResolve(false)
                    ?.asSequence()
                    ?.filter(ResolveResult::isValidResult)
                    ?.mapNotNull(ResolveResult::getElement)
                    ?.filterIsInstance<Call>()
                    ?.filter { CallDefinitionClause.`is`(it) }
                    ?.flatMap { fromCallDefinitionClause(it) }
                    .orEmpty()

    private fun fromCallDefinitionClause(callDefinitionClause: Call): Sequence<Dep> =
            callDefinitionClause
                    .stabBodyChildExpressions()
                    ?.flatMap { fromChildExpression(it) }
                    ?: emptySequence()

    private tailrec fun fromChildExpression(childExpression: PsiElement): Sequence<Dep> =
            when (childExpression) {
                is Call -> fromChildExpression(childExpression)
                is ElixirAccessExpression -> fromChildExpression(childExpression.stripAccessExpression())
                is ElixirTuple -> fromTuple(childExpression)
                else -> {
                    emptySequence()
                }
            }

    private fun fromChildExpression(childExpression: Call): Sequence<Dep> =
        if (childExpression.isCalling(KERNEL, "if")) {
            fromIf(childExpression)
        } else {
            emptySequence()
        }

    private fun fromTuple(tuple: ElixirTuple): Sequence<Dep> =
            Dep.from(tuple)?.let { sequenceOf(it) }.orEmpty()

    private fun fromIf(`if`: Call): Sequence<Dep> {
        val branchStabSequences = `if`.doBlock?.let { doBlock ->
            val trueStab = doBlock.stab
            val falseStab = doBlock
                    .blockList
                    ?.blockItemList?.singleOrNull { it.blockIdentifier.text == "else" }
                    ?.stab

            sequenceOf(trueStab, falseStab).filterNotNull()
        } ?: emptySequence()

        return branchStabSequences
                .flatMap { stab -> stab.stabBody?.childExpressions().orEmpty() }
                .flatMap { fromChildExpression(it) }
    }
}
