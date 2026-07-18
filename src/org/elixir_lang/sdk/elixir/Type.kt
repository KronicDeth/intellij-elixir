package org.elixir_lang.sdk.elixir

import com.intellij.notification.NotificationAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import org.apache.commons.io.FilenameUtils
import org.elixir_lang.Icons
import org.elixir_lang.cli.getExecutableFilepathWslSafe
import org.elixir_lang.jps.shared.ElixirSdkTypeId
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.*
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.wsl.wslCompat
import org.elixir_lang.util.WriteActions
import org.elixir_lang.util.runWithEdtGuard
import org.jdom.Element
import org.jetbrains.annotations.Unmodifiable
import java.io.File
import java.nio.file.Path
import javax.swing.Icon


class Type : org.elixir_lang.sdk.erlang_dependent.Type(ElixirSdkTypeId.ELIXIR_SDK_TYPE_ID) {
    /**
     * If a path selected in the file chooser is not a valid SDK home path, and the base name is one of the commonly
     * incorrectly selected subdirectories - bin, lib, or src - then return the parent path, so it can be checked for
     * validity.
     *
     * @param homePath the path selected in the file chooser.
     * @return the path to be used as the SDK home.
     */
    override fun adjustSelectedSdkHome(homePath: String): String {
        val homePathFile = File(homePath)
        var adjustedSdkHome = homePath
        if (homePathFile.isDirectory) {
            val baseName = FilenameUtils.getBaseName(homePath)
            if (baseName == "bin") {
                adjustedSdkHome = homePathFile.parent

                // adjustSelectedSdkHome is only called once, but `bin` could either be the correct `bin` OR in `kiex` a false `bin`.
                if (!isValidSdkHome(adjustedSdkHome)) {
                    val libSibling = File(adjustedSdkHome, "lib")

                    // `kiex` has `lib/elixir` and the false `bin` as the same level
                    if (libSibling.exists()) {
                        adjustedSdkHome = File(libSibling, "elixir").path
                    }
                }
            } else if (SDK_HOME_CHILD_BASE_NAME_SET.contains(baseName)) {
                adjustedSdkHome = homePathFile.parent
            } else if (baseName.startsWith("elixir-") && FilenameUtils.getBaseName(homePathFile.parent) == "elixirs") {
                // `kiex` versioned directory like `~/.kiex/elixirs/elixir-VERSION`.
                adjustedSdkHome = File(File(homePathFile, "lib"), "elixir").path
            }
        }
        return adjustedSdkHome
    }

    override fun getDefaultDocumentationUrl(sdk: Sdk): String? =
        getDefaultDocumentationUrl(sdk.getUserData(ElixirVersionDetector.ELIXIR_VERSION_KEY))

    override fun getHomeChooserDescriptor(): FileChooserDescriptor =
        org.elixir_lang.sdk.Type.createHomeChooserDescriptor(presentableName, ::validateSdkHomePath)

    override fun getIcon(): Icon = Icons.LANGUAGE

    override fun getPresentableName(): String = "Elixir SDK"

    override fun getVersionString(sdkHome: String): String {
        return versionStringForHome(sdkHome, null)
            ?: buildString {
                SdkPaths.detectSource(sdkHome)?.let { append(it).append(" ") }
                append("Elixir Unknown")
            }
    }

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return Map
     */
    private fun homePathByVersion(path: Path?): Map<SdkHomeKey, String> {
        return SdkHomeScan.homePathByVersion(
            path, SdkHomeScan.Config(
                toolName = "elixir",
                nixPattern = NIX_PATTERN,
                linuxDefaultPath = LINUX_DEFAULT_HOME_PATH,
                linuxMintPath = LINUX_MINT_HOME_PATH,
                windowsDefaultPath = WINDOWS_64BIT_DEFAULT_HOME_PATH,
                windows32BitPath = WINDOWS_32BIT_DEFAULT_HOME_PATH,
                elixirInstallScriptDirName = "elixir",
                homebrewTransform = null,  // identity
                nixTransform = null,  // identity
                kerlTransform = null,  // TODO: Investigate if Elixir supports kerl builds
                travisCIKerlTransform = null  // TODO: Verify Travis CI kerl for Elixir
            )
        )
    }

