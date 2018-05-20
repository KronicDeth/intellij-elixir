package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.Visibility
import org.elixir_lang.navigation.item_presentation.NameArity
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.structure_view.element.modular.Modular
import org.jetbrains.annotations.Contract

class Callback(private val modular: Modular, navigationItem: Call) :
        Element<AtUnqualifiedNoParenthesesCall<*>>(navigationItem as AtUnqualifiedNoParenthesesCall<*>), Timed {
    /**
     * A callback's [.getPresentation] is a [NameArity] like a [CallDefinition], so like a call
     * definition, it's children are the specifications and clauses, since the callback has no clauses, the only child
     * is the specification.
     *
     * @return the list of children.
     */
    override fun getChildren(): Array<TreeElement> =
            arrayOf(CallDefinitionSpecification(
                    modular,
                    navigationItem,
                    true,
                    time()
            ))

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val location = (modular.presentation as Parent).locatedPresentableText

        var name = "?"
        var arity = -1

        headCall(navigationItem)?.let { headCall ->
            CallDefinitionHead.nameArityRange(headCall)?.let { nameArityRange ->
                name = nameArityRange.name
                arity = nameArityRange.arityRange.singleOrNull() ?: arity
            }
        }

        return NameArity(
                location,
                true,
                time(),
                Visibility.PUBLIC,
                false,
                false,
                name,
                arity
        )
    }

    /**
     * When the defined call is usable
     *
     * @return [Time.COMPILE] for compile time (`defmacro`, `defmacrop`);
     * [Time.RUN] for run time `def`, `defp`)
     */
    override fun time(): Timed.Time =
            ElixirPsiImplUtil.moduleAttributeName(navigationItem).let { moduleAttributeName ->
                when (moduleAttributeName) {
                    "@callback" -> Timed.Time.RUN
                    "@macrocallback" -> Timed.Time.COMPILE
                    else -> TODO("Unknown callback $moduleAttributeName")
                }
            }

    companion object {
        @JvmStatic
        fun elementDescription(
                @Suppress("UNUSED_PARAMETER") call: Call,
                location: ElementDescriptionLocation
        ): String? =
            if (location === UsageViewTypeLocation.INSTANCE) {
                "callback"
            } else {
                null
            }

        @Contract(pure = true)
        fun headCall(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Call? =
                atUnqualifiedNoParenthesesCall
                        .noParenthesesOneArgument
                        .children
                        .singleOrNull()
                        ?.let { specificationHeadCall(it) }

        @Contract(pure = true)
        fun `is`(call: Call): Boolean =
                (call as? AtUnqualifiedNoParenthesesCall<*>)?.let {
                    val moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(it)

                    moduleAttributeName == "@callback" || moduleAttributeName == "@macrocallback"
                } ?:
                false

        @Contract(pure = true)
        fun nameIdentifier(call: Call): PsiElement? =
                (call as? AtUnqualifiedNoParenthesesCall<*>)?.let { nameIdentifier(it) }

        @Contract(pure = true)
        fun nameIdentifier(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): PsiElement? =
            headCall(atUnqualifiedNoParenthesesCall)?.let { headCall ->
                CallDefinitionHead.nameIdentifier(headCall)
            }

        private fun parameterizedTypeHeadCall(whenOperation: ElixirMatchedWhenOperation): Call? =
            (whenOperation.leftOperand() as? Type)?.let { type ->
                typeHeadCall(type)
            }

        private fun specificationHeadCall(specification: PsiElement): Call? =
                when (specification) {
                    is Type -> typeHeadCall(specification)
                    is ElixirMatchedWhenOperation -> parameterizedTypeHeadCall(specification)
                    else -> null
                }

        private fun typeHeadCall(typeOperation: Type): Call? = typeOperation.leftOperand() as? Call
    }
}
