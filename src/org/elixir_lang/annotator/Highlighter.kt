package org.elixir_lang.annotator

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

object Highlighter {
    fun highlight(annotationHolder: AnnotationHolder, textAttributesKey: TextAttributesKey) {
        highlight(annotationHolder, textAttributesKey) { it }
    }

    fun highlight(annotationHolder: AnnotationHolder, psiElement: PsiElement, textAttributesKey: TextAttributesKey) {
        highlight(annotationHolder, textAttributesKey) { it.range(psiElement) }
    }

    fun highlight(annotationHolder: AnnotationHolder, astNode: ASTNode, textAttributesKey: TextAttributesKey) {
        highlight(annotationHolder, textAttributesKey) { it.range(astNode) }
    }

    fun highlight(annotationHolder: AnnotationHolder, textRange: TextRange, textAttributesKey: TextAttributesKey) {
        highlight(annotationHolder, textAttributesKey) { it.range(textRange) }
    }

    fun highlight(annotationHolder: AnnotationHolder, textRange: TextRange, textAttributes: TextAttributes) {
        highlight(annotationHolder, textAttributes) { it.range(textRange) }
    }

    private fun highlight(annotationHolder: AnnotationHolder, textAttributesKey: TextAttributesKey, build: (builder: AnnotationBuilder) -> AnnotationBuilder) {
        highlight(annotationHolder, EditorColorsManager.getInstance().globalScheme.getAttributes(textAttributesKey), build)
    }

    private fun highlight(annotationHolder: AnnotationHolder, textAttributes: TextAttributes, build: (builder: AnnotationBuilder) -> AnnotationBuilder) {
        build(
                annotationHolder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
        ).create()

        build(
                annotationHolder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .enforcedTextAttributes(textAttributes)
        ).create()
    }
}
