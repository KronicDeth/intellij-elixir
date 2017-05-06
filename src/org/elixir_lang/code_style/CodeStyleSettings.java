package org.elixir_lang.code_style;

import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class CodeStyleSettings extends CustomCodeStyleSettings {
    public CodeStyleSettings(com.intellij.psi.codeStyle.CodeStyleSettings container) {
        super("ElixirCodeStyleSettings", container);
    }
}
