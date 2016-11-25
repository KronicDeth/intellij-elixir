package org.elixir_lang.mix.runner.exunit;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.elixir_lang.mix.runner.MixRunConfigurationEditorForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class MixExUnitRunConfigurationEditorForm extends SettingsEditor<MixExUnitRunConfiguration>{
  private JPanel myComponent;
  private MixRunConfigurationEditorForm mixRunConfigurationEditorForm;

  @Override
  protected void resetEditorFrom(@NotNull MixExUnitRunConfiguration configuration) {
    mixRunConfigurationEditorForm.reset(configuration);
  }

  @Override
  protected void applyEditorTo(@NotNull MixExUnitRunConfiguration configuration) throws ConfigurationException {
    mixRunConfigurationEditorForm.applyTo(configuration);
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return myComponent;
  }

  @Override
  protected void disposeEditor() {
    myComponent.setVisible(false);
  }
}
