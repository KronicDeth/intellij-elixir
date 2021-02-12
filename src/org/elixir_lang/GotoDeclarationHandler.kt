package org.elixir_lang

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.outerMostQualifiableAlias

class GotoDeclarationHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? =
            when (val parent = sourceElement?.parent) {
                is QualifiableAlias -> {
                    parent.outerMostQualifiableAlias().reference?.let { reference ->
                        when (reference) {
                            is PsiPolyVariantReference -> {
                                reference.multiResolve(false).mapNotNull { resolveResult -> resolveResult.element }
                            }
                            else -> reference.resolve()?.let { listOf(it) }.orEmpty()
                        }.toTypedArray()
                    }
                }
                else -> null
            }
}
