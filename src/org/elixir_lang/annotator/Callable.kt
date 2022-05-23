package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.psi.AtOperation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.UnqualifiedBracketOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS
import org.elixir_lang.reference.Callable.Companion.BIT_STRING_TYPES
import org.elixir_lang.reference.Callable.Companion.isBitStreamSegmentOption
import org.elixir_lang.safeMultiResolve
import org.elixir_lang.structure_view.element.Timed
import java.util.*

/**
 * Annotates callables.
 */
class Callable : Annotator, DumbAware {
    /**
     * Annotates the specified PSI element.
     * It is guaranteed to be executed in non-reentrant fashion.
     * I.e there will be no call of this method for this instance before previous call get completed.
     * Multiple instances of the annotator might exist simultaneously, though.
     *
     * @param element to annotate.
     * @param holder  the container which receives annotations created by the plugin.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(
            object : PsiElementVisitor() {
                /*
                 * Public Instance Methods
                 */

                private fun visitCall(call: Call) {
                    if (!(call is AtOperation || call is AtUnqualifiedNoParenthesesCall<*>)) {
                        visitNonModuleAttributeCall(call)
                    }
                }

                /*
                 * Private Instance Methods
                 */

                private fun visitCallDefinitionClause(call: Call) {
                    // visit the `def(macro)?p? for Kernel PREDEFINED highlighting
                    visitPlainCall(call)

                    CallDefinitionClause.head(call)?.let { head ->
                        visitCallDefinitionHead(head, call)
                    }
                }

                /*
                 * Private Instance Methods
                 */

                private fun visitCallDefinitionHead(head: PsiElement, clause: Call) {
                    val stripped = org.elixir_lang.structure_view.element.CallDefinitionHead.strip(head)

                    if (stripped is Call) {
                        visitStrippedCallDefinitionHead(stripped, clause)
                    }
                }

                override fun visitElement(element: PsiElement) {
                    when (element) {
                        is Call -> visitCall(element)
                        is UnqualifiedBracketOperation -> visitUnqualifiedBracketOperation(element)
                    }
                }

                private fun visitNonModuleAttributeCall(call: Call) {
                    if (CallDefinitionClause.`is`(call)) {
                        visitCallDefinitionClause(call)
                    } else {
                        visitPlainCall(call)
                    }
                }

                private fun visitPlainCall(call: Call) {
                    val reference = call.reference

                    if (reference != null) {
                        val resolvedCollection =
                            if (reference is PsiPolyVariantReference) {
                                safeMultiResolve(reference, false)
                                    .filter { it.isValidResult }
                                    .mapNotNull(ResolveResult::getElement)
                            } else {
                                reference.resolve()?.let { resolved ->
                                    setOf(resolved)
                                }
                            }

                        if (resolvedCollection != null && resolvedCollection.isNotEmpty()) {
                            highlight(call, reference.rangeInElement, resolvedCollection, holder)
                        } else if (call.hasDoBlockOrKeyword()) {
                            /* Even though it can't be resolved, it is called like a macro, so highlight like one */
                            call.functionNameElement()?.let { functionNameElement ->
                                highlight(
                                    functionNameElement.textRange,
                                    holder,
                                    ElixirSyntaxHighlighter.MACRO_CALL
                                )
                            }
                        }
                    } else if (isBitStreamSegmentOption(call)) {
                        val name = call.name

                        if (name != null && BIT_STRING_TYPES.contains(name)) {
                            highlight(call, holder, ElixirSyntaxHighlighter.TYPE)
                        }
                    }
                }

                private fun visitStrippedCallDefinitionHead(stripped: Call, clause: Call) {
                    stripped.functionNameElement()?.let { functionNameElement ->
                        val textAttributeKey = when {
                            CallDefinitionClause.isFunction(clause) -> ElixirSyntaxHighlighter.FUNCTION_DECLARATION
                            CallDefinitionClause.isMacro(clause) -> ElixirSyntaxHighlighter.MACRO_DECLARATION
                            else -> null
                        }

                        if (textAttributeKey != null) {
                            highlight(functionNameElement, holder, textAttributeKey)
                        }
                    }
                }

