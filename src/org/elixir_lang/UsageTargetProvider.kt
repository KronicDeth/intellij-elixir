package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil.adjustOffset
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call

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
            is Call -> getTargets(psiElement)
            is ModuleImpl<*> -> getTargets(psiElement)
            is QualifiableAlias -> getTargets(psiElement)
            else -> null
        }

    private fun getTargets(call: Call): Array<UsageTarget> =
            PsiElement2UsageTargetAdapter(call).let { arrayOf(it) }

    private fun getTargets(moduleImpl: ModuleImpl<*>): Array<UsageTarget> =
            PsiElement2UsageTargetAdapter(moduleImpl).let { arrayOf(it) }

    private fun getTargets(qualifiableAlias: QualifiableAlias): Array<UsageTarget> =
        qualifiableAlias.outerMostQualifiableAlias().let { PsiElement2UsageTargetAdapter(it) }.let { arrayOf(it) }
}

private tailrec fun QualifiableAlias.outerMostQualifiableAlias(): QualifiableAlias {
    val parent = parent!!

    return if (parent is QualifiableAlias) {
        parent.outerMostQualifiableAlias()
    } else {
        val grandParent = parent.parent

        if (grandParent is QualifiableAlias) {
            grandParent.outerMostQualifiableAlias()
        } else {
            this
        }
    }
}
