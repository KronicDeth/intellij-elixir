package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation

interface Variable : Named {
    override val name: String

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewLongNameLocation.INSTANCE, UsageViewShortNameLocation.INSTANCE -> name
            UsageViewTypeLocation.INSTANCE -> "variable"
            else -> null
        }
}