    private fun invalidSdkHomeException(virtualFile: VirtualFile): Exception =
        Exception(invalidSdkHomeMessage(virtualFile))

    private fun invalidSdkHomeMessage(virtualFile: VirtualFile): String =
        if (virtualFile.isDirectory) {
            """A valid home for $presentableName has the following structure:

ELIXIR_SDK_HOME
* bin
** elixir
** elixirc
** iex
** mix
* lib
** eex
** elixir
** ex_unit
** iex
** logger
** mix
"""
        } else {
            "A directory must be select for the home for $presentableName"
        }

    override fun isValidSdkHome(path: String): Boolean {
        val elixir = File(CliTool.ELIXIR.getExecutableFilepathWslSafe(path))
        val elixirc = File(CliTool.ELIXIRC.getExecutableFilepathWslSafe(path))
        val iex = File(CliTool.IEX.getExecutableFilepathWslSafe(path))
        val mix = File(CliTool.MIX.getExecutableFilepathWslSafe(path))
        return elixir.canExecute() &&
                elixirc.canExecute() &&
                iex.canExecute() &&
                mix.canRead() &&
                SdkEbinPaths.hasEbinPath(path)
    }

    override fun setupSdkPaths(sdk: Sdk) {
        ElixirSdkPathConfigurator.configure(sdk)
    }

    @Deprecated("Deprecated in Java")
    // IntelliJ SDKType still requires this deprecated override.
    @Suppress("DEPRECATION")
    override fun suggestHomePath(): String? = suggestHomePaths().firstOrNull()

    override fun suggestHomePath(path: Path): String? {
        return homePathByVersion(path).values.firstOrNull()
    }

    @Deprecated("Deprecated in Java")
    override fun suggestHomePaths(): Collection<String> = homePathByVersion(null).values
    override fun suggestHomePaths(project: Project?): @Unmodifiable Collection<String> =
        // SdkDetectionContext falls back to the wizard's import/new-project directory when the
        // platform supplies the default project (import wizard, New Project), so WSL locations
        // are still scanned for suggestions.
        homePathByVersion(SdkDetectionContext.resolve(project)).values

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String {
        val canonicalHome = wslCompat.canonicalizePath(sdkHome)
        val otpMajor = runWithEdtGuard("Detecting Elixir SDK...") {
            ElixirBuildInfo.elixirOtpRelease(canonicalHome)
        }
        return suggestSdkNameForHome(sdkHome, null, otpMajor, null)
    }

    private fun validateSdkHomePath(virtualFile: VirtualFile) {
        val selectedPath = virtualFile.path

        val valid = isValidSdkHome(selectedPath)
        if (!valid) {
            throw invalidSdkHomeException(virtualFile)
        }
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator,
    ): com.intellij.openapi.projectRoots.AdditionalDataConfigurable {
        return AdditionalDataConfigurable(sdkModel, sdkModificator)
    }

    override fun saveAdditionalData(
        additionalData: com.intellij.openapi.projectRoots.SdkAdditionalData,
        additional: Element,
    ) {
        if (additionalData is SdkAdditionalData) {
            try {
                additionalData.writeExternal(additional)
            } catch (e: WriteExternalException) {
                LOG.error(e)
            }
        }
    }

    override fun loadAdditionalData(
        elixirSdk: Sdk,
        additional: Element,
    ): com.intellij.openapi.projectRoots.SdkAdditionalData {
        val sdkAdditionalData = SdkAdditionalData(elixirSdk)
        try {
            sdkAdditionalData.readExternal(additional)
        } catch (e: InvalidDataException) {
            LOG.error(e)
        }
        return sdkAdditionalData
    }

