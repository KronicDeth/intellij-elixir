package org.elixir_lang.beam.chunk;

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
            long atomCount = unsignedInt(chunk.data, offset);
            List<String> atomList = new ArrayList<String>();
            offset += 4;

            for (long i = 0; i < atomCount; i++) {
                int atomLength = unsignedByte(chunk.data[offset]);
                offset += 1;

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

    @Nullable
    public String moduleName() {
        String moduleName = null;

        if (atomList.size() > 0) {
            moduleName = atomList.get(0);
        }

        return moduleName;
    }
}
