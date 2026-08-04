package sdk

import java.io.File

/**
 * Shared helpers for running `mix`/`elixir` from a RESOLVED Elixir SDK against a RESOLVED Erlang SDK
 * (as produced by [ElixirErlangSdkResolver] / the `resolveElixirErlangSdks` task). Used by both the
 * resolver (for version detection) and the quoter build tasks, so the quoter no longer needs a
 * from-source Elixir build.
 */

/** Platform-specific `mix` launcher name. */
fun mixExecutableName(): String = if (isWindows()) "mix.bat" else "mix"

/** Absolute path to the `mix` launcher inside a resolved Elixir SDK home. */
fun mixExecutable(elixirHome: File): String =
    File(File(elixirHome, "bin"), mixExecutableName()).absolutePath

/**
 * PATH/ERTS environment so `elixir`/`mix` locate the given Erlang SDK regardless of ambient PATH.
 * Prepends `<erlangHome>/bin` to PATH (and `Path` on Windows) and sets `ERTS_BIN`/`ERLANG_SDK_HOME`.
 * This is what makes the build independent of shell PATH / mise shims / Git Bash truncation.
 */
fun erlangRuntimeEnvironment(erlangHome: File): Map<String, String> {
    val binDir = File(erlangHome, "bin")
    val existingPath = System.getenv("PATH") ?: System.getenv("Path") ?: ""
    val newPath = if (existingPath.isBlank()) {
        binDir.absolutePath
    } else {
        "${binDir.absolutePath}${File.pathSeparator}$existingPath"
    }
    return buildMap {
        put("ERTS_BIN", binDir.absolutePath + File.separator)
        put("PATH", newPath)
        if (isWindows()) put("Path", newPath)
        put("ERLANG_SDK_HOME", binDir.parentFile.absolutePath)
    }
}

/**
 * Full environment for `mix` commands: the Erlang runtime environment plus MIX_HOME/MIX_ARCHIVES so
 * hex/rebar and fetched dependencies are cached under the project rather than the user's home.
 *
 * Pass [mixArchives] the per-version directory from [mixArchivesDir], not the shared root.
 */
fun mixEnvironment(erlangHome: File, mixHome: File, mixArchives: File): Map<String, String> =
    erlangRuntimeEnvironment(erlangHome) + mapOf(
        "MIX_HOME" to mixHome.absolutePath,
        "MIX_ARCHIVES" to mixArchives.absolutePath,
    )

/**
 * MIX_ARCHIVES directory for one Elixir/OTP pair, under the shared [root].
 *
 * Mix namespaces MIX_HOME by Elixir version itself (`<MIX_HOME>/elixir/1-15/`), but installs archives
 * flat into MIX_ARCHIVES (`hex-2.5.1/`). A single shared archives directory therefore hands a hex
 * compiled by one Elixir/OTP to another, which fails to load:
 *
 *     Error loading module 'Elixir.Hex': corrupt atom table
 *
 * Namespacing by both versions keeps each pair's hex separate. OTP is part of the key because the
 * archive is BEAM code: an Elixir release ships per-OTP builds, and the loader rejects a chunk layout
 * from a different OTP.
 */
fun mixArchivesDir(root: File, elixirVersion: String?, erlangVersion: String?): File =
    File(root, "elixir-${pathToken(elixirVersion)}-otp-${pathToken(erlangVersion)}")

/** Reduce a version string to something safe to use as a single path segment. */
private fun pathToken(version: String?): String =
    version?.trim()?.takeIf { it.isNotEmpty() }?.replace(Regex("[^A-Za-z0-9._-]"), "-") ?: "unknown"

/**
 * Environment for the JVM test tasks (`test`, jps-builder `test`) that read the Elixir
 * stdlib source/ebin: the resolved Erlang runtime env (so `erl` is on PATH) plus the
 * ELIXIR_LANG_ELIXIR_PATH / ELIXIR_EBIN_DIRECTORY / ELIXIR_VERSION vars pointed at the resolved SDK.
 * Read the properties file produced by `resolveElixirErlangSdks`.
 */
fun elixirTestEnvironment(sdkPropertiesFile: File): Map<String, String> {
    val props = readPropertiesFile(sdkPropertiesFile)
    val elixirHome = File(props["elixir.sdk.path"] ?: error("Missing elixir.sdk.path in ${sdkPropertiesFile.absolutePath}"))
    val erlangHome = File(props["erlang.sdk.path"] ?: error("Missing erlang.sdk.path in ${sdkPropertiesFile.absolutePath}"))
    val version = props["elixir.version"].orEmpty()
    val ebin = File(elixirHome, "lib${File.separator}elixir${File.separator}ebin")
    return erlangRuntimeEnvironment(erlangHome) + mapOf(
        "ELIXIR_LANG_ELIXIR_PATH" to elixirHome.absolutePath,
        "ELIXIR_EBIN_DIRECTORY" to ebin.absolutePath + File.separator,
        "ELIXIR_VERSION" to version,
    )
}
