package org.elixir_lang.beam.assembly.file.type

import com.intellij.openapi.fileTypes.FileTypeConsumer
import org.elixir_lang.beam.assembly.file.Type

class Factory: com.intellij.openapi.fileTypes.FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(Type, Type.defaultExtension)
    }
}
