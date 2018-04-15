package org.elixir_lang

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget

class UsageTargetProvider : com.intellij.usages.UsageTargetProvider {
    override fun getTargets(editor: Editor?, file: PsiFile?): Array<UsageTarget>? = null

    override fun getTargets(psiElement: PsiElement?): Array<UsageTarget>? = null

//    override fun getTargets(psiElement: PsiElement?): Array<UsageTarget>? =
//        if (psiElement?.language == ElixirLanguage) {
//            val targetList = psiElement.references.flatMap {
//                getTargets(it)
//            }
//
//            if (targetList.isEmpty()) {
//                null
//            } else {
//                targetList.toTypedArray()
//            }
//        } else {
//            null
//        }
//
//    private fun getTargets(psiPolyVariantReference: PsiPolyVariantReference): kotlin.collections.List<UsageTarget> =
//        psiPolyVariantReference
//                .multiResolve(false)
//                .filter { it.isValidResult }
//                .mapNotNull { it.element }
//                .map { PsiElement2UsageTargetAdapter(it) }
//
//    private fun getTargets(psiReference: PsiReference): kotlin.collections.List<UsageTarget> =
//            when (psiReference) {
//                is PsiPolyVariantReference -> getTargets(psiReference)
//                else ->
//                    psiReference.resolve()?.let {
//                        PsiElement2UsageTargetAdapter(it).let { listOf(it) }
//                    } ?:
//                    emptyList()
//            }
}
