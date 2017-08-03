package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunConfigurationBase;
import org.elixir_lang.mix.settings.MixSettings;
import org.jetbrains.annotations.NotNull;

public final class MixExUnitRunConfiguration extends MixRunConfigurationBase {
  MixExUnitRunConfiguration(@NotNull String name, @NotNull Project project){
    super(name, project, MixExUnitRunConfigurationFactory.getInstance());
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new MixExUnitRunConfigurationEditorForm();
  }

  @NotNull
  @Override
  public RunProfileState getState(@NotNull Executor executor,
                                  @NotNull ExecutionEnvironment environment) throws ExecutionException {
    return new MixExUnitRunningState(environment, this);
  }

  @NotNull
  public ParametersList mixParametersList() {
    ParametersList superParametersList = super.mixParametersList();
    ParametersList parametersList = new ParametersList();

    MixSettings mixSettings = MixSettings.getInstance(getProject());
    String task = mixSettings.getSupportsFormatterOption() ? "test" : "test_with_formatter";
    parametersList.add(task);

    parametersList.addAll("--formatter", "TeamCityExUnitFormatter");
    parametersList.addAll(superParametersList.getParameters());

    return parametersList;
  }
}
