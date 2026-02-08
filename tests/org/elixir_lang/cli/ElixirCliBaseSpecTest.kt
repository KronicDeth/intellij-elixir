package org.elixir_lang.cli

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.RootProvider
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.ElixirCliBase
import org.elixir_lang.ElixirCliDryRun
import org.elixir_lang.PlatformTestCase
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class ElixirCliBaseSpecTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/cli"

    fun testElixirCliBaseMatchesSpecFeatures() {
        val entries = loadSpecEntries()
        val errors = mutableListOf<String>()

        for (entry in entries) {
            val actualTokens = buildActualTokens(entry) ?: continue
            val missing = findMissingFeatures(entry, actualTokens)
            if (missing.isNotEmpty()) {
                errors.add("${entry.tool} ${entry.version}: ${missing.joinToString(", ")}")
            }
        }

        assertTrue(
            "Missing expected CLI features:\n" + errors.joinToString("\n"),
            errors.isEmpty()
        )
    }

    private fun buildActualTokens(entry: SpecEntry): List<String>? {
        val homePath = entry.mixPath?.let { mixPath ->
            Paths.get(mixPath).parent?.parent?.toString()
        }
        val elixirSdk = createMockSdk(entry.version, entry.paPaths, homePath)
        val erlangSdk = createMockSdk(null, emptyList())
        val commandLine = GeneralCommandLine()

        val tool = when (entry.tool) {
            "elixir" -> ElixirCliDryRun.Tool.ELIXIR
            "mix" -> ElixirCliDryRun.Tool.MIX
            "iex" -> ElixirCliDryRun.Tool.IEX
            else -> null
        } ?: return null

        ElixirCliBase.addFallbackArguments(
            tool = tool,
            commandLine = commandLine,
            elixirSdk = elixirSdk,
            erlangSdk = erlangSdk,
            erlArgumentList = emptyList(),
        )
        return commandLine.parametersList.list
    }

    private fun findMissingFeatures(entry: SpecEntry, actualTokens: List<String>): List<String> {
        val missing = mutableListOf<String>()
        val specTokens = entry.tokens

        if (specTokens.contains("-noshell") && !actualTokens.contains("-noshell")) {
            missing.add("-noshell")
        }
        if (containsSequence(specTokens, listOf("-s", "elixir", "start_cli")) &&
            !containsSequence(actualTokens, listOf("-s", "elixir", "start_cli"))) {
            missing.add("start_cli")
        }
        if (containsSequence(specTokens, listOf("-s", "elixir", "start_iex")) &&
            !containsSequence(actualTokens, listOf("-s", "elixir", "start_iex"))) {
            missing.add("start_iex")
        }
        if (containsSequence(specTokens, listOf("-user", "elixir")) &&
            !containsSequence(actualTokens, listOf("-user", "elixir"))) {
            missing.add("user_elixir")
        }
        if (containsSequence(specTokens, listOf("-user", "Elixir.IEx.CLI")) &&
            !containsSequence(actualTokens, listOf("-user", "Elixir.IEx.CLI"))) {
            missing.add("user_iex_cli")
        }
        if (specTokens.contains(":elixir.start_iex()") && !actualTokens.contains(":elixir.start_iex()")) {
            missing.add("start_iex_fn")
        }
        if (specTokens.contains("-extra") && !actualTokens.contains("-extra")) {
            missing.add("-extra")
        }
        if (!containsSequence(actualTokens, listOf("-elixir", "ansi_enabled", "true"))) {
            missing.add("-elixir ansi_enabled true")
        }
        if (specTokens.contains("--no-halt") && !actualTokens.contains("--no-halt")) {
            missing.add("--no-halt")
        }
        if (specTokens.contains("+iex") && !actualTokens.contains("+iex")) {
            missing.add("+iex")
        }
        entry.mixPath?.let { mixPath ->
            val normalizedActual = actualTokens.map { it.replace('\\', '/') }
            val normalizedMixPath = mixPath.replace('\\', '/')
            val hasMixPath = normalizedActual.any { actual ->
                actual == normalizedMixPath || actual.endsWith(normalizedMixPath)
            }
            if (!hasMixPath) {
                missing.add("mix_path")
            }
        }

        return missing
    }

    private fun containsSequence(tokens: List<String>, sequence: List<String>): Boolean {
        if (sequence.isEmpty() || sequence.size > tokens.size) {
            return false
        }
        for (index in 0..(tokens.size - sequence.size)) {
            if (tokens.subList(index, index + sequence.size) == sequence) {
                return true
            }
        }
        return false
    }

    private fun loadSpecEntries(): List<SpecEntry> {
        val specPath = Paths.get(getTestDataPath(), "elixir_cli_base_args.txt")
        val lines = Files.readAllLines(specPath, StandardCharsets.UTF_8)
        val entries = mutableListOf<SpecEntry>()
        var tool: String? = null

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                continue
            }
            if (line.startsWith("#")) {
                tool = line.removePrefix("#").trim()
                continue
            }
            val parts = line.split("|", limit = 2)
            if (parts.size != 2 || tool == null) {
                continue
            }
            val version = parts[0].trim()
            val tokens = tokenize(parts[1]).dropWhile { it == "erl" }.dropLastWhile { it == "--" }
            if (tokens.isEmpty()) {
                continue
            }

            val paPaths = parsePaPaths(tokens)
            val mixPath = parseMixPath(tool, tokens)
            entries.add(SpecEntry(tool, version, tokens, paPaths, mixPath))
        }

        return entries
    }

    private fun tokenize(command: String): List<String> =
        command.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

    private fun parsePaPaths(tokens: List<String>): List<String> {
        val paths = mutableListOf<String>()
        var index = 0
        while (index < tokens.size) {
            if (tokens[index] == "-pa" && index + 1 < tokens.size) {
                paths.add(tokens[index + 1])
                index += 2
            } else {
                index += 1
            }
        }
        return paths
    }

    private fun parseMixPath(tool: String, tokens: List<String>): String? {
        if (tool != "mix") {
            return null
        }
        val extraIndex = tokens.indexOf("-extra")
        return if (extraIndex >= 0 && extraIndex + 1 < tokens.size) tokens[extraIndex + 1] else null
    }

    private fun createMockSdk(version: String?, ebinPaths: List<String>, homePath: String? = null): Sdk {
        val sdk = mock(Sdk::class.java)
        `when`(sdk.versionString).thenReturn(version)
        `when`(sdk.homePath).thenReturn(homePath)

        val rootProvider = mock(RootProvider::class.java)
        val virtualFiles = ebinPaths.map { path ->
            val virtualFile = mock(VirtualFile::class.java)
            `when`(virtualFile.canonicalPath).thenReturn(path)
            virtualFile
        }.toTypedArray()

        `when`(rootProvider.getFiles(OrderRootType.CLASSES)).thenReturn(virtualFiles)
        `when`(sdk.rootProvider).thenReturn(rootProvider)

        return sdk
    }

    private data class SpecEntry(
        val tool: String,
        val version: String,
        val tokens: List<String>,
        val paPaths: List<String>,
        val mixPath: String?,
    )
}
