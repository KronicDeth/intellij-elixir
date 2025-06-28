package org.elixir_lang.heex.html;

import com.intellij.lang.xml.XMLLanguage;

public class HeexHTMLLanguage extends XMLLanguage {
    public static final HeexHTMLLanguage INSTANCE = new HeexHTMLLanguage();

    protected HeexHTMLLanguage() {
        super(XMLLanguage.INSTANCE, "HEExHTML", "text/html", "text/htmlh");
    }
}
