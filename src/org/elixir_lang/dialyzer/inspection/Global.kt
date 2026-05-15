package org.elixir_lang.dialyzer.inspection

import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import org.elixir_lang.dialyzer.service.DialyzerService
import org.elixir_lang.dialyzer.service.DialyzerWarn
import org.elixir_lang.psi.impl.document
import java.io.File

/**
 * Implements an inspection showing dialyzer warnings.
 *
 * Uses [isReadActionNeeded] = false because [dialyzerWarnsByModule] runs external processes
 * (mix dialyzer) which must NOT execute under a read lock (2026.1: waitFor() under read lock
 * logs an error). PSI and model access is wrapped in explicit runReadAction blocks instead.
 */
class Global : GlobalInspectionTool() {

    override fun runInspection(scope: AnalysisScope, manager: InspectionManager, globalContext: GlobalInspectionContext, problemDescriptionsProcessor: ProblemDescriptionsProcessor) {
        val moduleSet = moduleSet(scope)
        val dialyzerWarnsByModule = dialyzerWarnsByModule(moduleSet)

        scope.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                val problems = ApplicationManager.getApplication().runReadAction<List<ProblemDescriptor>> {
                    checkFile(file, manager, dialyzerWarnsByModule)
                }
                for (problem in problems) {
                    problemDescriptionsProcessor.addProblemElement(globalContext.refManager.getReference(file), problem)
                }
            }
        })
        super.runInspection(scope, manager, globalContext, problemDescriptionsProcessor)
    }

    override fun isReadActionNeeded(): Boolean = false

    private fun moduleSet(scope: AnalysisScope): Set<Module> {
        val moduleSet = mutableSetOf<Module>()

        scope.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                ApplicationManager.getApplication().runReadAction {
                    ModuleUtil.findModuleForFile(file)?.let { module ->
                        moduleSet.add(module)
                    }
                }
            }
        })

        return moduleSet
    }

    private fun dialyzerWarnsByModule(moduleSet: Set<Module>): Map<Module, MutableList<DialyzerWarn>> =
            moduleSet.associate { module ->
                module to module.project.getService(DialyzerService::class.java).dialyzerWarnings(module).toMutableList()
            }

    fun checkFile(file: PsiFile,
                  manager: InspectionManager,
                  dialyzerWarnsByModule: Map<Module, MutableList<DialyzerWarn>>): List<ProblemDescriptor> {
        val problemsHolder = ProblemsHolder(manager, file, false)

        ModuleUtil.findModuleForFile(file)?.let { module ->
            dialyzerWarnsByModule[module]?.let { dialyzerWarns ->
                val filePath = (file.containingDirectory.toString() + File.separator + file.name)

                file.accept(
                        object : PsiRecursiveElementWalkingVisitor() {
                            override fun visitElement(element: PsiElement) {
                                val lineNumber = element.document()?.getLineNumber(element.textOffset)
                                if (lineNumber != null) {
                                    val found = dialyzerWarns.filter { it.line == lineNumber + 1 }
                                            .filter { filePath.endsWith(it.fileName) }
                                            .map {
                                                problemsHolder.registerProblem(element, it.message, ProblemHighlightType.ERROR)
                                                it
                                            }
                                    dialyzerWarns.removeAll(found)
                                }
                                super.visitElement(element)
                            }
                        }
                )
            }
        }

        return problemsHolder.results
    }
}
