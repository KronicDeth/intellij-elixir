package org.elixir_lang.mix.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.openapi.extensions.Extensions;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationType.java
 */
public final class MixRunConfigurationType extends ConfigurationTypeBase {
  public static final String TYPE_ID = "MixRunConfigurationType";
  public static final String TYPE_NAME = "Elixir Mix";


  protected MixRunConfigurationType() {
    super(TYPE_ID, TYPE_NAME, "Runs a Mix command", ElixirIcons.MIX);
  }

  public static MixRunConfigurationType getInstance(){
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, MixRunConfigurationType.class);
  }

  @NotNull
  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{MixRunConfigurationFactory.getInstance()};
  }
}
