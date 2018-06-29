package org.elixir_lang.debugger.configuration

import com.intellij.openapi.project.Project
import org.elixir_lang.debugger.settings.stepping.ModuleFilter

/**
 * A [com.intellij.execution.configurations.RunConfiguration] that can be debugged by [org.elixir_lang.debugger.Process]
 */
interface Debuggable<out T : org.elixir_lang.run.Configuration> {
    /**
     * The configuration for running the debugger `mix intellij_elixir.debug_task` process with enough of the original
     * [Debuggable] configuration's arguments copied over, so that the same modules can be loaded.
     *
     * @param name the `erl` argument `-name` value
     * @param configPath the `erl` argument `-config` value
     * @param javaPort the TCP port the Elixir debugger process should connect to the Java [org.elixir_lang.debugger.Node].
     */
    fun debuggerConfiguration(name: String, configPath: String, javaPort: Int): org.elixir_lang.debugger.Configuration

    /**
     * The original [Debuggable] configuration with the additions to allow [debuggerConfiguration] to attach to the
     * process for remote debugging.
     *
     * @param name the `erl` argument `-name` value
     * @param configPath the `erl` argument `-config` value
     */
    fun debuggedConfiguration(name: String, configPath: String): T

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
