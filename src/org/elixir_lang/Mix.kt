package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.jps.sdk_type.Elixir

object Mix {
    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.mixCommandLine]
     */
    @JvmStatic
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlParameters: kotlin.collections.List<String> = emptyList(),
            elixirParameters: kotlin.collections.List<String> = emptyList()
    ): GeneralCommandLine {
        val commandLine = org.elixir_lang.Elixir.commandLine(
                environment,
                workingDirectory,
                elixirSdk,
                erlParameters
        )
        commandLine.addParameters(elixirParameters)
        addMix(commandLine, elixirSdk)

        return commandLine
    }

    private fun addMix(commandLine: GeneralCommandLine, sdk: Sdk) {
        val mixPath = Elixir.mixPath(sdk.homePath)
        commandLine.addParameter(mixPath)
    }
}
