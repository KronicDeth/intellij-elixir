package org.elixir_lang.heex;

import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/HbLanguage.java
public class HeexLanguage extends com.intellij.lang.Language implements TemplateLanguage {
    public static final HeexLanguage INSTANCE = new HeexLanguage();

    protected HeexLanguage(@Nullable com.intellij.lang.Language baseLanguage,
                           @NotNull String ID,
                           @NotNull String... mimeTypes) {
        super(baseLanguage, ID, mimeTypes);
    }

    public HeexLanguage() {
        super("HEEx");
    }

    @Contract(pure = true)
    public static LanguageFileType defaultTemplateLanguageFileType() {
        return FileTypes.PLAIN_TEXT;
    }
}
