package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Timed
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.QuotableKeywordPair
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.structure_view.element.modular.Modular

/**
 * Much like a [CallDefinition], but
 *
 * 1) A call reference exists as an element in the PSI.
 * 2) The arity or name may not be fully resolved due to syntactically valid, but semantically invalid code.
 */
class CallReference(
    private val modular: Modular,
    quotableKeywordPair: QuotableKeywordPair,
    override val time: Time,
    private val overridable: Boolean,
    private val nameArityInterval: org.elixir_lang.NameArityInterval
) : Element<QuotableKeywordPair?>(quotableKeywordPair), Timed, Visible {
    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        val itemPresentation = modular.presentation
        var location: String? = null
        if (itemPresentation is Parent) {
            val parentPresentation = itemPresentation as Parent
            location = parentPresentation.locatedPresentableText
        }

        // pseudo-named-arguments
        val callback = false
        return NameArityInterval(
            location,
            callback,
            time,
            visibility(),
            overridable,
            false,
            nameArityInterval
        )
    }

    /**
     * Returns the list of children of the tree element.
     *
     * @return an empty list of children.
     */
    override fun getChildren(): Array<TreeElement> = emptyArray()

    /**
     * When the defined call is usable
     *
     * @return [Time.COMPILE] for compile time (`defmacro`, `defmacrop`);
     * [Time.RUN] for run time `def`, `defp`)
     */
    fun time(): Time = time

    /**
     * The visibility of the element.
     *
     * @return [Visibility.PUBLIC].
     */
    override fun visibility(): Visibility? = Visibility.PUBLIC
}
