package org.elixir_lang.mix.project._import

import com.intellij.compiler.CompilerWorkspaceConfiguration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl
import com.intellij.packaging.artifacts.ModifiableArtifactModel
import com.intellij.projectImport.ProjectImportBuilder
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.configuration.ElixirCompilerSettings
import org.elixir_lang.mix.Icons
import org.elixir_lang.module.ElixirModuleType
import org.elixir_lang.sdk.elixir.Type

import javax.swing.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by zyuyou on 15/7/1.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectImportBuilder.java
 */
class Builder : ProjectImportBuilder<OtpApp>() {

    private var myProjectRoot: VirtualFile? = null
    private var myFoundOtpApps = emptyList<OtpApp>()
    private var mySelectedOtpApps = emptyList<OtpApp>()
    private var myIsImportingProject: Boolean = false


    override fun getName(): String {
        return "Mix"
    }

    override fun getIcon(): Icon {
        return Icons.PROJECT
    }

    override fun isSuitableSdkType(sdkType: SdkTypeId): Boolean {
        return sdkType === Type.getInstance()
    }

    override fun getList(): List<OtpApp> {
        return ArrayList(myFoundOtpApps)
    }

    @Throws(ConfigurationException::class)
    override fun setList(selectedOtpApps: List<OtpApp>?) {
        if (selectedOtpApps != null) {
            mySelectedOtpApps = selectedOtpApps
        }
    }

    override fun isMarked(otpApp: OtpApp?): Boolean {
        return otpApp != null && mySelectedOtpApps.contains(otpApp)
    }

    override fun setOpenProjectSettingsAfter(openProjectSettingsAfter: Boolean) {}

    override fun cleanup() {
        myProjectRoot = null
        myFoundOtpApps = emptyList()
        mySelectedOtpApps = emptyList()
    }

    /**
     * check for reusing *.iml or *.eml
     *
     */
    override fun validate(current: Project?, dest: Project): Boolean {
        if (!findIdeaModuleFiles(mySelectedOtpApps)) {
            return true
        }

        val resultCode = Messages.showYesNoCancelDialog(ApplicationInfoEx.getInstanceEx().fullApplicationName + " module files found:\n\n" +
                StringUtil.join(myFoundOtpApps, { importedOtpApp ->
                    val ideaModuleFile = importedOtpApp.ideaModuleFile
                    if (ideaModuleFile != null) "    " + ideaModuleFile.path + "\n" else ""
                }, "") + "\nWould you like to reuse them?", "Module files found", Messages.getQuestionIcon())

        return if (resultCode == Messages.YES) {
            true
        } else if (resultCode == Messages.NO) {
            try {
                deleteIdeaModuleFiles(mySelectedOtpApps)
                true
            } catch (e: IOException) {
                LOG.error(e)
                false
            }

        } else {
            false
        }
    }

