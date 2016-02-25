package org.elixir_lang.templates;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtilCore;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;

public class ElixirContextType extends TemplateContextType {

    ElixirContextType() {
        super("ELIXIR_CODE", "Elixir", EverywhereContextType.class);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile psiFile, int offset) {
        if (!PsiUtilCore.getLanguageAtOffset(psiFile, offset).isKindOf(ElixirLanguage.INSTANCE)) return false;
        PsiElement element = psiFile.findElementAt(offset);
        return !(element instanceof PsiWhiteSpace) && element != null;
    }
}
