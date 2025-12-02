package org.elixir_lang.mix.project

import com.intellij.configurationStore.StoreUtil
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.PlatformProjectOpenProcessor.Companion.runDirectoryProjectConfigurators
import com.intellij.projectImport.ProjectAttachProcessor
import com.intellij.util.PlatformUtils
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.elixir_lang.DepsWatcher
import org.elixir_lang.Facet
import org.elixir_lang.mix.Project.addFolders
import org.elixir_lang.mix.Watcher
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

/**
 * Used in Small IDEs like Rubymine/Webstorm that don't support [OpenProcessor].
 */
class DirectoryConfigurator : com.intellij.platform.DirectoryProjectConfigurator {
    companion object {
        private val LOG = Logger.getInstance(DirectoryConfigurator::class.java)
    }

    override fun configureProject(
        project: Project,
        baseDir: VirtualFile,
        moduleRef: Ref<Module>,
        isProjectCreatedWithWizard: Boolean
    ) {
        var foundOtpApps: List<OtpApp> = emptyList()
        LOG.debug("configuring $baseDir for project $project, created with wizard: $isProjectCreatedWithWizard")

        ProgressManager.getInstance().run(object : Task.Modal(project, "Scanning Mix Projects", true) {
            override fun run(indicator: ProgressIndicator) {
                foundOtpApps = org.elixir_lang.mix.Project.findOtpApps(baseDir, indicator)
            }
        })

        // If this is an umbrella app, RubyMine currently freezes.
        // Instead, let's just show a notification that the user needs to use the Wizard.
        if (!isProjectCreatedWithWizard && foundOtpApps.isNotEmpty() && foundOtpApps.size > 1) {
            LOG.info("not configuring project $project because it is an umbrella app")
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "Umbrella App Detected",
                    "Elixir Umbrella app detected, please use the Project Wizard to properly configure it when using an IDE like RubyMine.",
                    NotificationType.WARNING
                )
                .notify(project)

            return
        }

        for (otpApp in foundOtpApps) {
            LOG.debug("configuring descendant otp app: ${otpApp.name}")
            if (otpApp.root == baseDir) {
                LOG.debug("configuring root otp app: ${otpApp.name}")
                configureRootOtpApp(project, otpApp)
            } else {
                runBlocking {
                    try {
                        withTimeout(2000L) {
                            LOG.debug("Not otp app root: ${otpApp.name}, configuring descendant otp app.")
                            configureDescendantOtpApp(project, otpApp)
                        }
                    } catch (e: TimeoutCancellationException) {
                        // Handle the timeout exception, e.g., log a warning or notify the user
                        LOG.error("Timeout while configuring descendant OTP app: ${otpApp.name}", e)
                    }
                }
            }
        }
    }

    private fun configureRootOtpApp(project: Project, otpApp: OtpApp) {
        val module = ModuleManager.getInstance(project).modules[0]

        if (FacetManager.getInstance(module).findFacet(Facet.ID, "Elixir") == null) {
            FacetUtil.addFacet(module, FacetType.findInstance(org.elixir_lang.facet.Type::class.java))

            ModuleRootModificationUtil.updateModel(module) { modifiableRootModel ->
                addFolders(modifiableRootModel, otpApp.root)
            }

            ProgressManager.getInstance()
                .run(object : Task.Modal(project, "Scanning dependencies for Libraries", true) {
                    override fun run(indicator: ProgressIndicator) {
                        DepsWatcher(project).syncLibraries(indicator)
                    }
                })
        }
    }

    private suspend fun configureDescendantOtpApp(rootProject: Project, otpApp: OtpApp) {
        if (!PlatformUtils.isGoIde() && ProjectAttachProcessor.canAttachToProject()) {
            newProject(otpApp)?.let { otpAppProject ->
                LOG.debug("attaching $otpAppProject to $rootProject")
                attachToProject(rootProject, Paths.get(otpApp.root.path))

                LOG.debug("scanning libraries for newly attached project for OTP app ${otpApp.name}")
                ProgressManager.getInstance().run(object : Task.Modal(
                    otpAppProject,
                    "Scanning mix.exs to connect libraries for newly attached project for OTP app ${otpApp.name}",
                    true
                ) {
                    override fun run(progressIndicator: ProgressIndicator) {
                        for (module in ModuleManager.getInstance(otpAppProject).modules) {
                            if (progressIndicator.isCanceled) {
                                LOG.debug("canceled scanning libraries for newly attached project for OTP app ${otpApp.name}")
                                break
                            }
                            LOG.debug("scanning libraries for newly attached project for OTP app ${otpApp.name} for module ${module.name}")

                            Watcher(otpAppProject).syncLibraries(module, progressIndicator)
                        }
                    }
                })
            }
        }
    }

    /**
     * @return Only returns a project if it is new.
     */
    private suspend fun newProject(otpApp: OtpApp): Project? {
        val projectDir = Paths.get(FileUtil.toSystemDependentName(otpApp.root.path), Project.DIRECTORY_STORE_FOLDER)
        LOG.debug("Checking if $projectDir exists")

        return if (projectDir.exists()) {
            LOG.debug("$projectDir already exists")
            null
        } else {
            val path = otpApp.root.path.let { Paths.get(it) }
            val openProjectTask = OpenProjectTask {
                isNewProject = true
                useDefaultProjectAsTemplate = false
                projectName = otpApp.name
            }

            LOG.debug("Creating new project at $path with isNewProject: ${openProjectTask.isNewProject} and useDefaultProjectAsTemplate: ${openProjectTask.useDefaultProjectAsTemplate} and projectName: ${openProjectTask.projectName}")

            ProjectManagerEx
                .getInstanceEx()
                .newProject(
                    path,
                    openProjectTask
                )
                ?.let { project ->
                    LOG.debug("runDirectoryProjectConfigurators for project: $project at $path")
                    runDirectoryProjectConfigurators(path, project, false)
                    LOG.debug("runDirectoryProjectConfigurators complete for project: $project at $path")

                    LOG.debug("Saving settings for project: $project at $path")

                    StoreUtil.saveSettings(project, true)
                    LOG.debug("Saved settings for project: $project at $path")

                    project
                }
        }
    }

    private fun attachToProject(project: Project, baseDir: Path) {
        for (processor in ProjectAttachProcessor.EP_NAME.extensionList) {
            if (processor.attachToProject(project, baseDir, null)) {
                break
            }
        }
    }
}
