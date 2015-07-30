package org.elixir_lang.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.OutputEditor;

import javax.swing.*;

/**
 * Created by zyuyou on 15/6/5.
 *
 */
public class DefaultModuleEditorsProvider implements ModuleConfigurationEditorProvider{

  @Override
  public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
    Module module = state.getRootModel().getModule();
    if(ModuleType.get(module) instanceof ElixirModuleType){
      return new ModuleConfigurationEditor[]{
          new ElixirContentEntriesEditor(module.getName(), state),
          new OutputEditorEx(state),
          new ClasspathEditor(state)
      };
    }
    return ModuleConfigurationEditor.EMPTY;
  }

  public static class OutputEditorEx extends OutputEditor{
    protected OutputEditorEx(ModuleConfigurationState state){
      super(state);
    }

    protected JComponent createComponentImpl(){
      JComponent component =  super.createComponentImpl();
      component.remove(1);  // todo: looks ugly
      return component;
    }
  }
}