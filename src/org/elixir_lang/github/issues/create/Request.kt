package org.elixir_lang.github.issues.create

import com.intellij.diagnostic.AbstractMessage
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.ExceptionUtil

class Request private constructor(val title: String, val body: String) {
    constructor(additionalInfo: String?, events: Array<IdeaLoggingEvent>) : this(title(additionalInfo, events), body(additionalInfo, events))

    companion object {
        private fun title(additionalInfo: String?, events: Array<IdeaLoggingEvent>): String =
                additionalInfo?.lineSequence()?.first() ?: title(events)

        private fun title(events: Array<IdeaLoggingEvent>): String = title(events.first())
        private fun title(event: IdeaLoggingEvent): String = title(event.throwable)
        private fun title(throwable: Throwable): String {
            val lines = ExceptionUtil.getThrowableText(throwable).lineSequence()
            val message = lines.takeWhile { !it.startsWith("\tat ") }.joinToString()
            val location =
                    lines
                            .filter { it.startsWith("\tat ") }
                            .filter { !it.startsWith("\tat org.elixir_lang.errorreport.Logger") }
                            .filter { !it.startsWith("\tat com.intellij.openapi.diagnostic.Logger") }
                            .first()

            return "$message $location"
        }

        private fun body(additionalInfo: String?, events: Array<IdeaLoggingEvent>): String {
            val stringBuilder = StringBuilder()
            val level = 0
            version(stringBuilder)
            additionalInfo(stringBuilder, level + 1, additionalInfo)
            events(stringBuilder, level + 1, events)
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

        private fun events(stringBuilder: StringBuilder, level: Int, events: Array<IdeaLoggingEvent>) {
            if (events.size == 1) {
                val event = events.single()
                header(stringBuilder, level, "Event")
                message(stringBuilder, level + 1, event.message)
                exception(stringBuilder, level + 1, event)
                attachments(stringBuilder, level + 1, event)
            } else {
                events.forEachIndexed { index, event ->
                    header(stringBuilder, level, "Event $index")
                    message(stringBuilder, level + 1, event.message)
                    exception(stringBuilder, level + 1, event)
                    attachments(stringBuilder, level + 1, event)
                }
            }
        }

        private fun exception(stringBuilder: StringBuilder, level: Int, event: IdeaLoggingEvent) {
            event.throwable?.let { throwable ->
                exception(stringBuilder, level, throwable)
            }
        }

        private fun exception(stringBuilder: StringBuilder, level: Int, throwable: Throwable) {
            header(stringBuilder, level, "Exception")
            message(stringBuilder, level + 1, throwable.message)
            stacktrace(stringBuilder, level + 1, throwable)
        }

        private fun attachments(stringBuilder: StringBuilder, level: Int, event: IdeaLoggingEvent) {
            event.data?.let { it as? AbstractMessage }?.includedAttachments.takeUnless { it.isNullOrEmpty() }?.let { attachments ->
                attachments(stringBuilder, level, attachments)
            }
        }

        private fun attachments(stringBuilder: StringBuilder, level: Int, attachmentList: List<Attachment>) {
            header(stringBuilder, level, "Attachments")
            for (attachment in attachmentList) {
                attachment(stringBuilder, level + 1, attachment)
            }
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

        private fun codeBlockSection(stringBuilder: StringBuilder,
                                     level: Int,
                                     name: String,
                                     code: String) {
            if (code.isNotEmpty()) {
                header(stringBuilder, level, name)
                codeBlock(stringBuilder, code)
            }
        }

        private fun codeBlock(stringBuilder: StringBuilder, code: String) {
            codeFence(stringBuilder)
            stringBuilder.append(code)
            stringBuilder.append('\n')
            codeFence(stringBuilder)
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
            header(stringBuilder, level, "Stacktrace")

            codeFence(stringBuilder)

            val lines = ExceptionUtil.getThrowableText(throwable).lineSequence()
            val lastPluginLine = lines.indexOfLast { it.contains("at org.elixir_lang.") }
            lines.take(lastPluginLine + 1).forEach { stringBuilder.append(it).append('\n') }

            codeFence(stringBuilder)
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
