package org.elixir_lang.jps.mix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

public class ConfigurationExtension extends JpsCompositeElementBase<ConfigurationExtension> {
  private static final JpsElementChildRole<ConfigurationExtension> ROLE = JpsElementChildRoleBase.create("Mix");

  private SettingsState myState;

  private ConfigurationExtension(SettingsState state){
    myState = state;
  }

  @Nullable
  private static ConfigurationExtension getExtension(@Nullable JpsProject project){
    return null != project ? project.getContainer().getChild(ROLE) : null;
  }

  @NotNull
  public static ConfigurationExtension getOrCreateExtension(@NotNull JpsProject project){
    ConfigurationExtension extension = getExtension(project);
    if(extension == null){
      extension = project.getContainer().setChild(ROLE, new ConfigurationExtension(new SettingsState()));
    }
    return extension;
  }

  @NotNull
  @Override
  public ConfigurationExtension createCopy() {
    return new ConfigurationExtension(new SettingsState(myState));
  }

  public void setMixSettingsState(SettingsState myState) {
    this.myState = myState;
  }
}
