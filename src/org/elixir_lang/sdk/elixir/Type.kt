package org.elixir_lang.sdk.elixir

import com.intellij.facet.FacetManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.Version
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiElement
import gnu.trove.THashSet
import org.apache.commons.io.FilenameUtils
import org.elixir_lang.Facet
import org.elixir_lang.Icons
import org.elixir_lang.jps.HomePath
import org.elixir_lang.jps.model.SerializerExtension
import org.elixir_lang.jps.sdk_type.Elixir
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.SdkHomeScan
import org.elixir_lang.sdk.Type.ebinPathChainVirtualFile
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.wsl.wslCompat
import org.jdom.Element
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.Unmodifiable
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.Icon

class Type : org.elixir_lang.sdk.erlang_dependent.Type(SerializerExtension.ELIXIR_SDK_TYPE_ID) {
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

    override fun getDefaultDocumentationUrl(sdk: Sdk): String? = getDefaultDocumentationUrl(getRelease(sdk))

    override fun getHomeChooserDescriptor(): FileChooserDescriptor =
        org.elixir_lang.sdk.Type.createHomeChooserDescriptor(presentableName, ::validateSdkHomePath)

    override fun getIcon(): Icon = Icons.LANGUAGE

    override fun getPresentableName(): String = "Elixir SDK"

    override fun getVersionString(sdkHome: String): String {
        val source = HomePath.detectSource(sdkHome)
        val version = Release.fromString(File(sdkHome).name)?.version() ?: "Unknown"
        return buildString {
            if (source != null) {
                append(source).append(" ")
            }
            append("Elixir ").append(version)
        }
    }

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return Map
     */
    private fun homePathByVersion(path: Path?): Map<Version, String> {
        return SdkHomeScan.homePathByVersion(path, SdkHomeScan.Config(
            toolName = "elixir",
            nixPattern = NIX_PATTERN,
            linuxDefaultPath = LINUX_DEFAULT_HOME_PATH,
            linuxMintPath = LINUX_MINT_HOME_PATH,
            windowsDefaultPath = WINDOWS_64BIT_DEFAULT_HOME_PATH,
            windows32BitPath = WINDOWS_32BIT_DEFAULT_HOME_PATH,
            homebrewTransform = null,  // identity
            nixTransform = null,  // identity
            kerlTransform = null,  // TODO: Investigate if Elixir supports kerl builds
            travisCIKerlTransform = null  // TODO: Verify Travis CI kerl for Elixir
        ))
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
        val elixir = Elixir.getScriptInterpreterExecutable(path)
        val elixirc = Elixir.getByteCodeCompilerExecutable(path)
        val iex = Elixir.getIExExecutable(path)
        val mix = Elixir.mixFile(path)
        return elixir.canExecute() &&
                elixirc.canExecute() &&
                iex.canExecute() &&
                mix.canRead() &&
                HomePath.hasEbinPath(path)
    }

    override fun setupSdkPaths(sdk: Sdk) {
        configureSdkPaths(sdk)
    }


    @Deprecated("Deprecated in Java")
    override fun suggestHomePath(): String? {
        @Suppress("DEPRECATION")
        return suggestHomePaths().firstOrNull()
    }

    override fun suggestHomePath(path: Path): String? {
        return homePathByVersion(path).values.firstOrNull()
    }

    @Deprecated("Deprecated in Java")
    override fun suggestHomePaths(): Collection<String> = homePathByVersion(null).values
    override fun suggestHomePaths(project: Project?): @Unmodifiable Collection<String> =
        homePathByVersion(project?.guessProjectDir()?.toNioPathOrNull()).values

