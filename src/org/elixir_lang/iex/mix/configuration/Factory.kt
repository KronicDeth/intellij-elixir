package org.elixir_lang.iex.mix.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.elixir_lang.iex.mix.Configuration

object Factory : ConfigurationFactory(Type.getInstance()) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
            Configuration("IEx Mix", project, this)
}
