package org.elixir_lang

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget
import org.elixir_lang.psi.QualifiableAlias

class UsageTargetProvider : com.intellij.usages.UsageTargetProvider {
    override fun getTargets(editor: Editor, file: PsiFile): Array<UsageTarget>? = null

    override fun getTargets(psiElement: PsiElement): Array<UsageTarget>? =
        when (psiElement) {
            is QualifiableAlias -> getTargets(psiElement)
            else -> null
        }

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
