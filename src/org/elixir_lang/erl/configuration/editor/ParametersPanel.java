package org.elixir_lang.erl.configuration.editor;

import com.intellij.execution.ui.CommonProgramParametersPanel;
import org.jetbrains.annotations.NotNull;

public class ParametersPanel extends CommonProgramParametersPanel {
    private String getErlArguments() {
        return getProgramParametersComponent().getComponent().getText();
    }

    private void setErlArguments(@NotNull String text) {
        getProgramParametersComponent().getComponent().setText(text);
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        setProgramParametersLabel("erl arguments:");
    }
}
