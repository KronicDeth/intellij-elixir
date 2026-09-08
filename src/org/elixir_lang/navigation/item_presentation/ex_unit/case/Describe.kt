package org.elixir_lang.navigation.item_presentation.ex_unit.case

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Icons.DESCRIBE
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpressions
import javax.swing.Icon

/**
 * [Parent] as well as [ItemPresentation]: the element classes nested inside a `describe` cast their
 * parent's presentation to [Parent] unguarded, so one that is not throws while the tree renders.
 */
class Describe(private val location: String?, val call: Call): ItemPresentation, Parent {
    override fun getPresentableText(): String =
            "describe ${call.finalArguments()?.stripAccessExpressions()?.firstOrNull()?.text}"

    override fun getLocationString(): String? = location

    override fun getLocatedPresentableText(): String =
            locationString?.let { location -> "$location $presentableText" } ?: presentableText

    override fun getIcon(unused: Boolean): Icon = DESCRIBE
}
