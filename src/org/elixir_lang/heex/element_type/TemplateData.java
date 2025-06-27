package org.elixir_lang.heex.element_type;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.heex.lexer.TemplateData.HEEX;
import static org.elixir_lang.heex.psi.Types.DATA;

public class TemplateData extends TemplateDataElementType {
    private final Language templateFileLanguage;

    public TemplateData(@NotNull Language templateFileLanguage) {
        super(
                "HEEX_TEMPLATE_DATA",
                org.elixir_lang.heex.Language.INSTANCE,
                DATA,
                HEEX
        );
        this.templateFileLanguage = templateFileLanguage;
    }

    @NotNull
    @Override
    protected Lexer createBaseLexer(@NotNull TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return new org.elixir_lang.heex.lexer.TemplateData();
    }

    @NotNull
    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return templateFileLanguage;
    }
}
