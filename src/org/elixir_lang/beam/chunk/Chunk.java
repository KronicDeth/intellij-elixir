package org.elixir_lang.beam.chunk;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;

import static com.intellij.openapi.util.Pair.pair;

/**
 * Chunk of a `.beam` file.  Same chunk format as base IFF
 */
public class Chunk {
    private static final int ALIGNMENT = 4;
    @NotNull
    public final String typeID;
    @NotNull
    public final byte[] data;
    private Chunk(@NotNull String typeID, @NotNull byte[] data) {
        this.typeID = typeID;
        this.data = data;
    }

    @Nullable
    public static Chunk from(@NotNull DataInputStream dataInputStream) throws IOException {
        String typeID = typeID(dataInputStream);
        Chunk chunk = null;

        if (typeID != null) {
            long length = length(dataInputStream);

            byte[] data = new byte[(int) length];
            dataInputStream.readFully(data);

            int padding = (int) ((ALIGNMENT - (length % ALIGNMENT)) % ALIGNMENT);
            dataInputStream.skipBytes(padding);

            chunk = new Chunk(typeID, data);
        }

        return chunk;
    }

    public static long length(@NotNull DataInputStream dataInputStream) throws IOException {
        return readUnsignedInt(dataInputStream);
    }

    @Nullable
    public static String typeID(@NotNull DataInputStream dataInputStream) throws IOException {
        byte[] bytes = new byte[4];
        int bytesRead = dataInputStream.read(bytes, 0, bytes.length);
        String typeID = null;

        if (bytesRead == bytes.length) {
            typeID = new String(bytes);
        }

        return typeID;
    }

    private static long readUnsignedInt(@NotNull DataInputStream dataInputStream) throws IOException {
        byte[] bytes = new byte[32 / 8];

        dataInputStream.readFully(bytes);

        return unsignedInt(bytes).first;
    }

    @NotNull
    @Contract(pure = true)
    @SuppressWarnings("PMD.DefaultPackage")
    static Pair<Integer, Integer> unsignedByte(byte signedByte) {
        return pair(signedByte & 0xFF, 1);
    }

    @NotNull
    @Contract(pure = true)
    @SuppressWarnings("PMD.DefaultPackage")
    static Pair<Long, Integer> unsignedInt(@NotNull byte[] bytes) {
        return unsignedInt(bytes, 0);
    }

    @NotNull
    @Contract(pure = true)
    @SuppressWarnings("PMD.DefaultPackage")
    static Pair<Long, Integer> unsignedInt(@NotNull byte[] bytes, int offset) {
        assert bytes.length >= offset + 4;

        long unsignedInt = 0;

        for (int i = 0; i < 4; i++) {
            int unsignedByte = unsignedByte(bytes[offset + i]).first;
            unsignedInt += unsignedByte << 8 * (4 - 1 - i);
        }

        return pair(unsignedInt, 4);
    }

    public enum TypeID {
        ATOM("Atom"),
        EXPT("ExpT");

        private final String typeID;

        TypeID(@NotNull String typeID) {
            this.typeID = typeID;
        }

        @Override
        public String toString() {
            return typeID;
        }
    }
}
