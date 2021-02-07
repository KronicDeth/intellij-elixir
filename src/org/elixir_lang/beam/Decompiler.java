package org.elixir_lang.beam;

import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.beam_documentation.Documentation;
import org.elixir_lang.beam.chunk.CallDefinitions;
import org.elixir_lang.beam.chunk.beam_documentation.Doc;
import org.elixir_lang.beam.chunk.beam_documentation.FunctionMetadata;
import org.elixir_lang.beam.decompiler.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static org.elixir_lang.beam.chunk.Chunk.TypeID.ATOM;
import static org.elixir_lang.psi.call.name.Function.*;
import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    private static final Map<String, String> HEADER_NAME_BY_MACRO = new HashMap<>();

    static {
        HEADER_NAME_BY_MACRO.put(DEFMACRO, "Macros");
        HEADER_NAME_BY_MACRO.put(DEFMACROP, "Private Macros");
        HEADER_NAME_BY_MACRO.put(DEF, "Functions");
        HEADER_NAME_BY_MACRO.put(DEFP, "Private Functions");
    }

    public static final List<org.elixir_lang.beam.decompiler.MacroNameArity> MACRO_NAME_ARITY_DECOMPILER_LIST =
            new ArrayList<org.elixir_lang.beam.decompiler.MacroNameArity>();

    static {
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(InfixOperator.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(PrefixOperator.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(Unquoted.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(SignatureOverride.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(Default.INSTANCE);
    }

    @NotNull
    private static CharSequence decompiled(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") @NotNull Optional<Beam> beamOptional
    ) {
        StringBuilder decompiled = new StringBuilder("# Decompilation Error: ");

        if (beamOptional.isPresent()) {
            Beam beam = beamOptional.get();
            Atoms atoms = beam.atoms();

            if (atoms != null) {
                String moduleName = atoms.moduleName();
                if (moduleName != null) {
                    String defmoduleArgument = defmoduleArgument(moduleName);

                    decompiled = new StringBuilder(
                            "# Source code recreated from a .beam file by IntelliJ Elixir\n"
                    )
                            .append("defmodule ")
                            .append(defmoduleArgument)
                            .append(" do\n");

                    Documentation documentation = beam.documentation();
                    if (documentation != null) {
                        String moduleDocs = documentation.getModuleDocs() != null
                                ? documentation.getModuleDocs().getEnglishDocs()
                                : null;

                        if (moduleDocs != null) {
                            appendDocumentation(decompiled, "moduledoc", moduleDocs);
                        }
                    }

                    appendCallDefinitions(decompiled, beam, atoms, documentation);

                    decompiled.append("end\n");
                } else {
                    decompiled.append("No module name found in ").append(ATOM).append(" chunk in BEAM");
                }
            } else {
                decompiled.append("No ").append(ATOM).append(" chunk found in BEAM");
            }
        } else {
            decompiled.append("BEAM format could not be read");
        }

        return decompiled;
    }

    private static void appendCallDefinitions(@NotNull StringBuilder decompiled,
                                              @NotNull Beam beam,
                                              @NotNull Atoms atoms, Documentation documentation) {
        SortedSet<MacroNameArity> macroNameAritySortedSet = CallDefinitions.macroNameAritySortedSet(beam, atoms);
        appendCallDefinitions(decompiled, macroNameAritySortedSet, documentation);
    }

    @NotNull
    private static String macroToHeaderName(@NotNull String macro) {
        return HEADER_NAME_BY_MACRO.get(macro);
    }

    private static void appendCallDefinitions(@NotNull StringBuilder decompiled,
                                              @NotNull SortedSet<MacroNameArity> macroNameAritySortedSet, Documentation documentation) {
        MacroNameArity lastMacroNameArity = null;

        for (MacroNameArity macroNameArity : macroNameAritySortedSet) {
            String macro = macroNameArity.macro;

            if (lastMacroNameArity == null) {
                appendHeader(decompiled, macroToHeaderName(macro));
            } else if (!lastMacroNameArity.macro.equals(macro)) {
                appendHeader(decompiled, macroToHeaderName(macro));
            }
            decompiled.append("\n");

            if (documentation != null){
                List<FunctionMetadata> functionMetadata = documentation.getDocs() != null
                        ? documentation.getDocs().getFunctionMetadataOrSimilar(macroNameArity.name, macroNameArity.arity)
                        : null;
                if (functionMetadata != null){
                    functionMetadata.stream().filter(x -> x.getName().equals("deprecated")).forEach(x -> {
                        if (x.getValue() != null){
                            decompiled.append("\n  @deprecated \"\"\"\n")
                                    .append("  ")
                                    .append(x.getValue())
                                    .append("\n  \"\"\"\n\n");
                        }else{
                            decompiled.append("\n  @deprecated\n");
                        }
                    });
                }
                List<Doc> functionDocs = documentation.getDocs() != null
                        ? documentation.getDocs().getFunctionDocs(macroNameArity.name, macroNameArity.arity)
                        : null;
                if (functionDocs != null){
                    functionDocs.forEach(x -> {
                        appendDocumentation(decompiled, "doc", x.getDocumentationText());
                    });
                }
            }

            appendMacroNameArity(decompiled, macroNameArity, documentation);

            lastMacroNameArity = macroNameArity;
        }
    }

    private static void appendHeader(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled
                .append("\n")
                .append("  # ")
                .append(name)
                .append("\n");
    }

    private static void appendDocumentation(@NotNull StringBuilder decompiled, @NotNull String moduleAttribute, @NotNull String text) {
        String safePromoterTerminator = safePromoterTerminator(text);

        String promoterTerminator;
        if (safePromoterTerminator != null) {
            promoterTerminator = safePromoterTerminator;
        } else {
            promoterTerminator = "\"\"\"";
        }

        decompiled
                .append("  @")
                .append(moduleAttribute)
                // Use ~S sigil to stop interpolation in docs as an interpolation stored in the docs was
                // escaped in the original source.
                .append(" ~S")
                .append(promoterTerminator)
                .append('\n');
        appendDocumentationText(decompiled, safePromoterTerminator, text);
        decompiled
                .append("\n  ")
                .append(promoterTerminator)
                .append('\n');
    }

    private static final String CHARLIST_HEREDOC_PROMOTER_TERMINATOR = "'''";
    private static final String STRING_HEREDOC_PROMOTER_TERMINATOR = "\"\"\"";

    @Nullable
    private static String safePromoterTerminator(String text) {
        boolean containsCharlistHeredoc = text.contains(CHARLIST_HEREDOC_PROMOTER_TERMINATOR);
        boolean containsStringHeredoc = text.contains(STRING_HEREDOC_PROMOTER_TERMINATOR);

        @Nullable String safePromoterTerminator;
        if (containsCharlistHeredoc && containsStringHeredoc) {
            safePromoterTerminator = null;
        } else if (containsCharlistHeredoc) {
            safePromoterTerminator = STRING_HEREDOC_PROMOTER_TERMINATOR;
        } else if (containsStringHeredoc) {
            safePromoterTerminator = CHARLIST_HEREDOC_PROMOTER_TERMINATOR;
        } else {
            // Default to String since it is what actual developers would use most often
            safePromoterTerminator = STRING_HEREDOC_PROMOTER_TERMINATOR;
        }

        return safePromoterTerminator;
    }

    private static void appendDocumentationText(@NotNull StringBuilder decompiled, @Nullable String safePromoterTerminator, @NotNull String text) {
        String[] lines = text.split("\n");

        int lastI = lines.length - 1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            String stripped = line.stripTrailing();

            if (!stripped.isEmpty()) {
                decompiled.append("  ");

                if (safePromoterTerminator == null) {
                    decompiled.append(stripped.replace(STRING_HEREDOC_PROMOTER_TERMINATOR, "\"\"\""));
                } else {
                    decompiled.append(stripped);
                }
            }

            if (i != lastI) {
                decompiled.append("\n");
            }
        }
    }

    private static void appendMacroNameArity(@NotNull StringBuilder decompiled,
                                             @NotNull MacroNameArity macroNameArity, Documentation documentation) {
        org.elixir_lang.beam.decompiler.MacroNameArity decompiler = decompiler(macroNameArity);

        if (decompiler != null) {
            // The signature while easier for users to read are not proper code for those that need to use unquote, so
            // only allow signatures for default decompiler
            if (decompiler == Default.INSTANCE) {
                List<String> signaturesFromDocs = documentation != null && documentation.getBeamLanguage().equals("elixir")
                        ? documentation.getDocs().getSignatures(macroNameArity.name, macroNameArity.arity)
                        : null;

                if (signaturesFromDocs != null && !signaturesFromDocs.isEmpty()) {
                    decompiled.append("  ").append(macroNameArity.macro).append(' ');
                    Optional<String> optional = signaturesFromDocs.stream().findFirst();
                    decompiled.append(optional.get());
                    decompiled.append(" do\n    # body not decompiled\n  end\n");
                } else {
                    decompiler.append(decompiled, macroNameArity);
                }
            } else {
                decompiler.append(decompiled, macroNameArity);
            }
        }
    }

    @Nullable
    static org.elixir_lang.beam.decompiler.MacroNameArity decompiler(@NotNull MacroNameArity macroNameArity) {
        org.elixir_lang.beam.decompiler.MacroNameArity accepted = null;

        for (org.elixir_lang.beam.decompiler.MacroNameArity decompiler : MACRO_NAME_ARITY_DECOMPILER_LIST) {
            if (decompiler.accept(macroNameArity)) {
                accepted = decompiler;
                break;
            }
        }

        if (accepted == null) {
            error(macroNameArity);
        }

        return accepted;
    }

    private static void error(@NotNull MacroNameArity macroNameArity) {
        com.intellij.openapi.diagnostic.Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(
                Decompiler.class
        );
        String message = "No decompiler for MacroNameArity (" + macroNameArity + ")";
        logger.error(message);
    }

    @NotNull
    public static String defmoduleArgument(String moduleName) {
        String defmoduleArgument;
        if (moduleName.startsWith(ELIXIR_PREFIX)) {
            defmoduleArgument = moduleName.substring(ELIXIR_PREFIX.length());
        } else {
            defmoduleArgument = ":" + moduleNameToAtomName(moduleName);
        }
        return defmoduleArgument;
    }

    @NotNull
    private static String moduleNameToAtomName(@NotNull String moduleName) {
        String atom;

        if (moduleName.contains("-")) {
            atom = "\"" + moduleName + "\"";
        } else {
            atom = moduleName;
        }

        return atom;
    }

    @NotNull
    @Override
    public CharSequence decompile(@NotNull VirtualFile virtualFile) {
        return decompiled(Optional.ofNullable(Beam.Companion.from(virtualFile)));
    }
}
