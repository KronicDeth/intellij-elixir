package org.elixir_lang;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

// Reference
// https://upsource.jetbrains.com/idea-ce/file/idea-ce-7cf49a16e4e15a18fa2f742635053647ae94abfb/plugins/properties/src/com/intellij/lang/properties/PropertiesDocumentationProvider.java

public class ElixirDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    @Nullable
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return "Documentation to come getQuickNavigateInfo";
    }

    // Pressing F1
    @Override
    public String generateDoc(final PsiElement element, @Nullable final PsiElement originalElement) {
        return "Documentation to come generateDoc";
    }
}
