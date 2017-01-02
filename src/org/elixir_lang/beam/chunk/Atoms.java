package org.elixir_lang.beam.chunk;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.beam.chunk.Chunk.TypeID.ATOM;
import static org.elixir_lang.beam.chunk.Chunk.unsignedByte;
import static org.elixir_lang.beam.chunk.Chunk.unsignedInt;

public class Atoms {
    /*
     * Fields
     */

    @NotNull
    private List<String> atomList;

    /*
     * Constructors
     */

    public Atoms(@NotNull List<String> atomList) {
        this.atomList = Collections.unmodifiableList(atomList);
    }

    /*
     * Static Methods
     */

    @Nullable
    public static Atoms from(@NotNull Chunk chunk) {
        Atoms atoms = null;

        if (chunk.typeID.equals(ATOM.toString()) && chunk.data.length >= 4) {
            int offset = 0;
            Pair<Long, Integer> atomCountByteCount = unsignedInt(chunk.data, offset);
            long atomCount = atomCountByteCount.first;
            offset += atomCountByteCount.second;

            List<String> atomList = new ArrayList<String>();

            for (long i = 0; i < atomCount; i++) {
                Pair<Integer, Integer> atomLengthByteCount = unsignedByte(chunk.data[offset]);
                int atomLength = atomLengthByteCount.first;
                offset += atomLengthByteCount.second;

                String entry = new String(chunk.data, offset, atomLength);
                offset += atomLength;
                atomList.add(entry);
            }

            atoms = new Atoms(atomList);
        }

        return atoms;
    }

    /*
     * Instance Methods
     */

    /**
     *
     * @param index 1-based index.  1 is reserved for {#link moduleName}
     * @return atom if
     */
    @Nullable
    public String get(int index) {
        String atom = null;

        if (index >= 1) {
            // index is 1-based, so subtract 1 to get 0-based
            atom = atomList.get(index - 1);
        }

        return atom;
    }

    @Nullable
    public String moduleName() {
        String moduleName = null;

        if (atomList.size() > 0) {
            moduleName = atomList.get(0);
        }

        return moduleName;
    }
}
