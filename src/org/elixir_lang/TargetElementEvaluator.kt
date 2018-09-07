package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.QualifiedAlias
import org.elixir_lang.psi.call.Call

class TargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun adjustTargetElement(editor: Editor?, offset: Int, flags: Int, targetElement: PsiElement): PsiElement? =
            when (targetElement) {
                /* to prevent Goto To Declaration becoming Find Usages as happens when there is only one element
                   https://github.com/JetBrains/intellij-community/blob/5600f7c843e77b8b02fd41568ff1ea1b99e69d34/platform/lang-impl/src/com/intellij/codeInsight/navigation/actions/GotoDeclarationAction.java#L116-L119 */
                is Call -> null
                else -> super.adjustTargetElement(editor, offset, flags, targetElement)
            }

    override fun getElementByReference(reference: PsiReference, flags: Int): PsiElement? =
        reference.resolve().let { resolved ->
            /* DO NOT let references to definers resolve as targets as it causes the definer to be the target, which
               then has a reference to the definer's macro */
            if (resolved != null &&
                    resolved is Call &&
                    CallDefinitionClause.`is`(resolved)) {
                reference.element
            } else {
                resolved
            }
        }

    override fun getGotoDeclarationTarget(element: PsiElement, navElement: PsiElement?): PsiElement? =
        when (element) {
            is QualifiedAlias -> {
                element
                        .references
                        .asSequence()
                        .flatMap {
                            it.resolve()?.let { sequenceOf(it) } ?:
                            emptySequence()
                        }
                        .singleOrNull()
            }
            else -> super.getGotoDeclarationTarget(element, navElement)
        }
}
