package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

class Docs(val docList: List<Doc>) {
    operator fun get(index: Int): Doc = docList[index]
    operator fun get(doc: Doc): Int = docList.indexOf(doc)
    fun size(): Int = docList.size

    companion object {
        private val logger = Logger.getInstance(Docs::class.java)

        fun from(term: OtpErlangObject?): Docs? =
                when (term) {
                    null -> null
                    is OtpErlangList -> from(term)
                    else -> {
                        logger.error("""
                                     :docs is a `${term.javaClass}`, not an `OtpErlangList`

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """)

                        null
                    }
                }

        private fun from(list: OtpErlangList): Docs = list.mapNotNull { Doc.from(it) }.let { Docs(it) }

    }
}
