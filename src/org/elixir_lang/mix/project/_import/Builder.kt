package org.elixir_lang.mix.project._import

import com.intellij.compiler.CompilerWorkspaceConfiguration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.NewVirtualFile
import com.intellij.packaging.artifacts.ModifiableArtifactModel
import com.intellij.projectImport.ProjectImportBuilder
import org.elixir_lang.configuration.ElixirCompilerSettings
import org.elixir_lang.mix.Icons
import org.elixir_lang.mix.Project.createModulesForOtpApps
import org.elixir_lang.mix.project.OtpApp
import org.elixir_lang.sdk.elixir.Type
import java.io.File
import java.io.IOException
import javax.swing.Icon

/**
 * Created by zyuyou on 15/7/1.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectImportBuilder.java
 */
class Builder : ProjectImportBuilder<OtpApp>() {
    private var myProjectRoot: VirtualFile? = null
    private var myFoundOtpApps = emptyList<OtpApp>()
    private var mySelectedOtpApps = emptyList<OtpApp>()
    private var myIsImportingProject: Boolean = false
    private var myNeedsScan: Boolean = false

    override fun getIcon(): Icon = Icons.PROJECT
    override fun getName(): String = "Mix"
    override fun isSuitableSdkType(sdkType: SdkTypeId): Boolean = sdkType === Type.instance
    override fun getList(): List<OtpApp> {
        // Perform deferred scanning if needed
        if (myNeedsScan && myProjectRoot != null) {
            scanProjectRoot(myProjectRoot!!)
            myNeedsScan = false
        }
        return myFoundOtpApps
    }

    @Throws(ConfigurationException::class)
    override fun setList(selectedOtpApps: List<OtpApp>?) {
        if (selectedOtpApps != null) {
            mySelectedOtpApps = selectedOtpApps
        }
    }

    override fun isMarked(otpApp: OtpApp?): Boolean = otpApp != null && mySelectedOtpApps.contains(otpApp)
    override fun setOpenProjectSettingsAfter(openProjectSettingsAfter: Boolean) {}

    override fun cleanup() {
        myProjectRoot = null
        myFoundOtpApps = emptyList()
        mySelectedOtpApps = emptyList()
        myNeedsScan = false
    }

    /**
     * check for reusing *.iml or *.eml
     *
     */
    override fun validate(currentProject: Project?, project: Project): Boolean {
        if (!findIdeaModuleFiles(mySelectedOtpApps)) {
            return true
        }

        val resultCodeRef = java.util.concurrent.atomic.AtomicInteger(Messages.CANCEL)
        ApplicationManager.getApplication().invokeAndWait {
            resultCodeRef.set(
                Messages.showYesNoCancelDialog(
                    ApplicationInfoEx.getInstanceEx().fullApplicationName + " module files found:\n\n" +
                            StringUtil.join(myFoundOtpApps, { importedOtpApp ->
                                val ideaModuleFile = importedOtpApp.ideaModuleFile
                                if (ideaModuleFile != null) "    " + ideaModuleFile.path + "\n" else ""
                            }, "") + "\nWould you like to reuse them?",
                    "Module Files Found",
                    Messages.getQuestionIcon()
                )
            )
        }
        val resultCode = resultCodeRef.get()

        return when (resultCode) {
            Messages.YES -> true
            Messages.NO -> try {
                ApplicationManager.getApplication().invokeAndWait {
                    deleteIdeaModuleFiles(mySelectedOtpApps)
                }
                true
            } catch (e: IOException) {
                LOG.error(e)
                false
            }

            else -> false
        }
    }

    override fun commit(
        project: Project,
        moduleModel: ModifiableModuleModel?,
        modulesProvider: ModulesProvider,
        artifactModel: ModifiableArtifactModel?
    ): List<Module> {
        fixProjectSdk(project)
        val createModules = createModulesForOtpApps(
            project,
            mySelectedOtpApps,
            { moduleModel ?: ModuleManager.getInstance(project).getModifiableModel() },
            { otpApp, rootModel ->
                val compilerModuleExt = rootModel.getModuleExtension(CompilerModuleExtension::class.java)
                compilerModuleExt.inheritCompilerOutputPath(false)
                val ideaModuleDir = otpApp.root

                val _buildDir = if (myProjectRoot != null && myProjectRoot == ideaModuleDir) {
                    ideaModuleDir
                } else {
                    ideaModuleDir.parent.parent
                }

                compilerModuleExt.setCompilerOutputPath(
                    _buildDir!!.toString() + StringUtil.replace(
                        "/_build/dev/lib/" + otpApp.name + "/ebin",
                        "/",
                        File.separator
                    )
                )
                compilerModuleExt.setCompilerOutputPathForTests(
                    _buildDir.toString() + StringUtil.replace(
                        "/_build/test/lib/" + otpApp.name + "/ebin",
                        "/",
                        File.separator
                    )
                )
                // output paths need to be included, so that they are indexed for Phoenix EEx Template Elixir Line Breakpoints
                compilerModuleExt.isExcludeOutput = false
            }
        )

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

        myProjectRoot = projectRoot
        // Only mark for scan if we don't already have pre-scanned results for this root
        if (myFoundOtpApps.isEmpty()) {
            myNeedsScan = true
        }

        // Return true to indicate the project root was set successfully
        // Actual scanning is deferred until getList() is called
        return true
    }

    /**
     * Set pre-scanned OTP apps to avoid slow I/O on EDT.
     * Called from OpenProcessor.openProjectAsync() which runs in background.
     */
    fun setPreScannedApps(projectRoot: VirtualFile, apps: List<OtpApp>) {
        myProjectRoot = projectRoot
        myFoundOtpApps = apps
        mySelectedOtpApps = apps
        myNeedsScan = false
    }

    private fun scanProjectRoot(projectRoot: VirtualFile) {
        val unitTestMode = ApplicationManager.getApplication().isUnitTestMode

        // Run the scanning in a modal task to avoid EDT slow operations
        val task = object : com.intellij.openapi.progress.Task.Modal(null, "Scanning for Mix Projects", true) {
            override fun run(indicator: com.intellij.openapi.progress.ProgressIndicator) {
                if (!unitTestMode && projectRoot is NewVirtualFile) {
                    projectRoot.refreshAndFindChild("deps")
                }
                myFoundOtpApps = org.elixir_lang.mix.Project.findOtpApps(projectRoot, indicator)
                mySelectedOtpApps = myFoundOtpApps
            }
        }
        com.intellij.openapi.progress.ProgressManager.getInstance().run(task)
    }

    fun setIsImportingProject(isImportingProject: Boolean) {
        myIsImportingProject = isImportingProject
    }

    companion object {
        private val LOG = Logger.getInstance(Builder::class.java)

        private fun fixProjectSdk(project: Project): Sdk? {
            val projectRootMgr = ProjectRootManagerEx.getInstanceEx(project)
            val selectedSdk = projectRootMgr.projectSdk
            val fixedProjectSdk: Sdk?

            if (selectedSdk == null || selectedSdk.sdkType !== Type.instance) {
                fixedProjectSdk = ProjectJdkTable.getInstance().findMostRecentSdkOfType(Type.instance)
                ApplicationManager.getApplication().runWriteAction { projectRootMgr.projectSdk = fixedProjectSdk }
            } else {
                fixedProjectSdk = selectedSdk
            }

            return fixedProjectSdk
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
    }
}
