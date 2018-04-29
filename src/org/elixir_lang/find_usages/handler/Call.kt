package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.elixir_lang.ArityRange
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.overlaps
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.nameArityRange

class Call(call: Call) : FindUsagesHandler(call) {
    private val _primaryElements by lazy {
        super
                .getPrimaryElements()
                .flatMap { it.references.toList() }
                .flatMap { it.toPsiElementList() }
                .toTypedArray()
    }

    private val _secondaryElements by lazy {
        _primaryElements
                .toCallDefinitionCallSet()
                .withNameArityRange()
                .withEnclosingModularMacroCall()
                .toSecondaryElements()
                .filterNot { _primaryElements.contains(it) }
                .toTypedArray()
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements
    override fun getSecondaryElements(): Array<PsiElement> = _secondaryElements
}

private data class CallNameArityRange(val call: Call, val name: String, val arityRange: ArityRange) {
    fun toEnclosingCallEnclosedCallNameArityRange(): EnclosingCallEnclosedCallNameArityRange? =
            enclosingModularMacroCall(call)?.let { enclosingCall ->
                EnclosingCallEnclosedCallNameArityRange(enclosingCall, this.call, this.name, this.arityRange)
            }
}

private data class EnclosingCallEnclosedCallNameArityRange(
        val enclosingCall: Call,
        val enclosedCall: Call,
        val name: String,
        val arityRange: ArityRange
) {
    fun toSecondaryElements(): Iterable<Call> =
        Modular.callDefinitionClauseCallSequence(enclosingCall)
                .map { it to nameArityRange(it) }
                .filter { (_, nameArityRange) ->
                    nameArityRange != null &&
                            nameArityRange.name == name &&
                            nameArityRange.arityRange.overlaps(arityRange)
                }.map { (call, _) -> call }
                .asIterable()
}

private fun Array<PsiElement>.toCallDefinitionCallSet(): Set<Call> =
        this
                .filterIsInstance<Call>()
                .filter { org.elixir_lang.structure_view.element.CallDefinitionClause.`is`(it) }
                .toSet()

private fun Call.toCallNameArityRange(): CallNameArityRange? =
        nameArityRange(this)?.let { (name, arityRange) ->
            CallNameArityRange(this, name, arityRange)
        }

private fun Iterable<Call>.withNameArityRange(): Iterable<CallNameArityRange> =
    this.mapNotNull { it.toCallNameArityRange() }

private fun Iterable<CallNameArityRange>.withEnclosingModularMacroCall():
        Iterable<EnclosingCallEnclosedCallNameArityRange> =
        this.mapNotNull { it.toEnclosingCallEnclosedCallNameArityRange() }

private fun Iterable<EnclosingCallEnclosedCallNameArityRange>.toSecondaryElements(): List<PsiElement> =
        this.flatMap { it.toSecondaryElements() }
