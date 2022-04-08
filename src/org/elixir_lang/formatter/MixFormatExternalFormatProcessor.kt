package org.elixir_lang.formatter

import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.ExternalFormatProcessor
import org.elixir_lang.Mix
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk
import java.util.concurrent.TimeUnit

@Suppress("UnstableApiUsage")
@SuppressWarnings("UnstableApiUsage")
class MixFormatExternalFormatProcessor : ExternalFormatProcessor {
    override fun activeForFile(source: PsiFile): Boolean {
        return source is ElixirFile
    }

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
                        mostSpecificSdk(source)?.let { sdk ->
                            format(workingDirectory, sdk, document.text)?.let { formattedText ->
                                application.invokeLater {
                                    if (source.isValid) {
                                        CommandProcessor.getInstance().runUndoTransparentAction {
                                            application.runWriteAction {
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
                    ProjectRootManager.getInstance(psiFile.project).fileIndex.getContentRootForFile(virtualFile)
                }
                ?.takeIf { contentRoot ->
                    contentRoot.findChild("mix.exs") != null
                }
                ?.path

        private fun format(workingDirectory: String, sdk: Sdk, unformattedText: String): String? {
            val commandline = Mix.commandLine(emptyMap(), workingDirectory, sdk)
            commandline.addParameter("format")
            // `-` turns on stdin/stdout for text to format
            commandline.addParameter("-")
            val processHandler = CapturingProcessHandler(commandline)
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
