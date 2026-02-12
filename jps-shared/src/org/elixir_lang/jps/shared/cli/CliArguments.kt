package org.elixir_lang.jps.shared.cli

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.util.Version
import com.intellij.util.system.OS
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

// This probably could have been a Pair, but using a data class allows for future expansion with minimal refactoring.
data class CliArgs(val exePath: String, val arguments: List<String>)

/**
 * Base function which is called directly by JPS-Builder and by the
 * main plugin. Do not use this directly from the main plugin, use the wrapper [org.elixir_lang.cli.CliArguments]
 * which has a more compact API.
 */
object CliArguments {
    private val logger = Logger.getInstance(CliArguments::class.java)
    private val iexLegacy = listOf(
        "-user", "Elixir.IEx.CLI",
        "-extra", "--no-halt"
    )
    private val iex150 = listOf(
        "-s", "elixir", "start_cli",
        "-elixir", "ansi_enabled", "true",
        "-user", "elixir",
        "-extra", "--no-halt",
        "-e", ":elixir.start_iex()"
    )
    private val iex151To116 = listOf(
        "-s", "elixir", "start_iex",
        "-elixir", "ansi_enabled", "true",
        "-user", "elixir",
        "-extra", "--no-halt"
    )
    private val iex117Plus = listOf(
        "-user", "elixir",
        "-extra", "--no-halt"
    )

    @JvmStatic
    fun args(
        elixirSdkHomePath: String?,
        elixirVersion: String?,
        erlangSdkHomePath: String?,
        tool: CliTool,
        extraElixirArguments: List<String> = emptyList(),
        extraErlangArguments: List<String> = emptyList(),
        os: OS = OS.CURRENT
    ): CliArgs? {
        val elixirHomePath = elixirSdkHomePath ?: return null
        val erlangHomePath = erlangSdkHomePath ?: return null
        val elixirVersion = parseElixirVersion(elixirVersion)
        logger.trace { "Calculating CliArguments (elixirHomePath=$elixirHomePath, elixirVersion=$elixirVersion, erlangHomePath=$erlangHomePath"}
        val erlCommonArgs = erlCommonArguments(elixirVersion, elixirHomePath)
        val toolArgs = when (tool) {
            CliTool.IEX -> iexArguments(elixirVersion) + "+iex"  + extraElixirArguments
            CliTool.ELIXIR -> elixirCommonArguments(elixirVersion)  + extraElixirArguments
            CliTool.ELIXIRC -> elixirCommonArguments(elixirVersion) + "+elixirc"   + extraElixirArguments
            CliTool.MIX -> elixirCommonArguments(elixirVersion)  + extraElixirArguments + localPathString(elixirHomePath, "bin", "mix")

            else -> throw RuntimeException("$tool is not implemented in CliArgs")
        }

        val erlExePath = CliTool.ERL.getExecutableFilepath(erlangHomePath, os)
        val args = erlCommonArgs + extraErlangArguments + toolArgs
        return CliArgs(erlExePath, args)
    }

    private fun erlCommonArguments(version: Version, homePath: String): List<String> =
        if (version.lessThan(1, 15, 0)) legacyPaArgs(homePath) + ansiEnableArg(version) + "-noshell"
        else if (version.isOrGreaterThan(1, 17, 0)) listOf("-noshell") + elixirRootArgs(homePath) + ansiEnableArg(
            version
        )
        else listOf("-noshell") + elixirRootArgs(homePath)

    private fun elixirCommonArguments(version: Version): List<String> =
        if (version.lessThan(1, 15, 0) || version.isOrGreaterThan(1, 17, 0))
            listOf("-s", "elixir", "start_cli", "-extra")
        else
            listOf("-s", "elixir", "start_cli") + ansiEnableArg(version) + "-extra"

    private fun ansiEnableArg(version: Version) =
        if (version.lessThan(1, 19, 0)) {
            listOf("-elixir", "ansi_enabled", "true")
        } else {
            emptyList()
        }

    private fun iexArguments(version: Version): List<String> = when {
        version.lessThan(1, 15, 0) -> iexLegacy
        version.`is`(1, 15, 0) -> iex150
        version.lessThan(1, 17, 0) -> iex151To116
        else -> iex117Plus
    }

    private fun elixirRootArgs(homePath: String): List<String> = listOf(
        "-elixir_root", localPathString(homePath, "lib"), "-pa", localPathString(homePath, "lib", "elixir", "ebin")
    )

    private fun legacyPaArgs(homePath: String): List<String> = listOf("-pa") + legacyLibs(homePath)

    private fun legacyLibs(homePath: String): List<String> =
        Path(homePath, "lib").listDirectoryEntries().sorted().map { it.resolve("ebin") }.filter { it.isDirectory() }
            .map { localPathString(it) }

    private fun localPathString(base: String, vararg subPaths: String): String = localPathString(Path(base, *subPaths))
    private fun localPathString(aPath: Path): String = aPath.absolutePathString()

    private fun parseElixirVersion(elixirVersion: String?): Version =
        extractElixirVersion(elixirVersion)?.let { Version.parseVersion(it) } ?: Version(0, 0, 0)

    private fun extractElixirVersion(elixirVersion: String?): String? =
        elixirVersion?.let {
            Regex("""(\d+\.\d+\.\d+(?:-[A-Za-z0-9]+)*)\b""")
                .find(it)
                ?.groupValues
                ?.get(1)
        }
}
