package org.elixir_lang.documentation

sealed class FetchedDocs(open val moduleName: String, open val docsMarkdown: String) {
    data class FunctionOrMacroDocumentation(override val moduleName: String,
                                            override val docsMarkdown: String,
                                            val kind: String,
                                            val methodName: String,
                                            val deprecated: String?,
                                            val arguments: List<String>) : FetchedDocs(moduleName, docsMarkdown)

    data class ModuleDocumentation(override val moduleName: String,
                                   override val docsMarkdown: String) : FetchedDocs(moduleName, docsMarkdown)
}

