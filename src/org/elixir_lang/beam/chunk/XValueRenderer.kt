package org.elixir_lang.beam.chunk

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueTextRendererBase

// Like XValuePresentationUtil.XValuePresentationTextExtractor, but StringValue is quoted
class XValueRenderer: XValueTextRendererBase() {
    private var stringBuilder = StringBuilder()

    override fun renderValue(value: String) {
        stringBuilder.append(value)
    }

    override fun renderRawValue(value: String, key: TextAttributesKey) {
        stringBuilder.append(value)
    }

    override fun renderStringValue(value: String, additionalSpecialCharsToHighlight: String?, maxLength: Int) {
        stringBuilder.append('"').append(value).append('"')
    }

    override fun renderComment(comment: String) {
        stringBuilder.append(comment)
    }

    override fun renderError(error: String) {
        stringBuilder.append(error)
    }

    override fun renderSpecialSymbol(symbol: String) {
        stringBuilder.append(symbol)
    }

    fun getText(): String {
        return stringBuilder.toString()
    }
}
