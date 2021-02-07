package org.elixir_lang.mix.project

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil.addFacet
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.components.impl.stores.IComponentStore
import com.intellij.openapi.components.stateStore
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
import com.intellij.platform.ProjectBaseDirectory
import com.intellij.projectImport.ProjectAttachProcessor
import com.intellij.util.PlatformUtils
import com.intellij.util.io.exists
import org.elixir_lang.DepsWatcher
import org.elixir_lang.Facet
import org.elixir_lang.mix.Project.addFolders
import org.elixir_lang.mix.Watcher
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Used in Small IDEs like Rubymine that don't support [OpenProcessor].
 */
class DirectoryConfigurator : com.intellij.platform.DirectoryProjectConfigurator {
    override fun configureProject(project: Project, baseDir: VirtualFile, moduleRef: Ref<Module>) {
        var foundOtpApps: List<OtpApp> = emptyList()

        ProgressManager.getInstance().run(object : Task.Modal(project, "Scanning Mix Projects", true) {
            override fun run(indicator: ProgressIndicator) {
                foundOtpApps = org.elixir_lang.mix.Project.findOtpApps(baseDir, indicator)
            }
        })

        for (otpApp in foundOtpApps) {
            if (otpApp.root == baseDir) {
                configureRootOtpApp(project, otpApp)
            } else {
                configureDescendantOtpApp(project, otpApp)
            }
        }
    }

    private fun configureRootOtpApp(project: Project, otpApp: OtpApp) {
        val module = ModuleManager.getInstance(project).modules[0]

        if (FacetManager.getInstance(module).findFacet(Facet.ID, "Elixir") == null) {
            addFacet(module, FacetType.findInstance(org.elixir_lang.facet.Type::class.java))

            ModuleRootModificationUtil.updateModel(module) { modifiableRootModel ->
                addFolders(modifiableRootModel, otpApp.root)
            }

            ProgressManager.getInstance().run(object : Task.Modal(project, "Scanning dependencies for Libraries", true) {
                override fun run(indicator: ProgressIndicator) {
                    DepsWatcher(project).syncLibraries(indicator)
                }
            })
        }
    }

    private fun configureDescendantOtpApp(rootProject: Project, otpApp: OtpApp) {
        if (!PlatformUtils.isGoIde() && ProjectAttachProcessor.canAttachToProject()) {
            newProject(otpApp)?.let { otpAppProject ->
                attachToProject(rootProject, Paths.get(otpApp.root.path))

                ProgressManager.getInstance().run(object : Task.Modal(otpAppProject, "Scanning mix.exs to connect libraries for newly attached project for OTP app ${otpApp.name}", true) {
                    override fun run(progressIndicator: ProgressIndicator) {
                        for (module in ModuleManager.getInstance(otpAppProject).modules) {
                            if (progressIndicator.isCanceled) {
                                break
                            }

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
    private fun newProject(otpApp: OtpApp): Project? {
        val projectDir = Paths.get(FileUtil.toSystemDependentName(otpApp.root.path), Project.DIRECTORY_STORE_FOLDER)

        return if (projectDir.exists()) {
            null
        } else {
            val projectManager = ProjectManagerEx.getInstanceEx()

            projectManager.newProject(otpApp.name, otpApp.root.path, false, false)?.let { project ->
                ProjectBaseDirectory.getInstance(project).baseDir = otpApp.root
                runDirectoryProjectConfigurators(Paths.get(otpApp.root.path), project, false)

                saveSettings(project)

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

    companion object {
        fun saveSettings(project: Project) {
            try {
                val storeUtil = Class.forName("com.intellij.configurationStore.StoreUtil")
                storeUtil.getDeclaredMethod("saveSettings", ComponentManager::class.java, Boolean::class.javaPrimitiveType).invoke(null, project, true)
            } catch (_: ClassNotFoundException) {
                val storeUtil = Class.forName("com.intellij.openapi.components.impl.stores.StoreUtil")
                storeUtil.getDeclaredMethod("save", IComponentStore::class.java, Project::class.java, Boolean::class.javaPrimitiveType).invoke(null, project.stateStore, project, true)
            }
        }
    }
}
