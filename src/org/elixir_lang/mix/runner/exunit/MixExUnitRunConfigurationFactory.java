package org.elixir_lang.mix.runner.exunit;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public final class MixExUnitRunConfigurationFactory extends ConfigurationFactory{
  private static final MixExUnitRunConfigurationFactory INSTANCE = new MixExUnitRunConfigurationFactory();

  private MixExUnitRunConfigurationFactory() {
    super(MixExUnitRunConfigurationType.getInstance());
  }

  @Override
  public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
    if(providerID == CompileStepBeforeRun.ID){
      task.setEnabled(false);
    }
  }

  @Override
  public RunConfiguration createTemplateConfiguration(Project project) {
    return new MixExUnitRunConfiguration(MixExUnitRunConfigurationType.TYPE_NAME, project);
  }

  @NotNull
  public static MixExUnitRunConfigurationFactory getInstance(){
    return INSTANCE;
  }
}
