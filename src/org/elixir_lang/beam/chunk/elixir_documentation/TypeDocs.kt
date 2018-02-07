package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

class TypeDocs(val typeDocList: List<TypeDoc>) {
    operator fun get(index: Int): TypeDoc = typeDocList[index]
    operator fun get(typeDoc: TypeDoc): Int = typeDocList.indexOf(typeDoc)
    fun size(): Int = typeDocList.size

    companion object {
        private val logger = Logger.getInstance(TypeDocs::class.java)

        fun from(term: OtpErlangObject?): TypeDocs? =
            when (term) {
                null -> null
                is OtpErlangList -> from(term)
                else -> {
                    logger.error("""
                                 :type_docs is a `${term.javaClass}, not an `OtpErlangList`

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }
            }

        private fun from(list: OtpErlangList): TypeDocs =
                list.mapNotNull { TypeDoc.from(it) }.let { TypeDocs(it) }
    }
}
