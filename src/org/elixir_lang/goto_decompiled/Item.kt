package org.elixir_lang.goto_decompiled

import com.intellij.navigation.GotoRelatedItem
import org.elixir_lang.psi.call.Call
import javax.swing.Icon

class Item(definer: Call) : GotoRelatedItem(definer, "Decompiled BEAM") {
    override fun getCustomContainerName(): String? = definerPresentation?.locationString
    override fun getCustomIcon(): Icon? = definerPresentation?.getIcon(true)

    private val definerPresentation by lazy { definer.presentation }
}
