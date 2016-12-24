package org.elixir_lang.beam;

public class Language extends com.intellij.lang.Language {
    public static Language INSTANCE = new Language();

    private Language() {
        super("BEAM");
    }
}
