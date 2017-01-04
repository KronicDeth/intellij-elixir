package org.elixir_lang.beam;

import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import gnu.trove.THashSet;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.elixir_lang.beam.chunk.exports.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    private static final Set<String> INFIX_OPERATOR_SET;

    static {
        INFIX_OPERATOR_SET = new THashSet<String>();
        INFIX_OPERATOR_SET.add("!=");
        INFIX_OPERATOR_SET.add("!==");
        INFIX_OPERATOR_SET.add("");
        INFIX_OPERATOR_SET.add("&&");
        INFIX_OPERATOR_SET.add("&&&");
        INFIX_OPERATOR_SET.add("*");
        INFIX_OPERATOR_SET.add("+");
        INFIX_OPERATOR_SET.add("-");
        INFIX_OPERATOR_SET.add("--");
        INFIX_OPERATOR_SET.add("--");
        INFIX_OPERATOR_SET.add("->");
        INFIX_OPERATOR_SET.add("..");
        INFIX_OPERATOR_SET.add("/");
        INFIX_OPERATOR_SET.add("::");
        INFIX_OPERATOR_SET.add("<");
        INFIX_OPERATOR_SET.add("<-");
        INFIX_OPERATOR_SET.add("<<<");
        INFIX_OPERATOR_SET.add("<<~");
        INFIX_OPERATOR_SET.add("<=");
        INFIX_OPERATOR_SET.add("<>");
        INFIX_OPERATOR_SET.add("<|>");
        INFIX_OPERATOR_SET.add("<~");
        INFIX_OPERATOR_SET.add("<~>");
        INFIX_OPERATOR_SET.add("=");
        INFIX_OPERATOR_SET.add("==");
        INFIX_OPERATOR_SET.add("===");
        INFIX_OPERATOR_SET.add("=>");
        INFIX_OPERATOR_SET.add(">");
        INFIX_OPERATOR_SET.add(">=");
        INFIX_OPERATOR_SET.add(">>>");
        INFIX_OPERATOR_SET.add("\\\\");
        INFIX_OPERATOR_SET.add("^");
        INFIX_OPERATOR_SET.add("^^^");
        INFIX_OPERATOR_SET.add("and");
        INFIX_OPERATOR_SET.add("or");
        INFIX_OPERATOR_SET.add("|>");
        INFIX_OPERATOR_SET.add("||");
        INFIX_OPERATOR_SET.add("|||");
        INFIX_OPERATOR_SET.add("~=");
        INFIX_OPERATOR_SET.add("~>");
        INFIX_OPERATOR_SET.add("~>>");
    }

    @NotNull
    private static CharSequence decompiled(@Nullable Beam beam) {
        StringBuilder decompiled = new StringBuilder("Decompilated Error");

        if (beam != null) {
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
                }
            }
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
        Pair<SortedMap<String, SortedMap<Integer, Export>>, SortedSet<Export>>
                exportByArityByNameNamelessExportSet = exports.exportByArityByName(atoms);
        SortedMap<String, SortedMap<Integer, Export>> exportByArityByName =
                exportByArityByNameNamelessExportSet.first;

        boolean first = true;
        for (SortedMap.Entry<String, SortedMap<Integer, Export>> nameExportByArity :
                exportByArityByName.entrySet()) {
            String name = nameExportByArity.getKey();

            SortedMap<Integer, Export> exportByArity = nameExportByArity.getValue();

            for (SortedMap.Entry<Integer, Export> arityExport : exportByArity.entrySet()) {
                if (!first) {
                    decompiled.append("\n");
                }

                @Nullable Integer arity = arityExport.getKey();

                MacroNameArity macroNameArity = new MacroNameArity(name, arity);
                appendMacroNameArity(decompiled, macroNameArity);

                first = false;
            }
        }
    }

    private static void appendMacroNameArity(@NotNull StringBuilder decompiled,
                                             @NotNull MacroNameArity macroNameArity) {
        String name = macroNameArity.name;

        if (isInfixOperator(name)) {
            appendInfixMacroNameArity(decompiled, macroNameArity);
        } else {
            appendPrefixMacroNameArity(decompiled, macroNameArity);
        }
    }

    private static void appendInfixMacroNameArity(@NotNull StringBuilder decompiled,
                                                  @NotNull MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" left ")
                .append(macroNameArity.name)
                .append(" right");
        appendBody(decompiled);
    }

    private static void appendPrefixMacroNameArity(@NotNull StringBuilder decompiled,
                                                   @NotNull MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(" ")
                .append(macroNameArity.name)
                .append("(");

        @Nullable Integer arity = macroNameArity.arity;

        if (arity != null) {
            for (int i = 0; i < arity; i++) {
                if (i != 0) {
                    decompiled.append(", ");
                }

                decompiled.append("p").append(i);
            }
        }

        decompiled.append(")");
        appendBody(decompiled);
    }

    private static void appendBody(@NotNull StringBuilder decompiled) {
        decompiled
                .append(" do\n")
                .append("    # body not decompiled\n")
                .append("  end\n");
    }

    @NotNull
    public static String defmoduleArgument(String moduleName) {
        String defmoduleArgument;
        if (moduleName.startsWith(ELIXIR_PREFIX)) {
            defmoduleArgument = moduleName.substring(ELIXIR_PREFIX.length());
        } else {
            defmoduleArgument = ":" + moduleName;
        }
        return defmoduleArgument;
    }

    /**
     * @param name {@link MacroNameArity#name}
     */
    private static boolean isInfixOperator(@NotNull String name) {
        return INFIX_OPERATOR_SET.contains(name);
    }

    @NotNull
    @Override
    public CharSequence decompile(@NotNull VirtualFile virtualFile) {
        Beam beam = Beam.from(virtualFile);

        return decompiled(beam);
    }
}
