package org.elixir_lang.mix.project

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.coroutineToIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectConfigurator
import com.intellij.projectImport.ProjectAttachProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.Facet
import org.elixir_lang.mix.Project.addFolders
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.mix.sync.SyncRequest
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Used in Small IDEs like Rubymine/Webstorm that don't support [OpenProcessor].
 *
 * Extends [DirectoryProjectConfigurator.AsyncDirectoryProjectConfigurator] because the scan for OTP
 * apps is too slow for the EDT and its result decides everything below it: only [configure] is
 * awaited by `PlatformProjectOpenProcessor.runDirectoryProjectConfigurators`.
 */
class DirectoryConfigurator : DirectoryProjectConfigurator.AsyncDirectoryProjectConfigurator() {
    companion object {
        private val LOG = Logger.getInstance(DirectoryConfigurator::class.java)
    }

    override suspend fun configure(
        project: Project,
        baseDir: VirtualFile,
        moduleRef: Ref<Module>,
        isProjectCreatedWithWizard: Boolean
    ) {
        LOG.debug("configuring $baseDir for project $project, created with wizard: $isProjectCreatedWithWizard")

        // The indicator coroutineToIndicator installs is backed by this coroutine's Job, so
        // cancelling the project open cancels the scan.
        val foundOtpApps = withContext(Dispatchers.IO) {
            coroutineToIndicator { indicator ->
                org.elixir_lang.mix.Project.findOtpApps(baseDir, indicator)
            }
        }

        // If this is an umbrella app, RubyMine currently freezes.
        // Instead, let's just show a notification that the user needs to use the Wizard.
        if (!isProjectCreatedWithWizard && foundOtpApps.size > 1) {
            LOG.info("not configuring project $project because it is an umbrella app")
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "Umbrella App detected",
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
                LOG.debug("Not otp app root: ${otpApp.name}, configuring descendant otp app.")
                configureDescendantOtpApp(project, otpApp)
            }
        }
    }

    private suspend fun configureRootOtpApp(project: Project, otpApp: OtpApp) {
        val module = readAction { ModuleManager.getInstance(project).modules.firstOrNull() }

        if (module == null) {
            LOG.debug("no module to attach the Elixir facet to in project $project")
            return
        }

        // The facet check shares the write action with the mutations it guards, so a second
        // configurator pass cannot add the facet twice.
        val configured = edtWriteAction {
            if (FacetManager.getInstance(module).findFacet(Facet.ID, "Elixir") != null) {
                false
            } else {
                FacetUtil.addFacet(module, FacetType.findInstance(org.elixir_lang.facet.Type::class.java))

                ModuleRootModificationUtil.updateModel(module) { modifiableRootModel ->
                    addFolders(modifiableRootModel, otpApp.root)
                }

                true
            }
        }

        if (configured) {
            project.service<MixDepsSyncService>().enqueue(SyncRequest.All)
        }
    }

    private suspend fun configureDescendantOtpApp(rootProject: Project, otpApp: OtpApp) {
        if (System.getProperty("idea.platform.prefix") == "GoLand" || !ProjectAttachProcessor.canAttachToProject()) {
            return
        }

        LOG.debug("attaching ${otpApp.name} to $rootProject")

        if (attachToProject(rootProject, Paths.get(otpApp.root.path))) {
            LOG.debug("scanning libraries for newly attached OTP app ${otpApp.name}")
            rootProject.service<MixDepsSyncService>().enqueue(SyncRequest.All)
        } else {
            LOG.info("no ProjectAttachProcessor attached ${otpApp.name} to $rootProject")
        }
    }

    /**
     * `attachToProjectAsync` is the only entry point `ModuleAttachProcessor` - the platform's sole
     * [ProjectAttachProcessor] - still implements; the non-suspend `attachToProject` it replaced is
     * left as a base-class stub returning `false`, so calling that attaches nothing.
     *
     * It also creates, configures, saves and disposes the attached app's own project itself, which
     * is why nothing here does that.
     */
    @Suppress("UnstableApiUsage")
    private suspend fun attachToProject(project: Project, baseDir: Path): Boolean =
        ProjectAttachProcessor.EP_NAME.extensionList.any { processor ->
            processor.attachToProjectAsync(project, baseDir, null)
        }
}
