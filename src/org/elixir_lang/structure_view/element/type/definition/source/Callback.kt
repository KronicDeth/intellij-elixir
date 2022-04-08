package org.elixir_lang.structure_view.element.type.definition.source

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.type.definition.source.Callback
import org.elixir_lang.structure_view.element.Element
import org.elixir_lang.structure_view.element.modular.Modular

class Callback(private val modular: Modular, val semantic: Callback) :
    Element<AtUnqualifiedNoParenthesesCall<*>>(semantic.atUnqualifiedNoParenthesesCall) {
    /**
     * A callback's [.getPresentation] is a [NameArityInterval] like a [CallDefinition], so like a call
     * definition, it's children are the specifications and clauses, since the callback has no clauses, the only child
     * is the specification.
     *
     * @return the list of children.
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val location = (modular.presentation as Parent).locatedPresentableText
        val semanticNameArity = semantic.nameArity
        val name = semanticNameArity.name
        val arity = semanticNameArity.arity

        return NameArityInterval(
            location,
            true,
            semantic.time,
            Visibility.PUBLIC,
            false,
            false,
            semantic.nameArity.toNameArityInterval()
        )
    }
}
