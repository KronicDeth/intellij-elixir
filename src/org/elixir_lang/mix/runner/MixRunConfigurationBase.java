package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.elixir_lang.runconfig.ElixirModuleBasedConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationBase.java
 */
public abstract class MixRunConfigurationBase extends ModuleBasedConfiguration<ElixirModuleBasedConfiguration>
    implements RunConfigurationWithSuppressedDefaultRunAction, RunConfigurationWithSuppressedDefaultDebugAction {

  @NotNull
  private String myCommand = "";
  private boolean mySkipDependencies = false;

  protected MixRunConfigurationBase(@NotNull String name , @NotNull Project project, @NotNull ConfigurationFactory configurationFactory){
    super(name, new ElixirModuleBasedConfiguration(project), configurationFactory);
  }

  @Override
  public Collection<Module> getValidModules() {
    Module[] modules = ModuleManager.getInstance(getProject()).getModules();
    return Arrays.asList(modules);
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new MixRunConfigurationEditorForm();
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
    return new MixRunningState(environment, this);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    // todo: parse mix command line to check if it is valid
  }

  @Override
  public void writeExternal(@NotNull Element element) throws WriteExternalException{
    super.writeExternal(element);
    XmlSerializer.deserializeInto(this, element);
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    XmlSerializer.deserializeInto(this, element);
  }

  @NotNull
  public String getCommand(){
    return myCommand;
  }

  public void setCommand(@NotNull String command){
    myCommand = command;
  }

  public boolean isSkipDependencies(){
    return mySkipDependencies;
  }

  public void setSkipDependencies(boolean skipDeps){
    mySkipDependencies = skipDeps;
  }
}
