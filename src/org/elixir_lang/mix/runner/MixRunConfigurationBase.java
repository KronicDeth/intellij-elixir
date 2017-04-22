package org.elixir_lang.mix.runner;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.ExternalizablePath;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.hash.LinkedHashMap;
import com.intellij.util.xmlb.SerializationFilter;
import com.intellij.util.xmlb.XmlSerializer;
import org.elixir_lang.runconfig.ElixirModuleBasedConfiguration;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationBase.java
 */
public abstract class MixRunConfigurationBase extends ModuleBasedConfiguration<ElixirModuleBasedConfiguration>
        implements CommonProgramRunConfigurationParameters,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
  /*
   * CONSTANTS
   */

  private static final SerializationFilter SERIALIZATION_FILTER = (accessor, bean) -> !accessor.getName().equals("envs");

  /*
   * Fields
   */

  @NotNull
  private final Map<String, String> envs = new LinkedHashMap<>();
  private boolean mySkipDependencies = false;
  private boolean passParentEnvs = false;
  @Nullable
  private String programParameters = null;
  @Nullable
  private String workingDirectory;

  /*
   * Constructors
   */

  protected MixRunConfigurationBase(@NotNull String name, @NotNull Project project, @NotNull ConfigurationFactory configurationFactory){
    super(name, new ElixirModuleBasedConfiguration(project), configurationFactory);
  }

  /*
   * Public Instance Methods
   */

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    // todo: parse mix command line to check if it is valid
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new MixRunConfigurationEditorForm();
  }

  @NotNull
  @Override
  public Map<String, String> getEnvs() {
    return envs;
  }

  @Nullable
  @Override
  public String getProgramParameters() {
    return programParameters;
  }

  /**
   * getModules changed to getSearchScope in
   * https://github.com/JetBrains/intellij-community/commit/4daac1c8cdb0cb98bbb26df7d4900c766626a742#diff-5fa0d6d7f6e6058e338ccec4efdb4596,
   * which affects IntelliJ 2016.*
   *
   * @return scope where to search sources for this configuration
   */
  @Nullable
  public GlobalSearchScope getSearchScope() {
    return GlobalSearchScope.projectScope(getProject());
  }

  @NotNull
  public List<String> getMixArgs() {
    String programParameters = getProgramParameters();

    if (programParameters != null) {
      return Arrays.asList(programParameters.split("\\s+"));
    } else {
      return new ArrayList<>(0);
    }
  }

  @NotNull
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
    return new MixRunningState(environment, this);
  }

  @Override
  public Collection<Module> getValidModules() {
    Module[] modules = ModuleManager.getInstance(getProject()).getModules();
    return Arrays.asList(modules);
  }

  @Nullable
  @Override
  public String getWorkingDirectory() {
    return ExternalizablePath.localPathValue(workingDirectory);
  }

  @Override
  public boolean isPassParentEnvs() {
    return passParentEnvs;
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    XmlSerializer.deserializeInto(this, element);
    EnvironmentVariablesComponent.readExternal(element, getEnvs());

    /*
     * Reading <= 4.6.0 format
     */

    List<Element> options = element.getChildren("option");

    for (Element option : options) {
      Attribute nameAttribute = option.getAttribute("name");

      if (nameAttribute.getValue().equals("command")) {
        Attribute valueAttribute = option.getAttribute("value");

        setProgramParameters(valueAttribute.getValue()) ;

        break;
      }
    }
  }

  @Override
  public void setEnvs(@NotNull Map<String, String> envs) {
    this.envs.clear();
    this.envs.putAll(envs);
  }

  @Override
  public void setPassParentEnvs(boolean passParentEnvs) {
    this.passParentEnvs = passParentEnvs;
  }

  @Override
  public void setProgramParameters(@Nullable String programParameters) {
    this.programParameters = programParameters;
  }

  @Override
  public void setWorkingDirectory(@Nullable String workingDirectory) {
    this.workingDirectory = ExternalizablePath.urlValue(workingDirectory);
  }

  @Override
  public void writeExternal(@NotNull Element element) throws WriteExternalException{
    super.writeExternal(element);
    XmlSerializer.serializeInto(
            this,
            element,
            SERIALIZATION_FILTER
    );
    EnvironmentVariablesComponent.writeExternal(element, getEnvs());
  }

//  @NotNull
//  abstract public GeneralCommandLine commandLine(List<String> elixirParams);

  /*
   * Package Instance Methods
   */

  boolean isSkipDependencies(){
    return mySkipDependencies;
  }

  void setSkipDependencies(boolean skipDeps){
    mySkipDependencies = skipDeps;
  }
}
