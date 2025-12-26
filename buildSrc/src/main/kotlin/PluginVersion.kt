import java.util.*

/**
 * Orchestrates plugin versioning logic for the IntelliJ Elixir project.
 */
object PluginVersion {

    /**
     * Determines the base version string.
     *
     * To prevent the IDE from suggesting updates to Marketplace versions during local development,
     * this utility increments the patch version for all non-release builds.
     *
     * Reasoning:
     * IntelliJ considers "22.0.1" (Marketplace) to be newer than "22.0.1-pre+timestamp" (Local).
     * By bumping the base to "22.0.2" locally, the local build is correctly identified as
     * "newer" than the current release, suppressing unnecessary update prompts.
     *
     * @param base The base version from gradle.properties (e.g., "22.0.1").
     * @param channel The publishing channel (e.g., "default", "canary").
     * @return The original base version for releases, or an incremented version for dev.
     */
    fun getBaseVersion(base: String, channel: String): String {
        return if (channel == "default") base else incrementPatch(base)
    }

    /**
     * Increments the patch number of a semver string (e.g., "22.0.1" -> "22.0.2").
     */
    private fun incrementPatch(version: String): String {
        val parts = version.split(".").toMutableList()
        return try {
            val lastIndex = parts.size - 1
            parts[lastIndex] = (parts[lastIndex].toInt() + 1).toString()
            parts.joinToString(".")
        } catch (e: Exception) {
            // If version isn't standard semver, append ".1" as a safety measure
            "$version.1"
        }
    }
}
