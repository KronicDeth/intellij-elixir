package org.elixir_lang.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.apache.commons.lang.NotImplementedException
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElementFactory
import org.elixir_lang.psi.ElixirAtIdentifier
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ancestorSequence
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.reference.resolver.ModuleAttribute
import org.elixir_lang.structure_view.element.modular.Implementation

class ModuleAttribute(psiElement: PsiElement) : PsiPolyVariantReferenceBase<PsiElement>(psiElement) {
    /**
     * Returns the array of String, [PsiElement] and/or [LookupElement]
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    override fun getVariants(): Array<Any> = getVariantsUpFromElement(myElement).toTypedArray()

    override fun handleElementRename(newModuleAttributeName: String): PsiElement =
            when (myElement) {
                is AtNonNumericOperation -> {
                    val moduleAttributeUsage = ElementFactory.createModuleAttributeUsage(
                            myElement.getProject(),
                            newModuleAttributeName
                    )
                    myElement.replace(moduleAttributeUsage)
                }
                is ElixirAtIdentifier -> {
                    // do nothing; handled by setName on ElixirAtUnqualifiedNoParenthesesCall
                    myElement
                }
                else -> throw NotImplementedException(
                        "Renaming module attribute reference on " + myElement.javaClass.canonicalName +
                                " PsiElements is not implemented yet.  Please open an issue " +
                                "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name and the " +
                                "sample text:\n" + myElement.text)
            }

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     * being resolved is considered incomplete, and the method may return additional
     * invalid results.
     * @return the array of results for resolving the reference.
     */
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
            ResolveCache
                    .getInstance(this.myElement.project)
                    .resolveWithCaching(
                            this,
                            ModuleAttribute,
                            false,
                            incompleteCode
                    )

    override fun calculateDefaultRangeInElement(): TextRange {
        val elementTextRange = element.textRange
        val startOffset = elementTextRange.startOffset

        return when (myElement) {
            is AtUnqualifiedNoParenthesesCall<*> -> {
                val atIdentifierTextRange = myElement.atIdentifier.textRange

                TextRange.create(
                        atIdentifierTextRange.startOffset - startOffset,
                        atIdentifierTextRange.endOffset - startOffset
                )
            }
            else -> TextRange.create(
                    startOffset - startOffset,
                    elementTextRange.endOffset - startOffset
            )
        }
    }

    private fun getVariantsSibling(lastSibling: PsiElement): List<LookupElement> =
            lastSibling
                    .prevSiblingSequence()
                    .mapNotNull {
                        when (it) {
                            is AtUnqualifiedNoParenthesesCall<*> -> {
                                LookupElementBuilder.createWithSmartPointer(
                                        ElixirPsiImplUtil.moduleAttributeName(it),
                                        it
                                )
                            }
                            is Call -> {
                                if (Implementation.`is`(it)) {
                                    val element = Implementation.protocolNameElement(it) ?: it

                                    LookupElementBuilder.createWithSmartPointer(
                                            "@protocol",
                                            element
                                    )
                                } else {
                                    null
                                }
                            }
                            else -> null
                        }
                    }
                    .toList()

    private fun getVariantsUpFromElement(element: PsiElement): List<LookupElement> =
            element
                    .ancestorSequence()
                    .flatMap { getVariantsSibling(it).asSequence() }
                    .toList()

    companion object {
        private const val BEHAVIOUR_NAME = "behaviour"
        private val CALLBACK_NAME_SET = setOf("callback", "macrocallback")
        private val DOCUMENTATION_NAME_SET = setOf("doc", "moduledoc", "typedoc")
        private const val SPECIFICATION_NAME = "spec"
        private val TYPE_NAME_SET = setOf("opaque", "type", "typep")
        private val NON_REFERENCING_NAME_SET =
                mutableSetOf<String>().apply {
                    add(BEHAVIOUR_NAME)
                    addAll(CALLBACK_NAME_SET)
                    addAll(DOCUMENTATION_NAME_SET)
                    add(SPECIFICATION_NAME)
                    addAll(TYPE_NAME_SET)
                }.toSet()

        fun isTypeSpecName(name: String): Boolean =
                isCallbackName(name) || isSpecificationName(name) ||  isTypeName(name)

        /**
         * Whether the module attribute is used to declare function or macro callbacks for behaviours
         *
         * @return `true` if `"@callback"` or `"@macrocallback"`; otherwise, `false`.
         */
        fun isCallbackName(name: String): Boolean = CALLBACK_NAME_SET.contains(name)

        /**
         * Whether the module attribute is used to declare function, module, or type documentation
         *
         * @return `true` if `"doc"`, `"moduledoc"`, or `"typedoc"`; otherwise, `false`.
         */
        fun isDocumentationName(name: String): Boolean = DOCUMENTATION_NAME_SET.contains(name)

        /**
         * All the predefined module attributes that aren't used to define constants, but for defining behaviors, callback,
         * documents, or types.
         *
         * @param moduleAttribute the module attribute
         * @return `true` if the module attribute should have a `null` reference because it is used for
         * library control instead of constant definition; otherwise, `false`.
         */
        fun isNonReferencing(moduleAttribute: AtNonNumericOperation): Boolean =
                moduleAttribute.text.substring(1).let { name -> isNonReferencingName(name) }

        /**
         * All the predefined module attributes that aren't used to define constants, but for defining behaviors, callback,
         * documents, or types.
         *
         * @param moduleAttribute the module attribute
         * @return `true` if the module attribute should have a `null` reference because it is used for
         * library control instead of constant definition; otherwise, `false`.
         */
        fun isNonReferencing(moduleAttribute: ElixirAtIdentifier): Boolean {
            val text = moduleAttribute.text
            val name = text.substring(1)

            return isNonReferencingName(name)
        }

        /**
         * Whether the module attribute is used to declare the specification for a function or macro.
         *
         * @return `true` if `"spec"`; otherwise, `false`
         */
        fun isSpecificationName(name: String): Boolean = SPECIFICATION_NAME == name

        /**
         * Whether the module attribute is used to declare opaque, transparent, or private types.
         *
         * @return `true` if `"opaque"`, `"type"`, or `"typep"; otherwise, { false}.`
         */
        fun isTypeName(name: String): Boolean = TYPE_NAME_SET.contains(name)

        private fun isNonReferencingName(name: String): Boolean = NON_REFERENCING_NAME_SET.contains(name)
    }
}
