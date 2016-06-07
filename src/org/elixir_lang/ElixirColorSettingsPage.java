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
            new AttributesDescriptor("Braces and Operators//Bit", ElixirSyntaxHighlighter.BIT),
            new AttributesDescriptor("Braces and Operators//Braces", ElixirSyntaxHighlighter.BRACES),
            new AttributesDescriptor("Braces and Operators//Brackets", ElixirSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Braces and Operators//Character Token", ElixirSyntaxHighlighter.CHAR_TOKEN_TOKEN),
            new AttributesDescriptor("Braces and Operators//Comma", ElixirSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Braces and Operators//Dot", ElixirSyntaxHighlighter.DOT),
            new AttributesDescriptor(
                    "Braces and Operators//Interpolation",
                    ElixirSyntaxHighlighter.EXPRESSION_SUBSTITUTION_MARK
            ),
            new AttributesDescriptor("Braces and Operators//Maps and Structs//Maps", ElixirSyntaxHighlighter.MAP),
            new AttributesDescriptor("Braces and Operators//Maps and Structs//Structs", ElixirSyntaxHighlighter.STRUCT),
            new AttributesDescriptor("Braces and Operators//Operation Sign", ElixirSyntaxHighlighter.OPERATION_SIGN),
            new AttributesDescriptor("Braces and Operators//Parentheses", ElixirSyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Braces and Operators//Semicolon", ElixirSyntaxHighlighter.SEMICOLON),
            new AttributesDescriptor("Character List", ElixirSyntaxHighlighter.CHAR_LIST),
            new AttributesDescriptor("Comment", ElixirSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Escape Sequence", ElixirSyntaxHighlighter.VALID_ESCAPE_SEQUENCE),
            new AttributesDescriptor("Identifier", ElixirSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Keywords", ElixirSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Module Attributes", ElixirSyntaxHighlighter.MODULE_ATTRIBUTE),
            new AttributesDescriptor("Module Attributes//Documentation", ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE),
            new AttributesDescriptor("Module Attributes//Documentation//Text", ElixirSyntaxHighlighter.DOCUMENTATION_TEXT),
            new AttributesDescriptor("Module Attributes//Types//Callback", ElixirSyntaxHighlighter.CALLBACK),
            new AttributesDescriptor("Module Attributes//Types//Specification", ElixirSyntaxHighlighter.SPECIFICATION),
            new AttributesDescriptor("Module Attributes//Types//Type", ElixirSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Module Attributes//Types//Type Parameter", ElixirSyntaxHighlighter.TYPE_PARAMETER),
            new AttributesDescriptor("Numbers//Binary, Decimal, Hexadecimal, and Octal Digits", ElixirSyntaxHighlighter.VALID_DIGIT),
            new AttributesDescriptor("Numbers//Decimal Exponent, Mark, and Separator", ElixirSyntaxHighlighter.DECIMAL),
            new AttributesDescriptor("Numbers//Invalid Binary, Decimal, Hexadecimal, and Octal Digits", ElixirSyntaxHighlighter.INVALID_DIGIT),
            new AttributesDescriptor("Numbers//Non-Decimal Base Prefix", ElixirSyntaxHighlighter.WHOLE_NUMBER_BASE),
            new AttributesDescriptor("Numbers//Obsolete Non-Decimal Base Prefix", ElixirSyntaxHighlighter.OBSOLETE_WHOLE_NUMBER_BASE),
            new AttributesDescriptor("Calls//Function", ElixirSyntaxHighlighter.FUNCTION_CALL),
            new AttributesDescriptor("Calls//Predefined", ElixirSyntaxHighlighter.PREDEFINED_CALL),
            new AttributesDescriptor("Calls//Macro", ElixirSyntaxHighlighter.MACRO_CALL),
            new AttributesDescriptor("Sigil", ElixirSyntaxHighlighter.SIGIL),
            new AttributesDescriptor("String", ElixirSyntaxHighlighter.STRING),
            new AttributesDescriptor("Variables//Ignored", ElixirSyntaxHighlighter.IGNORED_VARIABLE),
            new AttributesDescriptor("Variables//Parameter", ElixirSyntaxHighlighter.PARAMETER),
            new AttributesDescriptor("Variables//Variable", ElixirSyntaxHighlighter.VARIABLE)
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
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("callback", ElixirSyntaxHighlighter.CALLBACK);
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
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("function-call", ElixirSyntaxHighlighter.FUNCTION_CALL);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("ignored", ElixirSyntaxHighlighter.IGNORED_VARIABLE);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("macro-call", ElixirSyntaxHighlighter.MACRO_CALL);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("map", ElixirSyntaxHighlighter.MAP);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("module-attribute", ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("parameter", ElixirSyntaxHighlighter.PARAMETER);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("predefined-call", ElixirSyntaxHighlighter.PREDEFINED_CALL);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("struct", ElixirSyntaxHighlighter.STRUCT);
        // needed so that <error></error> doesn't override VALID_DIGIT token highlighting
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "valid-digit", ElixirSyntaxHighlighter.VALID_DIGIT
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put(
                "variable",
                ElixirSyntaxHighlighter.VARIABLE
        );
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("specification", ElixirSyntaxHighlighter.SPECIFICATION);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("type", ElixirSyntaxHighlighter.TYPE);
        TEXT_ATTRIBUTES_KEY_BY_HIGHLIGHTING_TAG.put("type-parameter", ElixirSyntaxHighlighter.TYPE_PARAMETER);
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
