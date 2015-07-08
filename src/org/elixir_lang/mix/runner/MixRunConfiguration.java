package org.elixir_lang.mix.runner;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfiguration.java
 */
public final class MixRunConfiguration extends MixRunConfigurationBase {
  public MixRunConfiguration(@NotNull String name, @NotNull Project project){
    super(name, project, MixRunConfigurationFactory.getInstance());
  }
}
