package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.MacroNameArity
import org.elixir_lang.beam.chunk.beam_documentation.docs.Documented
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.Doc
import java.util.*

class Docs(private val documentedByArityByNameByKind: MutableMap<String, TreeMap<String, TreeMap<Int, Documented>>>) {
    fun deprecated(macroNameArity: MacroNameArity): OtpErlangObject? = documented(macroNameArity)?.deprecated()

    fun doc(macroNameArity: MacroNameArity): Doc? = documented(macroNameArity)?.doc

    private fun documented(macroNameArity: MacroNameArity): Documented? =
            kind(macroNameArity)?.let { kind ->
                documented(kind, macroNameArity.name, macroNameArity.arity)
            }

    fun documented(kind: String, name: String, arity: Int): Documented? =
            documentedByArityByNameByKind[kind]?.get(name)?.get(arity)

    fun signatures(macroNameArity: MacroNameArity): List<String>? = documented(macroNameArity)?.signatures
    fun typeDocumentedByArityByName(): Map<String, Map<Int, Documented>> =
            documentedByArityByNameByKind["type"] ?: emptyMap()

    companion object {
        val logger = Logger.getInstance(Docs::class.java)

        fun from(list: OtpErlangList): Docs {
            val documentedByArityByNameByKind = mutableMapOf<String, TreeMap<String, TreeMap<Int, Documented>>>()

            for (element in list) {
                Documented.from(element)?.let { documented ->
                    documentedByArityByNameByKind
                            .computeIfAbsent(documented.kind) { TreeMap() }
                            .computeIfAbsent(documented.name) { TreeMap() }
                            .put(documented.arity, documented)
                }
            }

            return Docs(documentedByArityByNameByKind)
        }

        private fun kind(macroNameArity: MacroNameArity): String? =
                when (val macro = macroNameArity.macro) {
                    "def", "defp" -> "function"
                    "defmacro", "defmacrop" -> "macro"
                    else -> {
                        logger.error("Don't know how to convert macro (${macro}) to kind")

                        null
                    }
                }
    }
}
