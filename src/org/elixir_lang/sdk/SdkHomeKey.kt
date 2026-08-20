package org.elixir_lang.sdk

import com.intellij.openapi.util.Version

data class SdkHomeKey(
    val version: Version,
    val qualifier: String?,      // full suffix or raw version string when parse fails
    val source: String?,         // "asdf", "mise", "homebrew", "nix", "kerl", "travis", "system"
    val path: String
) : Comparable<SdkHomeKey> {
    override fun compareTo(other: SdkHomeKey): Int {
        // Newest first
        val v = other.version.compareTo(version)
        if (v != 0) return v

        val q = compareValues(other.qualifier, qualifier)
        if (q != 0) return q

        val s = compareValues(other.source, source)
        if (s != 0) return s

        return compareValues(other.path, path)
    }
}
