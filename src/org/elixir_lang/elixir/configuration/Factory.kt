package org.elixir_lang.elixir.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.elixir_lang.elixir.Configuration

object Factory : ConfigurationFactory(Type.getInstance()) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
            Configuration("Elixir", project, this)
}
