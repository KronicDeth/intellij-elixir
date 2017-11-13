package org.elixir_lang.eex.element_type;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.eex.lexer.TemplateData.EEX;
import static org.elixir_lang.eex.lexer.TemplateData.NOT_DATA;
import static org.elixir_lang.eex.lexer.TemplateData.NOT_ELIXIR;
import static org.elixir_lang.eex.psi.Types.DATA;
import static org.elixir_lang.eex.psi.Types.ELIXIR;

public class TemplateData extends TemplateDataElementType {
    private final Language templateFileLanguage;
    private final TokenSet mergedTokenSet;

    public TemplateData(@NotNull String debugName, @NotNull Language templateFileLanguage) {
        super(
                debugName,
                org.elixir_lang.eex.Language.INSTANCE,
                templateElementType(templateFileLanguage),
                EEX
        );
        this.templateFileLanguage = templateFileLanguage;
        this.mergedTokenSet = mergedTokenSet(templateFileLanguage);
    }

    @Contract(pure = true)
    @NotNull
    private static IElementType templateElementType(@NotNull Language language) {
        IElementType templateElementType;

        if (language == ElixirLanguage.INSTANCE) {
            templateElementType = ELIXIR;
        } else {
            templateElementType = DATA;
        }

        return templateElementType;
    }

    @Contract(pure = true)
    @NotNull
    private static TokenSet mergedTokenSet(@NotNull Language language) {
        TokenSet mergedTokenSet;

        if (language == ElixirLanguage.INSTANCE) {
            mergedTokenSet = NOT_ELIXIR;
        } else {
            mergedTokenSet = NOT_DATA;
        }

        return mergedTokenSet;
    }

    @NotNull
    @Override
    protected Lexer createBaseLexer(@NotNull TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return new org.elixir_lang.eex.lexer.TemplateData(mergedTokenSet);
    }

    @NotNull
    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider templateLanguageFileViewProvider) {
        return templateFileLanguage;
    }
}
