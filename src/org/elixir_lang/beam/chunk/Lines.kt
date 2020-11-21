package org.elixir_lang.beam.chunk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.chunk.Chunk.unsignedInt
import org.elixir_lang.beam.chunk.Chunk.unsignedShort
import org.elixir_lang.beam.chunk.lines.LineReference
import org.elixir_lang.beam.term.Atom
import org.elixir_lang.beam.term.Integer
import org.elixir_lang.beam.term.Term
import org.elixir_lang.beam.term.unsignedIntToInt
import java.nio.charset.Charset

class Lines(val lineReferenceList: List<LineReference>, val fileNameList: List<String>) {
    companion object {
        private val logger = Logger.getInstance(Lines::class.java)

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
            var offset = 0

            val (version, versionByteCount) = unsignedInt(data, offset)
            offset += versionByteCount

            assert(version == 0L)

            val (flags, flagsByteCount) = unsignedInt(data, offset)
            offset += flagsByteCount

            assert(flags == 0L)

            val (_, lineInstructionCountByteCount) = unsignedInt(data, offset)
            offset += lineInstructionCountByteCount

            val (lineReferenceCount, lineReferenceCountByteCount) = unsignedInt(data, offset)
            val lineReferenceCountInt = unsignedIntToInt(lineReferenceCount)
            offset += lineReferenceCountByteCount

            val (fileNameCount, fileNameCountByteCount) = unsignedInt(data, offset)
            offset += fileNameCountByteCount

            var fileNameIndex = 0
            val lineReferences = mutableListOf<LineReference>()
            var fileNameChanges = 0

            while (lineReferences.size < lineReferenceCountInt) {
                val (term, termByteCount) = Term.from(data, offset, literalFloat)
                offset += termByteCount

                when (term) {
                    is Atom -> {
                        fileNameChanges++
                        fileNameIndex = term.index
                    }
                    is Integer ->
                            lineReferences.add(LineReference(fileNameIndex, term.long))
                    else ->
                            TODO()
                }
            }

            val fileNames = mutableListOf<String>("Invalid")
            /* File names are always ISO-LATIN-1, which is ISO-8859-1 in Java
               https://github.com/erlang/otp/blob/OTP-20.2.2/erts/emulator/beam/beam_load.c?utf8=%E2%9C%93#L1795 */
            val charset = Charset.forName("ISO-8859-1")

            repeat(unsignedIntToInt(fileNameCount)) {
                val (fileNameSize, fileNameSizeByteCount) = unsignedShort(data, offset)
                offset += fileNameSizeByteCount

                val fileName = String(data, offset, fileNameSize, charset)
                offset += fileNameSize

                fileNames.add(fileName)
            }

            return Lines(lineReferences, fileNames)
        }
    }
}
