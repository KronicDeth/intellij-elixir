package org.elixir_lang.eex.element_type;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.eex.lexer.TemplateData.EEX;
import static org.elixir_lang.eex.psi.Types.DATA;

public class TemplateData extends TemplateDataElementType {
    private final Language templateFileLanguage;

    public TemplateData(@NotNull Language templateFileLanguage) {
        super(
                "EEX_TEMPLATE_DATA",
                org.elixir_lang.eex.Language.INSTANCE,
                DATA,
                EEX
        );
        this.templateFileLanguage = templateFileLanguage;
    }

    @NotNull
    @Override
    protected Lexer createBaseLexer(@NotNull TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return new org.elixir_lang.eex.lexer.TemplateData();
    }

    @NotNull
    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return templateFileLanguage;
    }
}
