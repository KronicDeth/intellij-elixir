import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.ProgressRunner
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiManager
import com.intellij.psi.SyntaxTraverser
import org.elixir_lang.errorreport.Logger

class BulkDecompilation : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Bulk Decompiling BEAM (.beam) files", true) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.isIndeterminate = false

                    val sdkSet = mutableSetOf<Sdk>()
                    val psiManager = PsiManager.getInstance(project)
                    val modules = ModuleManager.getInstance(project).modules
                    val moduleCount = modules.size

                    modules.withIndex().forEach { (index, module) ->
                        indicator.text2 = "Module ${module.name}"
                        indicator.fraction = index.toDouble() / moduleCount

                        val moduleRootManager = ModuleRootManager.getInstance(module)
                        val moduleSDK = moduleRootManager.sdk

                        if (moduleSDK != null) {
                            sdkSet.add(moduleSDK)
                        }

                        ModuleRootManager.getInstance(module).contentRoots.forEach { contentRoot ->
                            indicator.text2 = "Content root ${contentRoot.path}"
                            decompile(indicator, psiManager, contentRoot)
                        }
                    }

                    val projectSDK = ProjectRootManager.getInstance(project).projectSdk

                    if (projectSDK != null) {
                        sdkSet.add(projectSDK)
                    }

                    val sdkCount = sdkSet.size

                    sdkSet.withIndex().forEach { (index, sdk) ->
                        indicator.text2 = "SDK ${sdk.name}"
                        indicator.fraction = index.toDouble() / sdkCount

                        sdk.rootProvider.getFiles(OrderRootType.CLASSES).forEach { virtualFile ->
                            indicator.text2 = "SDK ${sdk.name} BEAM root ${virtualFile.path}"
                            decompile(indicator, psiManager, virtualFile)
                        }
                    }
                }
            })
        }
    }

    private fun decompile(indicator: ProgressIndicator, psiManager: PsiManager, virtualFile: VirtualFile) {
        if (virtualFile.isDirectory) {
            VfsUtilCore.visitChildrenRecursively(virtualFile, object : VirtualFileVisitor<Void>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    indicator.text2 = file.path

                    return if (file.isDirectory) {
                        true
                    } else {
                        if (file.fileType is org.elixir_lang.beam.FileType) {
                           runReadAction {
                                psiManager.findFile(file)?.let { it as? PsiCompiledFile }?.decompiledPsiFile?.let { decompiledPsiFile ->
                                    if (SyntaxTraverser.psiTraverser(decompiledPsiFile).traverse().filter(PsiErrorElement::class.java).isNotEmpty) {
                                        Logger.error(BulkDecompilation::class.java, "Parsing error in decompiled ${file.path}", decompiledPsiFile)
                                    }
                                }
                            }
                        }

                        false
                    }
                }
            })
        }
    }
}

