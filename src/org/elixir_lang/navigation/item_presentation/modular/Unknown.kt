package org.elixir_lang.navigation.item_presentation.modular

import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.Call
import javax.swing.Icon

class Unknown(private val location: String?, private val call: Call) : ItemPresentation, Parent {
    override fun getIcon(unused: Boolean): Icon = AllIcons.RunConfigurations.TestUnknown
    override fun getLocatedPresentableText(): String = locationString?.let { "$it $presentableText" } ?: presentableText
    override fun getLocationString(): String? = location
    override fun getPresentableText(): String =
        call.primaryArguments()?.firstOrNull()?.text ?: call.functionName() ?: "?"
}
