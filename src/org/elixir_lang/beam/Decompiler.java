package org.elixir_lang.beam;

import com.google.common.base.Joiner;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.elixir_lang.beam.decompiler.Default;
import org.elixir_lang.beam.decompiler.InfixOperator;
import org.elixir_lang.beam.decompiler.PrefixOperator;
import org.elixir_lang.beam.decompiler.Unquoted;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.elixir_lang.beam.chunk.Chunk.TypeID.ATOM;
import static org.elixir_lang.psi.call.name.Function.DEF;
import static org.elixir_lang.psi.call.name.Function.DEFMACRO;
import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    private static final List<org.elixir_lang.beam.decompiler.MacroNameArity> MACRO_NAME_ARITY_DECOMPILER_LIST =
            new ArrayList<org.elixir_lang.beam.decompiler.MacroNameArity>();

    static {
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(InfixOperator.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(PrefixOperator.INSTANCE);
        MACRO_NAME_ARITY_DECOMPILER_LIST.add(Unquoted.INSTANCE);
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

                    appendExports(decompiled, beam, atoms);

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

    private static void appendExports(@NotNull StringBuilder decompiled, @NotNull Beam beam, @NotNull Atoms atoms) {
        Exports exports = beam.exports();

        if (exports != null) {
            appendExports(decompiled, exports, atoms);
        }
    }

    private static void appendExports(@NotNull StringBuilder decompiled,
                                      @NotNull Exports exports,
                                      @NotNull Atoms atoms) {
        SortedSet<MacroNameArity> macroNameAritySortedSet = exports.macroNameAritySortedSet(atoms);
        MacroNameArity lastMacroNameArity = null;

        for (MacroNameArity macroNameArity : macroNameAritySortedSet) {
            if (lastMacroNameArity == null) {
                if (macroNameArity.macro.equals(DEFMACRO)) {
                    appendHeader(decompiled, "Macros");
                } else if (macroNameArity.macro.equals(DEF)) {
                    appendHeader(decompiled, "Functions");
                }
            } else if (lastMacroNameArity.macro.equals(DEFMACRO) && macroNameArity.macro.equals(DEF)) {
                appendHeader(decompiled, "Functions");
            }

            decompiled.append("\n");

            appendMacroNameArity(decompiled, macroNameArity);

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

    private static void appendMacroNameArity(@NotNull StringBuilder decompiled,
                                             @NotNull MacroNameArity macroNameArity) {
        boolean accepted = false;

        for (org.elixir_lang.beam.decompiler.MacroNameArity decompiler : MACRO_NAME_ARITY_DECOMPILER_LIST) {
            if (decompiler.accept(macroNameArity)) {
                decompiler.append(decompiled, macroNameArity);
                accepted = true;
                break;
            }
        }

        if (!accepted) {
            error(macroNameArity);
        }
    }

    private static void error(@NotNull MacroNameArity macroNameArity) {
        com.intellij.openapi.diagnostic.Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(
                Decompiler.class
        );
        String fullUserMessage = "No decompiler for MacroNameArity (" + macroNameArity + ")";
        logger.error(
                LogMessageEx.createEvent(
                        fullUserMessage,
                        Joiner
                                .on("\n")
                                .join(
                                        new Throwable().getStackTrace()
                                ),
                        fullUserMessage,
                        null,
                        Collections.<Attachment>emptyList()
                )
        );
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
        return decompiled(Beam.from(virtualFile));
    }
}
