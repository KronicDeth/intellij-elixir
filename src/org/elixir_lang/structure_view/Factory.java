package org.elixir_lang.structure_view;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Factory implements PsiStructureViewFactory {
    @Nullable
    public static StructureViewBuilder structureViewBuilder(@NotNull final PsiFile psiFile) {
        StructureViewBuilder structureViewBuilder = null;

        if (psiFile instanceof ElixirFile) {
            final ElixirFile elixirFile = (ElixirFile) psiFile;

            structureViewBuilder = new TreeBasedStructureViewBuilder() {
                @NotNull
                @Override
                public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
                    return new Model(elixirFile, editor);
                }

                @Override
                public boolean isRootNodeShown() {
                    return false;
                }
            };
        }

        return structureViewBuilder;
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
        return structureViewBuilder(psiFile);
    }
}
