package org.elixir_lang.beam

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.BinaryFileDecompiler
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.CallDefinitions
import org.elixir_lang.beam.chunk.Chunk.TypeID
import org.elixir_lang.beam.chunk.beam_documentation.Documentation
import org.elixir_lang.beam.decompiler.*
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.reference.resolver.Type.BUILTIN_ARITY_BY_NAME
import java.util.*
import java.util.function.Consumer

class Decompiler : BinaryFileDecompiler {
    override fun decompile(virtualFile: VirtualFile): CharSequence = decompiled(from(virtualFile))

    companion object {
        private val HEADER_NAME_BY_MACRO: Map<String, String> = mapOf(Function.DEFMACRO to "Macros", Function.DEFMACROP to "Private Macros", Function.DEF to "Functions", Function.DEFP to "Private Functions")
        private val MACRO_NAME_ARITY_DECOMPILER_LIST: List<org.elixir_lang.beam.decompiler.MacroNameArity> = listOf(
                InfixOperator.INSTANCE,
                PrefixOperator.INSTANCE,
                Unquoted.INSTANCE,
                SignatureOverride.INSTANCE,
                Default.INSTANCE
        )

        private const val DECOMPILATION_ERROR = "# Decompilation Error: "

        private fun decompiled(beam: Beam?): CharSequence {
            val decompiled = StringBuilder()

            return if (beam != null) {
                val atoms = beam.atoms()
                if (atoms != null) {
                    val moduleName = atoms.moduleName()
                    if (moduleName != null) {
                        val defmoduleArgument = defmoduleArgument(moduleName)
                        decompiled
                                .append("# Source code recreated from a .beam file by IntelliJ Elixir\n")
                                .append("defmodule ")
                                .append(defmoduleArgument)
                                .append(" do\n")
                        val documentation = beam.documentation()

                        if (documentation != null) {
                            documentation.moduleDocs?.englishDocs?.let { moduleDocs ->
                                appendDocumentation(decompiled, "moduledoc", moduleDocs)
                            }
                        }

                        appendTypes(decompiled, moduleName)
                        appendCallDefinitions(decompiled, beam, atoms, documentation)
                        decompiled.append("end\n")
                    } else {
                        decompiled
                                .append(DECOMPILATION_ERROR)
                                .append("No module name found in ")
                                .append(TypeID.ATOM)
                                .append(" chunk in BEAM")
                    }
                } else {
                    decompiled
                            .append(DECOMPILATION_ERROR)
                            .append("No ")
                            .append(TypeID.ATOM)
                            .append(" chunk found in BEAM")
                }
            } else {
                decompiled.append(DECOMPILATION_ERROR).append("BEAM format could not be read")
            }
        }

        private fun appendTypes(decompiled: StringBuilder, moduleName: String) {
            // fake built-in types being defined in `erlang`, so that built-in type resolution can point to a single location
            if (moduleName == "erlang") {
                decompiled
                        .append('\n')
                        .append("  # Built-in types (not actually declared in :erlang)\n")
                        .append('\n')

                for (name in BUILTIN_ARITY_BY_NAME.keys.sorted()) {
                    for (arity in BUILTIN_ARITY_BY_NAME[name]!!.sorted()) {
                        decompiled.append("  @type ").append(name).append('(')

                        if ((name == "maybe_improper_list" ||
                                        name == "nonempty_improper_list" ||
                                        name == "nonempty_maybe_improper_list")
                                && arity == 2) {
                            decompiled.append("element :: term(), tail :: term()")
                        } else if (name == "non_empty_list" && arity == 1) {
                            decompiled.append("element :: term()")
                        } else {
                            for (i in 1..arity) {
                                if (i > 1) {
                                    decompiled.append(", ")
                                }

                                decompiled.append("type").append(i)
                            }
                        }

                        decompiled.append(") :: ...\n")
                    }
                }
            }
        }

        private fun appendCallDefinitions(decompiled: StringBuilder,
                                          beam: Beam,
                                          atoms: Atoms, documentation: Documentation?) {
            val macroNameAritySortedSet = CallDefinitions.macroNameAritySortedSet(beam, atoms)
            appendCallDefinitions(decompiled, macroNameAritySortedSet, documentation)
        }

        private fun macroToHeaderName(macro: String): String = HEADER_NAME_BY_MACRO[macro]!!

        private fun appendCallDefinitions(decompiled: StringBuilder,
                                          macroNameAritySortedSet: SortedSet<MacroNameArity>, documentation: Documentation?) {
            var lastMacroNameArity: MacroNameArity? = null
            for (macroNameArity in macroNameAritySortedSet) {
                val macro = macroNameArity.macro
                if (lastMacroNameArity == null) {
                    appendHeader(decompiled, macroToHeaderName(macro))
                } else if (lastMacroNameArity.macro != macro) {
                    appendHeader(decompiled, macroToHeaderName(macro))
                }
                decompiled.append("\n")
                if (documentation != null) {
                    val functionMetadata = if (documentation.docs != null) documentation.docs!!.getFunctionMetadataOrSimilar(macroNameArity.name, macroNameArity.arity) else null
                    functionMetadata?.stream()?.filter { (name) -> name == "deprecated" }?.forEach { (_, value) ->
                        if (value != null) {
                            decompiled.append("\n  @deprecated \"\"\"\n")
                                    .append("  ")
                                    .append(value)
                                    .append("\n  \"\"\"\n\n")
                        } else {
                            decompiled.append("\n  @deprecated\n")
                        }
                    }
                    val functionDocs = if (documentation.docs != null) documentation.docs!!.getFunctionDocs(macroNameArity.name, macroNameArity.arity) else null
                    functionDocs?.forEach(Consumer { (_, documentationText) -> appendDocumentation(decompiled, "doc", documentationText) })
                }
                appendMacroNameArity(decompiled, macroNameArity, documentation)
                lastMacroNameArity = macroNameArity
            }
        }

        private fun appendHeader(decompiled: StringBuilder, name: String) {
            decompiled
                    .append("\n")
                    .append("  # ")
                    .append(name)
                    .append("\n")
        }

        private fun appendDocumentation(decompiled: StringBuilder, moduleAttribute: String, text: String) {
            val safePromoterTerminator = safePromoterTerminator(text)
            val promoterTerminator: String = safePromoterTerminator ?: "\"\"\""
            decompiled
                    .append("  @")
                    .append(moduleAttribute)
                    // Use ~S sigil to stop interpolation in docs as an interpolation stored in the docs was
                    // escaped in the original source.
                    .append(" ~S")
                    .append(promoterTerminator)
                    .append('\n')
            appendDocumentationText(decompiled, safePromoterTerminator, text)
            decompiled
                    .append("\n  ")
                    .append(promoterTerminator)
                    .append('\n')
        }

        private const val CHARLIST_HEREDOC_PROMOTER_TERMINATOR = "'''"
        private const val STRING_HEREDOC_PROMOTER_TERMINATOR = "\"\"\""
        private fun safePromoterTerminator(text: String): String? {
            val containsCharlistHeredoc = text.contains(CHARLIST_HEREDOC_PROMOTER_TERMINATOR)
            val containsStringHeredoc = text.contains(STRING_HEREDOC_PROMOTER_TERMINATOR)

            return if (containsCharlistHeredoc && containsStringHeredoc) {
                null
            } else if (containsCharlistHeredoc) {
                STRING_HEREDOC_PROMOTER_TERMINATOR
            } else if (containsStringHeredoc) {
                CHARLIST_HEREDOC_PROMOTER_TERMINATOR
            } else {
                // Default to String since it is what actual developers would use most often
                STRING_HEREDOC_PROMOTER_TERMINATOR
            }
        }

        private fun appendDocumentationText(decompiled: StringBuilder, safePromoterTerminator: String?, text: String) {
            val lines = text.split("\n").toTypedArray()
            val lastI = lines.size - 1

            for (i in lines.indices) {
                val line = lines[i]
                val stripped: String = line.trimEnd()

                if (stripped.isNotEmpty()) {
                    decompiled.append("  ")

                    if (safePromoterTerminator == null) {
                        decompiled.append(stripped.replace(STRING_HEREDOC_PROMOTER_TERMINATOR, "\"\"\""))
                    } else {
                        decompiled.append(stripped)
                    }
                }

                if (i != lastI) {
                    decompiled.append("\n")
                }
            }
        }

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity, documentation: Documentation?) {
            val decompiler = decompiler(macroNameArity)
            if (decompiler != null) {
                // The signature while easier for users to read are not proper code for those that need to use unquote, so
                // only allow signatures for default decompiler
                if (decompiler === Default.INSTANCE) {
                    val signaturesFromDocs = if (documentation != null && documentation.beamLanguage == "elixir") documentation.docs!!.getSignatures(macroNameArity.name, macroNameArity.arity) else null

                    if (signaturesFromDocs != null && signaturesFromDocs.isNotEmpty()) {
                        decompiled.append("  ").append(macroNameArity.macro).append(' ')
                        val optional = signaturesFromDocs.stream().findFirst()
                        decompiled.append(optional.get())
                        decompiled.append(" do\n    # body not decompiled\n  end\n")
                    } else {
                        decompiler.append(decompiled, macroNameArity)
                    }
                } else {
                    decompiler.append(decompiled, macroNameArity)
                }
            }
        }

        fun decompiler(macroNameArity: MacroNameArity): org.elixir_lang.beam.decompiler.MacroNameArity? {
            var accepted: org.elixir_lang.beam.decompiler.MacroNameArity? = null

            for (decompiler in MACRO_NAME_ARITY_DECOMPILER_LIST) {
                if (decompiler.accept(macroNameArity)) {
                    accepted = decompiler
                    break
                }
            }

            if (accepted == null) {
                error(macroNameArity)
            }

            return accepted
        }

        private fun error(macroNameArity: MacroNameArity) {
            val logger = Logger.getInstance(Decompiler::class.java)
            val message = "No decompiler for MacroNameArity ($macroNameArity)"
            logger.error(message)
        }

        fun defmoduleArgument(moduleName: String): String = if (moduleName.startsWith(Module.ELIXIR_PREFIX)) {
            moduleName.substring(Module.ELIXIR_PREFIX.length)
        } else {
            ":" + moduleNameToAtomName(moduleName)
        }

        private fun moduleNameToAtomName(moduleName: String): String = if (moduleName.contains("-")) {
            "\"" + moduleName + "\""
        } else {
            moduleName
        }
    }
}
