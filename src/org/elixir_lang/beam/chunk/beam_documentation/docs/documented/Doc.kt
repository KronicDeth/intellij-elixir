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
                if (second != null && second != first) {
                    when (first) {
                        is None -> when (second) {
                            is None -> first
                            is Hidden, is MarkdownByLanguage -> second
                        }
                        is Hidden -> when (second) {
                            is Hidden -> first
                            is None, is MarkdownByLanguage -> second
                        }
                        is MarkdownByLanguage -> when (second) {
                            is None, is Hidden -> second
                            is MarkdownByLanguage -> {
                                val firstFormattedByLanguage = first.formattedByLanguage
                                val secondFormattedByLanguage = second.formattedByLanguage
                                val languageSet = firstFormattedByLanguage.keys + secondFormattedByLanguage.keys

                                val mergedFormattedByLanguage = languageSet.map { language ->
                                    val firstFormatted = firstFormattedByLanguage[language]
                                    val secondFormatted = secondFormattedByLanguage[language]

                                    val mergedFormatted: String = if (firstFormatted != null) {
                                        if (secondFormatted != null && secondFormatted != firstFormatted) {
                                            val (earlier, later) = if (firstFormatted < secondFormatted) {
                                                firstFormatted to secondFormatted
                                            } else {
                                                secondFormatted to firstFormatted
                                            }

                                            "${earlier}\n\n___\n\n${later}"
                                        } else {
                                            firstFormatted
                                        }
                                    } else {
                                        secondFormatted!!
                                    }

                                    language to mergedFormatted
                                }.toMap()

                                MarkdownByLanguage(mergedFormattedByLanguage)
                            }
                        }
                    }
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
