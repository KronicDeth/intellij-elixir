package org.elixir_lang.spell_checking

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer

class Tokenizer(private val splitter: com.intellij.spellchecker.inspections.Splitter) : Tokenizer<LeafPsiElement>() {
    override fun tokenize(element: LeafPsiElement, consumer: TokenConsumer) {
        consumer.consumeToken(element, false, splitter)
    }
}
