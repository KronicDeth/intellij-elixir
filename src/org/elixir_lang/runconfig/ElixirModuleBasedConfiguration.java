package org.elixir_lang.runconfig;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/runconfig/ErlangModuleBasedConfiguration.java
 */
public class ElixirModuleBasedConfiguration extends RunConfigurationModule {
  public ElixirModuleBasedConfiguration(Project project) {
    super(project);
  }
}
