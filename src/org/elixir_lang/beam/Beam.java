package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.FileContent;
import gnu.trove.THashMap;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Chunk;
import org.elixir_lang.beam.chunk.Exports;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.elixir_lang.beam.chunk.Chunk.TypeID.*;
import static org.elixir_lang.beam.chunk.Chunk.length;
import static org.elixir_lang.beam.chunk.Chunk.typeID;

/**
 * See http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html
 */
public class Beam {
    private static final String HEADER = "FOR1";

    /*
     * CONSTANTS
     */
    private static final Logger LOGGER = Logger.getInstance(Beam.class);

    /*
     * Fields
     */
    private Map<String, Chunk> chunkByTypeID;

    /*
     * Constructors
     */

    private Beam(@NotNull Collection<Chunk> chunkCollection) {
        chunkByTypeID = new THashMap<String, Chunk>(chunkCollection.size());

        for (Chunk chunk : chunkCollection) {
            chunkByTypeID.put(chunk.typeID, chunk);
        }
    }

    /*
     * Static Methods
     */

    @Nullable
    public static Beam from(@NotNull DataInputStream dataInputStream, @NotNull String path) {
        String header;

        try {
            header = typeID(dataInputStream, path);
        } catch (IOException ioException) {
            LOGGER.error("Could not read header from BEAM DataInputStream from " + path, ioException);
            return null;
        }

        if (!HEADER.equals(header)) {
            LOGGER.error("header typeID (" + header + ") did not match expected (" + HEADER + ") from " + path);
            return null;
        }

        try {
            length(dataInputStream);
        } catch (IOException ioException) {
            LOGGER.error("Could not read length from BEAM DataInputStream from " + path, ioException);
            return null;
        }

        String section;

        try {
            section = typeID(dataInputStream, path);
        } catch (IOException ioException) {
            LOGGER.error("Could not read section header from BEAM DataInputStream from " + path, ioException);
            return null;
        }


        if (!"BEAM".equals(section)) {
            LOGGER.error("Section header is not BEAM in " + path);
            return null;
        }

        List<Chunk> chunkList = new ArrayList<Chunk>();
        int i = 1;

        while (true) {
            Chunk chunk;

            try {
                chunk = Chunk.from(dataInputStream, path);
            } catch (IOException ioException) {
                LOGGER.error(
                        "Could not read chunk number " + i + " from BEAM DataInputStream from " + path +
                                ".  Returning truncated Beam object",
                        ioException
                );
                break;
            }

            if (chunk != null) {
                chunkList.add(chunk);
            } else {
                break;
            }
        }

        return new Beam(chunkList);
    }

    @Nullable
    public static Beam from(@NotNull byte[] content, @NotNull String path)
            throws IOException, OtpErlangDecodeException {
        return from(new DataInputStream(new ByteArrayInputStream(content)), path);
    }

    @Nullable
    public static Beam from(@NotNull FileContent fileContent) throws IOException, OtpErlangDecodeException {
        return from(fileContent.getContent(), fileContent.getFile().getPath());
    }

    @Nullable
    public static Beam from(@NotNull VirtualFile virtualFile) {
        Beam beam = null;

        DataInputStream dataInputStream;

        try {
            dataInputStream = new DataInputStream(virtualFile.getInputStream());
        } catch (IOException ioException) {
            dataInputStream = null;
        }

        if (dataInputStream != null) {
            String path = virtualFile.getPath();
            beam = Beam.from(dataInputStream, path);
        }

        return beam;
    }

    public static boolean is(@NotNull VirtualFile virtualFile) {
        return !virtualFile.isDirectory() && "beam".equals(virtualFile.getExtension());
    }

    /*
     * Instance Methods
     */

    @Nullable
    public Atoms atoms() {
        Atoms atoms = null;

        Chunk chunk = chunk(ATOM);

        if (chunk != null) {
            atoms = Atoms.from(chunk, ATOM, Charset.forName("LATIN1"));
        } else {
            chunk = chunk(ATU8);

            if (chunk != null) {
                atoms = Atoms.from(chunk, ATU8, Charset.forName("UTF-8"));
            }
        }

        return atoms;
    }

    @Nullable
    private Chunk chunk(@NotNull String typeID) {
        return chunkByTypeID.get(typeID);
    }

    @Nullable
    private Chunk chunk(@NotNull Chunk.TypeID typeID) {
        return chunk(typeID.toString());
    }

    @Nullable
    public Exports exports() {
        Exports exports = null;

        Chunk chunk = chunk(EXPT);

        if (chunk != null) {
            exports = Exports.from(chunk);
        }

        return exports;
    }
}
