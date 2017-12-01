package org.elixir_lang.eex;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/HbLanguage.java
public class Language extends com.intellij.lang.Language implements TemplateLanguage {
    public static final Language INSTANCE = new Language();

    protected Language(@Nullable com.intellij.lang.Language baseLanguage,
                       @NotNull String ID,
                       @NotNull String... mimeTypes) {
        super(baseLanguage, ID, mimeTypes);
    }

    public Language() {
        super("EEx");
    }

    @Contract(pure = true)
    public static LanguageFileType defaultTemplateLanguageFileType() {
        return StdFileTypes.PLAIN_TEXT;
    }
}
