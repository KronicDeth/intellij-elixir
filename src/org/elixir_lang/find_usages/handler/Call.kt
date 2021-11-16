package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.ArityRange
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.Call

class Call(call: Call) : FindUsagesHandler(call) {
    private val _primaryElements by lazy {
        val resolvedElements = resolvedElements()

        if (resolvedElements.isNotEmpty()) {
            resolvedElements
        } else {
            super.getPrimaryElements()
        }
    }

    private val _secondaryElements by lazy {
        _primaryElements
                .toCallDefinitionCallSet()
                .withNameArityInterval()
                .withEnclosingModularMacroCall()
                .toSecondaryElements()
                .filterNot { _primaryElements.contains(it) }
                .toTypedArray()
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements
    override fun getSecondaryElements(): Array<PsiElement> = _secondaryElements

    private fun resolvedElements() =
            super
                    .getPrimaryElements()
                    .flatMap { it.references.toList() }
                    .flatMap { it.toPsiElementList() }
                    .toTypedArray()

}

private data class CallNameArityInterval(val call: Call, val name: String, val arityInterval: ArityInterval) {
    fun toEnclosingCallEnclosedCallNameArityRange(): EnclosingCallEnclosedCallNameArityInterval? =
            enclosingModularMacroCall(call)?.let { enclosingCall ->
                EnclosingCallEnclosedCallNameArityInterval(enclosingCall, this.call, this.name, this.arityInterval)
            }
}

private data class EnclosingCallEnclosedCallNameArityInterval(
        val enclosingCall: Call,
        val enclosedCall: Call,
        val name: String,
        val arityInterval: ArityInterval
) {
    fun toSecondaryElements(): Iterable<Call> =
        Modular.callDefinitionClauseCallSequence(enclosingCall)
                .map { it to nameArityInterval(it, ResolveState.initial()) }
                .filter { (_, nameArityInterval) ->
                    nameArityInterval != null &&
                            nameArityInterval.name == name &&
                            nameArityInterval.arityInterval.overlaps(arityInterval)
                }.map { (call, _) -> call }
                .asIterable()
}

private fun Array<PsiElement>.toCallDefinitionCallSet(): Set<Call> =
        this
                .filterIsInstance<Call>()
                .filter { CallDefinitionClause.`is`(it) }
                .toSet()

private fun Call.toCallNameArityInterval(): CallNameArityInterval? =
        nameArityInterval(this, ResolveState.initial())?.let { (name, arityInterval) ->
            CallNameArityInterval(this, name, arityInterval)
        }

private fun Iterable<Call>.withNameArityInterval(): Iterable<CallNameArityInterval> =
    this.mapNotNull { it.toCallNameArityInterval() }

private fun Iterable<CallNameArityInterval>.withEnclosingModularMacroCall():
        Iterable<EnclosingCallEnclosedCallNameArityInterval> =
        this.mapNotNull { it.toEnclosingCallEnclosedCallNameArityRange() }

private fun Iterable<EnclosingCallEnclosedCallNameArityInterval>.toSecondaryElements(): List<PsiElement> =
        this.flatMap { it.toSecondaryElements() }
