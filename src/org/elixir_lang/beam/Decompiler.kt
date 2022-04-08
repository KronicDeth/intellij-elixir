package org.elixir_lang.beam

import com.ericsson.otp.erlang.OtpErlangBinary
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.BinaryFileDecompiler
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.NameArity
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.MacroNameArity.MACRO_ORDER
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.CallDefinitions
import org.elixir_lang.beam.chunk.Chunk.TypeID
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.beam_documentation.Documentation
import org.elixir_lang.beam.chunk.beam_documentation.docs.Documented
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.Hidden
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.MarkdownByLanguage
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.None
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.Spec
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.Type
import org.elixir_lang.beam.decompiler.*
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.semantic.type.definition.Builtin
import java.util.*

class Decompiler : BinaryFileDecompiler {
    override fun decompile(virtualFile: VirtualFile): CharSequence = decompiled(from(virtualFile))

    companion object {
        private val logger = Logger.getInstance(Decompiler::class.java)
        private val HEADER_NAME_BY_MACRO: Map<String, String> = mapOf(Function.DEFMACRO to "Macros", Function.DEFMACROP to "Private Macros", Function.DEF to "Functions", Function.DEFP to "Private Functions")
        private val MACRO_NAME_ARITY_DECOMPILER_LIST: List<org.elixir_lang.beam.decompiler.MacroNameArity> = listOf(
                InfixOperator,
                PrefixOperator,
                Unquoted,
                SignatureOverride,
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

                        val debugInfo = beam.debugInfo()

                        appendTypes(decompiled, moduleName, debugInfo, documentation)
                        appendCallDefinitions(decompiled, beam, atoms, debugInfo, documentation)
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

        private fun appendTypes(decompiled: StringBuilder,
                                moduleName: String,
                                debugInfo: DebugInfo?,
                                documentation: Documentation?) {
            // fake built-in types being defined in `erlang`, so that built-in type resolution can point to a single location
            if (moduleName == "erlang") {
                decompiled
                        .append('\n')
                        .append("  # Built-in types (not actually declared in :erlang)\n")
                        .append('\n')

                for ((name, arity) in Builtin.SORTED_NAME_ARITIES) {
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

            documentation?.docs?.typeDocumentedByArityByName()?.let { appendTypes(decompiled, it) } ?:
            debugInfo?.let { appendTypes(decompiled, it, Options()) }
        }

        private fun appendTypes(decompiled: StringBuilder, documentedByArityByName: Map<String, Map<Int, Documented>>) {
            if (documentedByArityByName.isNotEmpty()) {
                decompiled
                        .append('\n')
                        .append("  # Types\n")
                        .append('\n')
            }

            for ((name, documentedByArity) in documentedByArityByName) {
                for ((arity, documented) in documentedByArity) {
                    documented.doc?.let { doc ->
                        when (doc) {
                            is None -> Unit
                            is Hidden -> appendDocumentation(decompiled, "typedoc", false)
                            is MarkdownByLanguage -> {
                                for ((_language, formatted) in doc.formattedByLanguage) {
                                    appendDocumentation(decompiled, "typedoc", formatted)
                                }
                            }
                        }
                    }

                    val signatures = documented.signatures

                    if (signatures.isNotEmpty()) {
                        TODO()
                    } else {
                        decompiled.append("  @type ").append(name).append('(')

                        for (i in 1..arity) {
                            if (i > 1) {
                                decompiled.append(", ")
                            }

                            decompiled.append("type").append(i)
                        }

                        decompiled.append(") :: ...\n")
                    }
                }
            }
        }

        private fun appendTypes(decompiled: StringBuilder, debugInfo: DebugInfo, options: Options) {
            when (debugInfo) {
                is AbstractCodeCompileOptions -> appendTypes(decompiled, debugInfo, options)
                else -> Unit
            }
        }

        private fun appendTypes(decompiled: StringBuilder, abstractCodeCompileOptions: AbstractCodeCompileOptions, decompilerOptions: Options) {
            val macroStringAttributesByElixirAttributeName = abstractCodeCompileOptions.attributes.macroStringAttributes.filterIsInstance<Type>().groupBy { it.elixirAttributeName }

            appendTypes(decompiled, macroStringAttributesByElixirAttributeName, "type", "Types", decompilerOptions)
            appendTypes(decompiled, macroStringAttributesByElixirAttributeName, "typep", "Private Types", decompilerOptions)
        }

        private fun appendTypes(decompiled: StringBuilder,
                                macroStringAttributesByElixirAttributeName: Map<String, List<Type>>,
                                elixirAttributeName: String,
                                title: String,
                                options: Options) {
            val macroStringAttributes = macroStringAttributesByElixirAttributeName[elixirAttributeName]

            if (!macroStringAttributes.isNullOrEmpty()) {
                appendHeader(decompiled, title)

                for (macroStringAttribute in macroStringAttributes.sortedBy { it.name }) {
                    decompiled
                            .append('\n')
                            .append("  ").append(macroStringAttribute.toMacroString(options)).append('\n')
                }
            }
        }

        private fun appendCallDefinitions(decompiled: StringBuilder,
                                          beam: Beam,
                                          atoms: Atoms,
                                          debugInfo: DebugInfo?,
                                          documentation: Documentation?) {
            val macroNameAritySortedSetByMacro = CallDefinitions.macroNameAritySortedSetByMacro(beam, atoms)
            appendCallDefinitions(decompiled, macroNameAritySortedSetByMacro, debugInfo, documentation)
        }

        private fun macroToHeaderName(macro: String): String = HEADER_NAME_BY_MACRO[macro]!!

        private fun appendCallDefinitions(decompiled: StringBuilder,
                                          macroNameAritySortedSetByMacro: Map<String, SortedSet<MacroNameArity>>,
                                          debugInfo: DebugInfo?,
                                          documentation: Documentation?) {
            val options = options(macroNameAritySortedSetByMacro)

            for (macro in MACRO_ORDER) {
                val macroNameAritySortedSet = macroNameAritySortedSetByMacro[macro]

                if (macroNameAritySortedSet != null && options.decompileMacro(macro)) {
                    for ((index, macroNameArity) in macroNameAritySortedSet.withIndex()) {
                        if (index == 0) {
                            appendHeader(decompiled, macroToHeaderName(macro))
                        }
                        decompiled.append("\n")

                        if (documentation != null) {
                            documentation.docs?.let { docs ->
                                docs.deprecated(macroNameArity)?.let { deprecated ->
                                    when (deprecated) {
                                        is OtpErlangBinary ->
                                            decompiled.append("\n  @deprecated \"\"\"\n")
                                                    .append("  ")
                                                    .append(String(deprecated.binaryValue()))
                                                    .append("\n  \"\"\"\n\n")
                                        else -> {
                                            logger.error("Don't know how to decompiled @deprecated value (${inspect(deprecated)})")
                                        }
                                    }
                                }

                                docs.doc(macroNameArity)?.let { doc ->
                                    when (doc) {
                                        is None -> Unit
                                        is Hidden -> appendDocumentation(decompiled, "doc", false)
                                        is MarkdownByLanguage -> {
                                            for ((_language, formatted) in doc.formattedByLanguage) {
                                                appendDocumentation(decompiled, "doc", formatted)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        appendSpec(decompiled, macroNameArity, debugInfo, options)
                        appendMacroNameArity(decompiled, macroNameArity, debugInfo, documentation, options)
                    }
                }
            }
        }

        private const val definitionLimit = 500

        private fun options(macroNameAritySortedSet: Map<String, SortedSet<MacroNameArity>>): Options {
            val defmacroCount = macroNameAritySortedSet[DEFMACRO]?.size ?: 0
            val defCount = macroNameAritySortedSet[DEF]?.size ?: 0
            val publicCount = defmacroCount + defCount

            return if (publicCount > definitionLimit) {
               Options(decompileBodies = false, decompileMacros = setOf(DEFMACRO, DEF))
            } else {
                val defmacropCount = macroNameAritySortedSet[DEFMACROP]?.size ?: 0
                val defpCount = macroNameAritySortedSet[DEFP]?.size ?: 0
                val privateCount = defmacropCount + defpCount

                if (publicCount + privateCount > definitionLimit) {
                    Options(decompileBodies = false, decompileMacros = setOf(DEFMACRO, DEF))
                } else {
                    Options(decompileBodies = true, decompileMacros = setOf(DEFMACRO, DEFMACROP, DEF, DEFP))
                }
            }
        }

        private fun appendHeader(decompiled: StringBuilder, name: String) {
            decompiled
                    .append("\n")
                    .append("  # ")
                    .append(name)
                    .append("\n")
        }

        private fun appendDocumentation(decompiled: StringBuilder, moduleAttribute: String, shown: Boolean) {
            decompiled.append("  @").append(moduleAttribute).append(' ').append(shown).append('\n')
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

        private fun appendSpec(decompiled: StringBuilder,
                               macroNameArity: MacroNameArity,
                               debugInfo: DebugInfo?,
                               options: Options) {
            when (debugInfo) {
                is AbstractCodeCompileOptions -> appendSpec(decompiled, macroNameArity, debugInfo, options)
                else -> Unit
            }
        }

        private fun appendSpec(decompiled: StringBuilder,
                               macroNameArity: MacroNameArity,
                               debugInfo: AbstractCodeCompileOptions,
                               options: Options) {
            debugInfo
                    .attributes.macroStringAttributes
                    .find {
                        it is Spec &&
                                it.name == macroNameArity.name &&
                                it.arity == macroNameArity.arity.toBigInteger()
                    }
                    ?.let { spec ->
                        decompiled.append(spec.toMacroString(options).prependIndentToNonBlank()).append('\n')
                    }
        }

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity,
                                         debugInfo: DebugInfo?,
                                         documentation: Documentation?,
                                         options: Options) =
            appendMacroNameArity(decompiled, macroNameArity, debugInfo, options) ||
                    appendMacroNameArity(decompiled, macroNameArity, documentation)

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity,
                                         debugInfo: DebugInfo?,
                                         options: Options): Boolean =
                when (debugInfo) {
                    is AbstractCodeCompileOptions ->
                        appendMacroNameArity(decompiled, macroNameArity, debugInfo, options)
                    is org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1 ->
                        appendMacroNameArity(decompiled, macroNameArity, debugInfo, options)
                    else -> false
                }

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity,
                                         debugInfo: AbstractCodeCompileOptions,
                                         options: Options): Boolean =
                when (macroNameArity.macro) {
                    DEF, DEFP -> {
                        val function = debugInfo.functions.byNameArity[macroNameArity.toNameArity()]

                        if (function != null) {
                            decompiled.append(function.toMacroString(options).prependIndentToNonBlank()).append('\n')

                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity,
                                         debugInfo: org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1,
                                         options: Options): Boolean =
                debugInfo.definitions?.get(macroNameArity)?.toMacroString(options)?.let { macroString ->
                    decompiled.append(macroString.prependIndentToNonBlank()).append('\n')

                    true
                } ?: false

        private fun appendMacroNameArity(decompiled: StringBuilder,
                                         macroNameArity: MacroNameArity,
                                         documentation: Documentation?): Boolean {
            val beamLanguage = documentation?.beamLanguage ?: "elixir"
            val decompiler = decompiler(beamLanguage, macroNameArity.toNameArity())

            return if (decompiler != null) {
                // The signature while easier for users to read are not proper code for those that need to use unquote, so
                // only allow signatures for default decompiler
                if (decompiler === Default.INSTANCE) {
                    val signatures =  documentation?.takeIf { it.beamLanguage == "elixir" }?.docs?.signatures(macroNameArity)

                    if (signatures != null && signatures.isNotEmpty()) {
                        for (signature in signatures) {
                            decompiled.append("  ").append(macroNameArity.macro).append(' ')
                            decompiled.append(signature)
                            decompiled.append(" do\n    # body not decompiled\n  end\n")
                        }
                    } else {
                        decompiler.append(decompiled, macroNameArity)
                    }
                } else {
                    decompiler.append(decompiled, macroNameArity)
                }

                true
            } else {
                false
            }
        }

        fun decompiler(beamLanguage: String, nameArity: NameArity): org.elixir_lang.beam.decompiler.MacroNameArity? {
            var accepted: org.elixir_lang.beam.decompiler.MacroNameArity? = null

            for (decompiler in MACRO_NAME_ARITY_DECOMPILER_LIST) {
                if (decompiler.accept(beamLanguage, nameArity)) {
                    accepted = decompiler
                    break
                }
            }

            if (accepted == null) {
                error(nameArity)
            }

            return accepted
        }

        private fun error(nameArity: NameArity) {
            logger.error("No decompiler for MacroNameArity ($nameArity)")
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

fun String.prependIndentToNonBlank(indent: String = "  "): String =
        lineSequence()
                .map {
                    when {
                        it.isBlank() -> it
                        else -> indent + it
                    }
                }
                .joinToString("\n")
