package org.elixir_lang.credo.inspection_tool

import com.google.common.base.Charsets
import com.intellij.analysis.AnalysisScope
import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.*
import com.intellij.codeInspection.reference.*
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.lang.ExternalLanguageAnnotators
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationSession
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.credo.Annotator
import org.elixir_lang.jps.builder.ParametersList
import org.elixir_lang.mix.MissingSdk

import java.nio.file.Paths
import java.util.*


private fun put(pathSetByWorkingDirectory: MutableMap<String, MutableSet<String>>,
                workingDirectorySet: Set<String>) {
    for (workingDirectory in workingDirectorySet) {
        put(pathSetByWorkingDirectory, workingDirectory, workingDirectory)
    }
}

private fun put(pathSetByWorkingDirectory: MutableMap<String, MutableSet<String>>,
                workingDirectorySet: Set<String>,
                path: String) {
    for (workingDirectory in workingDirectorySet) {
        put(pathSetByWorkingDirectory, workingDirectory, path)
    }
}

private fun put(pathSetByWorkingDirectory: MutableMap<String, MutableSet<String>>,
                workingDirectory: String,
                path: String) {
    pathSetByWorkingDirectory
            .computeIfAbsent(workingDirectory) { mutableSetOf() }
            .add(path)
}

private fun generalCommandLine(workingDirectoryGeneralCommandLine: GeneralCommandLine,
                               module: Module,
                               mixParametersList: ParametersList): GeneralCommandLine =
        TODO()
//        MixRunningStateUtil.commandLine(
//                workingDirectoryGeneralCommandLine,
//                module,
//                ParametersList(),
//                mixParametersList
//        )

private fun runInspection(module: Module,
                          workingDirectory: String,
                          pathSet: Set<String>): List<Annotator.Issue> {
    return try {
        val processOutput = ExecUtil.execAndGetOutput(
                generalCommandLine(workingDirectory, module, pathSet)
        )

        Annotator.lineListToIssueList(processOutput.stdoutLines)
    } catch (executionException: ExecutionException) {
        emptyList()
    } catch (missingSdk: MissingSdk) {
        emptyList()
    }
}

private fun runInspection(project: Project,
                          workingDirectory: String,
                          pathSet: Set<String>): List<Annotator.Issue> =
        LocalFileSystem.getInstance().findFileByPath(workingDirectory)?.let { virtualFile ->
            ModuleUtilCore.findModuleForFile(virtualFile, project)?.let { module ->
                runInspection(module, workingDirectory, pathSet)
            }
        } ?: emptyList()

private fun generalCommandLine(workingDirectory: String,
                               module: Module,
                               mixParametersList: ParametersList): GeneralCommandLine =
        generalCommandLine(
                GeneralCommandLine().withCharset(Charsets.UTF_8).apply {
                    withWorkDirectory(workingDirectory)
                },
                module,
                mixParametersList
        )

private fun generalCommandLine(workingDirectory: String,
                               module: Module,
                               pathSet: Set<String>): GeneralCommandLine =
        generalCommandLine(workingDirectory, module, mixParameterList(pathSet))

private fun mixParametersList(): ParametersList = ParametersList().apply {
    add("credo")
}

private fun mixParameterList(pathSet: Set<String>): ParametersList {
    val parametersList = mixParametersList()
    parametersList.add("--format")
    parametersList.add("flycheck")

    for (path in pathSet) {
        parametersList.add(path)
    }

    return parametersList
}

private fun workingDirectorySet(module: Module): Set<String> {
    val moduleRootWorkingDirectorySet =
            ModuleRootManager
                    .getInstance(module)
                    .contentRoots
                    .filter { virtualFile ->
                        virtualFile.findChild("mix.exs") != null
                    }
                    .map(VirtualFile::getPath)
                    .toHashSet()

    return if (moduleRootWorkingDirectorySet.isEmpty()) {
        workingDirectorySet(module.project)
    } else {
        moduleRootWorkingDirectorySet
    }
}

private fun workingDirectorySet(project: Project): Set<String> {
    return setOf(project.basePath!!)
}

