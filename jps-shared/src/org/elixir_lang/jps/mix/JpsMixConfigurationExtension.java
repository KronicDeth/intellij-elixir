package org.elixir_lang.jps.mix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

/**
 * Created by zyuyou on 2015/5/26.
 *
 */
public class JpsMixConfigurationExtension extends JpsCompositeElementBase<JpsMixConfigurationExtension> {
  public static final JpsElementChildRole<JpsMixConfigurationExtension> ROLE = JpsElementChildRoleBase.create("Mix");

  private MixSettingsState myState;

  public JpsMixConfigurationExtension(MixSettingsState state){
    myState = state;
  }

  @NotNull
  @Override
  public JpsMixConfigurationExtension createCopy() {
    return new JpsMixConfigurationExtension(new MixSettingsState(myState));
  }

  public void setMixSettingsState(MixSettingsState myState) {
    this.myState = myState;
  }

  public MixSettingsState getMixSettingsState () {
    return myState;
  }

  public String getMixPath(){
    return myState.myMixPath;
  }

  @Nullable
  public static JpsMixConfigurationExtension getExtension(@Nullable JpsProject project){
    return null != project ? project.getContainer().getChild(ROLE) : null;
  }

  @NotNull
  public static JpsMixConfigurationExtension getOrCreateExtension(@NotNull JpsProject project){
    JpsMixConfigurationExtension extension = getExtension(project);
    if(extension == null){
      extension = project.getContainer().setChild(ROLE, new JpsMixConfigurationExtension(new MixSettingsState()));
    }
    return extension;
  }


}
