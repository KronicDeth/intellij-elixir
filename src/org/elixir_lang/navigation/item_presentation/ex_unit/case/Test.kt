package org.elixir_lang.navigation.item_presentation.ex_unit.case

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Icons.TEST
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpressions
import javax.swing.Icon

/**
 * [Parent] as well as [ItemPresentation]: the element classes nested inside a `test` cast their
 * parent's presentation to [Parent] unguarded, so one that is not throws while the tree renders.
 */
class Test(private val location: String?, val call: Call): ItemPresentation, Parent {
    // `test` is arity 1..3, so a message-less one has no final arguments to interpolate.
    override fun getPresentableText(): String =
            call.finalArguments()?.stripAccessExpressions()?.firstOrNull()?.text
                    ?.let { message -> "test $message" }
                    ?: "test"

    override fun getLocationString(): String? = location

    override fun getLocatedPresentableText(): String =
            locationString?.let { location -> "$location $presentableText" } ?: presentableText

    override fun getIcon(unused: Boolean): Icon = TEST
}
