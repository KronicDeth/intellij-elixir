package org.elixir_lang.annotator

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * Applies semantic syntax colouring on behalf of the annotators.
 *
 * **Severity must stay [HighlightInfoType.SYMBOL_TYPE_SEVERITY].** `HighlightInfoFilterImpl.accept` silently
 * discards any [com.intellij.codeInsight.daemon.impl.HighlightInfo] on a file whose `getOriginalFile()` is a
 * [com.intellij.psi.PsiCompiledFile] unless the severity is exactly that, so
 * [HighlightSeverity.INFORMATION] means no highlighting at all in decompiled `.beam` files, with nothing
 * logged.
 */
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

    /**
     * Resolves [textAttributesKey] eagerly rather than calling `AnnotationBuilder.textAttributes(key)`,
     * which looks more idiomatic but does not keep the key: `forcedTextAttributesKey` on the resulting
     * [com.intellij.codeInsight.daemon.impl.HighlightInfo] stays `null` either way. Tests therefore have to
     * compare resolved [TextAttributes] - see `BeamHighlightingTest`.
     */
    private fun highlight(annotationHolder: AnnotationHolder, textAttributesKey: TextAttributesKey, build: (builder: AnnotationBuilder) -> AnnotationBuilder) {
        highlight(annotationHolder, EditorColorsManager.getInstance().globalScheme.getAttributes(textAttributesKey), build)
    }

    /** For callers that merge attributes and so have no single key to name. */
    private fun highlight(annotationHolder: AnnotationHolder, textAttributes: TextAttributes, build: (builder: AnnotationBuilder) -> AnnotationBuilder) {
        eraseUnderlyingHighlighting(annotationHolder, build)

        build(
                annotationHolder
                        .newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                        .enforcedTextAttributes(textAttributes)
        ).create()
    }

    /** Wipes the lexer's colouring first, so the semantic attributes replace it rather than blend with it. */
    private fun eraseUnderlyingHighlighting(annotationHolder: AnnotationHolder, build: (builder: AnnotationBuilder) -> AnnotationBuilder) {
        build(
                annotationHolder
                        .newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
        ).create()
    }
}
