package org.elixir_lang.beam.chunk;

import com.intellij.openapi.util.Pair;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.elixir_lang.Visibility;
import org.elixir_lang.beam.Beam;
import org.elixir_lang.beam.MacroNameArity;
import org.elixir_lang.beam.chunk.call_definitions.CallDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.elixir_lang.beam.chunk.Chunk.unsignedInt;

public class CallDefinitions {
    /*
     * CONSTANTS
     */

    private static final Map<Chunk.TypeID, Visibility> VISIBILITY_BY_TYPE_ID = new THashMap<>();

    static {
        VISIBILITY_BY_TYPE_ID.put(Chunk.TypeID.EXPT, Visibility.PUBLIC);
        VISIBILITY_BY_TYPE_ID.put(Chunk.TypeID.LOCT, Visibility.PRIVATE);
    }

    /*
     * Fields
     */

    @NotNull
    private Collection<CallDefinition> callDefinitionCollection;
    @NotNull
    private final Chunk.TypeID typeID;

    /*
     * Constructors
     */

    public CallDefinitions(@NotNull Chunk.TypeID typeID, @NotNull Collection<CallDefinition> callDefinitionCollection) {
        this.typeID = typeID;
        this.callDefinitionCollection = callDefinitionCollection;
    }

    /*
     * Static Methods
     */

    @Nullable
    public static CallDefinitions from(@NotNull Chunk chunk, @NotNull Chunk.TypeID typeID) {
        CallDefinitions callDefinitions = null;

        if (chunk.typeID.equals(typeID.toString()) && chunk.data.length >= 4) {
            Collection<CallDefinition> callDefinitionCollection = new THashSet<CallDefinition>();

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

                callDefinitionCollection.add(new CallDefinition(atomIndex, arity));
            }

            callDefinitions = new CallDefinitions(typeID, callDefinitionCollection);
        }

        return callDefinitions;
    }

    @NotNull
    public static SortedSet<MacroNameArity> macroNameAritySortedSet(@NotNull Beam beam, @NotNull Atoms atoms) {
        return macroNameAritySortedSet(beam.callDefinitionsStream(), atoms);
    }

    @NotNull
    public static SortedSet<MacroNameArity> macroNameAritySortedSet(
            @NotNull Stream<CallDefinitions> callDefinitionsStream,
            @NotNull Atoms atoms
    ) {
        return callDefinitionsStream
                .map(callDefinitions -> callDefinitions.macroNameAritySortedSet(atoms))
                .collect(TreeSet::new, TreeSet::addAll, TreeSet::addAll);
    }

    /**
     * Set of {@link MacroNameArity} sorted as
     * 1. {@link MacroNameArity#macro}, so that {@code defmacro} is before {@code def}
     * 2. {@link MacroNameArity#name} is sorted alphabetically
     * 3. {@link MacroNameArity#arity} is sorted ascending
     *
     * @param atoms used to look up the names of the {@link CallDefinition}s in {@link #callDefinitionCollection}.
     * @return The sorted set will be the same size as {@link #callDefinitionCollection} unless
     *   {@link CallDefinition#name(Atoms)} returns {@code null} for some {@link CallDefinition}s.
     */
    @NotNull
    public SortedSet<MacroNameArity> macroNameAritySortedSet(@NotNull Atoms atoms) {
        SortedSet<MacroNameArity> macroNameAritySortedSet = new TreeSet<MacroNameArity>();

        for (CallDefinition callDefinition : callDefinitionCollection) {
            String exportName = callDefinition.name(atoms);

            if (exportName != null) {
                MacroNameArity macroNameArity = new MacroNameArity(visibility(), exportName, (int) callDefinition.arity());
                macroNameAritySortedSet.add(macroNameArity);
            }
        }

        return macroNameAritySortedSet;
    }

    @NotNull
    private Visibility visibility() {
        return VISIBILITY_BY_TYPE_ID.get(typeID);
    }

    /**
     * The number of call_definitions
     */
    public int size() {
        return callDefinitionCollection.size();
    }
}