    override fun commit(project: Project,
               moduleModel: ModifiableModuleModel?,
               modulesProvider: ModulesProvider,
               artifactModel: ModifiableArtifactModel?): List<Module> {

        val selectedAppNames = ContainerUtil.newHashSet<String>()

        for (importedOtpApp in mySelectedOtpApps) {
            selectedAppNames.add(importedOtpApp.name)
        }

        val projectSdk = fixProjectSdk(project)
        val createModules = ArrayList<Module>()
        val createdRootModels = ArrayList<ModifiableRootModel>()
        val obtainedModuleModel = moduleModel ?: ModuleManager.getInstance(project).modifiableModel

        var _buildDir: VirtualFile? = null
        for (importedOtpApp in mySelectedOtpApps) {
            // add Module
            val ideaModuleDir = importedOtpApp.root
            val ideaModuleFile = ideaModuleDir.canonicalPath + File.separator + importedOtpApp.name + ".iml"
            val module = obtainedModuleModel.newModule(ideaModuleFile, ElixirModuleType.getInstance().id)
            createModules.add(module)

            // add rootModule
            importedOtpApp.module = module
            if (importedOtpApp.ideaModuleFile == null) {
                val rootModel = ModuleRootManager.getInstance(module).modifiableModel

                // Make it inherit SDK from the project.
                rootModel.inheritSdk()

                // Initialize source and test paths.
                val content = rootModel.addContentEntry(importedOtpApp.root)
                addSourceDirToContent(content, ideaModuleDir, "lib", false)
                addSourceDirToContent(content, ideaModuleDir, "test", true)

                // Exclude standard folders
                excludeDirFromContent(content, ideaModuleDir, "deps")

                // Initialize output paths according to mix conventions.
                val compilerModuleExt = rootModel.getModuleExtension(CompilerModuleExtension::class.java)
                compilerModuleExt.inheritCompilerOutputPath(false)

                _buildDir = if (myProjectRoot != null && myProjectRoot == ideaModuleDir) ideaModuleDir else ideaModuleDir.parent.parent
                compilerModuleExt.setCompilerOutputPath(_buildDir!!.toString() + StringUtil.replace("/_build/dev/lib/" + importedOtpApp.name + "/ebin", "/", File.separator))
                compilerModuleExt.setCompilerOutputPathForTests(_buildDir.toString() + StringUtil.replace("/_build/test/lib/" + importedOtpApp.name + "/ebin", "/", File.separator))
                // output paths need to be included, so that they are indexed for Phoenix EEx Template Elixir Line Breakpoints
                compilerModuleExt.isExcludeOutput = false

                createdRootModels.add(rootModel)

                // Set inter-module dependencies
                resolveModuleDeps(rootModel, importedOtpApp, projectSdk, selectedAppNames)
            }
        }

        // Commit project structure.
        LOG.info("Commit project structure")
        ApplicationManager.getApplication().runWriteAction {
            for (rootModel in createdRootModels) {
                rootModel.commit()
            }
            obtainedModuleModel.commit()
        }

        if (myIsImportingProject) {
            ElixirCompilerSettings.getInstance(project).isUseMixCompilerEnabled = true
        }
        CompilerWorkspaceConfiguration.getInstance(project).CLEAR_OUTPUT_DIRECTORY = false

        return createModules
    }

    fun setProjectRoot(projectRoot: VirtualFile): Boolean {
        if (projectRoot == myProjectRoot) {
            return true
        }

        val unitTestMode = ApplicationManager.getApplication().isUnitTestMode

        myProjectRoot = projectRoot
        if (!unitTestMode && projectRoot is VirtualDirectoryImpl) {
            projectRoot.refreshAndFindChild("deps")
        }

        ProgressManager.getInstance().run(object : Task.Modal(ProjectImportBuilder.getCurrentProject(), "Scanning Mix Projects", true) {
            override fun run(indicator: ProgressIndicator) {
                val mixExsFiles = findMixExs(myProjectRoot!!, indicator)
                val importedOtpApps = mutableSetOf<OtpApp>()

                VfsUtilCore.visitChildrenRecursively(projectRoot, object : VirtualFileVisitor<Object>() {
                    override fun visitFile(file: VirtualFile): Boolean {
                        indicator.checkCanceled()

                        if (file.isDirectory) {
                            indicator.text2 = file.path
                            if (isBuildOrConfigOrDepsOrTestsDirectory(projectRoot.path, file.path)) return false
                        }

                        createImportedOtpApp(file)?.let { importedOtpApps.add(it) }

                        return true
                    }
                })

                myFoundOtpApps = ContainerUtil.newArrayList(importedOtpApps)
            }
        })

        Collections.sort(myFoundOtpApps) { o1, o2 ->
            val nameCompareResult = String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name)
            if (nameCompareResult == 0) {
                String.CASE_INSENSITIVE_ORDER.compare(o1.root.path, o1.root.path)
            } else nameCompareResult
        }

        mySelectedOtpApps = myFoundOtpApps

