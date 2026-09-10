package org.elixir_lang.beam.chunk

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.RefusedBeamData
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedShort
import org.elixir_lang.beam.chunk.lines.LineReference
import org.elixir_lang.beam.term.Atom
import org.elixir_lang.beam.term.Integer
import org.elixir_lang.beam.term.Term
import org.elixir_lang.beam.declaredCount
import java.nio.charset.Charset

class Lines(val lineReferenceList: List<LineReference>, val fileNameList: List<String>) {
    companion object {
        /** Version, flags, and the line instruction, line reference and file name counts. */
        private const val HEADER_BYTE_COUNT = 5 * Int.SIZE_BYTES

        /** A line item is a compact term, which can be a single byte. */
        private const val LINE_ITEM_MIN_BYTE_COUNT = 1

        /** An empty file name is only its 16-bit length. */
        private const val FILE_NAME_MIN_BYTE_COUNT = Short.SIZE_BYTES

        //
        /**
         * Reversing https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_asm.erl?utf8=%E2%9C%93#L285-L297
         *
         * ```erlang
         * build_line_table(Dict) ->
         *     {NumLineInstrs,NumFnames0,Fnames0,NumLines,Lines0} =
         *     beam_dict:line_table(Dict),
         *     NumFnames = NumFnames0 - 1,
         *     [_|Fnames1] = Fnames0,
         *     Fnames2 = [unicode:characters_to_binary(F) || F <- Fnames1],
         *     Fnames = << <<(byte_size(F)):16,F/binary>> || F <- Fnames2 >>,
         *     Lines1 = encode_line_items(Lines0, 0),
         *     Lines = iolist_to_binary(Lines1),
         *     Ver = 0,
         *     Bits = 0,
         *     <<Ver:32,Bits:32,NumLineInstrs:32,NumLines:32,NumFnames:32,
         *     Lines/binary,Fnames/binary>>.
         * ```
         *
         * One interpretation: http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html#line-line-numbers-table
         */
        fun from(chunk: Chunk, literalFloat: Boolean = true): Lines {
            val data = chunk.data

            if (data.size < HEADER_BYTE_COUNT) {
                throw RefusedBeamData("Line chunk is ${data.size} bytes, shorter than its $HEADER_BYTE_COUNT byte header")
            }

            var offset = 0

            val (version, versionByteCount) = unsignedInt(data, offset)
            offset += versionByteCount

            // OTP's loader ignores both, but the layout below is only known for version 0 without flags.
            if (version != 0L) throw RefusedBeamData("Line chunk version $version is not 0")

            val (flags, flagsByteCount) = unsignedInt(data, offset)
            offset += flagsByteCount

            if (flags != 0L) throw RefusedBeamData("Line chunk flags $flags are not 0")

            val (_, lineInstructionCountByteCount) = unsignedInt(data, offset)
            offset += lineInstructionCountByteCount

            val (lineReferenceCount, lineReferenceCountByteCount) = unsignedInt(data, offset)
            offset += lineReferenceCountByteCount

            val (fileNameCount, fileNameCountByteCount) = unsignedInt(data, offset)
            offset += fileNameCountByteCount

            val lineReferenceCountInt =
                declaredCount(lineReferenceCount, data.size - offset, LINE_ITEM_MIN_BYTE_COUNT, "Line references")

            var fileNameIndex = 0
            val lineReferences = mutableListOf<LineReference>()
            var fileNameChanges = 0

            while (lineReferences.size < lineReferenceCountInt) {
                // Items vary in size, so a count the chunk could hold can still run past it.
                val (term, termByteCount) = try {
                    Term.from(data, offset, literalFloat)
                } catch (_: IndexOutOfBoundsException) {
                    throw RefusedBeamData("Line references run past the end of a ${data.size} byte chunk")
                } catch (exception: IllegalArgumentException) {
                    throw RefusedBeamData("Line references hold an undecodable term: ${exception.message}")
                }
                offset += termByteCount

                when (term) {
                    is Atom -> {
                        fileNameChanges++
                        fileNameIndex = term.index
                    }
                    is Integer ->
                            lineReferences.add(LineReference(fileNameIndex, term.long))
                    else ->
                        throw RefusedBeamData(
                            "Line references hold a ${term.javaClass.simpleName}, which is neither a file name nor a line",
                        )
                }
            }

            val fileNames = mutableListOf<String>("Invalid")
            /* File names are always ISO-LATIN-1, which is ISO-8859-1 in Java
               https://github.com/erlang/otp/blob/OTP-20.2.2/erts/emulator/beam/beam_load.c?utf8=%E2%9C%93#L1795 */
            val charset = Charset.forName("ISO-8859-1")

            val fileNamesRunPast = "Line file names run past the end of a ${data.size} byte chunk"

            repeat(declaredCount(fileNameCount, data.size - offset, FILE_NAME_MIN_BYTE_COUNT, "Line file names")) {
                if (offset + FILE_NAME_MIN_BYTE_COUNT > data.size) throw RefusedBeamData(fileNamesRunPast)

                val (fileNameSize, fileNameSizeByteCount) = unsignedShort(data, offset)
                offset += fileNameSizeByteCount

                if (offset + fileNameSize > data.size) throw RefusedBeamData(fileNamesRunPast)

                val fileName = String(data, offset, fileNameSize, charset)
                offset += fileNameSize

                fileNames.add(fileName)
            }

            return Lines(lineReferences, fileNames)
        }
    }
}
