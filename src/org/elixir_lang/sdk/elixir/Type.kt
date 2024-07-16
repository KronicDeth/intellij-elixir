package org.elixir_lang.sdk.elixir

import com.intellij.facet.FacetManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.Version
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.serviceContainer.AlreadyDisposedException
import com.intellij.util.system.CpuArch
import gnu.trove.THashSet
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.ArrayUtils
import org.elixir_lang.Facet
import org.elixir_lang.Icons
import org.elixir_lang.jps.HomePath
import org.elixir_lang.jps.model.SerializerExtension
import org.elixir_lang.jps.sdk_type.Elixir
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.erlang_dependent.SdkModificatorRootTypeConsumer
import org.jdom.Element
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.swing.Icon
import java.util.concurrent.Callable

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

    override fun getHomeChooserDescriptor(): FileChooserDescriptor {
        val descriptor: FileChooserDescriptor =
            object : FileChooserDescriptor(false, true, false, false, false, false) {
                override fun validateSelectedFiles(files: Array<VirtualFile>) {
                    files.firstOrNull()?.let { validateSdkHomePath(it) }
                }
            }
        descriptor.title = ProjectBundle.message("sdk.configure.home.title", presentableName)
        return descriptor
    }

    override fun getIcon(): Icon = Icons.LANGUAGE

    override fun getPresentableName(): String = "Elixir SDK"

    override fun getVersionString(sdkHome: String): String =
        Release.fromString(File(sdkHome).name)?.version() ?: "Unknown"

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return Map
     */
    private fun homePathByVersion(): Map<Version, String> {
        val homePathByVersion: MutableMap<Version, String> = TreeMap(Comparator.reverseOrder())
        if (SystemInfo.isMac) {
            HomePath.mergeASDF(homePathByVersion, "elixir")
            HomePath.mergeHomebrew(homePathByVersion, "elixir", java.util.function.Function.identity())
            HomePath.mergeNixStore(homePathByVersion, NIX_PATTERN, java.util.function.Function.identity())
        } else {
            val sdkPath: String
            if (SystemInfo.isWindows) {
                sdkPath = if (CpuArch.CURRENT.width == 32) {
                    WINDOWS_32BIT_DEFAULT_HOME_PATH
                } else {
                    WINDOWS_64BIT_DEFAULT_HOME_PATH
                }
                homePathByVersion[HomePath.UNKNOWN_VERSION] = sdkPath
            } else if (SystemInfo.isLinux) {
                putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_DEFAULT_HOME_PATH)
                putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, LINUX_MINT_HOME_PATH)
                HomePath.mergeNixStore(homePathByVersion, NIX_PATTERN, java.util.function.Function.identity())
            }
        }
        return homePathByVersion
    }

    private fun invalidSdkHomeException(virtualFile: VirtualFile): Exception {
        return Exception(invalidSdkHomeMessage(virtualFile))
    }

    private fun invalidSdkHomeMessage(virtualFile: VirtualFile): String = if (virtualFile.isDirectory) {
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
        return elixir.canExecute() && elixirc.canExecute() && iex.canExecute() && mix.canRead() &&
                HomePath.hasEbinPath(path)
    }

    override fun setupSdkPaths(sdk: Sdk) {
        configureSdkPaths(sdk)
    }

    override fun suggestHomePath(): String? {
        val iterator = suggestHomePaths().iterator()
        var suggestedHomePath: String? = null
        if (iterator.hasNext()) {
            suggestedHomePath = iterator.next()
        }
        return suggestedHomePath
    }

    override fun suggestHomePaths(): Collection<String> {
        return homePathByVersion().values
    }

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String =
        Release.fromString(File(sdkHome).name)?.toString() ?: "Elixir at $sdkHome"

    private fun validateSdkHomePath(virtualFile: VirtualFile) {
        val selectedPath = virtualFile.path
        var valid = isValidSdkHome(selectedPath)
        if (!valid) {
            valid = isValidSdkHome(adjustSelectedSdkHome(selectedPath))
            if (!valid) {
                throw invalidSdkHomeException(virtualFile)
            }
        }
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): com.intellij.openapi.projectRoots.AdditionalDataConfigurable {
        return AdditionalDataConfigurable(sdkModel, sdkModificator)
    }

    override fun saveAdditionalData(
        additionalData: com.intellij.openapi.projectRoots.SdkAdditionalData,
        additional: Element
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
        additional: Element
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

        private fun releaseVersion(sdkModificator: SdkModificator): String? =
            sdkModificator.versionString?.let { Release.fromString(it) }?.version()

        private fun addDocumentationPath(
            sdkModificator: SdkModificator,
            releaseVersion: String?,
            appName: String
        ) {
            val hexdocUrlBuilder = StringBuilder("https://hexdoc.pm/").append(appName)
            if (releaseVersion != null) {
                hexdocUrlBuilder.append('/').append(releaseVersion)
            }
            val hexdocUrlVirtualFile = VirtualFileManager.getInstance().findFileByUrl(hexdocUrlBuilder.toString())
            if (hexdocUrlVirtualFile != null) {
                val documentationRootType = org.elixir_lang.sdk.Type.documentationRootType()
                if (documentationRootType != null) {
                    sdkModificator.addRoot(hexdocUrlVirtualFile, documentationRootType)
                }
            }
        }

        private fun addDocumentationPath(
            sdkModificator: SdkModificator,
            releaseVersion: String?,
            ebinPath: Path
        ) {
            val appName = ebinPath.parent.fileName.toString()
            addDocumentationPath(sdkModificator, releaseVersion, appName)
        }

        private fun addDocumentationPaths(sdkModificator: SdkModificator) {
            val releaseVersion = releaseVersion(sdkModificator)
            HomePath.eachEbinPath(
                sdkModificator.homePath
            ) { ebinPath: Path -> addDocumentationPath(sdkModificator, releaseVersion, ebinPath) }
        }

        private fun addSourcePaths(sdkModificator: SdkModificator) {
            HomePath.eachEbinPath(
                sdkModificator.homePath
            ) { ebinPath: Path -> addSourcePath(sdkModificator, ebinPath) }
        }

        private fun addSourcePath(sdkModificator: SdkModificator, libFile: File) {
            val sourcePath = VfsUtil.findFileByIoFile(libFile, true)
            if (sourcePath != null) {
                sdkModificator.addRoot(sourcePath, OrderRootType.SOURCES)
            }
        }

        private fun addSourcePath(sdkModificator: SdkModificator, ebinPath: Path) {
            val parentPath = ebinPath.parent
            val libPath = Paths.get(parentPath.toString(), "lib")
            val libFile = libPath.toFile()
            if (libFile.exists()) {
                addSourcePath(sdkModificator, libFile)
            }
        }

        private fun configureSdkPaths(sdk: Sdk) {
            val sdkModificator = sdk.sdkModificator
            org.elixir_lang.sdk.Type.addCodePaths(sdkModificator)
            addDocumentationPaths(sdkModificator)
            addSourcePaths(sdkModificator)
            configureInternalErlangSdk(sdk, sdkModificator)
            ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() }
        }

        private fun configureInternalErlangSdk(elixirSdk: Sdk, elixirSdkModificator: SdkModificator): Sdk? {
            val erlangSdk = defaultErlangSdk()
            if (erlangSdk != null) {
                val sdkAdditionData: com.intellij.openapi.projectRoots.SdkAdditionalData =
                    SdkAdditionalData(erlangSdk, elixirSdk)
                elixirSdkModificator.sdkAdditionalData = sdkAdditionData
                addNewCodePathsFromInternErlangSdk(elixirSdk, erlangSdk, elixirSdkModificator)
            }
            return erlangSdk
        }

        @JvmStatic
        fun addNewCodePathsFromInternErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator
        ) {
            codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator,
                SdkModificatorRootTypeConsumer { sdkModificator: SdkModificator, configuredRoots: Array<out @NotNull VirtualFile>, expandedInternalRoot: VirtualFile, type: OrderRootType ->
                    if (!ArrayUtils.contains(configuredRoots, expandedInternalRoot)) {
                        sdkModificator.addRoot(expandedInternalRoot, type)
                    }
                }
            )
        }

        @JvmStatic
        fun removeCodePathsFromInternalErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator
        ) {
            codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator,
                SdkModificatorRootTypeConsumer { sdkModificator: SdkModificator, _: Array<out @NotNull VirtualFile>, expandedInternalRoot: VirtualFile, type: OrderRootType ->
                    sdkModificator.removeRoot(
                        expandedInternalRoot,
                        type
                    )
                }
            )
        }

        private fun codePathsFromInternalErlangSdk(
            elixirSdk: Sdk,
            internalErlangSdk: Sdk,
            elixirSdkModificator: SdkModificator,
            sdkModificatorRootTypeConsumer: SdkModificatorRootTypeConsumer
        ) {
            val internalSdkType = internalErlangSdk.sdkType as SdkType
            val elixirSdkType = elixirSdk.sdkType as SdkType
            for (type in OrderRootType.getAllTypes()) {
                if (internalSdkType.isRootTypeApplicable(type) && elixirSdkType.isRootTypeApplicable(type)) {
                    val internalRoots = internalErlangSdk.sdkModificator.getRoots(type)
                    val configuredRoots = elixirSdkModificator.getRoots(type)
                    for (internalRoot in internalRoots) {
                        for (expandedInternalRoot in expandInternalErlangSdkRoot(internalRoot, type)) {
                            sdkModificatorRootTypeConsumer
                                .consume(elixirSdkModificator, configuredRoots, expandedInternalRoot, type)
                        }
                    }
                }
            }
        }

        private fun expandInternalErlangSdkRoot(
            internalRoot: VirtualFile,
            type: OrderRootType
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
                    HomePath.eachEbinPath(parentPath) { ebinPath: Path? ->
                        org.elixir_lang.sdk.Type.ebinPathChainVirtualFile(
                            ebinPath!!
                        ) { e: VirtualFile -> expandedInternalRootList.add(e) }
                    }
                } else {
                    expandedInternalRootList = listOf(internalRoot)
                }
            } else {
                expandedInternalRootList = listOf(internalRoot)
            }
            return expandedInternalRootList
        }

        @JvmStatic
        @TestOnly
        fun createMockSdk(sdkHome: String, release: Release): Sdk {
            val sdk: Sdk = ProjectJdkImpl(release.toString(), instance)
            val sdkModificator = sdk.sdkModificator
            sdkModificator.homePath = sdkHome
            sdkModificator.versionString =
                getVersionString(release) // must be set after home path, otherwise setting home path clears the version string
            ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() }
            configureSdkPaths(sdk)
            return sdk
        }

        private fun getDefaultDocumentationUrl(version: Release?): String? =
            if (version == null) null else "http://elixir-lang.org/docs/stable/elixir/"

        private fun defaultErlangSdkHomePath(): String? =
            // Will suggest newest version, unlike `intellij-erlang`
            findInstance(org.elixir_lang.sdk.erlang.Type::class.java).suggestHomePath()

        private fun createDefaultErlangSdk(
            projectJdkTable: ProjectJdkTable,
            erlangSdkType: SdkType,
            homePath: String
        ): Sdk? {
            val sdkName = erlangSdkType.suggestSdkName("Default " + erlangSdkType.name, homePath)
            val projectJdkImpl = ProjectJdkImpl(sdkName, erlangSdkType)
            projectJdkImpl.homePath = homePath
            erlangSdkType.setupSdkPaths(projectJdkImpl)

            return if (projectJdkImpl.versionString != null) {
                ApplicationManager.getApplication().invokeAndWait(
                    {
                        ApplicationManager.getApplication().runWriteAction { projectJdkTable.addJdk(projectJdkImpl) }
                    },
                    ModalityState.NON_MODAL
                )
                projectJdkImpl
            } else {
                null
            }
        }

        private fun createDefaultErlangSdk(
            projectJdkTable: ProjectJdkTable,
            erlangSdkType: SdkType
        ): Sdk? =
            defaultErlangSdkHomePath()?.let { homePath ->
                createDefaultErlangSdk(projectJdkTable, erlangSdkType, homePath)
            }

        @JvmStatic
        fun erlangSdkType(): SdkType = if (ProcessOutput.isSmallIde()) {
            /* intellij-erlang's "Erlang SDK" does not work in small IDEs because it uses JavadocRoot for documentation,
               which isn't available in Small IDEs. */
            null
        } else {
            EP_NAME.extensionList.find { sdkType -> sdkType.name == "Erlang SDK" }
        } ?: findInstance(org.elixir_lang.sdk.erlang.Type::class.java)

        private fun defaultErlangSdk(): Sdk? {
            val projectJdkTable = ProjectJdkTable.getInstance()
            val erlangSdkType = erlangSdkType()
            val mostRecentErlangSdk = projectJdkTable.findMostRecentSdkOfType(erlangSdkType)
            return mostRecentErlangSdk
                ?: createDefaultErlangSdk(projectJdkTable, erlangSdkType)
        }

        @JvmStatic
        val instance: Type
            get() = findInstance(Type::class.java)

        fun getNonNullRelease(element: PsiElement): Release = getRelease(element) ?: Release.LATEST

        private fun getRelease(element: PsiElement): Release? = getRelease(mostSpecificSdk(element))

        @JvmStatic
        @Contract("null -> null")
        fun getRelease(sdk: Sdk?): Release? = if (sdk != null && sdk.sdkType === instance) {
            Release.fromString(sdk.versionString)
                ?: sdk.homePath?.let { Release.fromString(File(it).name) }
        } else {
            null
        }

        private fun getVersionString(version: Release?): String? = version?.toString()

        private fun putIfDirectory(
            homePathByVersion: MutableMap<Version, String>,
            @Suppress("SameParameterValue") version: Version,
            homePath: String
        ) {
            val homeFile = File(homePath)
            if (homeFile.isDirectory) {
                homePathByVersion[version] = homePath
            }
        }

        private fun moduleSdk(module: Module): Sdk? = sdk(ModuleRootManager.getInstance(module).sdk)

        private fun projectSdk(project: Project): Sdk? = sdk(ProjectRootManager.getInstance(project).projectSdk)

        private fun sdk(sdk: Sdk?): Sdk? = if (sdk != null && sdk.sdkType === instance) {
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
                /* ModuleUtilCore.findModuleForPsiElement can fail with NullPointerException if the
                    ProjectFileIndex.SERVICE.getInstance(Project) returns {@code null}, so check that the
                    ProjectFileIndex is available first
                */
                if (ProjectFileIndex.SERVICE.getInstance(project) != null) {
                    ApplicationManager.getApplication().executeOnPooledThread(Callable {
                        ReadAction.compute<Sdk?, Throwable> {
                            try {
                                val module = ModuleUtilCore.findModuleForPsiElement(psiElement)
                                if (module != null) {
                                    mostSpecificSdk(module)
                                } else {
                                    mostSpecificSdk(project)
                                }
                            } catch (_: AlreadyDisposedException) {
                                null
                            }
                        }
                    }).get()
                } else {
                    mostSpecificSdk(project)
                }
            } else {
                null
            }
        }

        @JvmStatic
        fun mostSpecificSdk(project: Project): Sdk? = projectSdk(project)
    }
}