    override fun suggestSdkName(
        currentSdkName: String?,
        sdkHome: String,
    ): String {
        val source = HomePath.detectSource(sdkHome)
        val version = Release.fromString(File(sdkHome).name)?.version()
        val wslInstance = wslCompat.getDistributionByWindowsUncPath(sdkHome)?.msId
        return buildString {
            if (source != null) {
                append(source).append(" ")
            }
            append("Elixir ")
            if (version != null) {
                append(version)
            } else {
                append("at ").append(sdkHome)
            }

            if (wslInstance != null) {
                append(" (WSL: ").append(wslInstance).append(")")
            }
        }
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

    @Suppress("SameParameterValue")
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
        private const val LINUX_DEFAULT_HOME_PATH = HomePath.LINUX_DEFAULT_HOME_PATH + "/elixir"
        private const val LINUX_MINT_HOME_PATH = HomePath.LINUX_MINT_HOME_PATH + "/elixir"
        private val LOG = Logger.getInstance(Type::class.java)
        private val NIX_PATTERN = HomePath.nixPattern("elixir")
        private val SDK_HOME_CHILD_BASE_NAME_SET: Set<String> = THashSet(listOf("lib", "src"))
        private const val WINDOWS_32BIT_DEFAULT_HOME_PATH = "C:\\Program Files\\Elixir"
        private const val WINDOWS_64BIT_DEFAULT_HOME_PATH = "C:\\Program Files (x86)\\Elixir"

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
                    ApplicationManager.getApplication().invokeLater {
                        ApplicationManager.getApplication().runWriteAction {
                            projectRootManager.projectSdk = null
                            LOG.info("Cleared removed Elixir SDK '${deletedSdk.name}' from project '${project.name}'")
                        }
                    }
                }
            }
        }

        private fun cleanupOrphanedElixirSdkReferences(deletedErlangSdk: Sdk) {
            val projectJdkTable = ProjectJdkTable.getInstance()
            val elixirSdks = projectJdkTable.allJdks.filter { it.sdkType is Type }

            for (elixirSdk in elixirSdks) {
                val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
                if (additionalData != null) {
                    val currentErlangSdk = additionalData.getErlangSdk()
                    if (currentErlangSdk?.name == deletedErlangSdk.name) {
                        LOG.info("Clearing orphaned Erlang SDK reference '${deletedErlangSdk.name}' from Elixir SDK '${elixirSdk.name}'")
                        additionalData.setErlangSdk(null)

                        // Try to auto-assign a new Erlang SDK if available
                        val newErlangSdk = additionalData.getErlangSdk() // Will auto-discover
                        if (newErlangSdk != null) {
                            LOG.info("Auto-assigned new Erlang SDK '${newErlangSdk.name}' to Elixir SDK '${elixirSdk.name}'")
                        }
                    }
                }
            }
        }

        private fun updateElixirSdkReferencesAfterRename(renamedErlangSdk: Sdk, previousName: String) {
            LOG.debug("Updating Elixir SDK references from '$previousName' to '${renamedErlangSdk.name}'")
            val projectJdkTable = ProjectJdkTable.getInstance()
            val elixirSdks = projectJdkTable.allJdks.filter { it.sdkType is Type }

            for (elixirSdk in elixirSdks) {
                val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
                if (additionalData != null) {
                    val currentErlangSdk = additionalData.getErlangSdk()
                    if (currentErlangSdk?.name == previousName) {
                        LOG.info("Updating Erlang SDK reference from '${previousName}' to '${renamedErlangSdk.name}' in Elixir SDK '${elixirSdk.name}'")
                        additionalData.setErlangSdk(renamedErlangSdk)
                    }
                }
            }
        }

        private fun releaseVersion(sdkModificator: SdkModificator): String? =
            sdkModificator.versionString?.let { Release.fromString(it) }?.version()

        private fun addDocumentationPath(
            sdkModificator: SdkModificator,
            releaseVersion: String?,
            appName: String,
        ) {
            val hexdocUrlBuilder = StringBuilder("https://hexdoc.pm/").append(appName)
            if (releaseVersion != null) {
                hexdocUrlBuilder.append('/').append(releaseVersion)
            }
            val hexdocUrlVirtualFile = VirtualFileManager.getInstance().findFileByUrl(hexdocUrlBuilder.toString())
            if (hexdocUrlVirtualFile != null) {
                val documentationRootType =
                    org.elixir_lang.sdk.Type
                        .documentationRootType()
                if (documentationRootType != null) {
                    sdkModificator.addRoot(hexdocUrlVirtualFile, documentationRootType)
                }
            }
        }

        private fun addDocumentationPath(
            sdkModificator: SdkModificator,
            releaseVersion: String?,
            ebinPath: Path,
        ) {
            val appName = ebinPath.parent.fileName.toString()
            addDocumentationPath(sdkModificator, releaseVersion, appName)
        }

        private fun addDocumentationPaths(sdkModificator: SdkModificator) {
            val releaseVersion = releaseVersion(sdkModificator)
            HomePath.eachEbinPath(
                sdkModificator.homePath,
            ) { ebinPath: Path -> addDocumentationPath(sdkModificator, releaseVersion, ebinPath) }
        }

        private fun addSourcePaths(sdkModificator: SdkModificator) {
            HomePath.eachEbinPath(
                sdkModificator.homePath,
            ) { ebinPath: Path -> addSourcePath(sdkModificator, ebinPath) }
        }

        private fun addSourcePath(
            sdkModificator: SdkModificator,
            libFile: File,
        ) {
            val sourcePath = VfsUtil.findFileByIoFile(libFile, true)
            if (sourcePath != null) {
                sdkModificator.addRoot(sourcePath, OrderRootType.SOURCES)
            }
        }

        private fun addSourcePath(
            sdkModificator: SdkModificator,
            ebinPath: Path,
        ) {
            val parentPath = ebinPath.parent
            val libPath = Paths.get(parentPath.toString(), "lib")
            val libFile = libPath.toFile()
            if (libFile.exists()) {
                addSourcePath(sdkModificator, libFile)
            }
        }

        private fun configureSdkPaths(sdk: Sdk) {
            LOG.info("Configuring SDK paths for ${sdk.name}")
            val sdkModificator = sdk.sdkModificator

            // Configure base paths
            org.elixir_lang.sdk.Type.addCodePaths(sdkModificator)
            addDocumentationPaths(sdkModificator)
            addSourcePaths(sdkModificator)

            // Configure internal Erlang SDK - this will now create and fully setup the Erlang SDK synchronously
            val erlangSdk = configureInternalErlangSdk(sdk, sdkModificator)

            // Commit changes - check if we're already in a write action to avoid deadlock
            if (ApplicationManager.getApplication().isWriteAccessAllowed) {
                LOG.debug("Committing SDK changes for ${sdk.name} (already in write action)")
                sdkModificator.commitChanges()
            } else {
                runBlockingCancellable {
                    edtWriteAction {
                        LOG.debug("Committing SDK changes for ${sdk.name}")
                        sdkModificator.commitChanges()
                        LOG.debug("Committed SDK changes for ${sdk.name}")
                    }
                }
            }
            LOG.info("SDK paths configured for ${sdk.name} (Erlang SDK: ${erlangSdk?.name ?: "none"})")
        }

        private fun configureInternalErlangSdk(
            elixirSdk: Sdk,
            elixirSdkModificator: SdkModificator,
        ): Sdk? {
            // First, check if an Erlang SDK was explicitly set via UserData
            // This happens when creating a new Elixir SDK right after creating its Erlang SDK
            val explicitErlangSdk = elixirSdk.getUserData(ERLANG_SDK_KEY)

            val erlangSdk = explicitErlangSdk ?: defaultErlangSdk()

            if (erlangSdk != null) {
                val sdkAdditionData: com.intellij.openapi.projectRoots.SdkAdditionalData =
                    SdkAdditionalData(erlangSdk, elixirSdk)
                elixirSdkModificator.sdkAdditionalData = sdkAdditionData

                addNewCodePathsFromInternErlangSdk(elixirSdk, erlangSdk, elixirSdkModificator)

                // Clear the UserData after use
                elixirSdk.putUserData(ERLANG_SDK_KEY, null)
            } else {
                LOG.warn("No Erlang SDK found, Elixir SDK will be incomplete")
            }
            return erlangSdk
        }

        @JvmStatic
        fun addNewCodePathsFromInternErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator,
        ) {
            codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator
            ) { sdkModificator, configuredRoots, expandedInternalRoot, type ->
                if (expandedInternalRoot !in configuredRoots) {
                    sdkModificator.addRoot(expandedInternalRoot, type)
                }
            }
        }

        @JvmStatic
        fun removeCodePathsFromInternalErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator,
        ) {
            codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator
            ) { sdkModificator, _, expandedInternalRoot, type ->
                sdkModificator.removeRoot(expandedInternalRoot, type)
            }
        }

        private fun codePathsFromInternalErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator,
            sdkModificatorRootTypeConsumer: (SdkModificator, Array<VirtualFile>, VirtualFile, OrderRootType) -> Unit,
        ) {
            val internalSdkType = internalErlangSdk.sdkType as SdkType
            val elixirSdkType = elixirSdk.sdkType as SdkType
            for (type in OrderRootType.getAllTypes()) {
                if (internalSdkType.isRootTypeApplicable(type) && elixirSdkType.isRootTypeApplicable(type)) {
                    val internalRoots = internalErlangSdk.sdkModificator.getRoots(type)
                    val configuredRoots = elixirSdkModificator.getRoots(type)
                    for (internalRoot in internalRoots) {
                        for (expandedInternalRoot in expandInternalErlangSdkRoot(internalRoot, type)) {
                            sdkModificatorRootTypeConsumer(
                                elixirSdkModificator,
                                configuredRoots,
                                expandedInternalRoot,
                                type
                            )
                        }
                    }
                }
            }
        }

        private fun expandInternalErlangSdkRoot(
            internalRoot: VirtualFile,
            type: OrderRootType,
        ): Iterable<VirtualFile> {
            val expandedInternalRootList: List<VirtualFile>
            if (type === OrderRootType.CLASSES) {
                val path = internalRoot.path

                /* Erlang SDK from intellij-erlang uses lib/erlang/lib as class path, but intellij-elixir needs the ebin
               directories under lib/erlang/lib/APP-VERSION/ebin that works as a code path used by `-pa` argument to
               `erl.exe`

               For ASDF the path ends in erlang/VERSION/lib, in both cases, going to the parent directory will get the
               right ebin paths from `eachEbinPath` */
                if (path.endsWith("lib")) {
                    expandedInternalRootList = ArrayList()
                    val parentPath = Paths.get(path).parent.toString()
                    val isWslUncPath = wslCompat.isWslUncPath(parentPath)
                    HomePath.eachEbinPath(parentPath) { ebinPath: Path? ->
                        ebinPathChainVirtualFile(
                            ebinPath!!,
                            isWslUncPath
                        ) { virtualFile: VirtualFile? ->
                            virtualFile?.let { expandedInternalRootList.add(it) }
                        }
                    }
                } else {
                    expandedInternalRootList = listOf(internalRoot)
                }
            } else {
                expandedInternalRootList = listOf(internalRoot)
            }
            return expandedInternalRootList
        }

        private fun getDefaultDocumentationUrl(version: Release?): String? =
            if (version == null) null else "http://elixir-lang.org/docs/stable/elixir/"

        private fun defaultErlangSdkHomePath(): String? =
            // Will suggest newest version, unlike `intellij-erlang`
            findInstance(org.elixir_lang.sdk.erlang.Type::class.java).suggestHomePath()

        private fun createDefaultErlangSdk(
            projectJdkTable: ProjectJdkTable,
            erlangSdkType: SdkType,
            homePath: String,
        ): Sdk? {
            // Check version string using homePath directly, not via SDK (which doesn't have path set yet)
            val versionString = erlangSdkType.getVersionString(homePath)
            if (versionString == null) {
                LOG.warn("Cannot create Erlang SDK: getVersionString returned null for $homePath")
                return null
            }

            val sdkName = erlangSdkType.suggestSdkName("Default " + erlangSdkType.name, homePath)
            val projectJdkImpl = ProjectJdkImpl(sdkName, erlangSdkType)
            val modificator = projectJdkImpl.sdkModificator
            modificator.homePath = homePath

            // All SDK modifications require write action
            // Check if we're already in a write action to avoid deadlock
            if (ApplicationManager.getApplication().isWriteAccessAllowed) {
                modificator.commitChanges()
                projectJdkTable.addJdk(projectJdkImpl)
                erlangSdkType.setupSdkPaths(projectJdkImpl)
            } else {
                runBlockingCancellable {
                    edtWriteAction {
                        modificator.commitChanges()
                        projectJdkTable.addJdk(projectJdkImpl)
                        erlangSdkType.setupSdkPaths(projectJdkImpl)
                    }
                }
            }

            return projectJdkImpl
        }

        private fun createDefaultErlangSdk(
            projectJdkTable: ProjectJdkTable,
            erlangSdkType: SdkType,
        ): Sdk? =
            defaultErlangSdkHomePath()?.let { homePath ->
                createDefaultErlangSdk(projectJdkTable, erlangSdkType, homePath)
            }

        @JvmStatic
        fun erlangSdkType(): SdkType =
            if (ProcessOutput.isSmallIde) {
                /* intellij-erlang's "Erlang SDK" does not work in small IDEs because it uses JavadocRoot for documentation,
                   which isn't available in Small IDEs. */
                null
            } else {
                EP_NAME.extensionList.find { sdkType -> sdkType.name == "Erlang SDK" }
            } ?: findInstance(org.elixir_lang.sdk.erlang.Type::class.java)

        private fun defaultErlangSdk(): Sdk? {
            val projectJdkTable = ProjectJdkTable.getInstance()

            // Primary SDK type (external "Erlang SDK" if available, otherwise built-in)
            val erlangSdkType = erlangSdkType()

            val mostRecentErlangSdk = projectJdkTable.findMostRecentSdkOfType(erlangSdkType)

            if (mostRecentErlangSdk != null) {
                return mostRecentErlangSdk
            }

            // If external "Erlang SDK" plugin is installed but no SDK of that type exists,
            // also check for our built-in "Erlang SDK for Elixir SDK" type
            val builtInErlangSdkType = findInstance(org.elixir_lang.sdk.erlang.Type::class.java)
            if (erlangSdkType != builtInErlangSdkType) {
                val builtInErlangSdk = projectJdkTable.findMostRecentSdkOfType(builtInErlangSdkType)
                if (builtInErlangSdk != null) {
                    return builtInErlangSdk
                }
            }

            val createdSdk = createDefaultErlangSdk(projectJdkTable, erlangSdkType)
            return createdSdk
        }

        @JvmStatic
        val instance: Type
            get() = findInstance(Type::class.java)


        /**
         * Checks if the Elixir SDK's classpath contains entries from the Erlang SDK.
         * This can be false when JetBrains settings persistence fails to save the SDK configuration.
         *
         * @return true if Erlang classpath entries are present in the Elixir SDK, false if missing
         */
        @JvmStatic
        fun hasErlangClasspathInElixirSdk(elixirSdk: Sdk, erlangSdk: Sdk): Boolean {
            val erlangHomePath = erlangSdk.homePath ?: return false
            val classRoots = elixirSdk.rootProvider.getFiles(OrderRootType.CLASSES)
            return classRoots.any { root -> root.path.startsWith(erlangHomePath) }
        }

        @JvmStatic
        @Contract("null -> null")
        fun getRelease(sdk: Sdk?): Release? =
            if (sdk != null && sdk.sdkType === instance) {
                Release.fromString(sdk.versionString)
                    ?: sdk.homePath?.let { Release.fromString(File(it).name) }
            } else {
                null
            }

        private fun moduleSdk(module: Module): Sdk? = sdk(ModuleRootManager.getInstance(module).sdk)

        private fun projectSdk(project: Project): Sdk? = sdk(ProjectRootManager.getInstance(project).projectSdk)

        private fun sdk(sdk: Sdk?): Sdk? =
            if (sdk != null && sdk.sdkType === instance) {
                sdk
            } else {
                null
            }

        @JvmStatic
        fun mostSpecificSdk(module: Module): Sdk? =
            FacetManager.getInstance(module).getFacetByType(Facet.ID)?.sdk
                ?: moduleSdk(module)
                ?: mostSpecificSdk(module.project)

        fun mostSpecificSdk(psiElement: PsiElement): Sdk? {
            val project = psiElement.project

            return if (!project.isDisposed) {
                run {
                    // Use a background thread to perform the ReadAction
                    val module =
                        ApplicationManager
                            .getApplication()
                            .executeOnPooledThread<Module?> {
                                ReadAction.compute<Module, Throwable> {
                                    ModuleUtilCore.findModuleForPsiElement(psiElement)
                                }
                            }.get() // Wait for the result

                    if (module != null) {
                        mostSpecificSdk(module)
                    } else {
                        mostSpecificSdk(project)
                    }
                }
            } else {
                null
            }
        }

        @JvmStatic
        fun mostSpecificSdk(project: Project): Sdk? = projectSdk(project)
    }
}
