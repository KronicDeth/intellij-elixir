package org.elixir_lang;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.icons.ElixirIcons;
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
    private static final Map<String, TextAttributesKey> TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG;
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Alias", ElixirSyntaxHighlighter.ALIAS),
            new AttributesDescriptor("Atom", ElixirSyntaxHighlighter.ATOM),
            new AttributesDescriptor("Binary, Decimal, Hexadecimal, and Octal Digits", ElixirSyntaxHighlighter.VALID_DIGIT),
            new AttributesDescriptor("Braces and Operators//Bit", ElixirSyntaxHighlighter.BIT),
            new AttributesDescriptor("Braces and Operators//Braces", ElixirSyntaxHighlighter.BRACES),
            new AttributesDescriptor("Braces and Operators//Brackets", ElixirSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Braces and Operators//Comma", ElixirSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Braces and Operators//Dot", ElixirSyntaxHighlighter.DOT),
            new AttributesDescriptor("Braces and Operators//Semicolon", ElixirSyntaxHighlighter.SEMICOLON),
            new AttributesDescriptor("Callback", ElixirSyntaxHighlighter.CALLBACK),
            new AttributesDescriptor("Character List", ElixirSyntaxHighlighter.CHAR_LIST),
            new AttributesDescriptor("Character Token", ElixirSyntaxHighlighter.CHAR_TOKEN_TOKEN),
            new AttributesDescriptor("Comment", ElixirSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Decimal Exponent, Mark, and Separator", ElixirSyntaxHighlighter.DECIMAL),
            new AttributesDescriptor("Documentation Module Attributes", ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE),
            new AttributesDescriptor("Documentation Text", ElixirSyntaxHighlighter.DOCUMENTATION_TEXT),
            new AttributesDescriptor("Escape Sequence", ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE),
            new AttributesDescriptor("Expression Substitution Mark", ElixirSyntaxHighlighter.EXPRESSION_SUBSTITUTION_MARK),
            new AttributesDescriptor("Identifier", ElixirSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Ignored Variable", ElixirSyntaxHighlighter.VARIABLE),
            new AttributesDescriptor("Invalid Binary, Decimal, Hexadecimal, and Octal Digits", ElixirSyntaxHighlighter.INVALID_DIGIT),
            new AttributesDescriptor("Kernel Functions", ElixirSyntaxHighlighter.KERNEL_FUNCTION),
            new AttributesDescriptor("Kernel Macros", ElixirSyntaxHighlighter.KERNEL_MACRO),
            new AttributesDescriptor("Kernel.SpecialForms Macros", ElixirSyntaxHighlighter.KERNEL_SPECIAL_FORMS_MACRO),
            new AttributesDescriptor("Keywords", ElixirSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Module Attributes", ElixirSyntaxHighlighter.MODULE_ATTRIBUTE),
            new AttributesDescriptor("Non-Decimal Base Prefix", ElixirSyntaxHighlighter.WHOLE_NUMBER_BASE),
            new AttributesDescriptor("Obsolete Non-Decimal Base Prefix", ElixirSyntaxHighlighter.OBSOLETE_WHOLE_NUMBER_BASE),
            new AttributesDescriptor("Operation Sign", ElixirSyntaxHighlighter.OPERATION_SIGN),
            new AttributesDescriptor("Parameter", ElixirSyntaxHighlighter.PARAMETER),
            new AttributesDescriptor("Sigil", ElixirSyntaxHighlighter.SIGIL),
            new AttributesDescriptor("Specification", ElixirSyntaxHighlighter.SPECIFICATION),
            new AttributesDescriptor("String", ElixirSyntaxHighlighter.STRING),
            new AttributesDescriptor("Type", ElixirSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Type Parameter", ElixirSyntaxHighlighter.TYPE_PARAMETER),
            new AttributesDescriptor("Variable", ElixirSyntaxHighlighter.VARIABLE)
    };

    static {
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG = ContainerUtil.newHashMap();
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "alias",
                ElixirSyntaxHighlighter.ALIAS
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "atom",
                ElixirSyntaxHighlighter.ATOM
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "documentation-module-attribute",
                ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "documentation-text",
                ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "escape-sequence",
                ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE
        );
        // see https://github.com/JetBrains/intellij-community/blob/9aa6a55984e5d0563013e6c918b6f787587b3bf8/platform/lang-impl/src/com/intellij/openapi/options/colors/pages/GeneralColorsPage.java#L147
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "error", CodeInsightColors.ERRORS_ATTRIBUTES
        );
        // needed so that <error></error> doesn't override VALID_DIGIT token highlighting
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "valid-digit", ElixirSyntaxHighlighter.VALID_DIGIT
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "variable",
                ElixirSyntaxHighlighter.VARIABLE
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "type",
                ElixirSyntaxHighlighter.TYPE
        );
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ElixirIcons.FILE;
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
        return TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG;
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
