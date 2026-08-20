package org.elixir_lang.dialyzer.inspection

import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.xml.util.XmlStringUtil
import org.elixir_lang.dialyzer.service.DialyzerService
import org.elixir_lang.dialyzer.service.DialyzerWarn
import org.elixir_lang.psi.impl.document
import java.util.concurrent.Callable

/**
 * Implements an inspection showing dialyzer warnings.
 *
 * Uses [isReadActionNeeded] = false because [dialyzerWarnsByModule] runs external processes
 * (mix dialyzer) which must NOT execute under a read lock (2026.1: waitFor() under read lock
 * logs an error). PSI and model access is wrapped in explicit runReadAction blocks instead.
 */
internal class Global : GlobalInspectionTool() {

    override fun runInspection(
        scope: AnalysisScope,
        manager: InspectionManager,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor
    ) {
        // Save all modified documents before launching mix dialyzer so the external process
        // sees the current file contents.  runInspection runs on a background thread
        // (isReadActionNeeded = false); FileDocumentManager.saveAllDocuments() requires EDT
        // because it calls runWriteAction internally.
        //
        // invokeAndWait is the correct bridge from blocking background code to EDT - this is
        // a non-suspend override so coroutine APIs (edtWriteAction, withContext(Dispatchers.EDT))
        // are not available here.  ModalityState.nonModal() ensures the save is not deferred
        // by an open dialog - we must flush to disk before handing off to the external process.
        ApplicationManager.getApplication().invokeAndWait(
            { FileDocumentManager.getInstance().saveAllDocuments() },
            ModalityState.nonModal()
        )

        val moduleSet = moduleSet(scope)
        val dialyzerWarnsByModule = dialyzerWarnsByModule(moduleSet)

        scope.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                val problems = ReadAction.nonBlocking(Callable {
                    checkFile(file, manager, dialyzerWarnsByModule)
                }).executeSynchronously()
                for (problem in problems) {
                    problemDescriptionsProcessor.addProblemElement(globalContext.refManager.getReference(file), problem)
                }
            }
        })
    }

    override fun isReadActionNeeded(): Boolean = false

    /**
     * This inspection never consults the reference graph - it delegates entirely to the
     * external `mix dialyzer` process.  Returning false prevents the platform from building
     * the graph before each run (a significant upfront cost) and eliminates the redundant
     * default [runInspection] ref-graph iteration that would otherwise occur via `super`.
     */
    override fun isGraphNeeded(): Boolean = false

    private fun moduleSet(scope: AnalysisScope): Set<Module> {
        val moduleSet = mutableSetOf<Module>()

        scope.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                ReadAction.nonBlocking(Callable {
                    ModuleUtil.findModuleForFile(file)
                }).executeSynchronously()?.let { module ->
                    moduleSet.add(module)
                }
            }
        })

        return moduleSet
    }

    private fun dialyzerWarnsByModule(moduleSet: Set<Module>): Map<Module, MutableList<DialyzerWarn>> =
            moduleSet.associateWith { module ->
                module.project.getService(DialyzerService::class.java)
                    .dialyzerWarnings(module)
                    .toMutableList()
            }

    fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        dialyzerWarnsByModule: Map<Module, MutableList<DialyzerWarn>>
    ): List<ProblemDescriptor> {
        val problemsHolder = ProblemsHolder(manager, file, false)

        ModuleUtil.findModuleForFile(file)?.let { module ->
            dialyzerWarnsByModule[module]?.let { dialyzerWarns ->
                val filePath = file.virtualFile?.path ?: return problemsHolder.results

                file.accept(
                    object : PsiRecursiveElementWalkingVisitor() {
                        override fun visitElement(element: PsiElement) {
                            val doc = element.document() ?: return super.visitElement(element)
                            val lineNumber = doc.getLineNumber(element.textOffset)
                            val found = dialyzerWarns.filter { it.line == lineNumber + 1 }
                                .filter { filePath.endsWith(it.fileName) }
                                .map { warn ->
                                    val lineOffset = element.textOffset - doc.getLineStartOffset(lineNumber)
                                    val locationPrefix = "${warn.line}:${lineOffset + 1}: "

                                    problemsHolder.registerProblem(
                                        element,
                                        XmlStringUtil.wrapInHtml(
                                            XmlStringUtil.wrapInHtmlTag(
                                                XmlStringUtil.escapeString(locationPrefix + warn.message), "pre"
                                            )
                                        ),
                                        ProblemHighlightType.ERROR
                                    )
                                    warn
                                }
                            dialyzerWarns.removeAll(found)
                            super.visitElement(element)
                        }
                    }
                )
            }
        }

        return problemsHolder.results
    }
}
