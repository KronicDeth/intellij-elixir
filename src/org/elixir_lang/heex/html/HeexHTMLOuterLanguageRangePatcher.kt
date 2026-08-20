package org.elixir_lang.heex.html

import com.intellij.psi.templateLanguages.DefaultOuterLanguagePatcher
import com.intellij.psi.templateLanguages.TemplateDataElementType
import org.elixir_lang.heex.file.psi.TemplateData

/**
 * Registered for `language="HTML"` - [TemplateDataElementType.OuterLanguageRangePatcher.EXTENSION]
 * has one patcher per language, so this runs for every HTML-data template language in the IDE, not
 * just HEEx. [getTextForOuterLanguageInsertionRange] is passed the [TemplateDataElementType]
 * instance responsible for the range, so it must check that instance and return `null` - meaning
 * "insert nothing" - for anyone other than [TemplateData.INSTANCE], to leave every other HTML
 * template language's own (patcher-less) behaviour alone.
 */
class HeexHTMLOuterLanguageRangePatcher : TemplateDataElementType.OuterLanguageRangePatcher {
    override fun getTextForOuterLanguageInsertionRange(
        templateDataElementType: TemplateDataElementType,
        outerElementText: CharSequence
    ): String? {
        if (templateDataElementType !== TemplateData.INSTANCE) return null

        return DefaultOuterLanguagePatcher.OUTER_EXPRESSION_PLACEHOLDER
    }
}
