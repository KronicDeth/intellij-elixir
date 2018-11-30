package org.elixir_lang.mix.project

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.Project.createModulesForOtpApps

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

        createModulesForOtpApps(project, foundOtpApps, {
            ModuleManager.getInstance(project).modifiableModel
        })
    }
}
