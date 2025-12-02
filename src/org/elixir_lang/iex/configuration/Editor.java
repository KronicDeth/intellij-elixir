package org.elixir_lang.iex.configuration;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.elixir_lang.configuration.EditorHelper;
import org.elixir_lang.iex.Configuration;
import org.elixir_lang.iex.configuration.editor.ParametersPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class Editor extends SettingsEditor<Configuration> {
    private JPanel component;
    private JCheckBox runInModuleCheckBox;
    private ModulesComboBox modulesComboBox;
    private ParametersPanel parametersPanel;

    public Editor() {
        runInModuleCheckBox.addActionListener(e -> modulesComboBox.setEnabled(runInModuleCheckBox.isSelected()));
    }

    @Override
    protected void resetEditorFrom(@NotNull Configuration configuration) {
        reset(configuration);
    }

    public void reset(@NotNull Configuration configuration) {
        EditorHelper.reset(
                configuration.getProject(),
                configuration.getConfigurationModule().getModule(),
                modulesComboBox,
                this::setRunInModuleSelected,
                () -> parametersPanel.reset(configuration)
        );
    }

    @Override
    protected void applyEditorTo(@NotNull Configuration configuration) {
        Module selectedModule = runInModuleCheckBox.isSelected() ? modulesComboBox.getSelectedModule() : null;
        configuration.setModule(selectedModule);

        parametersPanel.applyTo(configuration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return component;
    }

    @Override
    protected void disposeEditor() {
        component.setVisible(false);
    }

    private void setRunInModuleSelected(boolean runInModuleSelected) {
        if (runInModuleSelected) {
            if (!runInModuleCheckBox.isSelected()) {
                runInModuleCheckBox.doClick();
            }
        } else {
            if (runInModuleCheckBox.isSelected()) {
                runInModuleCheckBox.doClick();
            }
        }
    }
}
