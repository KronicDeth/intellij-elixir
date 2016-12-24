package org.elixir_lang.beam;

import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.chunk.Atoms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Decompiler implements BinaryFileDecompiler {
    @NotNull
    private static CharSequence decompiled(@Nullable Beam beam) {
        CharSequence decompiled = "Decompilation Error";

        if (beam != null) {
            Atoms atoms = beam.atoms();

            if (atoms != null) {
                String name = atoms.moduleName();

                if (name != null) {
                    String defmoduleArgument;

                    if (name.startsWith(ELIXIR_PREFIX)) {
                        defmoduleArgument = name.substring(ELIXIR_PREFIX.length());
                    } else {
                        defmoduleArgument = ":" + name;
                    }

                    decompiled = "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                                 "defmodule " + defmoduleArgument + " do\n" +
                                 "end\n";
                }
            }
        }

        return decompiled;
    }

    @NotNull
    @Override
    public CharSequence decompile(@NotNull VirtualFile virtualFile) {
        Beam beam = Beam.from(virtualFile);

        return decompiled(beam);
    }
}
