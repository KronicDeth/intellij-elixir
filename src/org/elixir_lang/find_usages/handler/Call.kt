package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.util.BuildNumber
import com.intellij.psi.PsiElement
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

object AlreadyResolved {
    private val START = BuildNumber("", 213)
    private val END = BuildNumber("", 213, 6461)

    val alreadyResolved by lazy {
        val build = ApplicationInfoEx.getInstance().build

        START < build && build < END
    }
}

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
        val primaryElements = this.primaryElements

        primaryElements
            .mapNotNull(PsiElement::semantic)
            .filterIsInstance<Clause>()
            .mapTo(mutableSetOf(), Clause::definition)
            .flatMap(Definition::clauses)
            .map(Clause::psiElement)
            .filterNot { primaryElements.contains(it) }
            .toTypedArray()
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements
    override fun getSecondaryElements(): Array<PsiElement> = _secondaryElements

    private fun resolvedElements() =
        if (AlreadyResolved.alreadyResolved) {
            super.getPrimaryElements()
        } else {
            super
                .getPrimaryElements()
                .flatMap { it.references.toList() }
                .flatMap { it.toPsiElementList() }
                .filter { it !is Call || it.semantic !is org.elixir_lang.semantic.Import }
                .toTypedArray()
        }
}
