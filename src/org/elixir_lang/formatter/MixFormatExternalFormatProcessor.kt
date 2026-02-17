package org.elixir_lang.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.ExternalFormatProcessor
import org.elixir_lang.Elixir.elixirSdkHasErlangSdk
import org.elixir_lang.Mix
import org.elixir_lang.code_style.CodeStyleSettings
import org.elixir_lang.mix.Project
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk
import org.elixir_lang.util.WriteActions
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

@Suppress("UnstableApiUsage")
@SuppressWarnings("UnstableApiUsage")
class MixFormatExternalFormatProcessor : ExternalFormatProcessor {
    override fun activeForFile(source: PsiFile): Boolean =
        source is ElixirFile && CodeStyle.getCustomSettings(source, CodeStyleSettings::class.java).MIX_FORMAT

    override fun getId(): String = javaClass.name

    override fun format(
        source: PsiFile,
        range: TextRange,
        canChangeWhiteSpacesOnly: Boolean,
        keepLineBreaks: Boolean,
        enableBulkUpdate: Boolean,
        cursorOffset: Int
    ): TextRange? =
        // `mix format` doesn't support to format parts of a file
        if (source.isValid && range == source.textRange) {
            source.viewProvider.document?.let { document ->
                val application = ApplicationManager.getApplication()

                application.executeOnPooledThread {
                    workingDirectory(source)?.let { workingDirectory ->
                        mostSpecificSdk(source)?.takeIf(::elixirSdkHasErlangSdk)?.let { sdk ->
                            format(workingDirectory, sdk, source.project, document.text)?.let { formattedText ->
                                if (source.isValid) {
                                    WriteActions.runWriteActionLater {
                                        if (source.isValid) {
                                            CommandProcessor.getInstance().runUndoTransparentAction {
                                                document.setText(formattedText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                source.textRange
            }
        } else {
            null
        }

    override fun indent(source: PsiFile, lineStartOffset: Int): String? = null

    companion object {
        private val LOGGER = Logger.getInstance(MixFormatExternalFormatProcessor::class.java)

        private fun workingDirectory(psiFile: PsiFile): String? =
            psiFile
                .virtualFile
                ?.let { virtualFile ->
                    val fileIndex = ProjectRootManager.getInstance(psiFile.project).fileIndex

                    runReadAction {
                        fileIndex.getContentRootForFile(virtualFile)
                    }
                }
                ?.takeIf { contentRoot ->
                    contentRoot.findChild(Project.MIX_EXS) != null
                }
                ?.path

        private fun format(workingDirectory: String, sdk: Sdk, project: com.intellij.openapi.project.Project, unformattedText: String): String? =
            commandLine(workingDirectory, sdk, project)
                ?.let { format(it, unformattedText) }

        private fun commandLine(workingDirectory: String, sdk: Sdk, project: com.intellij.openapi.project.Project): GeneralCommandLine? =
            try {
                val commandLine = Mix.commandLine(emptyMap(), workingDirectory, sdk, project = project)
                commandLine.addParameter("format")
                // `-` turns on stdin/stdout for text to format
                commandLine.addParameter("-")

                commandLine
            } catch (e: FileNotFoundException) {
                LOGGER.info(e)
                null
            } catch (e: ExecutionException) {
                LOGGER.info(e)

                null
            }

        private fun format(commandLine: GeneralCommandLine, unformattedText: String): String? {
            val processHandler = CapturingProcessHandler(commandLine)
            processHandler.processInput.use { stdin ->
                stdin.write(unformattedText.toByteArray())
                stdin.flush()
            }

            val indicator = ProgressManager.getGlobalProgressIndicator()
            val timeout = TimeUnit.SECONDS.toMillis(5).toInt()
            val output = if (indicator != null) {
                processHandler.runProcessWithProgressIndicator(
                    indicator,
                    timeout,
                    true
                )
            } else {
                processHandler.runProcess(timeout, true)
            }

            return if (output.checkSuccess(LOGGER)) {
                output.stdout
            } else {
                null
            }
        }
    }
}
