package org.elixir_lang.beam;

import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.elixir_lang.beam.chunk.exports.Export;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedMap;
import java.util.SortedSet;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.psi.call.name.Function.DEF;
import static org.elixir_lang.psi.call.name.Function.DEFMACRO;
import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    private static final String MACRO_EXPORT_PREFIX = "MACRO-";

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
            Pair<String, String> macroArgument = macroArgument(name);

            SortedMap<Integer, Export> exportByArity = nameExportByArity.getValue();

            for (SortedMap.Entry<Integer, Export> arityExport : exportByArity.entrySet()) {
                if (!first) {
                    decompiled.append("\n");
                }

                appendExport(decompiled, macroArgument, arityExport.getKey());

                first = false;
            }
        }
    }

    private static void appendExport(@NotNull StringBuilder decompiled,
                                     @NotNull Pair<String, String> macroArgument,
                                     @Nullable Integer arity) {
        decompiled
                .append("  ")
                .append(macroArgument.first)
                .append(" ")
                .append(macroArgument.second)
                .append("(");


        if (arity != null) {
            for (int i = 0; i < arity; i++) {
                if (i != 0) {
                    decompiled.append(", ");
                }

                decompiled.append("p").append(i);
            }
        }

        decompiled.append(") do\n").append("    # body not decompiled\n").append("  end\n");
    }

    @NotNull
    private static String defmoduleArgument(String moduleName) {
        String defmoduleArgument;
        if (moduleName.startsWith(ELIXIR_PREFIX)) {
            defmoduleArgument = moduleName.substring(ELIXIR_PREFIX.length());
        } else {
            defmoduleArgument = ":" + moduleName;
        }
        return defmoduleArgument;
    }

    @Contract(pure = true)
    @NotNull
    private static Pair<String, String> macroArgument(@NotNull String name) {
        String argument = name;
        String macro = DEFMACRO;

        if (name.startsWith(MACRO_EXPORT_PREFIX)) {
            argument = name.substring(MACRO_EXPORT_PREFIX.length());
            macro = DEF;
        }

        return pair(macro, argument);
    }

    @NotNull
    @Override
    public CharSequence decompile(@NotNull VirtualFile virtualFile) {
        Beam beam = Beam.from(virtualFile);

        return decompiled(beam);
    }
}
