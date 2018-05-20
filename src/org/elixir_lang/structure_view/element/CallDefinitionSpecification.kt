package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.structure_view.element.modular.Modular
import org.jetbrains.annotations.Contract

/**
 * A call definition @spec
 */
class CallDefinitionSpecification(
        private val modular: Modular,
        moduleAttributeDefinition: AtUnqualifiedNoParenthesesCall<*>,
        private val callback: Boolean,
        private val time: Timed.Time
) : Element<AtUnqualifiedNoParenthesesCall<*>>(moduleAttributeDefinition) {
    /**
     * No children.
     *
     * @return empty array
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val location = (modular.presentation as Parent).locatedPresentableText

        return org.elixir_lang.navigation.item_presentation.CallDefinitionSpecification(
                location,
                specification(navigationItem),
                callback,
                time
        )
    }

    companion object {
        fun elementDescription(
                @Suppress("UNUSED_PARAMETER") call: Call,
                location: ElementDescriptionLocation
        ): String? =
            if (location === UsageViewTypeLocation.INSTANCE) {
                "specification"
            } else {
                null
            }

        @Contract(pure = true)
        fun `is`(call: Call): Boolean =
                (call as? AtUnqualifiedNoParenthesesCall<*>)?.let {
                    ElixirPsiImplUtil.moduleAttributeName(it) == "@spec"
                } ?:
                false

        @JvmStatic
        fun moduleAttributeNameArity(
                atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>
        ): org.elixir_lang.NameArity? =
                specification(atUnqualifiedNoParenthesesCall)?.let { specificationType(it) }?.let { typeNameArity(it) }

        @JvmStatic
        fun moduleAttributeNameArity(call: Call): org.elixir_lang.NameArity? =
            (call as? AtUnqualifiedNoParenthesesCall<*>)?.let { moduleAttributeNameArity(it) }

        fun nameIdentifier(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): PsiElement? =
            specification(atUnqualifiedNoParenthesesCall)?.let { specificationType(it) }?.let { typeNameIdentifier(it) }

        fun nameIdentifier(call: Call): PsiElement? =
                (call as? AtUnqualifiedNoParenthesesCall<*>)?.let { nameIdentifier(it) }

        fun specification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Call? =
            (atUnqualifiedNoParenthesesCall.noParenthesesOneArgument.arguments().singleOrNull() as? Call)

        fun specificationType(specification: Call): Call? =
                when (specification) {
                    is Type -> type(specification as Type)
                    is ElixirMatchedWhenOperation -> type(specification)
                    else -> null
                }

        fun type(typeOperation: Type): Call? = (typeOperation.leftOperand() as? Call)

        fun type(matchedWhenOperation: ElixirMatchedWhenOperation): Call? =
                (matchedWhenOperation.leftOperand() as? Type)?.let { type(it) }

        @JvmStatic
        fun typeNameArity(type: Call): org.elixir_lang.NameArity {
            val name = type.functionName()!!
            val arity = type.resolvedFinalArity()

            return org.elixir_lang.NameArity(name, arity)
        }

        private fun typeNameIdentifier(type: Call): PsiElement? = type.functionNameElement()
    }
}
