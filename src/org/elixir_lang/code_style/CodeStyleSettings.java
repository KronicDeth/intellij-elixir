package org.elixir_lang.code_style;

import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class CodeStyleSettings extends CustomCodeStyleSettings {
    public boolean SPACE_AFTER_CAPTURE_OPERATOR = false;
    public boolean SPACE_AROUND_IN_MATCH_OPERATORS = true;
    public boolean SPACE_AROUND_TYPE_OPERATOR = true;

    public CodeStyleSettings(com.intellij.psi.codeStyle.CodeStyleSettings container) {
        super("ElixirCodeStyleSettings", container);
    }
}
