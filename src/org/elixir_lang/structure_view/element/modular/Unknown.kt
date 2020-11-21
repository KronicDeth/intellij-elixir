package org.elixir_lang.structure_view.element.modular

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.modular.Unknown
import org.elixir_lang.psi.call.Call

class Unknown : Module {
    constructor(call: Call) : super(call)

    /**
     * @param parent the parent [Module] or [org.elixir_lang.structure_view.element.Quote] that scopes
     * `call`.
     * @param call   the `<module>.def<suffix>/2` call nested in `parent`.
     */
    constructor(parent: Modular?, call: Call) : super(parent, call)

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation = Unknown(location(), navigationItem)

    companion object {
        @JvmStatic
        fun `is`(call: Call): Boolean = call.hasDoBlockOrKeyword()
    }
}