    companion object {
        private const val LINUX_DEFAULT_HOME_PATH = SdkHomePaths.LINUX_DEFAULT_HOME_PATH + "/elixir"
        private const val LINUX_MINT_HOME_PATH = SdkHomePaths.LINUX_MINT_HOME_PATH + "/elixir"
        private val LOG = Logger.getInstance(Type::class.java)
        private val NIX_PATTERN = SdkHomePaths.nixPattern("elixir")
        private val SDK_HOME_CHILD_BASE_NAME_SET: Set<String> = setOf("lib", "src")
        private const val WINDOWS_32BIT_DEFAULT_HOME_PATH = "C:\\Program Files\\Elixir"
        private const val WINDOWS_64BIT_DEFAULT_HOME_PATH = "C:\\Program Files (x86)\\Elixir"

        @JvmStatic
        internal fun versionStringForHome(sdkHome: String, resolvedVersion: String?): String? {
            val version = ElixirVersionDetector.elixirVersion(sdkHome, resolvedVersion) ?: return null
            val source = SdkPaths.detectSource(sdkHome)
            val canonicalHome = wslCompat.canonicalizePath(sdkHome)
            val otpMajor = runWithEdtGuard("Detecting Elixir SDK...") {
                ElixirBuildInfo.elixirOtpRelease(canonicalHome)
            }
            return buildString {
                if (source != null) {
                    append(source).append(" ")
                }
                append("Elixir ").append(version)
                if (otpMajor != null) {
                    append(" (OTP ").append(otpMajor).append(")")
                }
            }
        }

        @JvmStatic
        internal fun suggestSdkNameForHome(sdkHome: String, resolvedVersion: String?): String {
            val source = SdkPaths.detectSource(sdkHome)
            val version = ElixirVersionDetector.elixirVersion(sdkHome, resolvedVersion)
            val base = buildString {
                if (source != null) {
                    append(source).append(" ")
                }
                append("Elixir ")
                if (version != null) {
                    append(version)
                } else {
                    append("at ").append(sdkHome)
                }
            }
            return org.elixir_lang.sdk.Type.appendWslSuffix(base, sdkHome)
        }

        /**
         * Produces a variant-qualified SDK name that includes the compiled-against OTP major and the
         * full Erlang release version, so two Elixir SDKs with the same base version but different
         * Erlang pairings get distinct names in the SDK table.
         *
         * Example: `"mise Elixir 1.13.4-otp-24 (Erlang 24.3.4.6)"`
         *
         * Falls back to [Type.suggestSdkNameForHome] when [otpMajor] is null (pre-Elixir-1.6 installs
         * where BEAM parsing is unavailable).
         */
        @JvmStatic
        internal fun suggestSdkNameForHome(
            sdkHome: String,
            resolvedVersion: String?,
            otpMajor: String?,
            erlangFullVersion: String?,
        ): String {
            if (otpMajor == null) return suggestSdkNameForHome(sdkHome, resolvedVersion)
            val source = SdkPaths.detectSource(sdkHome)
            val elixirVersion = ElixirVersionDetector.elixirVersion(sdkHome, resolvedVersion)
            val base = buildString {
                if (source != null) {
                    append(source).append(" ")
                }
                append("Elixir ")
                if (elixirVersion != null) {
                    append(elixirVersion).append("-otp-").append(otpMajor)
                } else {
                    append("at ").append(sdkHome)
                }
                if (erlangFullVersion != null) {
                    append(" (Erlang ").append(erlangFullVersion).append(")")
                }
            }
            return org.elixir_lang.sdk.Type.appendWslSuffix(base, sdkHome)
        }

        @JvmStatic
        @RequiresBackgroundThread
        fun canonicalVersion(sdk: Sdk): String? = ElixirVersionDetector.canonicalVersion(sdk)

        @JvmStatic
        internal fun setupSdkTableListener() {
            val messageBus = ApplicationManager.getApplication().messageBus
            messageBus.connect().subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
                override fun jdkRemoved(jdk: Sdk) {
                    // When an Erlang SDK is removed, clean up any Elixir SDKs that reference it
                    if (staticIsValidDependency(jdk)) {
                        cleanupOrphanedElixirSdkReferences(jdk)
                    }

                    // When an Elixir SDK is removed, clean up project references
                    if (jdk.sdkType is Type) {
                        cleanupProjectReferences(jdk)
                    }
                }

                override fun jdkNameChanged(jdk: Sdk, previousName: String) {
                    // When an Erlang SDK is renamed, update any Elixir SDKs that reference the old name
                    if (staticIsValidDependency(jdk)) {
                        updateElixirSdkReferencesAfterRename(jdk, previousName)
                    }
                }
            })
        }

        private fun cleanupProjectReferences(deletedSdk: Sdk) {
            LOG.info("Elixir SDK removed: ${deletedSdk.name}, cleaning up project references")
            com.intellij.openapi.project.ProjectManager.getInstance().openProjects.forEach { project ->
                val projectRootManager = ProjectRootManager.getInstance(project)
                if (projectRootManager.projectSdk == deletedSdk) {
                    WriteActions.runWriteActionLater {
                        projectRootManager.projectSdk = null
                        LOG.info("Cleared removed Elixir SDK '${deletedSdk.name}' from project '${project.name}'")
                    }
                }
            }
        }

        private fun cleanupOrphanedElixirSdkReferences(deletedErlangSdk: Sdk) {
            val deletedName = deletedErlangSdk.name
            LOG.info("Erlang SDK '$deletedName' removed, checking for orphaned Elixir SDK references")

            val projectJdkTable = ProjectJdkTable.getInstance()
            val elixirSdks = projectJdkTable.allJdks.filter { it.sdkType is Type }

            // Use getErlangSdkName() to check stored reference without triggering auto-discovery
            val orphanedElixirSdks = elixirSdks.filter { elixirSdk ->
                val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
                additionalData?.getErlangSdkName() == deletedName
            }

            if (orphanedElixirSdks.isEmpty()) {
                return
            }

            LOG.warn("Erlang SDK '$deletedName' was deleted and is referenced by ${orphanedElixirSdks.size} Elixir SDK(s)")
            orphanedElixirSdks.forEach {
                LOG.warn("Elixir SDK '${it.name}' references deleted Erlang SDK '$deletedName'")
            }

            // Show warning notification instructing user to reconfigure
            ApplicationManager.getApplication().invokeLater {
                val notificationGroup = com.intellij.notification.NotificationGroupManager.getInstance()
                    .getNotificationGroup("Elixir")

                val dependentNames = orphanedElixirSdks.joinToString("<br>") { "  • <b>${it.name}</b>" }
                val message = buildString {
                    append("Erlang SDK '<b>$deletedName</b>' was deleted.<br><br>")
                    append("The following Elixir SDKs reference it and must be reconfigured:<br>")
                    append(dependentNames)
                    append("<br><br>")
                    val settingsTarget = SdkSettingsOpener.getInstance().targetName()
                    append("Please configure the Elixir SDKs in $settingsTarget and select a different Erlang SDK<br>")
                    append("for each of these Elixir SDKs.")
                }

                val notification = notificationGroup.createNotification(
                    "Erlang SDK Deleted",
                    message,
                    com.intellij.notification.NotificationType.WARNING
                )
                notification.addAction(NotificationAction.create("Configure Elixir SDKs") { event, _ ->
                    SdkSettingsOpener.getInstance().open(event)
                })

                notification.notify(null)
            }
        }

        private fun updateElixirSdkReferencesAfterRename(renamedErlangSdk: Sdk, previousName: String) {
            val newName = renamedErlangSdk.name
            LOG.info("Erlang SDK renamed from '$previousName' to '$newName'")

            val projectJdkTable = ProjectJdkTable.getInstance()
            val elixirSdks = projectJdkTable.allJdks.filter { it.sdkType is Type }

            for (elixirSdk in elixirSdks) {
                val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData ?: continue

                // Use getErlangSdkName() to check stored reference
                if (additionalData.getErlangSdkName() != previousName) continue

                LOG.info("Updating Elixir SDK '${elixirSdk.name}' reference from '$previousName' to '$newName'")
                ApplicationManager.getApplication().runWriteAction {
                    ElixirSdkMutation.applyDependencySelection(elixirSdk, renamedErlangSdk)
                }
            }
        }

        private fun getDefaultDocumentationUrl(version: String?): String? =
            if (version == null) null else "https://elixir-lang.org/docs/stable/elixir/"

        @JvmStatic
        fun erlangSdkType(): SdkType =
            if (ProcessOutput.isSmallIde) {
                /* intellij-erlang's "Erlang SDK" does not work in small IDEs because it uses JavadocRoot for documentation,
                   which isn't available in Small IDEs. */
                null
            } else {
                EP_NAME.extensionList.find { sdkType -> sdkType.name == "Erlang SDK" }
            } ?: findInstance(org.elixir_lang.sdk.erlang.Type::class.java)

        @JvmStatic
        val instance: Type
            get() = findInstance(Type::class.java)
    }
}
