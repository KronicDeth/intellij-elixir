package org.elixir_lang.beam.chunk;

import com.intellij.openapi.util.Pair;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.elixir_lang.beam.chunk.exports.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    public Pair<Map<String, Map<Integer, Export>>, Set<Export>> exportByArityByName(@NotNull Atoms atoms) {
        Map<String, Map<Integer, Export>> exportByArityByName = new THashMap<String, Map<Integer, Export>>();
        Set<Export> nameless = new THashSet<Export>();

        for (Export export : exportCollection) {
            String name = export.name(atoms);

            if (name == null) {
                nameless.add(export);
            } else {
                Map<Integer, Export> exportByArity = exportByArityByName.get(name);

                if (exportByArity == null) {
                    exportByArity = new THashMap<Integer, Export>();
                    exportByArityByName.put(name, exportByArity);
                }

                exportByArity.put((int) export.arity(), export);
            }
        }

        return pair(exportByArityByName, nameless);
    }
}
