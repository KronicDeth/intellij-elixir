package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger

data class BeamModuleDoc(val language: String, val text: String)

class ModuleDocs(private val docsMap: OtpErlangMap) {
    val docsByLanguage: Iterable<BeamModuleDoc> by lazy { fetchModuleDocs() }
    val englishDocs: String? by lazy {
        docsByLanguage
            .filter { it.language == "en" }
            .map { it.text }
            .firstOrNull()
    }

    private fun fetchModuleDocs() : Iterable<BeamModuleDoc> {
        return docsMap
                .entrySet()
                .mapNotNull { entry ->
                    val key = entry.key.toStringValue() ?: return@mapNotNull null
                    val value = entry.value.toStringValue() ?: return@mapNotNull null
                    BeamModuleDoc(key, value)
                }
    }

    companion object {
        private val LOGGER = Logger.getInstance(ModuleDocs::class.java)

        fun from(term: OtpErlangMap?): ModuleDocs? =
                if (term != null) ModuleDocs(term) else null
    }

    /**
     * Converts an Erlang term to a String.
     *
     * Handles:
     * - `OtpErlangBinary` -- Elixir-style binaries (UTF-8 strings)
     * - `OtpErlangList` of integers -- Erlang-style charlists
     * - `OtpErlangList` of tuples -- `application/erlang+html` structured docs (unsupported, returns null)
     */
    private fun OtpErlangObject.toStringValue(): String? =
        when (this) {
            is OtpErlangBinary -> String(binaryValue(), Charsets.UTF_8)
            is OtpErlangList -> {
                try {
                    stringValue()
                } catch (_: Exception) {
                    // Not a charlist -- treat as application/erlang+html structured doc (OTP ≤26)
                    ErlangHtmlRenderer.render(this)
                }
            }
            else -> {
                LOGGER.debug("Don't know how to decode module doc value from ${this.javaClass.simpleName}")
                null
            }
        }
}
