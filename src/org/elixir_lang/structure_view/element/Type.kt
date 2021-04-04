package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import com.intellij.psi.PsiElement
import org.elixir_lang.Visibility
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.modular.Modular

class Type(private val modular: Modular,
        moduleAttributeDefinition: AtUnqualifiedNoParenthesesCall<*>,
        private val opaque: Boolean,
        private val visibility: Visibility) : Element<AtUnqualifiedNoParenthesesCall<*>?>(moduleAttributeDefinition), Visible {

    /**
     * No children.
     *
     * @return empty array.
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val parentPresentation = modular.presentation as Parent
        val location = parentPresentation.locatedPresentableText

        return org.elixir_lang.navigation.item_presentation.Type(
                location,
                type(navigationItem!!),
                opaque,
                visibility
        )
    }

    /**
     * The visibility of the element.
     *
     * @return [Visibility.PUBLIC] for `@type` and `@opaque`; [Visibility.PRIVATE] for
     * `@typep`
     */
    override fun visibility(): Visibility? = visibility

    companion object {
        fun elementDescription(call: Call?, location: ElementDescriptionLocation): String? =
                if (location === UsageViewTypeLocation.INSTANCE) {
                    "type"
                } else {
                    null
                }

        fun fromCall(modular: Modular, call: Call): Type =
                fromAtUnqualifiedNoParenthesesCall(modular, call as AtUnqualifiedNoParenthesesCall<*>)

        fun fromAtUnqualifiedNoParenthesesCall(
                modular: Modular,
                moduleAttributeDefinition: AtUnqualifiedNoParenthesesCall<*>): Type {
            val moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(moduleAttributeDefinition)
            val opaque = isOpaque(moduleAttributeName)
            val visibility = visibility(moduleAttributeName)
            return Type(modular, moduleAttributeDefinition, opaque, visibility)
        }

        @JvmStatic
        fun `is`(call: Call): Boolean = if (call is AtUnqualifiedNoParenthesesCall<*>) {
            val moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(call)
            moduleAttributeName == "@opaque" || moduleAttributeName == "@type" || moduleAttributeName == "@typep"
        } else {
            false
        }

        private fun isOpaque(moduleAttributeName: String): Boolean = moduleAttributeName == "@opaque"

        fun nameIdentifier(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): PsiElement? =
                specification(atUnqualifiedNoParenthesesCall)?.let { specificationType(it) }?.let { typeNameIdentifier(it) }

        fun nameIdentifier(call: Call): PsiElement? =
                (call as? AtUnqualifiedNoParenthesesCall<*>)?.let { nameIdentifier(it) }

        fun specification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Call? =
                (atUnqualifiedNoParenthesesCall.noParenthesesOneArgument.arguments().singleOrNull() as? Call)

        private fun specificationType(specification: Call): Call? =
                when (specification) {
                    is org.elixir_lang.psi.operation.Type -> CallDefinitionSpecification.type(specification as org.elixir_lang.psi.operation.Type)
                    is ElixirMatchedWhenOperation -> CallDefinitionSpecification.type(specification)
                    else -> null
                }

        fun type(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Call? =
            atUnqualifiedNoParenthesesCall.noParenthesesOneArgument.arguments().singleOrNull()?.let { it as? Call }

        private fun typeNameIdentifier(type: Call): PsiElement? = type.functionNameElement()

        fun visibility(moduleAttributeName: String): Visibility =
            when (moduleAttributeName) {
                "@opaque", "@type" -> Visibility.PUBLIC
                "@typep" -> Visibility.PRIVATE
                else -> TODO()
            }
    }
}
