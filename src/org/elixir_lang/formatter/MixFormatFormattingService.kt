package org.elixir_lang.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService.Feature
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import org.elixir_lang.Elixir.elixirSdkHasErlangSdk
import org.elixir_lang.Mix
import org.elixir_lang.code_style.CodeStyleSettings
import org.elixir_lang.mix.Project as MixProject
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.sdk.elixir.ElixirSdkLookup.mostSpecificSdk
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

class MixFormatFormattingService : AsyncDocumentFormattingService() {

    override fun canFormat(file: PsiFile): Boolean =
        file is ElixirFile &&
            CodeStyle.getCustomSettings(file, CodeStyleSettings::class.java).MIX_FORMAT

    override fun getFeatures(): Set<Feature> = emptySet()

    override fun getNotificationGroupId(): String = "Elixir"

    override fun getName(): String = "mix format"

    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val formattingContext = request.context
        val psiFile = formattingContext.containingFile
        val project = formattingContext.project

        val workingDirectory = workingDirectory(psiFile) ?: return null
        val sdk = mostSpecificSdk(psiFile)?.takeIf(::elixirSdkHasErlangSdk) ?: return null

        // Build the command line in createFormattingTask() (must be fast and EDT-safe;
        // in sync/headless mode may still run on caller thread). Defer OSProcessHandler
        // creation to run() to avoid runBlockingMaybeCancellable EDT errors in WSL/Eel contexts.
        val commandLine = try {
            Mix.commandLine(emptyMap(), workingDirectory, sdk, ansiEnabled = false, project = project).apply {
                addParameter("format")
                addParameter("-")   // stdin/stdout mode
            }
        } catch (e: FileNotFoundException) {
            LOGGER.info(e)
            return null
        } catch (e: ExecutionException) {
            LOGGER.info(e)
            return null
        }

        return object : FormattingTask {
            @Volatile
            private var handler: OSProcessHandler? = null

            override fun run() {
                val h = try {
                    OSProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8))
                } catch (e: ExecutionException) {
                    request.onError("Mix format", e.message ?: e.toString())
                    return
                }
                handler = h

                h.addProcessListener(object : CapturingProcessAdapter() {
                    override fun processTerminated(event: ProcessEvent) {
                        if (event.exitCode == 0) {
                            request.onTextReady(output.stdout)
                        } else {
                            val error = extractFormatError(output.stderr, psiFile.name)
                            val htmlMessage = error.message.toNotificationHtml()
                            if (error.line != null) {
                                // onError(title, message, offset) calls reportErrorAndNavigate —
                                // the platform shows the balloon and moves the caret.
                                val offset = textOffset(request.documentText, error.line, error.column ?: 1)
                                request.onError("Mix format", htmlMessage, offset)
                            } else {
                                request.onError("Mix format", htmlMessage)
                            }
                        }
                    }
                })
                h.startNotify()

                // Write the document text to stdin after starting the process
                h.processInput.use { stdin ->
                    stdin.write(request.documentText.toByteArray(StandardCharsets.UTF_8))
                    stdin.flush()
                }

                // Block until the process exits. In normal async mode, run() executes on
                // a background thread; in forced sync/headless mode it runs on the caller
                // formatting thread. Timeout is still enforced via DEFAULT_TIMEOUT.
                h.waitFor()
            }

            override fun cancel(): Boolean {
                handler?.destroyProcess()
                return true
            }

            override fun isRunUnderProgress(): Boolean = true
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(MixFormatFormattingService::class.java)
    }
}

private fun workingDirectory(psiFile: PsiFile): String? =
    runReadAction<String?> {
        psiFile
            .virtualFile
            ?.let { virtualFile ->
                ProjectRootManager
                    .getInstance(psiFile.project)
                    .fileIndex
                    .getContentRootForFile(virtualFile)
            }
            ?.takeIf { contentRoot ->
                contentRoot.findChild(MixProject.MIX_EXS) != null
            }
            ?.path
    }

/**
 * Wraps a plain-text error message in HTML suitable for an IntelliJ notification balloon.
 *
 * IntelliJ's [com.intellij.notification.Notification] renders its content as HTML.
 * [StringUtil.escapeXmlEntities] handles `&`, `<`, `>` escaping; `<pre>` preserves
 * both whitespace and newlines in Swing's HTMLEditorKit so the source-context caret
 * lines (`|` blocks) are rendered with correct indentation.
 */
private fun String.toNotificationHtml(): String =
    "<pre>${StringUtil.escapeXmlEntities(this)}</pre>"

/**
 * Parsed result of `mix format` stderr when formatting fails.
 *
 * @param message  Human-readable error: the exception line + source snippet, with
 *                 "stdin" replaced by the real filename. Stack frames are stripped.
 * @param line     1-based line number of the error, or null if not parseable.
 * @param column   1-based column number of the error, or null if not parseable.
 */
private data class FormatError(val message: String, val line: Int?, val column: Int?)

/**
 * Computes the character offset in [documentText] for a 1-based [line] and [column].
 *
 * Uses [String.lineSequence] to sum the lengths of preceding lines (each plus its
 * terminating `\n`), then adds the column offset.
 */
private fun textOffset(documentText: String, line: Int, column: Int): Int {
    val lineStart = documentText.lineSequence().take(line - 1).sumOf { it.length + 1 }
    return (lineStart + (column - 1).coerceAtLeast(0)).coerceAtMost(documentText.length)
}

/**
 * Extracts the actionable error from `mix format` stderr output.
 *
 * The full stderr contains:
 *  1. Zero or more deprecation/warning lines
 *  2. "mix format failed for stdin" header
 *  3. The Elixir exception: `** (ErrorType) message` with source-context snippet
 *  4. The OTP/Elixir stack trace (`    (module version) file:line: ...`)
 *
 * We anchor on `** (`, replace `stdin` with `<fileName>`, and drop everything after
 * the first stack-frame line.
 *
 * Example message after extraction (fileName = "my_module.ex"):
 * ```
 * ** (SyntaxError) my_module.ex:16:10: unexpected token: "\"
 *     |
 *  16 |     blah \+ 123
 *     |          ^
 * ```
 */
// Stack frames look like: "    (module version) path/file.ex:123: Module.function/2"
private val STACK_FRAME_REGEX = Regex("""^\s+\(\S+ \S+\) .+""")
// Location encoded by mix format when reading from stdin: "stdin:line:col"
private val STDIN_LOCATION_REGEX = Regex("""stdin:(\d+):(\d+)""")

private fun extractFormatError(stderr: String, fileName: String): FormatError {
    val exceptionIndex = stderr.indexOf("** (")
    val errorSection = if (exceptionIndex != -1) stderr.substring(exceptionIndex) else stderr

    val locationMatch = STDIN_LOCATION_REGEX.find(errorSection)
    val line = locationMatch?.groupValues?.get(1)?.toIntOrNull()
    val column = locationMatch?.groupValues?.get(2)?.toIntOrNull()

    val message = errorSection
        .lines()
        .takeWhile { !STACK_FRAME_REGEX.matches(it) }
        .joinToString("\n")
        .replace("stdin:", "$fileName:")
        .trim()

    return FormatError(message, line, column)
}
