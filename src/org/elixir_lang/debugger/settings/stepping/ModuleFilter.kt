package org.elixir_lang.debugger.settings.stepping

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.util.*

@Tag("module-filter")
class ModuleFilter(enabled: Boolean = true, pattern: String = "") {
    @Attribute("enabled")
    @Suppress("CanBePrimaryConstructorProperty")
    var enabled: Boolean = enabled

    @Attribute("pattern")
    @Suppress("CanBePrimaryConstructorProperty")
    var pattern: String = pattern

    override fun equals(other: Any?) =
            other is ModuleFilter && enabled == other.enabled && pattern == other.pattern

    override fun hashCode() = Objects.hashCode(arrayOf(enabled, pattern))
}
