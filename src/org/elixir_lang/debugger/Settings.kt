package org.elixir_lang.debugger

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SimpleConfigurable
import com.intellij.openapi.util.Getter
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.xdebugger.settings.DebuggerSettingsCategory
import com.intellij.xdebugger.settings.XDebuggerSettings
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.debugger.settings.stepping.UI

class Settings(moduleFilters: List<ModuleFilter> = defaultModuleFilters()):
        XDebuggerSettings<Settings>("elixir"), Getter<Settings> {
    @Tag("module-filters")
    // `var` only for `XmlSerializerUtil.copyBean(state, this)`
    var moduleFilters: MutableList<ModuleFilter> = moduleFilters.toMutableList()

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

    fun enabledModuleFilterPatternList()
            = moduleFilters.filter(ModuleFilter::enabled).map(ModuleFilter::pattern)

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
    override fun getState()= this
    override fun hashCode(): Int = moduleFilters.hashCode()

    override fun loadState(state: Settings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    // Private Functions

    companion object {
        fun defaultModuleFilters() = listOf(
                ModuleFilter(pattern = "Access"),
                ModuleFilter(pattern = "Agent"),
                ModuleFilter(pattern = "Agent.*"),
                ModuleFilter(pattern = "Application"),
                ModuleFilter(pattern = "Atom"),
                ModuleFilter(pattern = "Base"),
                // bcrypt_elixir
                ModuleFilter(pattern = "Bcrypt.Base"),
                /* Benchee is for benchmarking, you probably don't want to benchmark while debugging.  Developers of
                   Benchee itself, can either turn off this filter OR placing breakpoints in a specific module will
                   still allow it to be interpreted */
                ModuleFilter(pattern = "Benchee"),
                ModuleFilter(pattern = "Benchee.*"),
                ModuleFilter(pattern = "Behaviour"),
                ModuleFilter(pattern = "Bitwise"),
                ModuleFilter(pattern = "Calendar"),
                ModuleFilter(pattern = "Calendar.*"),
                ModuleFilter(pattern = "Code"),
                ModuleFilter(pattern = "Code.*"),
                ModuleFilter(pattern = "Collectable"),
                ModuleFilter(pattern = "Collectable.*"),
                ModuleFilter(pattern = "Connection"),
                // credo
                ModuleFilter(pattern = "Credo"),
                ModuleFilter(pattern = "Credo.*"),
                ModuleFilter(pattern = "DBConnection"),
                ModuleFilter(pattern = "DBConnection.*"),
                ModuleFilter(pattern = "Date"),
                ModuleFilter(pattern = "Date.*"),
                ModuleFilter(pattern = "DateTime"),
                ModuleFilter(pattern = "Decimal"),
                ModuleFilter(pattern = "Dict"),
                ModuleFilter(pattern = "DynamicSupervisor"),
                // earmark
                ModuleFilter(pattern = "Earmark"),
                ModuleFilter(pattern = "Earmark.*"),
                ModuleFilter(pattern = "Ecto"),
                ModuleFilter(pattern = "Ecto.*"),
                ModuleFilter(pattern = "Enum"),
                ModuleFilter(pattern = "Enumerable"),
                ModuleFilter(pattern = "Enumerable.*"),
                // ex_doc
                ModuleFilter(pattern = "ExDoc"),
                ModuleFilter(pattern = "ExDoc.*"),
                ModuleFilter(pattern = "ExUnit"),
                ModuleFilter(pattern = "ExUnit.*"),
                ModuleFilter(pattern = "File"),
                ModuleFilter(pattern = "File.Stat"),
                ModuleFilter(pattern = "File.Stream"),
                ModuleFilter(pattern = "FileSystem"),
                ModuleFilter(pattern = "FileSystem.*"),
                ModuleFilter(pattern = "Float"),
                ModuleFilter(pattern = "GenEvent"),
                ModuleFilter(pattern = "GenEvent.Stream"),
                ModuleFilter(pattern = "GenServer"),
                ModuleFilter(pattern = "Gettext"),
                ModuleFilter(pattern = "Gettext.*"),
                ModuleFilter(pattern = "HashDict"),
                ModuleFilter(pattern = "HashSet"),
                ModuleFilter(pattern = "IEx"),
                ModuleFilter(pattern = "IEx.*"),
                ModuleFilter(pattern = "IO"),
                ModuleFilter(pattern = "IO.*"),
                // inch_ex
                ModuleFilter(pattern = "InchEx"),
                ModuleFilter(pattern = "InchEx.*"),
                ModuleFilter(pattern = "Inspect"),
                ModuleFilter(pattern = "Inspect.*"),
                ModuleFilter(pattern = "Kernel"),
                ModuleFilter(pattern = "Kernel.*"),
                ModuleFilter(pattern = "Keyword"),
                ModuleFilter(pattern = "List"),
                ModuleFilter(pattern = "List.Chars.*"),
                ModuleFilter(pattern = "Logger"),
                ModuleFilter(pattern = "Logger.*"),
                ModuleFilter(pattern = "Macro"),
                ModuleFilter(pattern = "Macro.*"),
                ModuleFilter(pattern = "Mix"),
                ModuleFilter(pattern = "Mix.*"),
                ModuleFilter(pattern = "OptionParser"),
                ModuleFilter(pattern = "Path"),
                ModuleFilter(pattern = "Path.*"),
                ModuleFilter(pattern = "Phoenix"),
                ModuleFilter(pattern = "Phoenix.*"),
                ModuleFilter(pattern = "Plug"),
                ModuleFilter(pattern = "Plug.*"),
                ModuleFilter(pattern = "Poison"),
                ModuleFilter(pattern = "Poison.*"),
                ModuleFilter(pattern = "Port"),
                ModuleFilter(pattern = "Postgrex"),
                ModuleFilter(pattern = "Postgrex.*"),
                ModuleFilter(pattern = "Process"),
                ModuleFilter(pattern = "Protocol"),
                ModuleFilter(pattern = "Range"),
                ModuleFilter(pattern = "Record"),
                ModuleFilter(pattern = "Record.*"),
                ModuleFilter(pattern = "Regex"),
                ModuleFilter(pattern = "Registry"),
                ModuleFilter(pattern = "Registry.*"),
                ModuleFilter(pattern = "Set"),
                ModuleFilter(pattern = "Stream"),
                ModuleFilter(pattern = "Stream.*"),
                ModuleFilter(pattern = "String"),
                ModuleFilter(pattern = "String.*"),
                ModuleFilter(pattern = "StringIO"),
                ModuleFilter(pattern = "Supervisor"),
                ModuleFilter(pattern = "Supervisor.*"),
                ModuleFilter(pattern = "System"),
                ModuleFilter(pattern = "Task"),
                ModuleFilter(pattern = "Task.*"),
                ModuleFilter(pattern = "Time"),
                ModuleFilter(pattern = "Tuple"),
                ModuleFilter(pattern = "URI"),
                ModuleFilter(pattern = "Version"),
                ModuleFilter(pattern = ":cow*"),
                ModuleFilter(pattern = ":elixir_*"),
                // See https://github.com/KronicDeth/intellij-elixir/issues/915
                ModuleFilter(pattern = ":erocksdb"),
                // See https://github.com/KronicDeth/intellij-elixir/issues/989
                ModuleFilter(pattern = ":lz4"),
                ModuleFilter(pattern = ":ranch*"),
                // See https://github.com/KronicDeth/intellij-elixir/issues/989
                ModuleFilter(pattern = ":re2")
        )

        fun getInstance(): Settings = XDebuggerSettings.getInstance(Settings::class.java)
    }
}
