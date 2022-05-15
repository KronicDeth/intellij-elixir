package org.elixir_lang.credo.state

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import java.util.*

class EnvironmentVariablesData(
    @Attribute("pass-parent-envs") var passParentEnvs: Boolean = false,
    @Tag("envs") var envs: SortedSet<Env> = sortedSetOf(),
) {
    override fun equals(other: Any?): Boolean =
        other is EnvironmentVariablesData && other.passParentEnvs == this.passParentEnvs && other.envs == this.envs

    override fun hashCode(): Int = Objects.hash(passParentEnvs, this.envs)

    fun toConfiguration(): com.intellij.execution.configuration.EnvironmentVariablesData =
        com.intellij.execution.configuration.EnvironmentVariablesData.create(
            envs.map { env -> env.name to env.value }.toMap(),
            passParentEnvs
        )

    companion object {
        fun fromConfiguration(configuration: com.intellij.execution.configuration.EnvironmentVariablesData):
                EnvironmentVariablesData {
            val envs =
                configuration
                    .envs
                    .map { (name, value) ->
                        Env(name, value)
                    }
                    .toSortedSet()
            return EnvironmentVariablesData(configuration.isPassParentEnvs, envs)
        }
    }
}
