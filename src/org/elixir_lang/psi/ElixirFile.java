package org.elixir_lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
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

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return ElixirPsiImplUtil.processDeclarationsRecursively(this, processor, state, lastParent, place);
    }
}
