package org.elixir_lang.spell_checking

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.spellchecker.inspections.BaseSplitter
import com.intellij.util.Consumer

open class Splitter : BaseSplitter() {
    override fun split(text: String?, range: TextRange, consumer: Consumer<TextRange>) {
        if (text != null && 1 <= range.length && 0 <= range.startOffset) {
            wordTextRanges(text, range).forEach { consumer.consume(it) }
        }
    }

    private val WORD_REGEX = Regex("[^_0-9?!]+")

    protected open fun wordTextRanges(text: String, range: TextRange): List<TextRange> {
        val startOffset = range.startOffset

        return try {
                range
                        .substring(text)
                        .let { StringUtil.newBombedCharSequence(it, 500) }
                        .let { WORD_REGEX.findAll(it) }
                        .toList()
                        .map { matchResult ->
                            val matchResultRange = matchResult.range

                            TextRange(startOffset + matchResultRange.first, startOffset + matchResultRange.last + 1)
                        }
            } catch (processCanceledException: ProcessCanceledException) {
                listOf(range)
            }
    }
}
