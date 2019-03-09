package org.elixir_lang.espec.configuration;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.module.Module;
import org.elixir_lang.espec.Configuration;
import org.elixir_lang.espec.configuration.editor.ParametersPanel;
import org.elixir_lang.module.ElixirModuleType;
import org.elixir_lang.sdk.ProcessOutput;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @link https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationEditorForm.java
 */
public final class Editor extends com.intellij.openapi.options.SettingsEditor<Configuration> {
    private JPanel component;
    private JCheckBox runInModuleCheckBox;
    private ModulesComboBox modulesComboBox;
    private ParametersPanel parametersPanel;

    public Editor() {
        runInModuleCheckBox.addActionListener(e -> modulesComboBox.setEnabled(runInModuleCheckBox.isSelected()));

        boolean richIde = !ProcessOutput.isSmallIde();

        modulesComboBox.setVisible(richIde);
        runInModuleCheckBox.setVisible(richIde);
    }

    @Override
    protected void resetEditorFrom(@NotNull Configuration configuration) {
        reset(configuration);
    }

    public void reset(@NotNull Configuration configuration) {
        Module module = null;
        if (!ProcessOutput.isSmallIde()) {
            modulesComboBox.fillModules(configuration.getProject(), ElixirModuleType.getInstance());
            module = configuration.getConfigurationModule().getModule();
        }

        if (module != null) {
            setRunInModuleSelected(true);
            modulesComboBox.setSelectedModule(module);
        } else {
            setRunInModuleSelected(false);
        }

        parametersPanel.reset(configuration);
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

    private void setRunInModuleSelected(boolean runInModule) {
        if (runInModule) {
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
