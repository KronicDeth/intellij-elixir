package org.elixir_lang.debugger.settings.stepping

import com.intellij.util.attribute
import org.jdom.Element
import java.lang.String.valueOf

data class ModuleFilter(val enabled: Boolean = true, val pattern: String = "") {
    fun toElement() =
        Element("module-filter").attribute("enabled", valueOf(enabled)).attribute("pattern", pattern)

    companion object {
        fun fromElement(element: Element) =
                ModuleFilter(
                        element.getAttribute("enabled")!!.booleanValue,
                        element.getAttribute("pattern")!!.value
                )
    }
}
