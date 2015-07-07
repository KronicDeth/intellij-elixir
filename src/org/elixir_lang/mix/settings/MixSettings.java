package org.elixir_lang.mix.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.elixir_lang.jps.mix.JpsMixSettingsSerializer;
import org.elixir_lang.jps.mix.MixSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Created by zyuyou on 2015/5/26.
 *
 */
@State(
    name = JpsMixSettingsSerializer.MIX_COMPONENT_NAME,
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/" + JpsMixSettingsSerializer.MIX_CONFIG_FILE_NAME, scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class MixSettings implements PersistentStateComponent<MixSettingsState>{
  @NotNull
  private MixSettingsState myMixSettingsState = new MixSettingsState();

  @NotNull
  public static MixSettings getInstance(@NotNull Project project){
    MixSettings persisted = ServiceManager.getService(project, MixSettings.class);
    return persisted != null ? persisted : new MixSettings();
  }

  @Nullable
  @Override
  public MixSettingsState getState() {
    return myMixSettingsState;
  }

  @Override
  public void loadState(@NotNull MixSettingsState mixSettings) {
    myMixSettingsState = mixSettings;
  }

  @NotNull
  public String getMixPath(){
    return myMixSettingsState.myMixPath;
  }

  public void setMixPath(@NotNull String mixPath){
    myMixSettingsState.myMixPath = mixPath;
  }

  @Override
  public String toString() {
    return "MixSettings(state='" + myMixSettingsState.toString() + "')";
  }
}
