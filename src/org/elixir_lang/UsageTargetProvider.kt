package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil.adjustOffset
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.AtOperation
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call

class UsageTargetProvider : com.intellij.usages.UsageTargetProvider {
    override fun getTargets(editor: Editor, file: PsiFile): Array<UsageTarget>? = if (file is ElixirFile) {
        val document = editor.document
        val offset = editor.caretModel.offset
        val adjustedOffset = adjustOffset(file, document, offset)

        file.findReferenceAt(adjustedOffset)?.element?.let { element ->
            getTargets(element)
        }
    } else {
        null
    }

    override fun getTargets(psiElement: PsiElement): Array<UsageTarget>? =
        if (psiElement.containingFile is ElixirFile) {
            when (psiElement) {
                is AtOperation, is Call, is ModuleImpl<*>, is QualifiableAlias ->
                    arrayOf(PsiElement2UsageTargetAdapter(psiElement, true))
                else ->
                    null
            }
        } else {
            null
        }
}

