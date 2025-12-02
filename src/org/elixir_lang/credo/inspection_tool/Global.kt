package org.elixir_lang.credo.inspection_tool

import com.google.common.base.Charsets
import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.*
import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.elixir_lang.Mix
import org.elixir_lang.credo.Action
import org.elixir_lang.jps.builder.ParametersList
import org.elixir_lang.mix.Project
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk
import java.nio.file.Paths

class Global : GlobalInspectionTool() {
    override fun runInspection(
        scope: AnalysisScope,
        manager: InspectionManager,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor,
    ) {
        val project = scope.project

        for (module in ModuleManager.getInstance(project).modules) {
            runInspection(scope, manager, globalContext, problemDescriptionsProcessor, module)
        }
    }

    private fun workingDirectorySet(module: Module): Set<String> =
        ModuleRootManager
            .getInstance(module)
            .contentRoots
            .filter { virtualFile ->
                virtualFile.findChild(Project.MIX_EXS) != null
            }
            .map(VirtualFile::getPath)
            .toHashSet()

    private fun runInspection(
        scope: AnalysisScope,
        manager: InspectionManager,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor,
        module: Module,
    ) {
        val workingDirectorySet = workingDirectorySet(module)

        if (workingDirectorySet.isNotEmpty()) {
            val elixirSdk = mostSpecificSdk(module)
            val project = module.project

            if (elixirSdk != null) {
                val service = org.elixir_lang.credo.Service.getInstance(project)
                val environment = service.environmentVariableData.envs
                val erlParameters = ParametersList.parse(service.erlArguments).toList()
                val elixirParameters = ParametersList.parse(service.elixirArguments).toList()

                for (workingDirectory in workingDirectorySet) {
                    val processOutput = ProgressManager
                        .getInstance()
                        .run(object : Task.WithResult<ProcessOutput, ExecutionException>(
                            project,
                            "mix credo in $workingDirectory",
                            true
                        ) {
                            @Throws(ExecutionException::class)
                            override fun compute(indicator: ProgressIndicator): ProcessOutput {
                                indicator.isIndeterminate = true

                                val commandLine =
                                    Mix
                                        .commandLine(
                                            environment,
                                            workingDirectory,
                                            elixirSdk,
                                            erlParameters,
                                            elixirParameters
                                        )
                                        .withCharset(Charsets.UTF_8)
                                        .withWorkDirectory(workingDirectory)
                                        .apply { addParameters("credo", "--format", "flycheck") }

                                return ExecUtil.execAndGetOutput(commandLine)
                            }
                        })

                    val stderr = processOutput.stderr

                    if (stderr.isNotEmpty()) {
                        NotificationGroupManager
                            .getInstance()
                            .getNotificationGroup("Elixir")
                            .createNotification(
                                "Error running credo",
                                stderr.stripColor().toHTML(),
                                NotificationType.ERROR
                            )
                            .addAction(Action(project))
                            .notify(project)
                    }

                    // lib/level_web/ui/empty_state.ex:1:11: R: Modules should have a @moduledoc tag.
                    val flycheckRegex =
                        Regex("(?<path>.+?):(?<line>\\d+):(?:(?<column>\\d+):)? (?<tag>[CFRSW]): (?<message>.+)")

                    for (colorizedLine in processOutput.stdoutLines) {
                        val line = colorizedLine.stripColor()
                        val match = flycheckRegex.matchEntire(line)

                        if (match != null) {
                            val groups = match.groups as MatchNamedGroupCollection
                            val path = groups["path"]!!.value
                            val absolutePath = Paths.get(workingDirectory, path).toString()
                            val virtualFile = LocalFileSystem.getInstance().findFileByPath(absolutePath)

                            if (virtualFile != null) {
                                val psiFile =
                                    ApplicationManager
                                        .getApplication()
                                        .runReadAction(Computable<PsiFile?> {
                                            PsiManager.getInstance(project).findFile(virtualFile)
                                        })

                                if (psiFile != null) {
                                    val viewProvider = psiFile.viewProvider
                                    val document =
                                        ApplicationManager
                                            .getApplication()
                                            .runReadAction(Computable<Document?> {
                                                viewProvider.document
                                            })

                                    if (document != null) {
                                        val lineNumber = groups["line"]!!.value.toInt() - 1
                                        val lineStartOffset = document.getLineStartOffset(lineNumber)
                                        val start: Int
                                        val end: Int

                                        val columnNumber = groups["column"]?.value?.toInt()

                                        if (columnNumber != null) {
                                            start = lineStartOffset + columnNumber
                                            end = start + 1
                                        } else {
                                            start = lineStartOffset
                                            end = document.getLineEndOffset(lineNumber)
                                        }

                                        val refElement = globalContext.refManager.getReference(psiFile)

                                        val startElement =
                                            ApplicationManager
                                                .getApplication()
                                                .runReadAction(Computable<PsiElement?> {
                                                    psiFile.findElementAt(start)
                                                })
                                        val endElement =
                                            ApplicationManager
                                                .getApplication()
                                                .runReadAction(Computable<PsiElement?> {
                                                    psiFile.findElementAt(end - 1)
                                                })

                                        if (startElement != null && endElement != null) {
                                            val message = groups["message"]!!.value

                                            val problemDescriptor =
                                                ApplicationManager
                                                    .getApplication()
                                                    .runReadAction(Computable<ProblemDescriptor> {
                                                        manager.createProblemDescriptor(
                                                            startElement,
                                                            endElement,
                                                            message,
                                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                                            false
                                                        )
                                                    })

                                            problemDescriptionsProcessor.addProblemElement(
                                                refElement,
                                                problemDescriptor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Notifier.error(
                    module,
                    "Missing module Elixir SDK",
                    "There is no configured Elixir SDK for the module ${module.name} or its project ${project.name}, so credo" +
                            " cannot be run"
                )
            }
        }
    }

    private fun String.stripColor(): String = this.replace(Regex("\u001B\\[[;\\d]*m"), "")
    private fun String.toHTML(): String = this.replace("\n", "<br/>\n")

    override fun isGraphNeeded(): Boolean = true
    override fun isReadActionNeeded(): Boolean = false
    override fun worksInBatchModeOnly(): Boolean = true

    companion object {
        val SHORT_NAME = "Credo"
    }
}
