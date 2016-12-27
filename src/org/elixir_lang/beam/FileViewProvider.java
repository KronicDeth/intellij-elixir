package org.elixir_lang.beam;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.beam.psi.BeamFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileViewProvider extends SingleRootFileViewProvider {
    public FileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file) {
        this(manager, file, true);
    }

    public FileViewProvider(@NotNull PsiManager manager,
                            @NotNull VirtualFile virtualFile,
                            boolean eventSystemEnabled) {
        super(manager, virtualFile, eventSystemEnabled, ElixirLanguage.INSTANCE);
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file, @NotNull FileType fileType) {
        return new BeamFileImpl(this);
    }

    @NotNull
    @Override
    public SingleRootFileViewProvider createCopy(@NotNull VirtualFile copy) {
        return new FileViewProvider(getManager(), copy, false);
    }
}
