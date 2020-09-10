package org.elixir_lang.dialyzer.inspection

import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.GlobalInspectionContext
import com.intellij.codeInspection.GlobalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptionsProcessor
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import org.elixir_lang.dialyzer.service.DialyzerService
import org.elixir_lang.dialyzer.service.DialyzerWarn
import org.elixir_lang.psi.impl.document
import java.io.File

/**
 * Implements an inspection showing dialyzer warnings
 */
class Global : GlobalInspectionTool() {

    override fun runInspection(scope: AnalysisScope, manager: InspectionManager, globalContext: GlobalInspectionContext, problemDescriptionsProcessor: ProblemDescriptionsProcessor) {
        val service = globalContext.project.getService(DialyzerService::class.java)
        val dialyzerErrors = service.getDialyzerWarnings().toMutableList()
        scope.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                for (problem in checkFile(file, manager, dialyzerErrors)) {
                    problemDescriptionsProcessor.addProblemElement(globalContext.refManager.getReference(file), problem)
                }
            }
        })
        super.runInspection(scope, manager, globalContext, problemDescriptionsProcessor)
    }

    fun checkFile(file: PsiFile,
                  manager: InspectionManager,
                  dialyzerErrors: MutableList<DialyzerWarn>): List<ProblemDescriptor> {

        val problemsHolder = ProblemsHolder(manager, file, false)

        val filePath = (file.containingDirectory.toString() + File.separator + file.name)

        file.accept(
                object : PsiRecursiveElementWalkingVisitor() {
                    override fun visitElement(element: PsiElement) {
                        val lineNumber = element.document()?.getLineNumber(element.textOffset)
                        if (lineNumber != null) {
                            val found = dialyzerErrors.filter { it.line == lineNumber + 1}
                                    .filter { filePath.endsWith(it.fileName) }
                                    .map {
                                        problemsHolder.registerProblem(element, it.message, ProblemHighlightType.ERROR)
                                        it
                                    }
                            dialyzerErrors.removeAll(found)
                        }
                        super.visitElement(element)
                    }
                }
        )

        return problemsHolder.results
    }
}
