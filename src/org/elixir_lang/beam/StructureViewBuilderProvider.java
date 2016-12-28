package org.elixir_lang.beam;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.elixir_lang.beam.psi.BeamFileImpl;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.structure_view.Factory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author max
 */
public class StructureViewBuilderProvider implements com.intellij.ide.structureView.StructureViewBuilderProvider {
    /**
     * Returns the structure view builder for the specified file.
     *
     * @param fileType    file type of the file to provide structure for
     * @param virtualFile The file for which the structure view builder is requested.
     * @param project     The project to which the file belongs.
     * @return The structure view builder, or null if no structure view is available for the file.
     */
    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull FileType fileType,
                                                        @NotNull VirtualFile virtualFile,
                                                        @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        StructureViewBuilder structureViewBuilder = null;

        if (psiFile instanceof BeamFileImpl) {
            BeamFileImpl beamFileImpl = (BeamFileImpl) psiFile;
            PsiElement mirror = beamFileImpl.getMirror();

            if (mirror instanceof ElixirFile) {
                structureViewBuilder = Factory.structureViewBuilder((ElixirFile) mirror);
            }
        }

        return structureViewBuilder;
    }
}
