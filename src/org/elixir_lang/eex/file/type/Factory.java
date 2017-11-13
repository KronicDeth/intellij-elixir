package org.elixir_lang.eex.file.type;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.elixir_lang.eex.file.Type;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class Factory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        LanguageFileType type = Type.INSTANCE;
        fileTypeConsumer.consume(type, type.getDefaultExtension());
    }
}
