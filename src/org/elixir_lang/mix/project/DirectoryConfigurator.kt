package org.elixir_lang.mix.project

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil.addFacet
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectAttachProcessor
import org.elixir_lang.Facet
import org.elixir_lang.mix.Project.addFolders
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Used in Small IDEs like Rubymine that don't support [OpenProcessor].
 */
class DirectoryConfigurator : com.intellij.platform.DirectoryProjectConfigurator {
    override fun configureProject(project: Project, baseDir: VirtualFile, moduleRef: Ref<Module>) {
        var foundOtpApps: List<OtpApp> = emptyList()

        ProgressManager.getInstance().run(object : Task.Modal(project, "Scanning Mix Projects", true) {
            override fun run(indicator: com.intellij.openapi.progress.ProgressIndicator) {
                foundOtpApps = org.elixir_lang.mix.Project.findOtpApps(baseDir, indicator)
            }
        })

        for (otpApp in foundOtpApps) {
            if (otpApp.root == baseDir) {
                val module = ModuleManager.getInstance(project).modules[0]

                if (FacetManager.getInstance(module).findFacet(Facet.ID, "Elixir") == null) {
                    addFacet(module, FacetType.findInstance(org.elixir_lang.facet.Type::class.java))

                    ModuleRootModificationUtil.updateModel(module) { modifiableRootModel ->
                        addFolders(modifiableRootModel, baseDir)
                    }
                }
            } else {
                attachToProject(project, Paths.get(otpApp.root.path))
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
