package org.elixir_lang.mix.runner;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.elixir_lang.module.ElixirModuleType;
import org.elixir_lang.sdk.ElixirSystemUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zyuyou on 15/7/8.
 *
 * @link https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationEditorForm.java
 */
final class MixRunConfigurationEditorForm extends SettingsEditor<MixRunConfigurationBase>{
  private JPanel myComponent;
  private JTextField myCommandText;
  private JCheckBox myRunInModuleChekcBox;
  private JCheckBox mySkipDependenciesCheckBox;
  private ModulesComboBox myModulesComboBox;

  MixRunConfigurationEditorForm(){
    myRunInModuleChekcBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        myModulesComboBox.setEnabled(myRunInModuleChekcBox.isSelected());
      }
    });

    myModulesComboBox.setVisible(!ElixirSystemUtil.isSmallIde());
    myRunInModuleChekcBox.setVisible(!ElixirSystemUtil.isSmallIde());
  }

  @Override
  protected void resetEditorFrom(@NotNull MixRunConfigurationBase configuration) {
    myCommandText.setText(configuration.getCommand());
    mySkipDependenciesCheckBox.setSelected(configuration.isSkipDependencies());
    Module module = null;
    if(!ElixirSystemUtil.isSmallIde()){
      myModulesComboBox.fillModules(configuration.getProject(), ElixirModuleType.getInstance());
      module = configuration.getConfigurationModule().getModule();
    }

    if(module != null){
      setRunInModuleSelected(true);
      myModulesComboBox.setSelectedModule(module);
    }else{
      setRunInModuleSelected(false);
    }
  }

  @Override
  protected void applyEditorTo(@NotNull MixRunConfigurationBase mixRunConfiguration) throws ConfigurationException {
    mixRunConfiguration.setCommand(myCommandText.getText());
    mixRunConfiguration.setSkipDependencies(mySkipDependenciesCheckBox.isSelected());
    Module selectedModule = myRunInModuleChekcBox.isSelected() ? myModulesComboBox.getSelectedModule() : null;
    mixRunConfiguration.setModule(selectedModule);
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

  private void setRunInModuleSelected(boolean b){
    if(b){
      if(!myRunInModuleChekcBox.isSelected()){
        myRunInModuleChekcBox.doClick();
      }
    }else{
      if(myRunInModuleChekcBox.isSelected()){
        myRunInModuleChekcBox.doClick();
      }
    }
  }
}
