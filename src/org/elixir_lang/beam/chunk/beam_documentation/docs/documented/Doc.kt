package org.elixir_lang.beam.chunk.beam_documentation.docs.documented

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangMap
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

sealed class Doc {
    companion object {
        private val logger = Logger.getInstance(Doc::class.java)

        fun from(element: OtpErlangObject): Doc? =
                when (element) {
                    is OtpErlangAtom -> from(element)
                    is OtpErlangMap -> from(element)
                    else -> {
                        logger.error("Don't know how to decode docs with term ${inspect(element)}")

                        null
                    }
                }

        private fun from(element: OtpErlangAtom): Doc? =
                when (element.atomValue()) {
                    "none" -> None
                    "hidden" -> Hidden
                    else -> {
                        logger.error("Don't know how to decode docs with atom ${inspect(element)}")

                        null
                    }
                }

        private fun from(element: OtpErlangMap): Doc? =
                element
                        .entrySet()
                        .mapNotNull {
                            languageFrom(it.key)?.let { language ->
                                formattedFrom(it.value)?.let { formatted ->
                                    language to formatted
                                }
                            }
                        }
                        .toMap()
                        .let { MarkdownByLanguage(it) }

        private fun languageFrom(key: OtpErlangObject): String? =
                when (key) {
                    is OtpErlangBinary -> String(key.binaryValue())
                    else -> {
                        logger.error("Don't know how to decode doc language from ${inspect(key)}")

                        null
                    }
                }

        private fun formattedFrom(value: OtpErlangObject): String? =
                when (value) {
                    is OtpErlangBinary -> String(value.binaryValue())
                    else -> {
                        logger.error("Don't know how to decode doc formatted from ${inspect(value)}")

                        null
                    }
                }

        fun merge(first: Doc?, second: Doc?): Doc? =
            if (first != null) {
                if (second != null) {
                    logger.error("Don't know how to merge Docs (${first} and ${second})")

                    null
                } else {
                    first
                }
            } else {
                second
            }
    }
}

object None : Doc()
object Hidden : Doc()

data class MarkdownByLanguage(val formattedByLanguage: Map<String, String>) : Doc() {
    companion object {
        fun english(markdown: String): MarkdownByLanguage =
            MarkdownByLanguage(mapOf("en" to markdown))
    }
}
