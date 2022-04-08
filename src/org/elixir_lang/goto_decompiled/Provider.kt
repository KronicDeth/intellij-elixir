package org.elixir_lang.goto_decompiled

import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.goto_decompiled.item.CallDefinitionClause
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

/**
 * Go To Related from source to decompiled version of the same function
 */
class Provider : GotoRelatedProvider() {
    override tailrec fun getItems(psiElement: PsiElement): List<GotoRelatedItem> {
        val semantic = psiElement.semantic

        return if (semantic != null) {
            getItems(semantic)
        } else {
            val parent = psiElement.parent

            if (parent != null && parent !is PsiFile) {
                getItems(parent)
            } else {
                emptyList()
            }
        }
    }

    private fun getItems(semantic: Semantic): List<GotoRelatedItem> =
        when (semantic) {
            is org.elixir_lang.semantic.Modular -> getItems(semantic)
            is Clause -> getItems(semantic)
            else -> emptyList()
        }

    private fun getItems(modular: org.elixir_lang.semantic.Modular): List<GotoRelatedItem> =
        modular.decompiled.map { decompiledModular ->
            org.elixir_lang.goto_decompiled.item.Modular(decompiledModular)
        }

    private fun getItems(clause: Clause): List<GotoRelatedItem> =
        getItems(clause.definition)

    private fun getItems(definition: Definition): List<GotoRelatedItem> =
        definition.decompiled.flatMap { decompiledDefinition ->
            decompiledDefinition.clauses.map { decompiledClause ->
                CallDefinitionClause(decompiledClause)
            }
        }
}
