package org.elixir_lang.navigation.item_presentation.ex_unit.case

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Icons.DESCRIBE
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpressions
import javax.swing.Icon

class Describe(val call: Call): ItemPresentation {
    override fun getPresentableText(): String =
            "describe ${call.finalArguments()?.stripAccessExpressions()?.firstOrNull()?.text}"
    override fun getIcon(unused: Boolean): Icon = DESCRIBE
}
