package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil.adjustOffset
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.outerMostQualifiableAlias

class UsageTargetProvider : com.intellij.usages.UsageTargetProvider {
    override fun getTargets(editor: Editor, file: PsiFile): Array<UsageTarget>? {
        val document = editor.document
        val offset = editor.caretModel.offset
        val adjustedOffset = adjustOffset(file, document, offset)

        return file.findReferenceAt(adjustedOffset)?.element?.let { element ->
            getTargets(element)
        }
    }

    override fun getTargets(psiElement: PsiElement): Array<UsageTarget>? =
        when (psiElement) {
            is AtNonNumericOperation, is Call, is ModuleImpl<*>, is QualifiableAlias ->
                PsiElement2UsageTargetAdapter(psiElement).let { arrayOf<UsageTarget>(it) }
            else ->
                null
        }
}

