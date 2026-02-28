package org.elixir_lang.mix

import com.intellij.execution.ExecutionException
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.Mix
import org.elixir_lang.package_manager.DepGatherer
import org.elixir_lang.package_manager.DepsStatusResult

private val LOG = logger<PackageManager>()
private val ANSI_REGEX = Regex("\u001B\\[[;\\d]*m")

private fun String.stripColor(): String = replace(ANSI_REGEX, "")

class PackageManager : org.elixir_lang.PackageManager {
    override val fileName: String = org.elixir_lang.mix.Project.MIX_EXS
    override fun depGatherer(): DepGatherer = org.elixir_lang.mix.DepGatherer()

    override fun depsStatus(project: Project, packageVirtualFile: VirtualFile, sdk: Sdk?): DepsStatusResult {
        if (sdk == null) {
            return DepsStatusResult.Error("No Elixir SDK configured for Mix")
        }

        val workingDirectory = packageVirtualFile.parent?.path
            ?: return DepsStatusResult.Error("Missing working directory for ${packageVirtualFile.path}")

        val commandLine = Mix.commandLine(emptyMap(), workingDirectory, sdk)
        commandLine.addParameters("deps")

        return try {
            val output = ExecUtil.execAndGetOutput(commandLine)

            if (output.isTimeout) {
                return DepsStatusResult.Error("mix deps timed out")
            }

            if (output.isCancelled) {
                return DepsStatusResult.Error("mix deps was cancelled")
            }

            if (output.exitCode != 0) {
                val message = output.stderr.stripColor().trim().ifEmpty {
                    output.stdout.stripColor().trim().ifEmpty { "mix deps failed" }
                }
                return DepsStatusResult.Error(message)
            }

            val status = MixDepsStatusParser.parse(output.stdout)
            DepsStatusResult.Available(status)
        } catch (e: ExecutionException) {
            LOG.warn("mix deps failed for ${packageVirtualFile.path}", e)
            DepsStatusResult.Error(e.message ?: "mix deps failed")
        }
    }

}
