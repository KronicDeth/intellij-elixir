package org.elixir_lang.sdk.erlang

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.Version
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.jps.HomePath
import org.elixir_lang.jps.sdk_type.Erlang
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.jdom.Element
import java.io.File

class Type : SdkType("Erlang SDK for Elixir SDK") {
    private val releaseBySdkHome: MutableMap<String, Release> = ContainerUtil.createWeakMap()

    companion object {
        private const val OTP_RELEASE_PREFIX_LINE = "org.elixir_lang.sdk.erlang.Type OTP_RELEASE:"
        private const val ERTS_VERSION_PREFIX_LINE = "org.elixir_lang.sdk.erlang.Type ERTS_VERSION:"
        private const val PRINT_VERSION_INFO_EXPRESSION =
            "io:format(\"~n~s~n~s~n~s~n~s~n\",[" +
                "\"$OTP_RELEASE_PREFIX_LINE\"," +
                "erlang:system_info(otp_release)," +
                "\"$ERTS_VERSION_PREFIX_LINE\"," +
                "erlang:system_info(version)" +
                "]),erlang:halt()."
        private const val WINDOWS_DEFAULT_HOME_PATH = "C:\\Program Files\\erl9.0"
        private val NIX_PATTERN = HomePath.nixPattern("erlang")
        private const val LINUX_MINT_HOME_PATH = "${HomePath.LINUX_MINT_HOME_PATH}/erlang"
        private const val LINUX_DEFAULT_HOME_PATH = "${HomePath.LINUX_DEFAULT_HOME_PATH}/erlang"
        private val VERSION_PATH_TO_HOME_PATH: (File) -> File = { versionPath -> File(versionPath, "lib/erlang") }
        private val LOGGER = Logger.getInstance(Type::class.java)

        @JvmStatic
        fun getDefaultSdkName(
            sdkHome: String,
            version: Release?,
        ): String =
            buildString {
                append("Erlang for Elixir ")
                if (version != null) {
                    append(version.otpRelease)
                } else {
                    append(" at ").append(sdkHome)
                }
            }

        private fun getVersionCacheKey(sdkHome: String?): String? = sdkHome?.let { File(it).absolutePath }

        private fun parseSdkVersion(printVersionInfoOutput: List<String>): Release? {
            var otpRelease: String? = null
            var ertsVersion: String? = null

            val iterator = printVersionInfoOutput.listIterator()
            while (iterator.hasNext()) {
                when (val line = iterator.next()) {
                    OTP_RELEASE_PREFIX_LINE -> if (iterator.hasNext()) otpRelease = iterator.next()
                    ERTS_VERSION_PREFIX_LINE -> if (iterator.hasNext()) ertsVersion = iterator.next()
                }
            }

            return if (otpRelease != null && ertsVersion != null) Release(otpRelease, ertsVersion) else null
        }

        @JvmStatic
        fun homePathByVersion(): Map<Version, String> {
            val homePathByVersion = HomePath.homePathByVersion().toMutableMap()

            when {
                SystemInfo.isMac -> {
                    HomePath.mergeASDF(homePathByVersion, "erlang")
                    HomePath.mergeHomebrew(homePathByVersion, "erlang", VERSION_PATH_TO_HOME_PATH)
                    HomePath.mergeNixStore(homePathByVersion, NIX_PATTERN, VERSION_PATH_TO_HOME_PATH)
                }
                SystemInfo.isWindows -> {
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, WINDOWS_DEFAULT_HOME_PATH)
                }
                SystemInfo.isLinux -> {
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_DEFAULT_HOME_PATH)
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_MINT_HOME_PATH)
                    HomePath.mergeTravisCIKerl(homePathByVersion) { it }
                    HomePath.mergeNixStore(homePathByVersion, NIX_PATTERN, VERSION_PATH_TO_HOME_PATH)
                }
            }

            return homePathByVersion
        }

        private fun putIfDirectory(
            homePathByVersion: MutableMap<Version, String>,
            version: Version,
            homePath: String,
        ) {
            val homeFile = File(homePath)
            if (homeFile.isDirectory) {
                homePathByVersion[version] = homePath
            }
        }
    }

    override fun isRootTypeApplicable(type: OrderRootType): Boolean =
        type == OrderRootType.CLASSES ||
            type == OrderRootType.SOURCES ||
            type ==
            org.elixir_lang.sdk.Type
                .documentationRootType()

    override fun setupSdkPaths(sdk: Sdk) {
        val sdkModificator = sdk.sdkModificator
        org.elixir_lang.sdk.Type
            .addCodePaths(sdkModificator)
        ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() }
    }

    override fun suggestHomePath(): String? = suggestHomePaths().firstOrNull()

    override fun suggestHomePaths(): Collection<String> = homePathByVersion().values

    override fun isValidSdkHome(path: String): Boolean = Erlang.getByteCodeInterpreterExecutable(path).canExecute()

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String = getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome))

    override fun getVersionString(sdkHome: String): String? = detectSdkVersion(sdkHome)?.otpRelease

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator,
    ): AdditionalDataConfigurable? = null

    override fun getPresentableName(): String = name

    override fun saveAdditionalData(
        additionalData: com.intellij.openapi.projectRoots.SdkAdditionalData,
        additional: Element,
    ) {
        // Intentionally left blank
    }

    private fun detectSdkVersion(sdkHome: String): Release? {
        val cachedRelease = getVersionCacheKey(sdkHome)?.let { releaseBySdkHome[it] }
        if (cachedRelease != null) return cachedRelease

        val erl = Erlang.getByteCodeInterpreterExecutable(sdkHome)
        if (!erl.canExecute()) {
            val message =
                buildString {
                    append("Can't detect Erlang version: ${erl.path}")
                    if (erl.exists()) append(" is not executable.") else append(" is missing.")
                }
            LOGGER.warn(message)
            return null
        }

        var release: Release? = null

        ApplicationManager
            .getApplication()
            .executeOnPooledThread {
                try {
                    val output =
                        org.elixir_lang.sdk.ProcessOutput.getProcessOutput(
                            10 * 1000,
                            sdkHome,
                            erl.absolutePath,
                            "-noshell",
                            "-eval",
                            PRINT_VERSION_INFO_EXPRESSION,
                        )

                    if (output.exitCode == 0 && !output.isCancelled && !output.isTimeout) {
                        release =
                            parseSdkVersion(output.stdoutLines)?.also { detectedRelease ->
                                getVersionCacheKey(sdkHome)?.let { key ->
                                    releaseBySdkHome[key] = detectedRelease
                                }
                            }
                    } else {
                        LOGGER.warn("Failed to detect Erlang version.\nStdOut: ${output.stdout}\nStdErr: ${output.stderr}")
                    }
                } catch (e: ExecutionException) {
                    LOGGER.warn(e)
                }
            }.get() // Wait for the task to complete

        return release
    }
}
