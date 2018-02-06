package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

class CallbackDocs(val callbackDocList: List<CallbackDoc>) {
    operator fun get(callbackDoc: CallbackDoc): Int = callbackDocList.indexOf(callbackDoc)
    operator fun get(index: Int): CallbackDoc = callbackDocList[index]
    fun size(): Int = callbackDocList.size

    companion object {
        private val logger = Logger.getInstance(CallbackDocs::class.java)

        fun from(term: OtpErlangObject?): CallbackDocs? =
                when (term) {
                    null -> null
                    is OtpErlangList -> from(term)
                    else -> {
                        logger.error("""
                                     :callback_docs is a `${term.javaClass}`, not an `OtpErlangList`

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """.trimIndent())

                        null
                    }
                }

        private fun from(list: OtpErlangList): CallbackDocs =
                list.mapNotNull { CallbackDoc.from(it) }.let { CallbackDocs(it) }
    }
}
