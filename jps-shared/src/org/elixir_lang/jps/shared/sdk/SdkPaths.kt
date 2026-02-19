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
    const val MISE_POSIX_PATH_FROM_HOME = ".local/share/mise/installs"
    const val MISE_WINDOWS_PATH_FROM_HOME = "appdata/local/mise/installs"

    fun detectSource(homePath: String): String? {
        val posixPath = FileUtil.toSystemIndependentName(homePath)
        val matchPath = posixPath.lowercase(Locale.ROOT)

        if (matchPath.contains(MISE_POSIX_PATH_FROM_HOME) || matchPath.contains(MISE_WINDOWS_PATH_FROM_HOME)) {
            return SOURCE_NAME_MISE
        }
        if (matchPath.contains("/.asdf/installs/")) {
            return SOURCE_NAME_ASDF
        }
        if (matchPath.contains("/.elixir-install/")) {
            return SOURCE_NAME_ELIXIR_INSTALL
        }
        if (matchPath.contains("/usr/local/cellar/") || matchPath.contains("/opt/homebrew/cellar/")) {
            return SOURCE_NAME_HOMEBREW
        }
        if (matchPath.contains("/nix/store/")) {
            return SOURCE_NAME_NIX
        }
        if (matchPath.contains("/otp/") || File(homePath, ".kerl_config").exists()) {
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

    fun mixHomeReplacePrefix(source: String?, homePath: String?): String? {
        if (SOURCE_NAME_MISE == source) {
            if (homePath != null) {
                val posixPath = FileUtil.toSystemIndependentName(homePath)
                val matchPath = posixPath.lowercase(Locale.ROOT)
                if (matchPath.contains(MISE_WINDOWS_PATH_FROM_HOME)) {
                    return MISE_WINDOWS_PATH_FROM_HOME
                }
            }
            return MISE_POSIX_PATH_FROM_HOME
        }
        if (SOURCE_NAME_ASDF == source) {
            return "/.asdf/installs/"
        }
        if (SOURCE_NAME_ELIXIR_INSTALL == source) {
            return "/.elixir-install/installs/"
        }
        return null
    }

    fun shouldReplaceMixHome(existingMixHome: String?, replacePrefix: String?): Boolean {
        if (existingMixHome == null) {
            return true
        }
        if (replacePrefix == null) {
            return false
        }

        val posixPath = FileUtil.toSystemIndependentName(existingMixHome)
        if (IS_WINDOWS) {
            val matchPath = posixPath.lowercase(Locale.ROOT)
            val matchPrefix = replacePrefix.lowercase(Locale.ROOT)
            return matchPath.contains(matchPrefix)
        }
        return posixPath.contains(replacePrefix)
    }

    @JvmStatic
    fun maybeUpdateMixHome(environment: MutableMap<String, String>, homePath: String?) {
        val mixHome = mixHome(homePath) ?: return
        val source = detectSource(homePath ?: return)
        val replacePrefix = mixHomeReplacePrefix(source, homePath)
        val existingMixHome = environment["MIX_HOME"]
        if (shouldReplaceMixHome(existingMixHome, replacePrefix)) {
            environment["MIX_HOME"] = mixHome.absolutePath
            environment["MIX_ARCHIVES"] = mixHome.resolve("archives").absolutePath
            logger.info("Updated mix environment variables (MIX_HOME=${environment["MIX_HOME"]}, MIX_ARCHIVES=${environment["MIX_ARCHIVES"]})")
        } else {
            logger.debug { "Using existing mix environment variables (MIX_HOME=${environment["MIX_HOME"]}, MIX_ARCHIVES=${environment["MIX_ARCHIVES"]})" }
        }
    }
}
