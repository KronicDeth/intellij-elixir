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
import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    @NotNull
    private static CharSequence decompiled(@Nullable Beam beam) {
        StringBuilder decompiled = new StringBuilder("Decompilated Error");

        if (beam != null) {
            Atoms atoms = beam.atoms();

            if (atoms != null) {
                String moduleName = atoms.moduleName();

                if (moduleName != null) {
                    String defmoduleArgument;

                    if (moduleName.startsWith(ELIXIR_PREFIX)) {
                        defmoduleArgument = moduleName.substring(ELIXIR_PREFIX.length());
                    } else {
                        defmoduleArgument = ":" + moduleName;
                    }

                    decompiled = new StringBuilder(
                            "# Source code recreated from a .beam file by IntelliJ Elixir\n"
                    )
                            .append("defmodule ")
                            .append(defmoduleArgument)
                            .append(" do\n");

                    Exports exports = beam.exports();

                    if (exports != null) {
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

                                decompiled
                                        .append("  ")
                                        .append(macroArgument.first)
                                        .append(" ")
                                        .append(macroArgument.second)
                                        .append("(");

                                Integer arity = arityExport.getKey();

                                if (arity != null) {
                                    for (int i = 0; i < arity; i++) {
                                        if (i != 0) {
                                            decompiled.append(", ");
                                        }

                                        decompiled.append("p").append(i);
                                    }
                                }

                                decompiled.append(") do\n").append("    # body not decompiled\n").append("  end\n");
                                first = false;
                            }
                        }
                    }

                    decompiled.append("end\n");
                }
            }
        }

        return decompiled;
    }

    @Contract(pure = true)
    @NotNull
    private static Pair<String, String> macroArgument(@NotNull String name) {
        String argument = name;
        String macro = "def";

        if (name.startsWith("MACRO-")) {
            argument = name.substring("MACRO-".length());
            macro = "defmacro";
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
