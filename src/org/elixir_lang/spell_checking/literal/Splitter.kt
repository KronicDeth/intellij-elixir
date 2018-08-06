package org.elixir_lang.spell_checking.literal

import com.intellij.openapi.util.TextRange
import com.intellij.spellchecker.inspections.BaseSplitter
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.util.Consumer

object Splitter : BaseSplitter() {
    override fun split(text: String?, range: TextRange, consumer: Consumer<TextRange>?) {
        if (text != null && 1 <= range.length && 0 <= range.startOffset) {
            val nonescapedTextRangeList = excludeByPattern(text, range, ESCAPE_SEQUENCE_PATTERN, 0)

            if (nonescapedTextRangeList.isNotEmpty()) {
                val plainTextSplitter = PlainTextSplitter.getInstance()

                nonescapedTextRangeList.forEach {
                    plainTextSplitter.split(text, it, consumer)
                }
            }
        }
    }

    private val ESCAPE_SEQUENCE_PATTERN = Regex("\\\\+(e\\[([0-9]|[1-9][0-9])m|\\\\x([0-9A-Fa-f]{1,2}|\\{[0-9A-Fa-f]{1,6}\\})|\\\\u([0-9A-Fa-f]{1,4}|\\{[0-9A-Fa-f]{1,6}\\})|.)|%[0-9A-Fa-f]{2}").toPattern()
}
