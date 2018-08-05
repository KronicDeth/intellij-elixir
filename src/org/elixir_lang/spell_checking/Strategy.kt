package org.elixir_lang.spell_checking

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.spellchecker.tokenizer.Tokenizer
import org.elixir_lang.psi.ElixirTypes

class Strategy : com.intellij.spellchecker.tokenizer.SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement): Tokenizer<*> =
        when (element) {
            is LeafPsiElement -> getTokenizer(element)
            else -> EMPTY_TOKENIZER
        }

    private fun getTokenizer(element: LeafPsiElement) =
            element.elementType.let { elementType ->
                when (elementType) {
                    ElixirTypes.ALIAS_TOKEN -> org.elixir_lang.spell_checking.Tokenizer(
                            org.elixir_lang.spell_checking.alias.Splitter
                    )
                    ElixirTypes.IDENTIFIER_TOKEN -> org.elixir_lang.spell_checking.Tokenizer(
                            org.elixir_lang.spell_checking.identifier.Splitter
                    )
                    else -> EMPTY_TOKENIZER
                }
            }
}