                private fun visitUnqualifiedBracketOperation(unqualifiedBracketOperation: UnqualifiedBracketOperation) {
                    val identifier = unqualifiedBracketOperation.identifier

                    identifier.reference?.let { it as PsiPolyVariantReference }?.let { reference ->
                        val resolvedElements = reference.multiResolve(false).filter { it.isValidResult }
                            .map { (it as PsiElementResolveResult).element }

                        if (resolvedElements.isNotEmpty()) {
                            highlight(identifier, reference.rangeInElement, resolvedElements, holder)
                        }
                    }
                }
            }
        )
    }

    private fun callHighlight(resolved: Call, previousCallHighlight: CallHighlight?): CallHighlight? =
        when {
            CallDefinitionClause.isFunction(resolved) -> {
                val referrerTextAttributesKeys = referrerTextAttributesKeys(
                    resolved,
                    FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS,
                    PREDEFINED_FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS
                )

                CallHighlight.nullablePut(
                    previousCallHighlight,
                    referrerTextAttributesKeys
                )
            }
            CallDefinitionClause.isMacro(resolved) -> {
                val referrerTextAttributesKeys = referrerTextAttributesKeys(
                    resolved,
                    MACRO_CALL_TEXT_ATTRIBUTES_KEYS,
                    PREDEFINED_MACRO_CALL_TEXT_ATTRIBUTES_KEYS
                )

                CallHighlight.nullablePut(
                    previousCallHighlight,
                    referrerTextAttributesKeys
                )
            }
            org.elixir_lang.reference.Callable.isParameter(resolved) -> {
                CallHighlight.nullablePut(
                    previousCallHighlight,
                    PARAMETER_TEXT_ATTRIBUTE_KEYS
                )
            }
            org.elixir_lang.reference.Callable.isParameterWithDefault(resolved) -> {
                CallHighlight.nullablePut(
                    previousCallHighlight,
                    PARAMETER_TEXT_ATTRIBUTE_KEYS
                )
            }
            org.elixir_lang.reference.Callable.isVariable(resolved) -> {
                CallHighlight.nullablePut(
                    previousCallHighlight,
                    VARIABLE_TEXT_ATTRIBUTE_KEYS
                )
            }
            else -> {
                previousCallHighlight
            }
        }

    private fun callHighlight(resolved: PsiElement, previousCallHighlight: CallHighlight?): CallHighlight? =
        when (resolved) {
            is Call -> callHighlight(resolved, previousCallHighlight)
            is CallDefinitionImpl<*> -> callHighlight(resolved, previousCallHighlight)
            else ->
                if (org.elixir_lang.reference.Callable.isIgnored(resolved)) {
                    CallHighlight.nullablePut(
                        previousCallHighlight,
                        IGNORED_VARIABLE_TEXT_ATTRIBUTE_KEYS
                    )
                } else {
                    previousCallHighlight
                }
        }

    private fun callHighlight(resolved: CallDefinitionImpl<*>, previousCallHighlight: CallHighlight?): CallHighlight? {
        val referrerTextAttributesKeys = when (resolved.time) {
            Timed.Time.COMPILE -> referrerTextAttributesKeys(
                resolved,
                MACRO_CALL_TEXT_ATTRIBUTES_KEYS,
                PREDEFINED_MACRO_CALL_TEXT_ATTRIBUTES_KEYS
            )
            Timed.Time.RUN -> referrerTextAttributesKeys(
                resolved,
                FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS,
                PREDEFINED_FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS
            )
        }

        return CallHighlight.nullablePut(
            previousCallHighlight,
            referrerTextAttributesKeys
        )
    }

    private fun callHighlight(resolvedCollection: Collection<PsiElement>): CallHighlight? =
        resolvedCollection.fold(null) { acc: CallHighlight?, resolved ->
            callHighlight(resolved, acc)
        }

    private fun highlight(
        referrer: PsiElement,
        rangeInReferrer: TextRange,
        resolvedCollection: Collection<PsiElement>,
        annotationHolder: AnnotationHolder
    ) {
        val callHighlight = callHighlight(resolvedCollection)

        if (callHighlight != null) {
            val referrerTextAttributesKeys = callHighlight.referrerTextAttributeKeys

            if (referrerTextAttributesKeys != null) {
                highlight(referrer, rangeInReferrer, annotationHolder, *referrerTextAttributesKeys)
            }
        }
    }

    private fun highlight(
        element: PsiElement,
        annotationHolder: AnnotationHolder,
        textAttributesKey: TextAttributesKey
    ) {
        highlight(element.textRange, annotationHolder, textAttributesKey)
    }

    private fun highlight(
        element: PsiElement,
        rangeInElement: TextRange,
        annotationHolder: AnnotationHolder,
        vararg textAttributesKeys: TextAttributesKey
    ) {
        val elementRangeInDocument = element.textRange
        val startOffset = elementRangeInDocument.startOffset

        val rangeInElementInDocument = TextRange(
            startOffset + rangeInElement.startOffset,
            startOffset + rangeInElement.endOffset
        )

        highlight(rangeInElementInDocument, annotationHolder, *textAttributesKeys)
    }

    /**
     * Highlights `textRange` with the given `textAttributesKey`.
     *
     * @param textRange          textRange in the document to highlight
     * @param annotationHolder   the container which receives annotations created by the plugin.
     * @param textAttributesKeys text attributes to apply to the `node`.
     */
    private fun highlight(
        textRange: TextRange,
        annotationHolder: AnnotationHolder,
        vararg textAttributesKeys: TextAttributesKey
    ) {
        if (textAttributesKeys.isNotEmpty()) {
            val editorColorsScheme = EditorColorsManager.getInstance().globalScheme
            val mergedTextAttributes = textAttributesKeys.fold(null) { acc: TextAttributes?, textAttributesKey ->
                val textAttributes = editorColorsScheme.getAttributes(textAttributesKey)

                TextAttributes.merge(acc, textAttributes)
            }!!

            Highlighter.highlight(annotationHolder, textRange, mergedTextAttributes)
        }
    }

    private class CallHighlight private constructor(val referrerTextAttributeKeys: Array<TextAttributesKey>?) {
        fun put(referrerTextAttributeKeys: Array<TextAttributesKey>?): CallHighlight {
            val updatedReferrerTextAttributeKeys = bestReferrerTextAttributeKeys(referrerTextAttributeKeys)

            return if (updatedReferrerTextAttributeKeys == this.referrerTextAttributeKeys) {
                this
            } else {
                CallHighlight(
                    updatedReferrerTextAttributeKeys
                )
            }
        }

        private fun bestReferrerTextAttributeKeys(referrerTextAttributeKeys: Array<TextAttributesKey>?): Array<TextAttributesKey>? =
            if (this.referrerTextAttributeKeys != null) {
                if (referrerTextAttributeKeys != null) {
                    var currentPredefined = false

                    for (textAttributesKey in this.referrerTextAttributeKeys) {
                        if (textAttributesKey === ElixirSyntaxHighlighter.PREDEFINED_CALL) {
                            currentPredefined = true
                            break
                        }
                    }

                    if (currentPredefined) {
                        this.referrerTextAttributeKeys
                    } else {
                        referrerTextAttributeKeys
                    }
                } else {
                    this.referrerTextAttributeKeys
                }
            } else {
                referrerTextAttributeKeys
            }

        companion object {
            internal fun nullablePut(
                callHighlight: CallHighlight?,
                referrerTextAttributeKeys: Array<TextAttributesKey>?
            ): CallHighlight =
                callHighlight?.put(
                    referrerTextAttributeKeys
                ) ?: CallHighlight(
                    referrerTextAttributeKeys
                )
        }
    }

    companion object {
        private val FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS = arrayOf(ElixirSyntaxHighlighter.FUNCTION_CALL)
        private val IGNORED_VARIABLE_TEXT_ATTRIBUTE_KEYS = arrayOf(ElixirSyntaxHighlighter.IGNORED_VARIABLE)
        private val MACRO_CALL_TEXT_ATTRIBUTES_KEYS = arrayOf(ElixirSyntaxHighlighter.MACRO_CALL)
        private val PARAMETER_TEXT_ATTRIBUTE_KEYS = arrayOf(ElixirSyntaxHighlighter.PARAMETER)
        private val PREDEFINED_FUNCTION_CALL_TEXT_ATTRIBUTE_KEYS =
            arrayOf(ElixirSyntaxHighlighter.FUNCTION_CALL, ElixirSyntaxHighlighter.PREDEFINED_CALL)

        private val PREDEFINED_LOCATION_STRING_SET = HashSet(
            Arrays.asList(
                KERNEL,
                KERNEL_SPECIAL_FORMS
            )
        )
        private val PREDEFINED_MACRO_CALL_TEXT_ATTRIBUTES_KEYS =
            arrayOf(ElixirSyntaxHighlighter.MACRO_CALL, ElixirSyntaxHighlighter.PREDEFINED_CALL)
        private val VARIABLE_TEXT_ATTRIBUTE_KEYS = arrayOf(ElixirSyntaxHighlighter.VARIABLE)

        private fun referrerTextAttributesKeys(
            psiElement: PsiElement,
            standardTextAttributeKeys: Array<TextAttributesKey>,
            predefinedTextAttributesKeys: Array<TextAttributesKey>
        ): Array<TextAttributesKey> =
            when (psiElement) {
                is CallDefinitionImpl<*> -> if (psiElement.parent.name in PREDEFINED_LOCATION_STRING_SET) {
                    predefinedTextAttributesKeys
                } else {
                    null
                }
                is NavigationItem -> {
                    psiElement.presentation?.locationString?.let { locationString ->
                        if (PREDEFINED_LOCATION_STRING_SET.any { locationString.endsWith(it) }) {
                            predefinedTextAttributesKeys
                        } else {
                            null
                        }
                    }
                }
                else -> null
            }
                ?: standardTextAttributeKeys

        private fun sameFile(referrer: PsiElement, resolved: PsiElement): Boolean =
            referrer.containingFile.virtualFile == resolved.containingFile.virtualFile
    }
}
