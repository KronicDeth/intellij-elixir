package sdk

import java.io.File

/**
 * Reads elixirVersion from gradle.properties without loading other properties.
 */
fun readGradleElixirVersion(file: File?): String? {
    if (file == null || !file.isFile) {
        return null
    }

    var result: String? = null
    file.useLines { lines ->
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue
            }
            if (trimmed.startsWith("elixirVersion=")) {
                result = trimmed.substringAfter("elixirVersion=").trim().ifBlank { null }
                break
            }
        }
    }

    return result
}
