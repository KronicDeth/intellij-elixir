package org.elixir_lang.eex.file.view_provider;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;
import org.elixir_lang.eex.file.ViewProvider;
import org.jetbrains.annotations.NotNull;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/file/HbFileViewProviderFactory.java
public class Factory implements com.intellij.psi.FileViewProviderFactory {
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile virtualFile,
                                                   @NotNull Language language,
                                                   @NotNull PsiManager psiManager,
                                                   boolean eventSystemEnabled) {
        assert language.isKindOf(org.elixir_lang.eex.Language.INSTANCE);
        return new ViewProvider(psiManager, virtualFile, eventSystemEnabled, language);
    }
}
