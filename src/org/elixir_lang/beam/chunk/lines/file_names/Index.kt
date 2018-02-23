package org.elixir_lang.beam.chunk.lines.file_names

import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import org.elixir_lang.beam.FileType

class Index: ScalarIndexExtension<String>() {
    override fun dependsOnFileContent() = true
    override fun getIndexer() = INDEXER
    override fun getInputFilter() = DefaultFileTypeSpecificInputFilter(FileType.INSTANCE)
    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE
    override fun getName() = NAME
    override fun getVersion() = VERSION

    companion object {
        const val VERSION = 1

        val INDEXER = Indexer()
        val NAME = ID.create<String, Void>("beam.chunk.lines.file_names")
    }
}
