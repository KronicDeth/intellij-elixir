package org.elixir_lang.sdk.erlang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import org.elixir_lang.cli.getExecutableFilepathWslSafe
import org.elixir_lang.jps.shared.ErlangSdkTypeId
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.SdkHomeKey
import org.elixir_lang.sdk.SdkHomePaths
import org.elixir_lang.sdk.SdkHomeScan
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.jdom.Element
import java.io.File
import java.nio.file.Path

class Type : SdkType(ErlangSdkTypeId.ERLANG_SDK_TYPE_ID) {
    companion object {
        private const val WINDOWS_DEFAULT_HOME_PATH = "C:\\Program Files\\erl9.0"
        private val NIX_PATTERN = SdkHomePaths.nixPattern("erlang")
        private const val LINUX_MINT_HOME_PATH = "${SdkHomePaths.LINUX_MINT_HOME_PATH}/erlang"
        private const val LINUX_DEFAULT_HOME_PATH = "${SdkHomePaths.LINUX_DEFAULT_HOME_PATH}/erlang"

        @JvmStatic
        val instance: Type
            get() = findInstance(Type::class.java)

        @JvmStatic
        private fun createConfig() = SdkHomeScan.Config(
            toolName = "erlang",
            nixPattern = NIX_PATTERN,
            linuxDefaultPath = LINUX_DEFAULT_HOME_PATH,
            linuxMintPath = LINUX_MINT_HOME_PATH,
            windowsDefaultPath = WINDOWS_DEFAULT_HOME_PATH,
            windows32BitPath = null,
            elixirInstallScriptDirName = "otp",
            homebrewTransform = { versionPath -> File(versionPath, "lib/erlang") },
            nixTransform = { versionPath -> File(versionPath, "lib/erlang") },
            kerlTransform = { it },
            travisCIKerlTransform = { it }
        )

        @JvmStatic
        fun getDefaultSdkName(
            sdkHome: String,
            version: Release?,
        ): String =
            buildString {
                val source = SdkPaths.detectSource(sdkHome)
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

        @JvmStatic
        internal fun suggestSdkNameForHome(
            sdkHome: String,
            resolvedVersion: String?,
        ): String {
            val normalizedVersion = resolvedVersion?.takeIf { it.isNotBlank() }
            val baseName =
                if (normalizedVersion == null) {
                    getDefaultSdkName(sdkHome, ErlangVersionDetector.detectSdkVersion(sdkHome))
                } else {
                    val source = SdkPaths.detectSource(sdkHome)
                    val dirVersion = File(sdkHome).name
                    val displayVersion =
                        if (dirVersion.startsWith(normalizedVersion)) dirVersion else normalizedVersion
                    buildString {
                        if (source != null) {
                            append(source).append(" ")
                        }
                        append("Erlang for Elixir ").append(displayVersion)
                    }
                }

            return org.elixir_lang.sdk.Type.appendWslSuffix(baseName, sdkHome)
        }

        @JvmStatic
        internal fun versionStringForHome(
            sdkHome: String,
            resolvedVersion: String?,
        ): String? {
            val normalizedVersion = resolvedVersion?.takeIf { it.isNotBlank() }
            val version = normalizedVersion ?: ErlangVersionDetector.detectSdkVersion(sdkHome)?.otpRelease ?: return null
            val displayVersion =
                if (normalizedVersion == null) {
                    val dirVersion = File(sdkHome).name
                    if (dirVersion.startsWith(version)) dirVersion else version
                } else {
                    version
                }
            return erlangDisplayString(SdkPaths.detectSource(sdkHome), displayVersion)
        }

        private fun erlangDisplayString(source: String?, version: String): String = buildString {
            source?.let { append(it).append(" ") }
            append("Erlang ").append(version)
        }

        @JvmStatic
        fun homePathByVersion(): Map<SdkHomeKey, String> {
            return SdkHomeScan.homePathByVersion(null, createConfig())
        }

        @JvmStatic
        fun homePathByVersion(path: Path?): Map<SdkHomeKey, String> {
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
            runWithModalProgressBlocking(ModalTaskOwner.guess(), "Setting Up Erlang SDK Paths...") {
                withContext(Dispatchers.IO) {
                    setupSdkPathsImpl(sdk)
                }
            }
        } else {
            setupSdkPathsImpl(sdk)
        }
    }

    private fun setupSdkPathsImpl(sdk: Sdk) {
        val sdkModificator = sdk.sdkModificator
        org.elixir_lang.sdk.Type
            .addCodePaths(sdkModificator)

        // Check if we're already in a write action (called from Elixir SDK setup)
        val app = ApplicationManager.getApplication()
        if (app.isWriteAccessAllowed) {
            // We're already in a write action, commit directly
            sdkModificator.commitChanges()
            return
        }

        val runnable = Runnable { app.runWriteAction { sdkModificator.commitChanges() } }
        if (app.isDispatchThread) {
            runnable.run()
        } else {
            app.invokeAndWait(runnable)
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
        val erlExe = File(CliTool.ERL.getExecutableFilepathWslSafe(path))
        return erlExe.canExecute()
    }

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String {
        return suggestSdkNameForHome(sdkHome, null)
    }

    override fun getVersionString(sdkHome: String): String? {
        val detectedVersion = ErlangVersionDetector.detectSdkVersion(sdkHome)?.otpRelease ?: return null
        val dirVersion = File(sdkHome).name
        val displayVersion = if (dirVersion.startsWith(detectedVersion)) dirVersion else detectedVersion
        return erlangDisplayString(SdkPaths.detectSource(sdkHome), displayVersion)
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
}
