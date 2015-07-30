package org.elixir_lang.mix.runner;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunner.java
 */
public final class MixRunner  extends DefaultProgramRunner{
  public static final String MIX_RUNNER_ID = "MixRunner";

  @NotNull
  @Override
  public String getRunnerId() {
    return MIX_RUNNER_ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof MixRunConfigurationBase;
  }
}
