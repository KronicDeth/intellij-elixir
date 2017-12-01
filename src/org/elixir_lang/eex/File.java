package org.elixir_lang.eex;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.elixir_lang.eex.file.Type;
import org.jetbrains.annotations.NotNull;

public class File extends PsiFileBase {
    public File(@NotNull FileViewProvider fileViewProvider) {
        super(fileViewProvider, Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Type.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return "Embedded Elixir File";
    }
}
