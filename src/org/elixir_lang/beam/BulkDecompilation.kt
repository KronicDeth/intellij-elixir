package org.elixir_lang.beam

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.currentThreadCoroutineScope
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportSequentialProgress
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiManager
import com.intellij.psi.SyntaxTraverser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.sdk.elixir.Type

class BulkDecompilation : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        currentThreadCoroutineScope().launch {
            BulkDecompilationRunLogger.begin(project)

            try {
                withBackgroundProgress(project, "Bulk Decompiling BEAM (.beam) files") {
                    withContext(Dispatchers.Default) {
                        val sdkSet = mutableSetOf<Sdk>()
                        val psiManager = PsiManager.getInstance(project)
                        val modules = ModuleManager.getInstance(project).modules

                        if (modules.isNotEmpty()) {
                            reportSequentialProgress(modules.size) { reporter ->
                                for (module in modules) {
                                    reporter.itemStep("Module ${module.name}") {
                                        ProgressManager.checkCanceled()

                                        val moduleRootManager = ModuleRootManager.getInstance(module)
                                        moduleRootManager.sdk?.let { sdkSet.add(it) }

                                        for (contentRoot in moduleRootManager.contentRoots) {
                                            ProgressManager.checkCanceled()
                                            decompile(psiManager, contentRoot)
                                        }
                                    }
                                }
                            }
                        }

                        ProjectRootManager.getInstance(project).projectSdk?.let { sdk ->
                            if (sdk.sdkType is Type) {
                                sdkSet.add(sdk)
                            }
                        }

                        if (sdkSet.isNotEmpty()) {
                            reportSequentialProgress(sdkSet.size) { reporter ->
                                for (sdk in sdkSet) {
                                    reporter.itemStep("SDK ${sdk.name}") {
                                        ProgressManager.checkCanceled()

                                        for (virtualFile in sdk.rootProvider.getFiles(OrderRootType.CLASSES)) {
                                            ProgressManager.checkCanceled()
                                            decompile(psiManager, virtualFile)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                BulkDecompilationRunLogger.end(project)
            }
        }
    }

    private suspend fun decompile(psiManager: PsiManager, virtualFile: VirtualFile) {
        if (!virtualFile.isDirectory) {
            if (isBeamFile(virtualFile)) {
                decompileFile(psiManager, virtualFile)
            }
            return
        }

        // Collect first because the VFS visitor callback is non-suspending.
        val beamFiles = mutableListOf<VirtualFile>()

        VfsUtilCore.visitChildrenRecursively(virtualFile, object : VirtualFileVisitor<Void>() {
            override fun visitFile(file: VirtualFile): Boolean {
                ProgressManager.checkCanceled()

                if (isBeamFile(file)) {
                    beamFiles.add(file)
                }

                return true
            }
        })

        for (file in beamFiles) {
            ProgressManager.checkCanceled()
            decompileFile(psiManager, file)
        }
    }

    private fun isBeamFile(file: VirtualFile): Boolean =
        !file.isDirectory && file.extension.equals("beam", ignoreCase = true)

    private suspend fun decompileFile(psiManager: PsiManager, file: VirtualFile) {
        BulkDecompilationRunLogger.recordDecompilation(psiManager.project)
        readAction {
            psiManager.findFile(file)
                ?.let { it as? PsiCompiledFile }
                ?.decompiledPsiFile
                ?.let { decompiledPsiFile ->
                    val firstParseError = SyntaxTraverser.psiTraverser(decompiledPsiFile)
                        .traverse()
                        .filter(PsiErrorElement::class.java)
                        .firstOrNull()

                    if (firstParseError != null) {
                        val failingLine = parseErrorLine(decompiledPsiFile.text, firstParseError.textRange.startOffset)
                        BulkDecompilationRunLogger.recordParseErrorLine(
                            psiManager.project,
                            failingLine
                        )

                        if (BulkDecompilationRunLogger.shouldLogParseError(psiManager.project, file.path)) {
                            Logger.error(
                                BulkDecompilation::class.java,
                                parseErrorTitle(file.path, firstParseError.errorDescription, failingLine),
                                decompiledPsiFile
                            )
                        }
                    }
                }
        }
    }

    private fun parseErrorTitle(filePath: String, errorDescription: String?, failingLine: String?): String {
        val descriptionPart = errorDescription?.takeIf { it.isNotBlank() }?.let {
            " description=${compactForTitle(it)}"
        } ?: ""
        val linePart = failingLine?.takeIf { it.isNotBlank() }?.let {
            " line=${compactForTitle(it)}"
        } ?: ""

        return "Parsing error in decompiled $filePath$descriptionPart$linePart"
    }

    private fun parseErrorLine(text: String, offset: Int): String? {
        if (text.isEmpty() || offset < 0 || offset > text.length) {
            return null
        }

        val lineStart = text.lastIndexOf('\n', startIndex = (offset - 1).coerceAtLeast(0)).let {
            if (it < 0) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', startIndex = offset).let {
            if (it < 0) text.length else it
        }

        return text.substring(lineStart, lineEnd)
    }

    private fun compactForTitle(text: String): String =
        text.trim().replace(Regex("\\s+"), " ").take(MAX_PARSE_TITLE_TEXT_LENGTH)

    companion object {
        private const val MAX_PARSE_TITLE_TEXT_LENGTH = 180
    }
}
