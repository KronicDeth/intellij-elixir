package org.elixir_lang.reference.module

import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.elixir_lang.Module.concat
import org.elixir_lang.psi.operation.Normalized.operatorIndex
import org.elixir_lang.psi.call.StubBased
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.operation.infix.Normalized

object ResolvableName {
    /**
     * The full name of the qualifiable alias, with any multiple aliases expanded
     */
    fun resolvableName(qualifiableAlias: QualifiableAlias): String {
        val tail = qualifiableAlias
                .fullyQualifiedName()
                ?.let { resolvableName -> mutableListOf(resolvableName) }
                .orEmpty()

        return up(qualifiableAlias.parent, tail)
    }

    private fun up(ancestor: PsiElement?, tail: List<String>): String = if (ancestor is ElixirAccessExpression ||
            ancestor is ElixirMultipleAliases) {
        up(ancestor.parent, tail)
    } else if (ancestor is QualifiedMultipleAliases) {
        up(ancestor, tail)
    } else {
        concat(tail)
    }

    private fun up(ancestor: QualifiedMultipleAliases, tail: List<String>): String {
        val children = ancestor.children
        val operatorIndex = operatorIndex(children)

        return Normalized
                .leftOperand(children, operatorIndex)
                ?.let(this::down)
                .orEmpty()
                .let { head -> head + tail }
                .let(::concat)
    }

    private fun down(qualifier: PsiElement): List<String> = when (qualifier) {
        is Call -> down(qualifier)
        is ElixirAccessExpression -> down(qualifier.getChildren())
        is QualifiableAlias -> down(qualifier)
        else -> listOf()
    }


    private fun down(qualifier: Call): List<String> = if (qualifier.isCalling(Module.KERNEL, Function.__MODULE__, 0)) {
        enclosingModularMacroCall(qualifier)?.let { enclosingCall -> enclosingCall as? StubBased<*> }?.canonicalName()
    } else {
        null
    }?.let(::listOf).orEmpty()

    private fun down(qualifiers: Array<PsiElement>): List<String> = qualifiers.flatMap { qualifier ->
        down(qualifier)
    }

    private fun down(qualifier: QualifiableAlias): List<String> =
            qualifier.name?.let { listOf(it) }.orEmpty()
}
