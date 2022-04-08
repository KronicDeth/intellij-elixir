package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.IMPORT
import org.elixir_lang.psi.call.name.Module.KERNEL

/**
 * An `import` call
 */
object Import {
    fun elementDescription(call: Call, location: ElementDescriptionLocation): String? =
            when {
                location === UsageViewTypeLocation.INSTANCE -> "import"
                location === UsageViewNodeTextLocation.INSTANCE -> call.text
                else -> null
            }
}
