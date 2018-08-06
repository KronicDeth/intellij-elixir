package org.elixir_lang.spell_checking

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.inspections.Splitter
import com.intellij.spellchecker.tokenizer.Tokenizer
import org.elixir_lang.psi.ElixirTypes

class Strategy : com.intellij.spellchecker.tokenizer.SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement): Tokenizer<*> =
            when (element) {
                is LeafPsiElement -> getTokenizer(element)
                else -> EMPTY_TOKENIZER
            }

    private fun getTokenizer(element: LeafPsiElement) =
            element
                    .elementType
                    .let { splitter(it) }
                    ?.let { org.elixir_lang.spell_checking.Tokenizer(it) }
                    ?: EMPTY_TOKENIZER

    private fun splitter(elementType: IElementType): Splitter? =
            when (elementType) {
                ElixirTypes.ALIAS_TOKEN -> org.elixir_lang.spell_checking.alias.Splitter
                ElixirTypes.CHAR_LIST_FRAGMENT,
                ElixirTypes.REGEX_FRAGMENT,
                ElixirTypes.SIGIL_FRAGMENT,
                ElixirTypes.STRING_FRAGMENT,
                ElixirTypes.WORDS_FRAGMENT ->
                    org.elixir_lang.spell_checking.literal.Splitter
                ElixirTypes.COMMENT -> PlainTextSplitter.getInstance()
                ElixirTypes.IDENTIFIER_TOKEN -> org.elixir_lang.spell_checking.identifier.Splitter
                else -> null
            }
}
