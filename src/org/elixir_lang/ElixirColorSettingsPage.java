package org.elixir_lang;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", ElixirSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Character List", ElixirSyntaxHighlighter.CHAR_LIST),
            new AttributesDescriptor("Escape Sequence", ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE),
            new AttributesDescriptor("Expression Substitution Mark", ElixirSyntaxHighlighter.EXPRESSION_SUBSTITUTION_MARK),
            new AttributesDescriptor("Number", ElixirSyntaxHighlighter.NUMBER),
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
        return "0b10 # binary\n" +
                "0B10 # deprecated binary\n" +
                "0o12345670 # octal\n" +
                "012345670 # deprecated octal\n" +
                "0x1234567890abcdefABCDEF # hexadecimal\n" +
                "0X1234567890abcdefABCDEF # deprecated hexadecimal\n" +
                "'This CharList\\'s body contains #{'interpolation'} and \\#{escapes}'\n" +
                "  '''\n" +
                "  This CharList Heredoc's body contains #{'interpolation'} and \\#{escapes}\n" +
                "  '''\n" +
                "\"This String's body contains #{\"interpolation\"} and \\#{escapes}\"\n" +
                "   \"\"\"\n" +
                "   This String Heredoc's body contains #{\"interpolation\"} and \\#{escapes}\n" +
                "   \"\"\"\n";
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
