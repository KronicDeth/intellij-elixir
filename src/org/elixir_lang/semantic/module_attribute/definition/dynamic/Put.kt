package org.elixir_lang.semantic.module_attribute.definition.dynamic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.module_attribute.definition.Dynamic

class Put(call: Call) : Dynamic(call) {
    override fun elementDescription(location: ElementDescriptionLocation): String? = when (location) {
        UsageViewTypeLocation.INSTANCE -> "register_attribute"
        else -> null
    }
}
