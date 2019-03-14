package org.elixir_lang.espec.configuration

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Ref
import com.intellij.psi.*
import org.elixir_lang.espec.Configuration
import org.elixir_lang.espec.Gatherer
import org.elixir_lang.file.containsFileWithSuffix
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.elixir.Type.mostSpecificSdk
import java.io.File

private const val UNKNOWN_LINE = -1

private fun configurationName(file: PsiFileSystemItem,
                              workingDirectory: String?,
                              basePath: String?): String {
    val filePath = file.virtualFile.path
    val suffix = if (workingDirectory != null) {
        val prefix = workingDirectory + File.separator
        val relativeFilePath = filePath.removePrefix(prefix)

        if (basePath != null && workingDirectory != basePath && workingDirectory.startsWith(basePath)) {
            val otpAppName = File(workingDirectory).name

            "$otpAppName $relativeFilePath"
        } else {
            relativeFilePath
        }
    } else {
        file.name
    }

    return "Mix Espec " + suffix
}

private fun configurationName(file: PsiFileSystemItem,
                              lineNumber: Int,
                              workingDirectory: String?,
                              basePath: String?): String =
        if (lineNumber == UNKNOWN_LINE) {
            configurationName(file, workingDirectory, basePath)
        } else {
            "${configurationName(file, workingDirectory, basePath)}:$lineNumber"
        }

private fun isConfigurationFromContextImpl(configuration: Configuration, psiElement: PsiElement): Boolean {
    val contextConfiguration = Configuration(configuration.name, configuration.project)

    return setupConfigurationFromContextImpl(contextConfiguration, psiElement) &&
            contextConfiguration.programParameters == configuration.programParameters &&
            contextConfiguration.workingDirectory == configuration.workingDirectory
}

private fun lineNumber(psiElement: PsiElement): Int {
    val containingFile = psiElement.containingFile
    val documentLineNumber = PsiDocumentManager
            .getInstance(containingFile.project)
            .getDocument(containingFile)
            ?.getLineNumber(psiElement.textOffset)
            ?:
            0

    return if (documentLineNumber == 0) {
        UNKNOWN_LINE
    } else {
        documentLineNumber + 1
    }
}

private fun programParameters(item: PsiFileSystemItem, workingDirectory: String?): String =
        programParameters(item, UNKNOWN_LINE, workingDirectory)

private fun programParameters(item: PsiFileSystemItem,
                              lineNumber: Int,
                              workingDirectory: String?): String {
    return if (item.isDirectory) {
        val specFileGatherer = Gatherer(workingDirectory)
        item.processChildren(specFileGatherer)

        specFileGatherer.programParameters
    } else {
        val path = item.virtualFile.path
        val relativePath = if (workingDirectory != null) {
            path.removePrefix(workingDirectory + File.separator)
        } else {
            path
        }

        if (lineNumber != UNKNOWN_LINE) {
            "$relativePath:$lineNumber"
        } else {
            relativePath
        }
    }
}

private fun setupConfigurationFromContextImpl(configuration: Configuration,
                                              psiElement: PsiElement): Boolean =
        when (psiElement) {
            is PsiDirectory -> {
                val module = ModuleUtilCore.findModuleForPsiElement(psiElement)
                val sdk = if (module != null) {
                    mostSpecificSdk(module)
                } else {
                    val projectRootManager = ProjectRootManager.getInstance(psiElement.project)
                    projectRootManager.projectSdk
                }
                val sdkTypeId = sdk?.sdkType

                if ((sdkTypeId == null || sdkTypeId == Type.getInstance()) &&
                        ProjectRootsUtil.isInTestSource(psiElement.virtualFile, psiElement.project) &&
                        containsFileWithSuffix(psiElement, "_spec.exs")) {
                    val basePath = psiElement.getProject().basePath
                    val workingDirectory = workingDirectory(psiElement, basePath)

                    configuration.workingDirectory = workingDirectory
                    configuration.name = configurationName(psiElement, workingDirectory, basePath)
                    configuration.programParameters = programParameters(psiElement, workingDirectory)

                    true
                } else {
                    false
                }
            }
            is ElixirFile -> {
                if (psiElement.virtualFile.path.endsWith("_spec.exs")) {
                    val basePath = psiElement.project.basePath
                    val workingDirectory = workingDirectory(psiElement, basePath)
                    val lineNumber = lineNumber(psiElement)

                    configuration.workingDirectory = workingDirectory
                    configuration.name = configurationName(psiElement, lineNumber, workingDirectory, basePath)
                    configuration.programParameters = programParameters(psiElement, lineNumber, workingDirectory)

                    true
                } else {
                    false
                }
            }
            else -> {
                val containingFile = psiElement.containingFile

                if (containingFile is ElixirFile) {
                    setupConfigurationFromContextImpl(configuration, containingFile)
                } else {
                    false
                }
            }
        }

private fun workingDirectory(directory: PsiDirectory, basePath: String?): String? =
        if (directory.findFile("mix.exs") != null) {
            directory.virtualFile.path
        } else {
            directory.parent?.let { workingDirectory(it, basePath) } ?: basePath
        }

private fun workingDirectory(element: PsiElement, basePath: String?): String? =
        when (element) {
            is PsiDirectory -> workingDirectory(element, basePath)
            is PsiFile -> workingDirectory(element, basePath)
            else -> workingDirectory(element.containingFile, basePath)
        }

private fun workingDirectory(file: PsiFile, basePath: String?): String? =
        workingDirectory(file.containingDirectory, basePath)

class MixEspecRunConfigurationProducer:
        RunConfigurationProducer<Configuration>(org.elixir_lang.espec.configuration.Type.INSTANCE) {
    override fun setupConfigurationFromContext(runConfig: Configuration,
                                               context: ConfigurationContext,
                                               ref: Ref<PsiElement>): Boolean =
            ref.get()?.let { it.isValid && setupConfigurationFromContextImpl(runConfig, it) } == true

    override fun isConfigurationFromContext(runConfig: Configuration,
                                            context: ConfigurationContext): Boolean =
            context.psiLocation?.let {
                it.isValid && isConfigurationFromContextImpl(runConfig, it)
            } == true
}
