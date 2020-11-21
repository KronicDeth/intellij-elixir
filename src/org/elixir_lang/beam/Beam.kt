package org.elixir_lang.beam

import com.ericsson.otp.erlang.OtpErlangDecodeException
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpInputStream
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.FileContent
import org.elixir_lang.beam.chunk.*
import org.elixir_lang.beam.chunk.Chunk.TypeID.*
import org.elixir_lang.beam.chunk.Chunk.length
import org.elixir_lang.beam.chunk.Chunk.typeID
import org.elixir_lang.beam.term.ByteCount
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.zip.GZIPInputStream

private val callDefinitionTypeIDs = arrayOf(EXPT, LOCT)

private const val GZIP_FIRST_UNSIGNED_BYTE = 0x1f
private const val GZIP_SECOND_UNSIGNED_BYTE = 0x8b
private const val HEADER = "FOR1"

fun binaryToTerm(byteArray: ByteArray, offset: Int): Pair<OtpErlangObject, ByteCount> {
    val stream = OtpInputStream(byteArray, offset, byteArray.size, 0)
    val term = stream.read_any()

    return Pair(term, stream.pos - offset)
}

private fun decompressedInputStream(inputStream: InputStream): InputStream? {
    assert(inputStream.markSupported())

    inputStream.mark(2)

    return try {
        if (inputStream.read() == GZIP_FIRST_UNSIGNED_BYTE && inputStream.read() == GZIP_SECOND_UNSIGNED_BYTE) {
            inputStream.reset()
            GZIPInputStream(inputStream)
        } else {
            inputStream.reset()
            inputStream
        }
    } catch (e: IOException) {
        null
    }
}

private fun virtualFileToInputStream(virtualFile: VirtualFile): InputStream? =
        try {
            virtualFile.inputStream
        } catch (e: IOException) {
            null
        }

/**
 * See http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html
 */
class Beam private constructor(chunkCollection: Collection<Chunk>) {
    private val chunkByTypeID: Map<String, Chunk> = chunkCollection.associateBy { it.typeID }

    fun atoms(): Atoms? =
            chunk(ATOM)?.let { Atoms.from(it, ATOM, Charset.forName("LATIN1")) } ?:
                    chunk(ATU8)?.let { Atoms.from(it, ATU8, Charset.forName("UTF-8")) }

    fun attributes(): Keyword? = chunk(ATTR)?.let(::from)
    private fun chunk(typeID: String): Chunk? = chunkByTypeID[typeID]
    private fun chunk(typeID: Chunk.TypeID): Chunk? = chunk(typeID.toString())
    fun chunkCollection(): Collection<Chunk> = chunkByTypeID.values

    private fun callDefinitions(typeID: Chunk.TypeID, atoms: Atoms?): CallDefinitions? =
        chunk(typeID)?.let {
            CallDefinitions.from(it, typeID, atoms)
        }

    fun callDefinitionsList(atoms: Atoms?): List<CallDefinitions> =
        callDefinitionTypeIDs.mapNotNull { typeID ->
            callDefinitions(typeID, atoms)
        }

    fun code(): Code? = chunk(CODE)?.let { Code.from(it) }
    fun compileInfo(): Keyword? = chunk(CINF)?.let(::from)
    fun elixirDocumentation(): ElixirDocumentation? = chunk(EXDC)?.let { ElixirDocumentation.from(it) }
    fun debugInfo(): DebugInfo? = chunk(DBGI)?.let { org.elixir_lang.beam.chunk.debug_info.from(it) }
    fun exports(atoms: Atoms?): CallDefinitions? = callDefinitions(EXPT, atoms)
    fun functions(atoms: Atoms?): Functions? = chunk(FUNT)?.let { Functions.from(it, atoms) }

    fun imports(atoms: Atoms?): Imports? =
        chunk(IMPT)?.let {
            Imports.from(it, atoms)
        }

    fun lines(): Lines? = chunk(LINE)?.let { Lines.from(it) }
    fun literals(): Literals? = chunk(LITT)?.let { Literals.from(it) }
    fun locals(atoms: Atoms?): CallDefinitions? = callDefinitions(LOCT, atoms)
    fun strings(): Strings? = chunk(STRT)?.let { Strings.from(it) }

    companion object {
        private val LOGGER = Logger.getInstance(Beam::class.java)

        fun from(dataInputStream: DataInputStream, path: String): Beam? {
            val header: String?

            try {
                header = typeID(dataInputStream, path)
            } catch (ioException: IOException) {
                LOGGER.error("Could not read header from BEAM DataInputStream from " + path, ioException)
                return null
            }

            if (HEADER != header) {
                if (header != null && !testCase(header)) {
                    LOGGER.error(
                            "header typeID ($header) did not match expected ($HEADER) from $path. " +
                                    "There are ${dataInputStream.available()} bytes available on the " +
                                    "dataInputStream. File size is ${File(path).length()} bytes."
                    )
                }
                return null
            }

            try {
                length(dataInputStream)
            } catch (ioException: IOException) {
                LOGGER.error("Could not read length from BEAM DataInputStream from " + path, ioException)
                return null
            }

            val section: String?

            try {
                section = typeID(dataInputStream, path)
            } catch (ioException: IOException) {
                LOGGER.error("Could not read section header from BEAM DataInputStream from " + path, ioException)
                return null
            }


            if ("BEAM" != section) {
                LOGGER.error("Section header is not BEAM in " + path)
                return null
            }

            val chunkList = ArrayList<Chunk>()
            val i = 1

            while (true) {
                val chunk: Chunk?

                try {
                    chunk = Chunk.from(dataInputStream, path)
                } catch (ioException: IOException) {
                    LOGGER.error(
                            "Could not read chunk number " + i + " from BEAM DataInputStream from " + path +
                                    ".  Returning truncated Beam object",
                            ioException
                    )
                    break
                }

                if (chunk != null) {
                    chunkList.add(chunk)
                } else {
                    break
                }
            }

            return Beam(chunkList)
        }

        @Throws(IOException::class, OtpErlangDecodeException::class)
        fun from(content: ByteArray, path: String): Beam? =
                decompressedInputStream(ByteArrayInputStream(content))
                        ?.let { DataInputStream(it) }
                        ?.let { Beam.from(it, path) }

        @Throws(IOException::class, OtpErlangDecodeException::class)
        fun from(fileContent: FileContent): Beam? = from(fileContent.content, fileContent.file.path)

        fun from(virtualFile: VirtualFile): Beam? =
                virtualFileToInputStream(virtualFile)
                        ?.let { decompressedInputStream(it) }
                        ?.let { DataInputStream(it) }
                        ?.let { Beam.from(it, virtualFile.path) }

        fun `is`(virtualFile: VirtualFile): Boolean = !virtualFile.isDirectory && "beam" == virtualFile.extension

        private fun testCase(header: String?): Boolean = header == "baz "
    }
}
