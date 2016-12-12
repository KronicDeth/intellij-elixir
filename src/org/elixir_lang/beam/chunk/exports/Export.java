package org.elixir_lang.beam.chunk.exports;

import org.elixir_lang.beam.chunk.Atoms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Export {
    // atomIndex is 1-based
    private final long atomIndex;
    private final long arity;

    public Export(long atomIndex, long arity) {
        this.atomIndex = atomIndex;
        this.arity = arity;
    }

    public long arity() {
        return arity;
    }

    @Nullable
    public String name(@NotNull Atoms atoms) {
        return atoms.get((int) atomIndex);
    }
}
