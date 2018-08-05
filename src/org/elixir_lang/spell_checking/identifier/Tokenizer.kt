package org.elixir_lang.spell_checking.identifier

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer

object Tokenizer : Tokenizer<LeafPsiElement>() {
    override fun tokenize(element: LeafPsiElement, consumer: TokenConsumer) {
        consumer.consumeToken(element, true, Splitter)
    }
}
