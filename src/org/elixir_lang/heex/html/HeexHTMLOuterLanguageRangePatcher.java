package org.elixir_lang.heex.html;

import com.intellij.psi.templateLanguages.TemplateDataElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeexHTMLOuterLanguageRangePatcher implements TemplateDataElementType.OuterLanguageRangePatcher {
    @Override
    public @Nullable String getTextForOuterLanguageInsertionRange(@NotNull TemplateDataElementType templateDataElementType, @NotNull CharSequence charSequence) {
        return "HEExInjection";
    }
}
