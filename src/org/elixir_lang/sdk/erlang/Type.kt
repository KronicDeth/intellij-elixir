package org.elixir_lang.sdk.erlang

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.Version
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.jps.HomePath
import org.elixir_lang.jps.sdk_type.Erlang
import org.elixir_lang.sdk.SdkHomeScan
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.jdom.Element
import java.io.File
import java.nio.file.Path

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
                when (iterator.next()) {
                    OTP_RELEASE_PREFIX_LINE -> if (iterator.hasNext()) otpRelease = iterator.next()
                    ERTS_VERSION_PREFIX_LINE -> if (iterator.hasNext()) ertsVersion = iterator.next()
                }
            }

            return if (otpRelease != null && ertsVersion != null) Release(otpRelease, ertsVersion) else null
        }

        @JvmStatic
        fun homePathByVersion(): Map<Version, String> {
            return SdkHomeScan.homePathByVersion(null, createConfig())
        }

        @JvmStatic
        fun homePathByVersion(path: Path?): Map<Version, String> {
            return SdkHomeScan.homePathByVersion(path, createConfig())
        }
    }

    override fun isRootTypeApplicable(type: OrderRootType): Boolean =
        type == OrderRootType.CLASSES ||
                type == OrderRootType.SOURCES ||
                type ==
                org.elixir_lang.sdk.Type
                    .documentationRootType()

    override fun getHomeChooserDescriptor(): FileChooserDescriptor =
        org.elixir_lang.sdk.Type.createHomeChooserDescriptor(presentableName, ::validateSdkHomePath)

    private fun validateSdkHomePath(virtualFile: VirtualFile) {
        val selectedPath = virtualFile.path
        val valid = isValidSdkHome(selectedPath)

        if (!valid) {
            throw Exception("The selected directory is not a valid home for $presentableName")
        }
    }

    override fun setupSdkPaths(sdk: Sdk) {
        val app = ApplicationManager.getApplication()

        // Following Java SDK pattern: if on EDT, run VirtualFile access in background thread
        // This avoids EEL environment check issues with WSL paths
        if (app.isDispatchThread && !app.isWriteAccessAllowed) {
            com.intellij.openapi.progress.ProgressManager.getInstance().runProcessWithProgressSynchronously(
                {
                    setupSdkPathsImpl(sdk)
                },
                "Setting Up Erlang SDK Paths...",
                false,
                null
            )
        } else {
            setupSdkPathsImpl(sdk)
        }
    }

    private fun setupSdkPathsImpl(sdk: Sdk) {
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
                    sdkModificator.commitChanges()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun suggestHomePath(): String? = suggestHomePaths().firstOrNull()

    @Deprecated("Deprecated in Java")
    override fun suggestHomePaths(): Collection<String> = homePathByVersion().values

    override fun suggestHomePath(path: Path): String? {
        return homePathByVersion(path).values.firstOrNull()
    }

    override fun suggestHomePaths(project: Project?): Collection<String> {
        return homePathByVersion(project?.guessProjectDir()?.toNioPathOrNull()).values
    }

    override fun isValidSdkHome(path: String): Boolean {
        val erlExe = Erlang.getByteCodeInterpreterExecutable(path)
        return erlExe.canExecute()
    }

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String {
        val baseName = getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome))
        return org.elixir_lang.sdk.Type.appendWslSuffix(baseName, sdkHome)
    }

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
        if (cachedRelease != null) {
            return cachedRelease
        }

        val erl = Erlang.getByteCodeInterpreterExecutable(sdkHome)
        LOGGER.debug("=== ERLANG SDK: Erl executable path: ${erl.absolutePath}")
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

        LOGGER.debug("=== ERLANG SDK: Executing erl to detect version")
        ApplicationManager
            .getApplication()
            .executeOnPooledThread {
                try {
                    val erlPath = erl.absolutePath
                    LOGGER.debug("=== ERLANG SDK: Calling getProcessOutput with workDir: $sdkHome, exe: $erlPath")
                    val output =
                        org.elixir_lang.sdk.ProcessOutput.getProcessOutput(
                            10 * 1000,
                            sdkHome,
                            erlPath,
                            "-noshell",
                            "-eval",
                            PRINT_VERSION_INFO_EXPRESSION,
                        )


                    if (output.exitCode == 0 && !output.isCancelled && !output.isTimeout) {
                        release =
                            parseSdkVersion(output.stdoutLines)?.also { detectedRelease ->
                                LOGGER.debug("=== ERLANG SDK: Detected release: ${detectedRelease.otpRelease}")
                                getVersionCacheKey(sdkHome)?.let { key ->
                                    releaseBySdkHome[key] = detectedRelease
                                }
                            }
                    } else {
                        LOGGER.warn("=== ERLANG SDK: Failed to detect Erlang version. Workdir: '$sdkHome' ErlPath: '$erlPath' Exit Code: ${output.exitCode}\nStdOut: ${output.stdout}\nStdErr: ${output.stderr}")
                    }
                } catch (e: ExecutionException) {
                    LOGGER.warn("=== ERLANG SDK: Exception during version detection", e)
                }
            }.get() // Wait for the task to complete

        LOGGER.debug("=== ERLANG SDK: Final release result: ${release?.otpRelease ?: "null"}")
        return release
    }
}
