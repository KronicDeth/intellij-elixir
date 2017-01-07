package org.elixir_lang.beam.chunk;

import com.intellij.openapi.util.Pair;
import gnu.trove.THashSet;
import org.elixir_lang.beam.MacroNameArity;
import org.elixir_lang.beam.chunk.exports.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.beam.chunk.Chunk.TypeID.EXPT;
import static org.elixir_lang.beam.chunk.Chunk.unsignedInt;

public class Exports {
    /*
     * Fields
     */

    @NotNull
    private Collection<Export> exportCollection;

    /*
     * Constructors
     */

    public Exports(@NotNull Collection<Export> exportCollection) {
        this.exportCollection = exportCollection;
    }

    /*
     * Static Methods
     */

    @Nullable
    public static Exports from(@NotNull Chunk chunk) {
        Exports exports = null;

        if (chunk.typeID.equals(EXPT.toString()) && chunk.data.length >= 4) {
            Collection<Export> exportCollection = new THashSet<Export>();

            int offset = 0;

            Pair<Long, Integer> exportCountByteCount = unsignedInt(chunk.data, 0);
            long exportCount = exportCountByteCount.first;
            offset += exportCountByteCount.second;

            for (long i = 0; i < exportCount; i++) {
                Pair<Long, Integer> atomIndexByteCount = unsignedInt(chunk.data, offset);
                long atomIndex = atomIndexByteCount.first;
                offset += atomIndexByteCount.second;

                Pair<Long, Integer> arityByteCount = unsignedInt(chunk.data, offset);
                long arity = arityByteCount.first;
                offset += arityByteCount.second;

                // label is currently unused, but it must be consumed to read the next export at the correct offset
                Pair<Long, Integer> labelByteCount = unsignedInt(chunk.data, offset);
                offset += labelByteCount.second;

                exportCollection.add(new Export(atomIndex, arity));
            }

            exports = new Exports(exportCollection);
        }

        return exports;
    }

    /**
     * Set of {@link MacroNameArity} sorted as
     * 1. {@link MacroNameArity#macro}, so that {@code defmacro} is before {@code def}
     * 2. {@link MacroNameArity#name} is sorted alphabetically
     * 3. {@link MacroNameArity#arity} is sorted ascending
     *
     * @param atoms used to look up the names of the {@link Export}s in {@link #exportCollection}.
     * @return The sorted set will be the same size as {@link #exportCollection} unless {@link Export#name(Atoms)}
     *   returns {@code null} for some {@link Export}s.
     */
    @NotNull
    public SortedSet<MacroNameArity> macroNameAritySortedSet(@NotNull Atoms atoms) {
        SortedSet<MacroNameArity> macroNameAritySortedSet = new TreeSet<MacroNameArity>();

        for (Export export : exportCollection) {
            String exportName = export.name(atoms);

            if (exportName != null) {
                MacroNameArity macroNameArity = new MacroNameArity(exportName, (int) export.arity());
                macroNameAritySortedSet.add(macroNameArity);
            }
        }

        return macroNameAritySortedSet;
    }

    /**
     * The number of exports
     */
    public int size() {
        return exportCollection.size();
    }
}
