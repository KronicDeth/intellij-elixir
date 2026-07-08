package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil.adjustOffset
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.UsageTarget
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.model.psi.module_attribute.ModuleAttributeSymbol
import org.elixir_lang.model.psi.variable.VariableSymbol
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.Type as TypeElement

internal class UsageTargetProvider : com.intellij.usages.UsageTargetProvider {
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
            when {
                psiElement.containingFile !is ElixirFile -> null
                Callback.isHead(psiElement) || TypeElement.isHead(psiElement) || ModuleAttributeSymbol.isHead(psiElement) || VariableSymbol.isHead(psiElement) || CallDefinitionClause.isHead(psiElement) -> {
                    // Call-definition clause heads (including `@callback`/`@macrocallback` and protocol function
                    // declarations) are owned by the Symbol model; don't contribute a redundant legacy usage target.
                    null
                }

                else -> {
                    when (psiElement) {
                        is Call, is ModuleImpl<*>, is QualifiableAlias ->
                            arrayOf(PsiElement2UsageTargetAdapter(psiElement, true))

                        else ->
                            null
                    }
                }
            }
}
