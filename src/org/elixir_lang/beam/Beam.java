package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.FileContent;
import gnu.trove.THashMap;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.CallDefinitions;
import org.elixir_lang.beam.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static org.elixir_lang.beam.chunk.Chunk.TypeID.*;
import static org.elixir_lang.beam.chunk.Chunk.length;
import static org.elixir_lang.beam.chunk.Chunk.typeID;

/**
 * See http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html
 */
public class Beam {
    private static final List<Chunk.TypeID> CALL_DEFINITION_TYPE_IDS = Arrays.asList(EXPT, LOCT);

    private static final int GZIP_FIRST_UNSIGNED_BYTE = 0x1f;
    private static final int GZIP_SECOND_UNSIGNED_BYTE = 0x8b;

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

    @NotNull
    public static Optional<Beam> from(@NotNull byte[] content, @NotNull String path)
            throws IOException, OtpErlangDecodeException {
        return decompressedInputStream(new ByteArrayInputStream(content))
                .map(DataInputStream::new)
                .flatMap(dataInputStream -> Optional.ofNullable(Beam.from(dataInputStream, path)));
    }

    @NotNull
    public static Optional<Beam> from(@NotNull FileContent fileContent) throws IOException, OtpErlangDecodeException {
        return from(fileContent.getContent(), fileContent.getFile().getPath());
    }

    private static Optional<InputStream> virtualFileToInputStream(@NotNull VirtualFile virtualFile) {
        Optional<InputStream> inputStreamOptional;

        try {
            inputStreamOptional = Optional.of(virtualFile.getInputStream());
        } catch (IOException e) {
            inputStreamOptional = Optional.empty();
        }

        return inputStreamOptional;
    }

    private static Optional<InputStream> decompressedInputStream(@NotNull InputStream inputStream) {
        assert inputStream.markSupported();

        inputStream.mark(2);
        Optional<InputStream> decompressedInputStreamOptional;

        try {
            if (inputStream.read() == GZIP_FIRST_UNSIGNED_BYTE && inputStream.read() == GZIP_SECOND_UNSIGNED_BYTE) {
                inputStream.reset();
                decompressedInputStreamOptional = Optional.of(new GZIPInputStream(inputStream));
            } else {
                inputStream.reset();
                decompressedInputStreamOptional = Optional.of(inputStream);
            }
        } catch (IOException e) {
            decompressedInputStreamOptional = Optional.empty();
        }

        return decompressedInputStreamOptional;
    }

    @NotNull
    public static Optional<Beam> from(@NotNull VirtualFile virtualFile) {
        return virtualFileToInputStream(virtualFile)
                .flatMap(Beam::decompressedInputStream)
                .map(DataInputStream::new)
                .flatMap(dataInputStream -> Optional.ofNullable(Beam.from(dataInputStream, virtualFile.getPath())));
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

    @NotNull
    private Optional<CallDefinitions> callDefinitions(@NotNull Chunk.TypeID typeID) {
        Optional<CallDefinitions> callDefinitions;

        Chunk chunk = chunk(typeID);

        if (chunk != null) {
            callDefinitions = Optional.ofNullable(CallDefinitions.from(chunk, typeID));
        } else {
            callDefinitions = Optional.empty();
        }

        return callDefinitions;
    }

    @NotNull
    public Stream<CallDefinitions> callDefinitionsStream() {
        return CALL_DEFINITION_TYPE_IDS
                .stream()
                .flatMap(typeID -> callDefinitions(typeID).map(Stream::of).orElseGet(Stream::empty));
    }
}
