package org.elixir_lang.beam.chunk.lines.file_names

import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import org.elixir_lang.beam.BeamReader

class Indexer: DataIndexer<String, Void, FileContent> {
    override fun map(inputData: FileContent): Map<String, Void?> =
        BeamReader.read(inputData) { reader ->
            reader.lines?.let { lines ->
                // Drop "invalid"
                lines
                        .fileNameList
                        .drop(1)
                        .associate { Pair(it, null) }
            }
        } ?: emptyMap()
}
