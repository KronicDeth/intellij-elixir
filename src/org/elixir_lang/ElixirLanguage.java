package org.elixir_lang;

import com.intellij.lang.Language;

/**
 * Created by luke.imhoff on 7/27/14.
 */
public class ElixirLanguage extends Language {
    public static final ElixirLanguage INSTANCE = new ElixirLanguage();

    private ElixirLanguage() {
        super("Elixir");
    }
}
