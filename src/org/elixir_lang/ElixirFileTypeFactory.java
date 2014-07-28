package org.elixir_lang;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class ElixirFileTypeFactory extends FileTypeFactory{
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(ElixirFileType.INSTANCE, "ex");
        fileTypeConsumer.consume(ElixirFileType.INSTANCE, "exs");
    }
}
