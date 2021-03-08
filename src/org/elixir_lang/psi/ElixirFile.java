package org.elixir_lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        return ProcessDeclarationsImpl.processDeclarations(this, processor, state, lastParent, place);
    }

    /**
     * @return modulars owned (declared) by this element.
     */
    @NotNull
    public StubBased[] modulars() {
        StubBased[] stubBaseds = PsiTreeUtil.getChildrenOfType(
                this,
                StubBased.class
        );

        List<StubBased> modularList = new ArrayList<StubBased>();

        if (stubBaseds != null) {
            for (StubBased stubBased : stubBaseds) {
                if (Implementation.is(stubBased) || Module.Companion.is(stubBased) || Protocol.is(stubBased)) {
                    modularList.add(stubBased);
                }
            }
        }

        return modularList.toArray(new StubBased[modularList.size()]);
    }
}
