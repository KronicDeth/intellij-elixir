package org.elixir_lang.jps.shared.sdk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.util.io.FileUtil
import java.io.File
import java.util.*

object SdkPaths {
    private val logger = Logger.getInstance(SdkPaths::class.java)

    const val SOURCE_NAME_ASDF = "asdf"
    const val SOURCE_NAME_MISE = "mise"
    const val SOURCE_NAME_ELIXIR_INSTALL = "elixir-install"
    const val SOURCE_NAME_HOMEBREW = "Homebrew"
    const val SOURCE_NAME_NIX = "Nix"
    const val SOURCE_NAME_KERL = "kerl"

    val VERSION_MANAGERS: List<String> =
        listOf(SOURCE_NAME_ASDF, SOURCE_NAME_MISE, SOURCE_NAME_ELIXIR_INSTALL)

    private val IS_WINDOWS =
        System.getProperty("os.name").lowercase(Locale.ROOT).contains("windows")

    // Installer roots, named once for both halves of the plugin: SdkHomePaths scans them and
    // detectSource recognises what a scan or a user produced. Adding one here is the whole change.
    const val MISE_POSIX_PATH_FROM_HOME = ".local/share/mise/installs"
    const val MISE_WINDOWS_PATH_FROM_HOME = "appdata/local/mise/installs"
    const val ASDF_INSTALLS_PATH_FROM_HOME = ".asdf/installs"
    const val ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME = ".elixir-install/installs"
    const val HOMEBREW_INTEL_CELLAR_PATH = "/usr/local/Cellar"
    const val HOMEBREW_APPLE_SILICON_CELLAR_PATH = "/opt/homebrew/Cellar"
    const val LINUXBREW_CELLAR_PATH = "/home/linuxbrew/.linuxbrew/Cellar"
    const val LINUXBREW_CELLAR_PATH_FROM_HOME = ".linuxbrew/Cellar"
    const val NIX_STORE_PATH = "/nix/store"

    /** kerl installs wherever it is told, so only Travis CI's `~/otp/<version>` is a known layout. */
    const val TRAVIS_CI_KERL_DIR_NAME = "otp"

    /**
     * Whether any of [segments] appears anywhere in this already-lowercased path.
     *
     * Deliberately a substring test rather than [FileUtil.isAncestor] or [FileUtil.startsWith]:
     * those anchor at the start of the path, and a home reached inside a WSL distribution is a UNC
     * path (`//wsl.localhost/<distro>/home/linuxbrew/...`) that no Linux root is a prefix of. The
     * roots above are absolute only on the machine that owns them, and the home-relative ones
     * belong to the distribution's user rather than this JVM's, so both are matched as segments.
     *
     * Matching is case-insensitive on every platform for the same reason - the case sensitivity of
     * the filesystem actually holding the path is not the host's. Lowercasing the needle here is
     * what lets the constants above keep their real casing; comparing against `"/usr/local/Cellar/"`
     * by hand silently never matches.
     */
    private fun String.containsAnyPath(vararg segments: String): Boolean =
        segments.any { contains(it.lowercase(Locale.ROOT)) }

    fun detectSource(homePath: String): String? {
        val posixPath = FileUtil.toSystemIndependentName(homePath)
        val matchPath = posixPath.lowercase(Locale.ROOT)

        if (matchPath.containsAnyPath(MISE_POSIX_PATH_FROM_HOME, MISE_WINDOWS_PATH_FROM_HOME)) {
            return SOURCE_NAME_MISE
        }
        if (matchPath.containsAnyPath("/$ASDF_INSTALLS_PATH_FROM_HOME/")) {
            return SOURCE_NAME_ASDF
        }
        if (matchPath.containsAnyPath("/$ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME/")) {
            return SOURCE_NAME_ELIXIR_INSTALL
        }
        if (matchPath.containsAnyPath(
                "$HOMEBREW_INTEL_CELLAR_PATH/",
                "$HOMEBREW_APPLE_SILICON_CELLAR_PATH/",
                "/$LINUXBREW_CELLAR_PATH_FROM_HOME/"
            )
        ) {
            return SOURCE_NAME_HOMEBREW
        }
        if (matchPath.containsAnyPath("$NIX_STORE_PATH/")) {
            return SOURCE_NAME_NIX
        }
        // Unlike the rest, the second test touches the filesystem - see TRAVIS_CI_KERL_DIR_NAME for
        // why there is no installer root to match instead.
        if (matchPath.containsAnyPath("/$TRAVIS_CI_KERL_DIR_NAME/") ||
            File(homePath, ".kerl_config").exists()
        ) {
            return SOURCE_NAME_KERL
        }

        return null
    }