private fun annotator(): Annotator {
    val externalAnnotatorList = ExternalLanguageAnnotators.INSTANCE.allForLanguage(ElixirLanguage)
    var annotator: Annotator? = null

    for (element in externalAnnotatorList) {
        if (element is Annotator) {
            annotator = element
            break
        }
    }

    return annotator!!
}

// See ExternalAnnotatorInspectionVisitor#toLocalQuickFixes
private fun convertToProblemDescriptors(annotations: List<Annotation>,
                                        file: PsiFile): Array<ProblemDescriptor> {
    if (annotations.isEmpty()) {
        return ProblemDescriptor.EMPTY_ARRAY
    }

    val problems = ArrayList<ProblemDescriptor>(annotations.size)
    val quickFixMappingCache = IdentityHashMap<IntentionAction, LocalQuickFix>()
    for (annotation in annotations) {
        if (annotation.severity === HighlightSeverity.INFORMATION || annotation.startOffset == annotation.endOffset && !annotation.isAfterEndOfLine) {
            continue
        }

        val startElement: PsiElement?
        val endElement: PsiElement?

        if (annotation.startOffset == annotation.endOffset && annotation.isAfterEndOfLine) {
            endElement = file.findElementAt(annotation.endOffset - 1)
            startElement = endElement
        } else {
            startElement = file.findElementAt(annotation.startOffset)
            endElement = file.findElementAt(annotation.endOffset - 1)
        }

        if (startElement == null || endElement == null) {
            continue
        }

        val quickFixes = toLocalQuickFixes(annotation.quickFixes, quickFixMappingCache)
        val descriptor = ProblemDescriptorBase(startElement,
                endElement,
                annotation.message,
                quickFixes,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                annotation.isAfterEndOfLine, null,
                true,
                false)
        problems.add(descriptor)
    }

    return problems.toTypedArray()
}

// See ExternalAnnotatorInspectionVisitor#toLocalQuickFixes
private fun toLocalQuickFixes(
        quickFixInfoList: List<Annotation.QuickFixInfo>?,
        localQuickFixByIntentionAction: IdentityHashMap<IntentionAction, LocalQuickFix>
): Array<LocalQuickFix> = quickFixInfoList.orEmpty().map { quickFixInfo ->
    val intentionAction = quickFixInfo.quickFix

    if (intentionAction is LocalQuickFix) {
        intentionAction
    } else {
        localQuickFixByIntentionAction.computeIfAbsent(intentionAction) {
            ExternalAnnotatorInspectionVisitor.LocalQuickFixBackedByIntentionAction(it)
        }
    }
}.toTypedArray()

private fun addProblemElement(problemDescriptionsProcessor: ProblemDescriptionsProcessor,
                              localFileSystem: LocalFileSystem,
                              psiManager: PsiManager,
                              refManager: RefManager,
                              externalAnnotator: ExternalAnnotator<PsiFile, List<Annotator.Issue>>,
                              fullPath: String,
                              issueList: List<Annotator.Issue>) {
    val virtualFile = localFileSystem.findFileByPath(fullPath)

    addProblemElement(
            problemDescriptionsProcessor,
            psiManager,
            refManager,
            externalAnnotator,
            virtualFile,
            issueList
    )
}

private fun addProblemElement(problemDescriptionsProcessor: ProblemDescriptionsProcessor,
                              psiManager: PsiManager,
                              refManager: RefManager,
                              externalAnnotator: ExternalAnnotator<PsiFile, List<Annotator.Issue>>,
                              virtualFile: VirtualFile?,
                              issueList: List<Annotator.Issue>) {
    if (virtualFile != null) {
        val psiFile = psiManager.findFile(virtualFile)

        addProblemElement(problemDescriptionsProcessor, refManager, externalAnnotator, psiFile, issueList)
    }
}

private fun addProblemElement(problemDescriptionsProcessor: ProblemDescriptionsProcessor,
                              refManager: RefManager,
                              externalAnnotator: ExternalAnnotator<PsiFile, List<Annotator.Issue>>,
                              psiFile: PsiFile?,
                              issueList: List<Annotator.Issue>) {
    if (psiFile != null) {
        val problemDescriptors = problemDescriptors(externalAnnotator, psiFile, issueList)
        val refElement = refManager.getReference(psiFile)
        problemDescriptionsProcessor.addProblemElement(refElement, *problemDescriptors)
    }
}

