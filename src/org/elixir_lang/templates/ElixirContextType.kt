package org.elixir_lang.templates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiUtilCore
import org.elixir_lang.ElixirLanguage

class ElixirContextType internal constructor() :
    TemplateContextType("Elixir") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val psiFile = templateActionContext.file
        val offset = templateActionContext.startOffset

        return if (!PsiUtilCore.getLanguageAtOffset(psiFile, offset).isKindOf(ElixirLanguage)) {
            false
        } else {
            val element = psiFile.findElementAt(offset)
            element !is PsiWhiteSpace && element != null
        }
    }
}
