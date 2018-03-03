package org.elixir_lang.jps.mix;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class SettingsSerializer extends JpsProjectExtensionSerializer {
  public static final String MIX_CONFIG_FILE_NAME = "mix.xml";
  public static final String MIX_COMPONENT_NAME = "MixSettings";

  public SettingsSerializer() {
    super(MIX_CONFIG_FILE_NAME, MIX_COMPONENT_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsProject jpsProject, @NotNull Element componentTag) {
    ConfigurationExtension extension = ConfigurationExtension.getOrCreateExtension(jpsProject);
    SettingsState settingsState = XmlSerializer.deserialize(componentTag, SettingsState.class);
    if(settingsState != null){
      extension.setMixSettingsState(settingsState);
    }
  }

  @Override
  public void saveExtension(@NotNull JpsProject jpsProject, @NotNull Element componentTag) {

  }
}