    fun mixHome(homePath: String?): File? {
        if (homePath == null) {
            return null
        }

        val source = detectSource(homePath)
        if (source == null || !VERSION_MANAGERS.contains(source)) {
            return null
        }

        val binDir = File(homePath, "bin")
        val parentDir = binDir.parentFile ?: return null

        return File(parentDir, ".mix")
    }

    private fun expectedMixHomePrefix(source: String?, homePath: String?): String? = when (source) {
        SOURCE_NAME_MISE -> expectedMisePrefix(homePath)
        SOURCE_NAME_ASDF -> "/$ASDF_INSTALLS_PATH_FROM_HOME/"
        SOURCE_NAME_ELIXIR_INSTALL -> "/$ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME/"
        else -> null
    }

    private fun expectedMisePrefix(homePath: String?): String {
        if (homePath == null) {
            return MISE_POSIX_PATH_FROM_HOME
        }

        val posixPath = FileUtil.toSystemIndependentName(homePath)
        return if (containsIgnoreCase(posixPath, MISE_WINDOWS_PATH_FROM_HOME)) {
            MISE_WINDOWS_PATH_FROM_HOME
        } else {
            MISE_POSIX_PATH_FROM_HOME
        }
    }

    /**
     * Only replace MIX_HOME when it is missing or still under the current manager's default prefix.
     * If the user configured a custom MIX_HOME elsewhere, keep it.
     */
    private fun shouldReplaceMixHomeIfExpectedPrefixMatches(existingMixHome: String?, expectedPrefix: String?): Boolean {
        if (existingMixHome == null) {
            return true
        }
        if (expectedPrefix == null) {
            return false
        }

        return pathContains(existingMixHome, expectedPrefix)
    }

    private fun pathContains(path: String, expectedPrefix: String): Boolean {
        val posixPath = FileUtil.toSystemIndependentName(path)
        return if (IS_WINDOWS) {
            containsIgnoreCase(posixPath, expectedPrefix)
        } else {
            posixPath.contains(expectedPrefix)
        }
    }

    private fun containsIgnoreCase(text: String, fragment: String): Boolean =
        text.lowercase(Locale.ROOT).contains(fragment.lowercase(Locale.ROOT))

    /**
     * Updates MIX_HOME and MIX_ARCHIVES when the existing values are missing or still under the
     * current version manager's default install prefix. If the user configured custom values
     * elsewhere, keep them intact.
     */
    @JvmStatic
    fun maybeUpdateMixHome(environment: MutableMap<String, String>, homePath: String?) {
        val mixHome = mixHome(homePath) ?: return
        val source = detectSource(homePath ?: return)
        val expected = expectedMixHomePrefix(source, homePath)
        val existingMixHome = environment["MIX_HOME"]
        if (shouldReplaceMixHomeIfExpectedPrefixMatches(existingMixHome, expected)) {
            environment["MIX_HOME"] = mixHome.absolutePath
            environment["MIX_ARCHIVES"] = mixHome.resolve("archives").absolutePath
            logger.info("Updated mix environment variables (MIX_HOME=${environment["MIX_HOME"]}, MIX_ARCHIVES=${environment["MIX_ARCHIVES"]})")
        } else {
            logger.debug { "Using existing mix environment variables (MIX_HOME=${environment["MIX_HOME"]}, MIX_ARCHIVES=${environment["MIX_ARCHIVES"]})" }
        }
    }
}
