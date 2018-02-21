package org.elixir_lang.debugger

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SimpleConfigurable
import com.intellij.openapi.util.Getter
import com.intellij.xdebugger.settings.DebuggerSettingsCategory
import com.intellij.xdebugger.settings.XDebuggerSettings
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.debugger.settings.stepping.UI
import org.jdom.Element

class Settings(moduleFilters: List<ModuleFilter> = listOf()):
        XDebuggerSettings<Element>("elixir"), Getter<Settings> {
    val moduleFilters: MutableList<ModuleFilter> = moduleFilters.toMutableList()

    override fun createConfigurables(category: DebuggerSettingsCategory): Collection<Configurable> =
        when (category) {
            DebuggerSettingsCategory.STEPPING ->
                    listOf(
                            SimpleConfigurable.create(
                                    "elixir.debug.stepping.configurable",
                                    "Elixir",
                                    UI::class.java,
                                    this
                            )
                    )
            else ->
                emptyList()
        }

    override fun equals(other: Any?) =
            other is Settings && other.moduleFilters.let { otherModuleFilters ->
              moduleFilters.size == otherModuleFilters.size &&
                      moduleFilters
                              .zip(otherModuleFilters)
                              .all { (moduleFilter, otherModuleFilter) ->
                                  moduleFilter == otherModuleFilter
                              }
            }

    override fun get(): Settings = this
    override fun getState(): Element = addModuleFilters(Element("module-filters"))
    override fun hashCode(): Int = moduleFilters.hashCode()

    override fun loadState(element: Element) {
        loadModuleFilters(element)
    }

    // Private Functions

    private fun addModuleFilters(parentNode: Element): Element {
        moduleFilters.forEach { moduleFilter ->
            parentNode.addContent(moduleFilter.toElement())
        }

        return parentNode
    }

    private fun loadModuleFilters(parentNode: Element) {
        moduleFilters.clear()

        moduleFilters.addAll(
                parentNode.children.map { element ->
                    ModuleFilter.fromElement(element)
                }
        )
    }
}
