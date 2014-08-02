package org.elixir_lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirFile extends PsiFileBase {
    public ElixirFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ElixirLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ElixirFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Elixir File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
