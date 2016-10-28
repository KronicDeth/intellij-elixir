package org.elixir_lang.mix.runner.exunit;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class MixExUnitRunConfigurationEditorForm extends SettingsEditor<MixExUnitRunConfiguration>{
  private JPanel myComponent;
  private JTextField mixTestArgs;

  @Override
  protected void resetEditorFrom(@NotNull MixExUnitRunConfiguration configuration) {
    mixTestArgs.setText(configuration.getMixTestArgs());
  }

  @Override
  protected void applyEditorTo(@NotNull MixExUnitRunConfiguration mixRunConfiguration) throws ConfigurationException {
    mixRunConfiguration.setMixTestArgs(mixTestArgs.getText());
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
