package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.apache.commons.lang.math.Range
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange

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

typealias ArityRange = IntRange

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
                            nameArityRange.first == name &&
                            nameArityRange.second.overlapsRange(arityRange.toRange())
                }.map { (call, _) -> call }
                .asIterable()
}

private fun IntRange.toRange(): Range? = org.apache.commons.lang.math.IntRange(this.first, this.last)

private fun Array<PsiElement>.toCallDefinitionCallSet(): Set<Call> =
        this
                .filterIsInstance<Call>()
                .filter { org.elixir_lang.structure_view.element.CallDefinitionClause.`is`(it) }
                .toSet()

private fun Call.toCallNameArityRange(): CallNameArityRange? =
        nameArityRange(this)?.let { nameArityRange ->
            nameArityRange.first?.let { name ->
                nameArityRange.second?.let { arityRange ->
                    CallNameArityRange(
                            this,
                            name,
                            ArityRange(arityRange.minimumInteger, arityRange.maximumInteger)
                    )
                }
            }

        }

private fun Iterable<Call>.withNameArityRange(): Iterable<CallNameArityRange> =
    this.mapNotNull { it.toCallNameArityRange() }

private fun Iterable<CallNameArityRange>.withEnclosingModularMacroCall(): Iterable<EnclosingCallEnclosedCallNameArityRange> =
        this.mapNotNull { it.toEnclosingCallEnclosedCallNameArityRange() }

private fun Iterable<EnclosingCallEnclosedCallNameArityRange>.toSecondaryElements(): List<PsiElement> =
        this.flatMap { it.toSecondaryElements() }
