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
            new AttributesDescriptor("Atom", ElixirSyntaxHighlighter.ATOM),
            new AttributesDescriptor("Comment", ElixirSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Character List", ElixirSyntaxHighlighter.CHAR_LIST),
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
        return "0b10 # binary\n" +
                "0B10 # deprecated binary\n" +
                "0o12345670 # octal\n" +
                "012345670 # deprecated octal\n" +
                "0x1234567890abcdefABCDEF # hexadecimal\n" +
                "0X1234567890abcdefABCDEF # deprecated hexadecimal\n" +
                ":literal_atom\n" +
                ":\"double #{\"quoted\"} atom\"\n" +
                ":'single #{\'quoted\'} atom'\n" +
                "'This CharList\\'s body contains #{'interpolation'} and \\#{escapes}'\n" +
                "  '''\n" +
                "  This CharList Heredoc's body contains #{'interpolation'} and \\#{escapes}\n" +
                "  '''\n" +
                "\"This String's body contains #{\"interpolation\"} and \\#{escapes}\"\n" +
                "   \"\"\"\n" +
                "   This String Heredoc's body contains #{\"interpolation\"} and \\#{escapes}\n" +
                "   \"\"\"\n" +
                "~c(CharList with #{\"interpolation\"})\n" +
                "  ~c\"\"\"\n" +
                "  CharList heredoc with #{\"interpolation\"}\n" +
                "  \"\"\"\n" +
                "~C/CharList without #{\"interpolation\"}/\n" +
                "  ~C\"\"\"\n" +
                "  CharList heredoc without #{\"interpolation\"}\n" +
                "  \"\"\"\n" +
                "~r{Regex with #{\"interpolation\"}\n" +
                "  ~r\"\"\"\n" +
                "  Regex heredoc with #{\"interpolation\"}\n" +
                "  \"\"\"\n" +
                "~R[Regex without #{\"interpolation\"}]\n" +
                "  ~R'''\n" +
                "  Regex heredoc without #{\"interpolation\"}\n" +
                "  '''\n" +
                "~s<String with #{\"interpolation\"}>\n" +
                "  ~s'''\n" +
                "  String heredoc with #{\"interpolation\"}\n" +
                "  '''\n" +
                "~S|String without #{\"interpolation\"}|\n" +
                "  ~S'''\n" +
                "  String heredoc without #{\"interpolation\"}\n" +
                "  '''\n" +
                "~w'Words with #{\"interpolation\"}'\n" +
                "  ~w'''\n" +
                "  Words heredoc with #{\"interpolation\"}\n" +
                "  '''\n" +
                "~W\"Words without #{'interpolation'}\"\n" +
                "  ~W'''\n" +
                "  Words heredoc without #{\"interpolation\"}\n" +
                "  '''\n" +
                "~x'Sigil with #{\"interpolation\"}'\n" +
                "  ~x'''\n" +
                "  Sigil heredoc with #{\"interpolation\"}\n" +
                "  '''\n" +
                "~X{Sigil without #{'interpolation'}}\n" +
                "  ~X'''\n" +
                "  Sigil heredoc without #{\"interpolation\"}\n" +
                "  '''";
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
