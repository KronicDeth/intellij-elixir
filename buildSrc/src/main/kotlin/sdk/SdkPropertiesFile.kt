package sdk

import org.gradle.api.GradleException
import java.io.File

/**
 * Reads a simple key=value properties file without relying on java.util.Properties.
 */
fun readPropertiesFile(file: File): Map<String, String> {
    if (!file.isFile) {
        throw GradleException("SDK properties file not found: ${file.absolutePath}")
    }
    val props = mutableMapOf<String, String>()
    file.forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return@forEachLine
        }
        val idx = trimmed.indexOf('=')
        if (idx <= 0) {
            return@forEachLine
        }
        val key = trimmed.substring(0, idx).trim()
        val value = trimmed.substring(idx + 1).trim()
        props[key] = value
    }
    return props
}
