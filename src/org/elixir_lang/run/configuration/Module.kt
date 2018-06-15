package org.elixir_lang.run.configuration

import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.openapi.project.Project

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/runconfig/ErlangModuleBasedConfiguration.java
 */
class Module(project: Project) : RunConfigurationModule(project)
