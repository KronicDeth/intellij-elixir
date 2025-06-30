package org.elixir_lang.heex.html;

import com.intellij.lang.html.HTMLLanguage;

public class HeexHTMLLanguage extends HTMLLanguage {
    public static final HeexHTMLLanguage INSTANCE = new HeexHTMLLanguage();

    protected HeexHTMLLanguage() {
        super(HTMLLanguage.INSTANCE, "HEExHTML", "text/html", "text/htmlh");
    }
}
