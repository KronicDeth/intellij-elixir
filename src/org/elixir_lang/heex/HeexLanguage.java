package org.elixir_lang.heex;

import com.intellij.ide.highlighter.HtmlFileType;
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

    // HEEx's data language is HTML by definition (Phoenix.LiveView.HTMLEngine), unlike EEx's
    // engine-agnostic PLAIN_TEXT default.
    @Contract(pure = true)
    public static LanguageFileType defaultTemplateLanguageFileType() {
        return HtmlFileType.INSTANCE;
    }
}
