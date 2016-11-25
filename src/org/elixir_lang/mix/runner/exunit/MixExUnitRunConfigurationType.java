package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.openapi.extensions.Extensions;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;

public final class MixExUnitRunConfigurationType extends ConfigurationTypeBase {
  public static final String TYPE_ID = "MixExUnitRunConfigurationType";
  public static final String TYPE_NAME = "Elixir Mix ExUnit";


  protected MixExUnitRunConfigurationType() {
    super(TYPE_ID, TYPE_NAME, "Runs Mix test", ElixirIcons.MIX_EX_UNIT);
  }

  public static MixExUnitRunConfigurationType getInstance(){
    return Extensions.findExtension(CONFIGURATION_TYPE_EP, MixExUnitRunConfigurationType.class);
  }

  @NotNull
  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{MixExUnitRunConfigurationFactory.getInstance()};
  }
}
