package org.elixir_lang.sdk.erlang

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.runBlockingCancellable
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
import org.elixir_lang.sdk.SdkHomeScan
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
        private fun createConfig() = SdkHomeScan.Config(
            toolName = "erlang",
            nixPattern = NIX_PATTERN,
            linuxDefaultPath = LINUX_DEFAULT_HOME_PATH,
            linuxMintPath = LINUX_MINT_HOME_PATH,
            windowsDefaultPath = WINDOWS_DEFAULT_HOME_PATH,
            windows32BitPath = null,
            homebrewTransform = { versionPath -> File(versionPath, "lib/erlang") },
            nixTransform = { versionPath -> File(versionPath, "lib/erlang") },
            kerlTransform = { it },
            travisCIKerlTransform = { it }
        )

        @JvmStatic
        internal fun setupSdkTableListener() {
            val messageBus = ApplicationManager.getApplication().messageBus
            messageBus.connect().subscribe(com.intellij.openapi.projectRoots.ProjectJdkTable.JDK_TABLE_TOPIC,
                object : com.intellij.openapi.projectRoots.ProjectJdkTable.Listener {
                    override fun jdkRemoved(jdk: Sdk) {
                        // When an Erlang SDK is removed, clean up project references
                        if (jdk.sdkType is Type) {
                            cleanupProjectReferences(jdk)
                        }
                    }
                })
        }

        private fun cleanupProjectReferences(deletedSdk: Sdk) {
            LOGGER.warn("Erlang SDK removed: ${deletedSdk.name}, cleaning up project references")
            com.intellij.openapi.project.ProjectManager.getInstance().openProjects.forEach { project ->
                val projectRootManager = com.intellij.openapi.roots.ProjectRootManager.getInstance(project)
                if (projectRootManager.projectSdk == deletedSdk) {
                    ApplicationManager.getApplication().invokeLater {
                        ApplicationManager.getApplication().runWriteAction {
                            projectRootManager.projectSdk = null
                            LOGGER.warn("Cleared removed Erlang SDK '${deletedSdk.name}' from project '${project.name}'")
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun getDefaultSdkName(
            sdkHome: String,
            version: Release?,
        ): String =
            buildString {
                val source = HomePath.detectSource(sdkHome)
                if (source != null) {
                    append(source).append(" ")
                }
                append("Erlang for Elixir ")
                if (version != null) {
                    // Use directory name for version if it's more specific (e.g., "28.3" vs "28")
                    val dirVersion = File(sdkHome).name
                    val displayVersion = if (dirVersion.startsWith(version.otpRelease)) dirVersion else version.otpRelease
                    append(displayVersion)
                } else {
                    append("at ").append(sdkHome)
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
                    HomePath.mergeMise(homePathByVersion, "erlang", java.util.function.Function.identity())
                    HomePath.mergeKerl(homePathByVersion, java.util.function.Function.identity())
                    HomePath.mergeHomebrew(homePathByVersion, "erlang", VERSION_PATH_TO_HOME_PATH)
                    HomePath.mergeNixStore(homePathByVersion, NIX_PATTERN, VERSION_PATH_TO_HOME_PATH)
                }

                SystemInfo.isWindows -> {
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, WINDOWS_DEFAULT_HOME_PATH)
                }

                SystemInfo.isLinux -> {
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_DEFAULT_HOME_PATH)
                    putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_MINT_HOME_PATH)
                    HomePath.mergeMise(homePathByVersion, "erlang", java.util.function.Function.identity())
                    HomePath.mergeKerl(homePathByVersion, java.util.function.Function.identity())
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

        // Check if we're already in a write action (called from Elixir SDK setup)
        if (ApplicationManager.getApplication().isWriteAccessAllowed) {
            // We're already in a write action, commit directly
            sdkModificator.commitChanges()
        } else {
            // Use coroutine-based approach for IntelliJ 2025.2+ compatibility when called independently
            runBlockingCancellable {
                edtWriteAction {
                    LOGGER.debug("Committing SDK changes for ${sdk.name}")
                    sdkModificator.commitChanges()
                    LOGGER.debug("Committed SDK changes for ${sdk.name}")
                }
            }
        }
    }

    override fun suggestHomePath(): String? = suggestHomePaths().firstOrNull()

    override fun suggestHomePaths(): Collection<String> = homePathByVersion().values

    override fun isValidSdkHome(path: String): Boolean = Erlang.getByteCodeInterpreterExecutable(path).canExecute()

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String = getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome))

    override fun getVersionString(sdkHome: String): String? {
        val detectedVersion = detectSdkVersion(sdkHome)?.otpRelease ?: return null
        val source = HomePath.detectSource(sdkHome)
        // Use directory name for version if it's more specific (e.g., "28.3" vs "28")
        val dirVersion = File(sdkHome).name
        val displayVersion = if (dirVersion.startsWith(detectedVersion)) dirVersion else detectedVersion
        return buildString {
            if (source != null) {
                append(source).append(" ")
            }
            append("Erlang ").append(displayVersion)
        }
    }

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
