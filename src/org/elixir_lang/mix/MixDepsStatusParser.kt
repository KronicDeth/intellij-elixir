package org.elixir_lang.mix

import org.elixir_lang.package_manager.DepState
import org.elixir_lang.package_manager.DepStatus
import org.elixir_lang.package_manager.DepsStatus

internal object MixDepsStatusParser {
    private const val DEP_PREFIX = "* "
    private const val LOCKED_AT_PREFIX = "locked at "
    private val ANSI_REGEX = Regex("\u001B\\[[;\\d]*m")

    fun parse(output: String): DepsStatus {
        val dependencies = mutableListOf<DepStatus>()
        var currentName: String? = null
        val currentLines = mutableListOf<String>()

        fun flushCurrent() {
            val name = currentName ?: return
            dependencies.add(depStatus(name, currentLines))
            currentName = null
            currentLines.clear()
        }

        output
            .lineSequence()
            .map { it.stripColor().trimEnd() }
            .filter { it.isNotBlank() }
            .forEach { line ->
                if (line.startsWith(DEP_PREFIX)) {
                    flushCurrent()
                    currentName = parseDepName(line)
                } else if (currentName != null) {
                    currentLines.add(line.trim())
                }
            }

        flushCurrent()

        return DepsStatus(dependencies)
    }

    private fun parseDepName(line: String): String {
        val remainder = line.removePrefix(DEP_PREFIX).trimStart()
        val nameEnd = remainder.indexOf(' ')
        return if (nameEnd == -1) remainder else remainder.substring(0, nameEnd)
    }

    private fun depStatus(name: String, lines: List<String>): DepStatus {
        val statusLines = lines.map { it.trim() }.filter { it.isNotEmpty() }
        val nonLockedLines = statusLines.filterNot { it.startsWith(LOCKED_AT_PREFIX) }

        val okLine = nonLockedLines.firstOrNull { it.equals("ok", ignoreCase = true) }
        return when {
            okLine != null -> DepStatus(name, DepState.OK)
            nonLockedLines.isEmpty() -> DepStatus(name, DepState.UNKNOWN)
            else -> DepStatus(name, DepState.OUTDATED, nonLockedLines.last())
        }
    }

    private fun String.stripColor(): String = replace(ANSI_REGEX, "")
}
