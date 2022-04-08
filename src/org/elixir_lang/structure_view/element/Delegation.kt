package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import org.elixir_lang.navigation.item_presentation.Delegation
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.call.definition.delegation.Head
import org.elixir_lang.structure_view.element.modular.Modular

class Delegation(private val modular: Modular, semantic: org.elixir_lang.semantic.call.definition.Delegation) :
    Element<Call>(semantic.call) {
    constructor(semantic: org.elixir_lang.semantic.call.definition.Delegation) :
            this(semantic.enclosingModular.structureViewTreeElement, semantic)

    private val childList: MutableList<TreeElement> = ArrayList()

    /**
     * If `true`, when delegated, the first argument passed to the delegated function will be relocated to the end
     * of the arguments when dispatched to the target.
     *
     * @return defaults to `false` and when keyword argument is not parsable as boolean.
     */
    fun appendFirst(): Boolean =
        navigationItem!!.keywordArgument("append_first")?.let { keywordValue ->
            keywordValue.text == "true"
        } ?: false

    /**
     * The value of the `:as` keyword argument
     *
     * @return text of the `:as` keyword value
     */
    fun `as`(): String? = keywordArgumentText("as")

    fun callDefinitionHeadCallList(): List<Call> = callDefinitionHeadCallList(navigationItem!!)

    fun definition(callDefinition: Definition) = childList.add(callDefinition)

    /**
     * The calls defined by this delegation
     *
     * @return the list of [Definition] elements;
     */
    override fun getChildren(): Array<TreeElement> = childList.toTypedArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val parent = modular.presentation as Parent
        val location = parent.locatedPresentableText

        return Delegation(
            location,
            to(),
            `as`(),
            appendFirst()
        )
    }

    /**
     * The value of the `:to` keyword argument
     *
     * @return text of the `:to` keyword value
     */
    fun to(): String? = keywordArgumentText("to")

    /*
     * Private Instance Methods
     */
    /**
     * The text of the `keywordValueText` keyword argument.
     *
     * @param keywordValueText the text of the keyword value
     * @return the `PsiElement.getText()` of the keyword value if the keyword argument exists of the
     * [Element.navigationItem]; `null` if the keyword argument does not exist.
     */
    private fun keywordArgumentText(keywordValueText: String): String? =
        navigationItem!!.keywordArgument(keywordValueText)?.text

    companion object {
        @JvmStatic
        fun callDefinitionHeadCallList(defdelegateCall: Call): List<Call> {
            var callDefinitionHeadCallList: List<Call>? = null
            val finalArguments = defdelegateCall.finalArguments()!!
            val firstFinalArgument = finalArguments[0]
            if (firstFinalArgument is ElixirAccessExpression) {
                val accessExpressionChild = firstFinalArgument.stripAccessExpression()

                callDefinitionHeadCallList = if (accessExpressionChild is ElixirList) {
                    accessExpressionChild.children.filterIsInstance<Call>().filter { Head.`is`(it) }
                } else {
                    null
                }
            } else if (firstFinalArgument is Call) {
                callDefinitionHeadCallList = filterCallDefinitionHeadCallList(firstFinalArgument)
            }
            if (callDefinitionHeadCallList == null) {
                callDefinitionHeadCallList = emptyList()
            }
            return callDefinitionHeadCallList
        }

        fun filterCallDefinitionHeadCallList(vararg calls: Call): List<Call> =
            calls.filter { Head.`is`(it) }

        fun nameIdentifier(call: Call): PsiElement? =
            call.finalArguments()?.get(0)?.let { it as? Call }?.functionNameElement()
    }
}
