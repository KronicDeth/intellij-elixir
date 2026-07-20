package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.model.Pointer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.usages.impl.rules.UsageType

/**
 * A [PsiUsage] backed by a file + range, optionally carrying a [UsageType] so it is grouped under a
 * dedicated heading (e.g. "Implementation") in the usage view. Port of Markdown's `MarkdownPsiUsage`.
 */
@Suppress("UnstableApiUsage")
class ElixirPsiUsage(
    override val file: PsiFile,
    override val range: TextRange,
    override val declaration: Boolean,
    override val usageType: UsageType? = null
) : PsiUsage {
    override fun createPointer(): Pointer<out PsiUsage> {
        val declaration = this.declaration // capture for the restore lambda
        val usageType = this.usageType
        return Pointer.fileRangePointer(file, range) { restoredFile, restoredRange ->
            ElixirPsiUsage(restoredFile, restoredRange, declaration, usageType)
        }
    }

    companion object {
        fun create(
            element: PsiElement,
            rangeInElement: TextRange,
            declaration: Boolean = false,
            usageType: UsageType? = null
        ): ElixirPsiUsage =
            if (element is PsiFile) {
                ElixirPsiUsage(element, rangeInElement, declaration, usageType)
            } else {
                ElixirPsiUsage(
                    element.containingFile,
                    rangeInElement.shiftRight(element.textRange.startOffset),
                    declaration,
                    usageType
                )
            }
    }
}
