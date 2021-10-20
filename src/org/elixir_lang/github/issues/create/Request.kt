package org.elixir_lang.github.issues.create

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.ExceptionUtil

class Request private constructor(val title: String, val body: String) {
    constructor(additionalInfo: String?, throwableList: List<Throwable>, attachmentList: List<Attachment>) : this(TITLE, body(additionalInfo, throwableList, attachmentList))

    companion object {
        private const val TITLE = "[auto-generated]"

        private fun body(additionalInfo: String?, throwableList: List<Throwable>, attachmentList: List<Attachment>): String {
            val stringBuilder = StringBuilder()
            val level = 0
            version(stringBuilder)
            additionalInfo(stringBuilder, level + 1, additionalInfo)
            exceptions(stringBuilder, level + 1, throwableList)
            attachments(stringBuilder, level + 1, attachmentList)
            return stringBuilder.toString()
        }

        private fun version(stringBuilder: StringBuilder) {
            header(stringBuilder, 1, "Version")
            stringBuilder.append(PluginManagerCore.getPlugin(PluginId.getId("org.elixir_lang"))!!.version)
            stringBuilder.append("\n\n")
        }

        private fun additionalInfo(stringBuilder: StringBuilder, level: Int, additionalInfo: String?) {
            if (additionalInfo != null) {
                textSection(stringBuilder, level, "What I was doing", additionalInfo)
            }
        }

        private fun exceptions(stringBuilder: StringBuilder, level: Int, throwableList: List<Throwable>) {
            header(stringBuilder, level, "Exceptions")
            throwableList.forEachIndexed { index, throwable ->
                exception(stringBuilder, level + 1, index, throwable)
            }

        }

        private fun exception(stringBuilder: StringBuilder, level: Int, index: Int, throwable: Throwable) {
            header(stringBuilder, level, "Exception $index")
            message(stringBuilder, level + 1, throwable.message)
            stacktrace(stringBuilder, level + 1, throwable)
        }

        private fun attachment(stringBuilder: StringBuilder, level: Int, attachment: Attachment) {
            header(stringBuilder, level, "`" + attachment.path + "`")
            stringBuilder.append(
                    "Please copy the contents of the above path into this report: files are too long to include in the " +
                            "URL when opening the browser.  You can get the exact contents of that path when the error " +
                            "occurred from the Attachments tab of the IDE Fatal Errors dialog that you had open before " +
                            "clicking the button to submit this issue."
            )
        }

        private fun attachments(stringBuilder: StringBuilder, level: Int, attachmentList: List<Attachment>) {
            if (!attachmentList.isEmpty()) {
                header(stringBuilder, level, "Attachments")
                for (attachment in attachmentList) {
                    attachment(stringBuilder, level + 1, attachment)
                }
            }
        }

        private fun codeBlock(stringBuilder: StringBuilder, code: String) {
            codeFence(stringBuilder)
            stringBuilder.append(code)
            stringBuilder.append('\n')
            codeFence(stringBuilder)
        }

        private fun codeBlockSection(stringBuilder: StringBuilder,
                                     level: Int,
                                     name: String,
                                     code: String) {
            if (code.isNotEmpty()) {
                header(stringBuilder, level, name)
                codeBlock(stringBuilder, code)
            }
        }

        private fun codeFence(stringBuilder: StringBuilder) {
            stringBuilder.append("```\n")
        }

        private fun header(stringBuilder: StringBuilder, level: Int, name: String) {
            stringBuilder.append("\n")
            for (i in 0 until level) {
                stringBuilder.append('#')
            }
            stringBuilder.append(' ')
            stringBuilder.append(name)
            stringBuilder.append("\n\n")
        }

        private fun message(stringBuilder: StringBuilder, level: Int, message: String?) {
            textSection(stringBuilder, level, "Message", message)
        }

        private fun stacktrace(stringBuilder: StringBuilder, level: Int, throwable: Throwable) {
            stacktrace(stringBuilder, level, ExceptionUtil.getThrowableText(throwable))
        }

        private fun stacktrace(stringBuilder: StringBuilder, level: Int, stacktrace: String) {
            codeBlockSection(stringBuilder, level, "Stacktrace", stacktrace)
        }

        private fun textSection(stringBuilder: StringBuilder,
                                level: Int,
                                name: String,
                                text: String?) {
            if (text != null && text.isNotEmpty()) {
                header(stringBuilder, level, name)
                stringBuilder.append(text)
                stringBuilder.append("\n\n")
            }
        }

    }
}
