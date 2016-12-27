package org.elixir_lang.beam;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileViewProviderFactory implements com.intellij.psi.FileViewProviderFactory {
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile file,
                                                   @Nullable Language language,
                                                   @NotNull PsiManager manager,
                                                   boolean eventSystemEnabled) {
        return new FileViewProvider(manager, file, eventSystemEnabled);
    }
}
