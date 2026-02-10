package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk

internal object ElixirCliCommandLine {
    fun commandLine(
        project: Project?,
        tool: ElixirCliDryRun.Tool,
        pty: Boolean,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
        elixirArgumentList: kotlin.collections.List<String> = emptyList(),
    ): GeneralCommandLine {
        val erlangSdk = Elixir.elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine = commandLine(
            pty = pty,
            environment = environment,
            workingDirectory = workingDirectory,
        )
        val dryRunArguments = ElixirCliBase.dryRunArguments(
            project = project,
            tool = tool,
            environment = environment,
            workingDirectory = workingDirectory,
            elixirSdk = elixirSdk,
            erlArgumentList = erlArgumentList,
        )
        if (dryRunArguments != null) {
            Erl.setErl(commandLine, erlangSdk, prependCodePaths = false)
            val mergedArguments = if (tool == ElixirCliDryRun.Tool.MIX) {
                insertElixirArgumentsAfterExtra(dryRunArguments, elixirArgumentList)
            } else {
                dryRunArguments
            }
            commandLine.addParameters(mergedArguments)
        } else {
            Erl.setErl(commandLine, erlangSdk, prependCodePaths = true)
            ElixirCliBase.addFallbackArguments(
                tool = tool,
                commandLine = commandLine,
                elixirSdk = elixirSdk,
                erlangSdk = erlangSdk,
                erlArgumentList = erlArgumentList,
                elixirArgumentList = elixirArgumentList,
            )
        }

        return commandLine
    }

    private fun insertElixirArgumentsAfterExtra(
        baseArguments: kotlin.collections.List<String>,
        elixirArgumentList: kotlin.collections.List<String>,
    ): kotlin.collections.List<String> {
        if (elixirArgumentList.isEmpty()) {
            return baseArguments
        }

        val extraIndex = baseArguments.indexOf("-extra")
        if (extraIndex == -1) {
            return baseArguments + elixirArgumentList
        }

        val insertIndex = extraIndex + 1
        val merged = ArrayList<String>(baseArguments.size + elixirArgumentList.size)
        merged.addAll(baseArguments.subList(0, insertIndex))
        merged.addAll(elixirArgumentList)
        merged.addAll(baseArguments.subList(insertIndex, baseArguments.size))
        return merged
    }
}