private fun problemDescriptors(
        externalAnnotator: ExternalAnnotator<PsiFile, List<Annotator.Issue>>,
        psiFile: PsiFile,
        issueList: List<Annotator.Issue>
): Array<ProblemDescriptor> =
        ReadAction.compute<Array<ProblemDescriptor>, RuntimeException> {
            val annotationSession = AnnotationSession(psiFile)
            val annotationHolder = AnnotationHolderImpl(annotationSession, true)
            externalAnnotator.apply(psiFile, issueList, annotationHolder)

            convertToProblemDescriptors(annotationHolder, psiFile)
        }

class Global : GlobalInspectionTool() {
    private fun pathSetByWorkingDirectory(globalContext: GlobalInspectionContext): Map<String, Set<String>> {
        val pathSetByWorkingDirectory = mutableMapOf<String, MutableSet<String>>()

        globalContext.refManager.iterate(object : RefVisitor() {
            override fun visitElement(refEntity: RefEntity) {
                if (globalContext.shouldCheck(refEntity, this@Global)) {
                    when (refEntity) {
                        is RefModule -> put(pathSetByWorkingDirectory, workingDirectorySet(refEntity.module))
                        is RefProject -> {
                            globalContext.project.basePath?.let {
                                put(pathSetByWorkingDirectory, setOf(it))
                            }
                        }
                        is RefFile -> {
                            val psiFile = refEntity.psiElement

                            val workingDirectorySet: Set<String>
                            val module = ModuleUtilCore.findModuleForPsiElement(psiFile)

                            workingDirectorySet = if (module != null) {
                                workingDirectorySet(module)
                            } else {
                                workingDirectorySet(psiFile.project)
                            }

                            put(pathSetByWorkingDirectory, workingDirectorySet, psiFile.virtualFile.path)
                        }
                        is RefDirectory -> {
                            val refModule = refEntity.module
                            val workingDirectorySet: Set<String>

                            workingDirectorySet = if (refModule != null) {
                                workingDirectorySet(refModule.module)
                            } else {
                                workingDirectorySet(refEntity.psiElement.project)
                            }

                            put(
                                    pathSetByWorkingDirectory,
                                    workingDirectorySet,
                                    refEntity.psiElement.containingFile.virtualFile.path
                            )
                        }
                    }
                }
            }
        })

        return pathSetByWorkingDirectory
    }

    override fun runInspection(scope: AnalysisScope,
                               manager: InspectionManager,
                               globalContext: GlobalInspectionContext,
                               problemDescriptionsProcessor: ProblemDescriptionsProcessor) {
        val pathSetByWorkingDirectory = pathSetByWorkingDirectory(globalContext)
        val issueListByFullPath = mutableMapOf<String, MutableList<Annotator.Issue>>()

        for ((workingDirectory, value) in pathSetByWorkingDirectory) {
            val issueList = runInspection(manager.project, workingDirectory, value)

            for (issue in issueList) {
                val fullPath = Paths.get(workingDirectory, issue.path).toString()
                issueListByFullPath.computeIfAbsent(fullPath, { mutableListOf() }).add(issue)
            }
        }

        val localFileSystem = LocalFileSystem.getInstance()
        val psiManager = PsiManager.getInstance(globalContext.project)
        val refManager = globalContext.refManager
        val externalAnnotator = annotator()

        for ((key, value) in issueListByFullPath) {
            addProblemElement(
                    problemDescriptionsProcessor,
                    localFileSystem,
                    psiManager,
                    refManager,
                    externalAnnotator,
                    key,
                    value
            )
        }
    }

    override fun getSharedLocalInspectionTool(): LocalInspectionTool = Local()
    override fun isGraphNeeded(): Boolean = true
    override fun worksInBatchModeOnly(): Boolean = true

    companion object {
        val SHORT_NAME = "Credo"
    }
}
