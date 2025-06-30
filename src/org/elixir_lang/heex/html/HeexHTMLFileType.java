package org.elixir_lang.heex.html;

import com.intellij.icons.AllIcons.FileTypes;
import com.intellij.ide.highlighter.HtmlFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HeexHTMLFileType extends HtmlFileType {
    public static final HeexHTMLFileType INSTANCE = new HeexHTMLFileType();

    private HeexHTMLFileType() {
        super(HeexHTMLLanguage.INSTANCE);
    }

    public @NotNull String getName() {
        return "HEEx HTML";
    }

    public @NotNull String getDescription() {
        return "HTML Embedded in HEEx";
    }

    public @NotNull String getDefaultExtension() {
        return "html";
    }

    public Icon getIcon() {
        return FileTypes.Html;
    }
}