        return !myFoundOtpApps.isEmpty()
    }

    fun setIsImportingProject(isImportingProject: Boolean) {
        myIsImportingProject = isImportingProject
    }

    private fun findMixExs(root: VirtualFile, indicator: ProgressIndicator): List<VirtualFile> {
        // synchronous and recursive
        root.refresh(false, true)

        val foundMixExs = ArrayList<VirtualFile>()
        VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Object>() {
            override fun visitFile(file: VirtualFile): Boolean {
                indicator.checkCanceled()
                if (file.isDirectory) {
                    if (isBuildOrConfigOrDepsOrTestsDirectory(root.path, file.path)) return false
                    indicator.text2 = file.path
                } else if (file.name.equals("mix.exs", ignoreCase = true)) {
                    foundMixExs.add(file)
                }
                return true
            }
        })

        return foundMixExs
    }

    companion object {
        private val LOG = Logger.getInstance(Builder::class.java)

        /**
         * private methos
         */
        private fun isBuildOrConfigOrDepsOrTestsDirectory(projectRootPath: String, path: String): Boolean {
            return ("$projectRootPath/_build" == path
                    || "$projectRootPath/config" == path
                    || "$projectRootPath/deps" == path
                    || "$projectRootPath/tests" == path)
        }

        private fun fixProjectSdk(project: Project): Sdk? {
            val projectRootMgr = ProjectRootManagerEx.getInstanceEx(project)
            val selectedSdk = projectRootMgr.projectSdk
            val fixedProjectSdk: Sdk?

            if (selectedSdk == null || selectedSdk.sdkType !== Type.getInstance()) {
                fixedProjectSdk = ProjectJdkTable.getInstance().findMostRecentSdkOfType(Type.getInstance())
                ApplicationManager.getApplication().runWriteAction { projectRootMgr.projectSdk = fixedProjectSdk }
            } else {
                fixedProjectSdk = selectedSdk
            }

            return fixedProjectSdk
        }

        private fun addSourceDirToContent(content: ContentEntry,
                                          root: VirtualFile,
                                          sourceDir: String,
                                          test: Boolean) {
            val sourceDirFile = root.findChild(sourceDir)
            if (sourceDirFile != null) {
                content.addSourceFolder(sourceDirFile, test)
            }
        }

        private fun excludeDirFromContent(content: ContentEntry, root: VirtualFile, excludedDir: String) {
            content.addExcludeFolder("${root.url}/$excludedDir")
        }

        private fun createImportedOtpApp(appRoot: VirtualFile): OtpApp? =
            appRoot.findChild("mix.exs")?.let {
                OtpApp(appRoot, it)
            }

        @Throws(IOException::class)
        private fun deleteIdeaModuleFiles(otpApps: List<OtpApp>) {
            val ex = arrayOfNulls<IOException>(1)

            ApplicationManager.getApplication().runWriteAction(object : Runnable {
                override fun run() {
                    for (importedOtpApp in otpApps) {
                        val ideaModuleFile = importedOtpApp.ideaModuleFile
                        if (ideaModuleFile != null) {
                            try {
                                ideaModuleFile.delete(this)
                                importedOtpApp.ideaModuleFile = null
                            } catch (e: IOException) {
                                ex[0] = e
                            }

                        }
                    }
                }
            })

            ex[0]?.let { ioException ->
                throw ioException
            }
        }

        private fun findIdeaModuleFiles(otpApps: List<OtpApp>): Boolean {
            var ideaModuleFileExists = false
            for (importedOtpApp in otpApps) {
                val applicationRoot = importedOtpApp.root
                val ideaModuleName = importedOtpApp.name
                val imlFile = applicationRoot.findChild("$ideaModuleName.iml")
                if (imlFile != null) {
                    ideaModuleFileExists = true
                    importedOtpApp.ideaModuleFile = imlFile
                } else {
                    val emlFile = applicationRoot.findChild("$ideaModuleName.eml")
                    if (emlFile != null) {
                        ideaModuleFileExists = true
                        importedOtpApp.ideaModuleFile = emlFile
                    }
                }
            }

            return ideaModuleFileExists
        }

        private fun resolveModuleDeps(rootModel: ModifiableRootModel,
                                      otpApp: OtpApp,
                                      projectSdk: Sdk?,
                                      allImportedAppNames: Set<String>): Set<String> {
            val unresolvedAppNames = ContainerUtil.newHashSet<String>()
            for (depAppName in otpApp.deps) {
                if (allImportedAppNames.contains(depAppName)) {
                    rootModel.addInvalidModuleEntry(depAppName)
                } else if (projectSdk != null && isSdkOtpApp(depAppName, projectSdk)) {
                    // Sdk is already a dependency
                    LOG.info("Sdk otp-app:[$depAppName] is already a dependy.")
                } else {
                    rootModel.addInvalidModuleEntry(depAppName)
                    unresolvedAppNames.add(depAppName)
                }
            }
            return unresolvedAppNames
        }

        private fun isSdkOtpApp(otpAppName: String, sdk: Sdk): Boolean {
            for (sdkLibDir in sdk.rootProvider.getFiles(OrderRootType.SOURCES)) {
                for (child in sdkLibDir.children) {
                    if (child.isDirectory && child.name == otpAppName) {
                        return true
                    }
                }
            }
            return false
        }
    }
}
