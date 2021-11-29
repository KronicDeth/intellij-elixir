package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.navigation.ItemPresentation

fun LookupElementPresentation.putItemPresentation(itemPresentation: ItemPresentation) {
    this.icon = itemPresentation.getIcon(true)

    itemPresentation.presentableText?.let { presentableText ->
        if (presentableText.startsWith(itemText!!)) {
            appendTailText(presentableText.removePrefix(itemText!!), true)
        }
    }

    itemPresentation.locationString?.let { locationString ->
        appendTailText(" ($locationString)", false)
    }
}
