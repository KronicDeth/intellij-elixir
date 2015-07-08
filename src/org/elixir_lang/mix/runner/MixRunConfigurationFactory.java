package org.elixir_lang.mix.runner;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationFactory.java
 */
public final class MixRunConfigurationFactory extends ConfigurationFactory{
  private static final MixRunConfigurationFactory INSTANCE = new MixRunConfigurationFactory();

  private MixRunConfigurationFactory() {
    super(MixRunConfigurationType.getInstance());
  }

  @Override
  public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
    if(providerID == CompileStepBeforeRun.ID){
      task.setEnabled(false);
    }
  }

  @Override
  public RunConfiguration createTemplateConfiguration(Project project) {
    return new MixRunConfiguration(MixRunConfigurationType.TYPE_NAME, project);
  }

  @NotNull
  public static MixRunConfigurationFactory getInstance(){
    return INSTANCE;
  }
}
