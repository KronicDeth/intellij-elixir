package org.elixir_lang.structure_view.element.modular

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.navigation.item_presentation.Implementation
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Implementation.forNameCollection
import org.elixir_lang.psi.Implementation.protocolName
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModular

class Implementation : Module {
    /**
     * The name of the [.navigationItem].
     *
     * @return the [NamedElement.getName] if [.navigationItem] is a [NamedElement]; otherwise,
     * `null`.
     */
    override fun getName(): String? = if (forNameOverride != null) {
        val protocolName = protocolName(navigationItem)
        if (protocolName != null) {
            "$protocolName.$forNameOverride"
        } else {
            null
        }
    } else if (navigationItem is NamedElement) {
        val namedElement = navigationItem as NamedElement
        namedElement.name
    } else {
        null
    }

    private val forNameOverride: String?

    constructor(call: Call) : this(null, call)
    constructor(parent: Modular?, call: Call) : super(parent, call) {
        forNameOverride = null
    }

    /**
     * Implementation that presents as having a single `forName` when the `defimpl` has a list as the
     * keyword argument of `for:`.
     *
     * @param parent enclosing modular
     * @param call the `defimpl` call
     * @param forNameOverride The forName to use when the `call` has a list for its `for:` value.  Needed so
     * that rendered named in the presentation uses `forName` for
     * [org.elixir_lang.navigation.GotoSymbolContributor]'s lookup menu
     */
    constructor(parent: Modular?, call: Call, forNameOverride: String) : super(parent, call) {
        this.forNameOverride = forNameOverride
    }

    /**
     * The name of the module the protocol is for as derived from the PSI tree
     *
     * @return the [.parent] fully-qualified name if no `:for` keyword argument is given; otherwise, the
     * `:for` keyword argument.
     */
    private fun derivedForName(): String {
        val finalArguments = navigationItem.finalArguments()!!
        return if (finalArguments.size > 1) {
            val finalArgument = finalArguments.last()
            if (finalArgument is QuotableKeywordList) {
                val keywordValue: PsiElement? = finalArgument.keywordValue(Function.FOR)
                if (keywordValue != null) {
                    keywordValue.text
                } else {
                    "?"
                }
            } else {
                "?"
            }
        } else if (parent != null) {
            val parentPresentation = parent.presentation as Parent
            parentPresentation.locatedPresentableText
        } else {
            "?"
        }
    }

    /**
     * The name of the module the protocol is for.
     *
     * @return the [.forNameOverride]; the [.parent] fully-qualified name if no `:for` keyword argument is
     * given; otherwise, the `:for` keyword argument.
     */
    private fun forName(): String = forNameOverride ?: derivedForName()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation = Implementation(
            protocolName(),
            forName()
    )

    /**
     * Unlike [.protocolName], will return "?" when the protocol name can't be derived from the call.
     */
    fun protocolName(): String = protocolName(navigationItem) ?: "?"
}
