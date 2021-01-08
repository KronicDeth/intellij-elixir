package org.elixir_lang.mix

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import org.elixir_lang.DepsWatcher
import org.elixir_lang.mix.project.OtpApp
import org.elixir_lang.module.ElixirModuleType
import java.io.File
import java.util.Comparator

object Project {
    fun addSourceDirToContent(content: ContentEntry,
                              root: VirtualFile,
                              sourceDir: String,
                              test: Boolean) {
        content.addSourceFolder("${root.url}/$sourceDir", test)
    }

    fun excludeDirFromContent(content: ContentEntry, root: VirtualFile, excludedDir: String) {
        content.addExcludeFolder("${root.url}/$excludedDir")
    }

    fun findOtpApps(root: VirtualFile, indicator: ProgressIndicator): List<OtpApp> {
        val importedOtpApps = mutableSetOf<OtpApp>()

        // synchronous and recursive
        root.refresh(false, true)

        VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Any>() {
            override fun visitFile(file: VirtualFile): Boolean {
                indicator.checkCanceled()

                if (file.isDirectory) {
                    indicator.text2 = file.path

                    if (isAssetsOrBuildOrConfigOrDepsOrTestsDirectory(root.path, file.path)) {
                        return false
                    }
                }

                createImportedOtpApp(file)?.let { importedOtpApps.add(it) }

                return true
            }
        })

        return importedOtpApps.sortedWith(Comparator { o1, o2 ->
            val nameCompareResult = String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name)

            if (nameCompareResult == 0) {
                String.CASE_INSENSITIVE_ORDER.compare(o1.root.path, o1.root.path)
            } else {
                nameCompareResult
            }
        })
    }

    fun createModulesForOtpApps(
            project: Project,
            otpApps: List<OtpApp>,
            modifiableModuleModelFactory: () -> ModifiableModuleModel,
            rootModelModifier: (OtpApp, ModifiableRootModel) -> Unit = { _, _ -> }
    ): List<Module> =
        if (otpApps.isNotEmpty()) {
            val moduleModel = modifiableModuleModelFactory()
            val createdRootModels = otpApps.mapNotNull { createModuleForOtpApp(it, moduleModel, rootModelModifier) }

            if (createdRootModels.isNotEmpty()) {
                runWriteAction {
                    for (rootModel in createdRootModels) {
                        rootModel.commit()
                    }

                    moduleModel.commit()
                }

                ProgressManager.getInstance().run(object : Task.Modal(project, "Scanning dependencies for Libraries", true) {
                    override fun run(indicator: ProgressIndicator) {
                        project.getComponent(DepsWatcher::class.java).syncLibraries(project, indicator)
                    }
                })
            }

            createdRootModels.map { it.module }
        } else {
            emptyList()
        }

    private fun createModuleForOtpApp(otpApp: OtpApp, moduleModel: ModifiableModuleModel, rootModelModifier: (OtpApp, ModifiableRootModel) -> Unit): ModifiableRootModel? {
        val ideaModuleDir = otpApp.root
        val ideaModuleFile = "${ideaModuleDir.canonicalPath}${File.separator}/${otpApp.name}.iml"
        val module = moduleModel.newModule(ideaModuleFile, ElixirModuleType.MODULE_TYPE_ID)
        otpApp.module = module

        return if (otpApp.ideaModuleFile == null) {
            val rootModel = ModuleRootManager.getInstance(module).modifiableModel

            addFolders(rootModel, otpApp.root)
            rootModelModifier(otpApp, rootModel)

            rootModel
        } else {
            null
        }
    }

    fun addFolders(modifiableRootModel: ModifiableRootModel, root: VirtualFile) {
        val content = modifiableRootModel.addContentEntry(root)

        addSourceDirToContent(content, root, "lib", false)
        addSourceDirToContent(content, root, "spec", true)
        addSourceDirToContent(content, root, "test", true)

        // Weird symlink phoenix and phoenix_html make to themselves in deps
        excludeDirFromContent(content, root, "assets/node_modules/phoenix")
        excludeDirFromContent(content, root, "assets/node_modules/phoenix_html")
        // Test coverage
        excludeDirFromContent(content, root, "cover")
        // Dependencies (added as Libraries)
        excludeDirFromContent(content, root, "deps")
        // Documentation generated by `ex_doc`
        excludeDirFromContent(content, root, "doc")
        // Conventional logs directory
        excludeDirFromContent(content, root, "logs")
    }

    private fun createImportedOtpApp(appRoot: VirtualFile): OtpApp? =
            appRoot.findChild("mix.exs")?.let {
                OtpApp(appRoot, it)
            }

    private fun isAssetsOrBuildOrConfigOrDepsOrTestsDirectory(projectRootPath: String, path: String): Boolean {
        return (path.endsWith("/assets")
                || "$projectRootPath/_build" == path
                || "$projectRootPath/config" == path
                || "$projectRootPath/deps" == path
                || "$projectRootPath/tests" == path)
    }
}
