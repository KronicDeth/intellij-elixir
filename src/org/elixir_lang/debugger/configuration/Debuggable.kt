package org.elixir_lang.debugger.configuration

import com.intellij.openapi.project.Project
import org.elixir_lang.debugger.Settings
import org.elixir_lang.debugger.settings.stepping.ModuleFilter

/**
 * A [com.intellij.execution.configurations.RunConfiguration] that can be debugged by [org.elixir_lang.debugger.Process]
 */
interface Debuggable<out T : org.elixir_lang.run.Configuration> {
    /**
     * Cookie set in original [Debuggable] configuration.
     *
     * @return if non-`null`, will be used for `cookie` passed to [debuggerConfiguration] and [debuggedConfiguration].
     */
    val cookie: String?

    /**
     * Name set in original [Debuggable] configuration.
     *
     * @return if non-`null`, will be used for `name` passed to [debuggedConfiguration] and the name for the debugged
     *   node used in `sys.config` at `configPath` passed to both [debuggerConfiguration] and [debuggedConfiguration].
     */
    val nodeName: String?

    /**
     * The original [Debuggable] configuration with the additions to allow [debuggerConfiguration] to attach to the
     * process for remote debugging.
     *
     * @param name the `erl` argument `-name` value
     * @param cookie the cookie used to authorize the debugger and debugged nodes to talk to each other
     */
    fun debuggedConfiguration(name: String, cookie: String): T

    /**
     * Returns the project in which the configuration exists.
     *
     * @return the project instance.
     */
    fun getProject(): Project

    /**
     * Whether to use ModuleFilters from Preferences > Build, Execution, Deployment > Debugger > Stepping > Elixir
     */
    var inheritApplicationModuleFilters: Boolean

    /**
     * 1. The enabled of new module filters added on top of the Preferences > Build, Execution, Deployment > Debugger > Stepping > Elixir.
     * 2. The disabled module filters inherited from Preferences > Build, Execution, Deployment > Debugger > Stepping > Elixir.
     */
    var moduleFilterList: MutableList<ModuleFilter>
}

fun Debuggable<*>.doNotInterpretPatterns(): List<String> {
        val inheritedPatternToModuleFilter = if (inheritApplicationModuleFilters) {
            Settings.getInstance().moduleFilters.associateBy { it.pattern }
        } else {
            emptyMap()
        }

        val configuredPatternToModuleFilter = moduleFilterList.associateBy { it.pattern }
        val inheritedPatternSet = inheritedPatternToModuleFilter.keys
        val configuredPatternSet = configuredPatternToModuleFilter.keys
        val patternList = inheritedPatternSet.union(configuredPatternSet).sorted()

        return patternList.mapNotNull { pattern ->
            val moduleFilter =
                    configuredPatternToModuleFilter[pattern]
                            ?: inheritedPatternToModuleFilter[pattern]!!

            if (moduleFilter.enabled) {
                moduleFilter.pattern
            } else {
                null
            }
        }
    }
