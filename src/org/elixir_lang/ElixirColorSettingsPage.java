package org.elixir_lang;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.intellij.openapi.util.io.FileUtil.loadTextAndClose;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Atom", ElixirSyntaxHighlighter.ATOM),
            new AttributesDescriptor("Character List", ElixirSyntaxHighlighter.CHAR_LIST),
            new AttributesDescriptor("Character Token", ElixirSyntaxHighlighter.CHAR_TOKEN),
            new AttributesDescriptor("Comment", ElixirSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Escape Sequence", ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE),
            new AttributesDescriptor("Expression Substitution Mark", ElixirSyntaxHighlighter.EXPRESSION_SUBSTITUTION_MARK),
            new AttributesDescriptor("Number", ElixirSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Sigil", ElixirSyntaxHighlighter.SIGIL),
            new AttributesDescriptor("String", ElixirSyntaxHighlighter.STRING)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ElixirIcon.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ElixirSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        InputStream demoTextInputStream = getClass().getResourceAsStream("/Color Settings Page Demo Text.ex");
        String demoText;

        try {
            demoText = loadTextAndClose(demoTextInputStream);
        } catch (IOException e) {
            demoText = "";
        }

        return demoText;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Elixir";
    }
}
