package org.elixir_lang.beam

import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * > Change the file element type [...] to a class that extends IStubFileElementType.
 * -- http://www.jetbrains.org/intellij/sdk/docs/basics/indexing_and_psi_stubs/stub_indexes.html
 */
class FileType : com.intellij.openapi.fileTypes.FileType {
    override fun getDefaultExtension(): String = "beam"
    override fun getDescription(): String = "Bogdan/Björn's Erlang Abstract Machine file"
    override fun getIcon(): Icon = Icons.FILE

    /**
     * Returns the character set for the specified file.
     *
     * @param file    The file for which the character set is requested.
     * @param content bytes in the `file`
     * @return null because it's a binary file
     */
    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null

    override fun getName(): String = "BEAM"

    /**
     * [Beam files](http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html) are based on the
     * [IFF format](https://en.wikipedia.org/wiki/Interchange_File_Format).
     *
     * @return true
     */
    override fun isBinary(): Boolean = true

    /**
     * `.beam` files are only meant for decompilation.
     *
     * @return true
     */
    override fun isReadOnly(): Boolean = true

    override fun toString(): String = "BEAM"

    companion object {
        @JvmField
        val INSTANCE = FileType()
    }
}
