package org.elixir_lang.errorreport

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.diagnostic.CoreAttachmentFactory
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object Logger {
    /*
     * Public Static Methods
     */
    /**
     * Logs error to the `klass`'s [com.intellij.openapi.diagnostic.Logger] instance with the given
     * `userMessage` and the text of `element` as the details and containing file of `element` as an
     * attachment
     *
     * @param klass   Class whose logger to use
     * @param title   Title of error stored in [Throwable].
     * @param element element responsible for the error
     */
    @JvmStatic
    fun error(klass: Class<*>, title: String, element: PsiElement) {
        error(Logger.getInstance(klass), title, element)
    }

    fun error(klass: Class<*>, title: String, term: OtpErlangObject) {
        error(Logger.getInstance(klass), title, term)
    }

    /**
     * Logs error [com.intellij.openapi.diagnostic.Logger] instance with the given `title` and the
     * text of `element` as the details and containing file of `element` as an * attachment
     *
     * @param logger  logger to which to log an error.
     * @param title   Title of error stored in [Throwable].
     * @param element element responsible for the error
     */
    fun error(
        logger: Logger,
        title: String,
        element: PsiElement
    ) {
        val throwable = Throwable(title)
        val containingFile = element.containingFile
        val message = message(containingFile, element)
        val virtualFile = containingFile.virtualFile
        if (virtualFile != null) {
            val attachment = CoreAttachmentFactory.createAttachment(virtualFile)
            logger.error(message, throwable, attachment)
        } else {
            logger.error(message, throwable)
        }
    }

    /**
     * Logs error [com.intellij.openapi.diagnostic.Logger] instance with the given `title` and the
     * `term`
     *
     * @param logger  logger to which to log an error.
     * @param title   Title of error stored in [Throwable].
     * @param term responsible for the error
     */
    fun error(
        logger: Logger,
        title: String,
        term: OtpErlangObject
    ) {
        val throwable = Throwable(title)
        val message = message(term)
        logger.error(message, throwable)
    }

    /*
     * Private Static Methods
     */
    private fun className(element: PsiElement): String {
        return """
             
             ### Element Class Name
             
             ```
             ${element.javaClass.name}
             ```
             """.trimIndent()
    }

    private val CONTEXT_LINES = 5

    private fun excerpt(containingFile: PsiFile, element: PsiElement): String {
        val excerptBuilder = StringBuilder()
        excerptBuilder
            .append('\n')
            .append("### Element\n")
            .append('\n')
        excerptBuilder
            .append("```\n")
            .append(element.text)
            .append('\n')
            .append("```\n")
        val fileViewProvider = containingFile.viewProvider
        val document = fileViewProvider.document
        if (document != null) {
            // Use `.originalFile.virtualFile` as only BEAM File will have virtual file and not decompiled.  For
            // normal source files, `containingFile` and `containingFile.originalFile` are the same.
            val virtualFile = containingFile.originalFile.virtualFile
            if (virtualFile != null) {
                val path = virtualFile.path
                val textRange = element.textRange
                val startingLine = document.getLineNumber(textRange.startOffset)
                val endingLine = document.getLineNumber(textRange.endOffset)

                excerptBuilder
                    .append('\n')
                    .append("From: `").append(path).append(':').append(startingLine)

                if (endingLine != startingLine) {
                    excerptBuilder.append('-').append(endingLine)
                }

                excerptBuilder.append('`').append('\n')

                excerptBuilder
                    .append('\n')
                    .append("#### Context\n")
                    .append('\n')

                val contextStartingLine = maxOf(startingLine - CONTEXT_LINES, 0)
                val contextEndingLine = minOf(endingLine + CONTEXT_LINES, document.lineCount - 1)

                val contextStartingOffset = document.getLineStartOffset(contextStartingLine)
                val contextEndingOffset = document.getLineEndOffset(contextEndingLine)
                val contextTextRange = TextRange(contextStartingOffset, contextEndingOffset)
                val contextText = document.getText(contextTextRange)

                excerptBuilder
                    .append("```\n")
                    .append(contextText).append('\n')
                    .append("```\n")
                excerptBuilder
                    .append('\n')
                    .append("From: `").append(path).append(':').append(contextStartingLine)

                if (contextEndingLine != contextStartingLine) {
                    excerptBuilder.append('-').append(contextEndingLine)
                }

                excerptBuilder.append('`')
            }
        }
        return excerptBuilder.toString()
    }

    private fun message(
        containingFile: PsiFile,
        element: PsiElement
    ): String {
        return """
            ${excerpt(containingFile, element)}
            ${className(element)}
            """.trimIndent()
    }

    private fun message(term: OtpErlangObject): String {
        return """
            
            ### Term
            
            $term
            
            """.trimIndent()
    }
}
