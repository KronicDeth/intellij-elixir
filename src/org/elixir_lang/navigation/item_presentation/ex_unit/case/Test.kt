package org.elixir_lang.navigation.item_presentation.ex_unit.case

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Icons.TEST
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpressions
import javax.swing.Icon

class Test(val call: Call): ItemPresentation {
    override fun getPresentableText(): String =
            "test ${call.finalArguments()?.stripAccessExpressions()?.firstOrNull()?.text}"
    override fun getIcon(unused: Boolean): Icon = TEST
}
