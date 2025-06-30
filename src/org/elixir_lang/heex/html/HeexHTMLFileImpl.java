package org.elixir_lang.heex.html;

import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLFileImpl extends HtmlFileImpl {
    public HeexHTMLFileImpl(FileViewProvider provider) {
        super(provider, HeexHTMLFileElementType.INSTANCE);
    }

    @Override
    public @NotNull Language getLanguage() {
        return HeexHTMLLanguage.INSTANCE;
    }

    @Override
    public String toString() {
        return "HEEx HTML File: "+ this.getName();
    }
}
