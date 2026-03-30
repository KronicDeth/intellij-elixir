package sdk

import java.io.File

/**
 * Parsed versions from .tool-versions for Erlang and Elixir.
 */
data class ToolVersions(
    val erlang: String?,
    val elixir: String?
)

/**
 * Reads .tool-versions, returning only the erlang and elixir entries if present.
 */
fun readToolVersions(file: File?): ToolVersions {
    if (file == null || !file.isFile) {
        return ToolVersions(null, null)
    }

    var erlang: String? = null
    var elixir: String? = null
    file.forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return@forEachLine
        }
        val parts = trimmed.split(Regex("\\s+"), limit = 2)
        if (parts.size == 2) {
            when (parts[0]) {
                "erlang" -> erlang = parts[1].trim()
                "elixir" -> elixir = parts[1].trim()
            }
        }
    }
    return ToolVersions(erlang, elixir)
}
