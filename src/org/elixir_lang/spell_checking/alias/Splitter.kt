package org.elixir_lang.spell_checking.alias

import com.intellij.openapi.util.TextRange

object Splitter : org.elixir_lang.spell_checking.Splitter() {
    override fun wordTextRanges(text: String, range: TextRange): List<TextRange> =
            super.wordTextRanges(text, range).flatMap { wordTextRange ->
                splitByCamelCase(text, wordTextRange)
            }

    /* Like Apache Commons StringUtils.splitByCharacterTypeCamelCase
       (https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/src-html/org/apache/commons/lang3/StringUtils.html#line.3164),
       but it returns on TextRanges instead of String */
    private fun splitByCamelCase(text: String, range: TextRange): List<TextRange> {
        var tokenStart = 0
        var currentType = Character.getType(text.codePointAt(range.startOffset))
        val list = mutableListOf<TextRange>()

        for (offset in range.startOffset + 1..range.endOffset) {
            if (offset >= text.length) {
                list.add(TextRange(tokenStart, offset))
                break
            }

            val type = Character.getType(text.codePointAt(offset))

            if (type == currentType) {
                continue
            }

            if (type == Character.LOWERCASE_LETTER.toInt() && currentType == Character.UPPERCASE_LETTER.toInt()) {
                val newTokenStart = offset - 1

                if (newTokenStart != tokenStart) {
                    list.add(TextRange(tokenStart, newTokenStart))
                    tokenStart = newTokenStart
                }
            } else {
                list.add(TextRange(tokenStart, offset))
                tokenStart = offset
            }

            currentType = type
        }

        return list
    }
}
