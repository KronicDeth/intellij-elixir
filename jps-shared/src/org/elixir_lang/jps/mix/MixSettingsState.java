package org.elixir_lang.jps.mix;

import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class MixSettingsState {
  @Tag("mixPath")
  @NotNull
  public String myMixPath;
  public boolean supportsFormatterOption;

  public MixSettingsState(){
    myMixPath = "";
    supportsFormatterOption = false;
  }

  MixSettingsState(MixSettingsState state){
    myMixPath = state.myMixPath;
    supportsFormatterOption = state.supportsFormatterOption;
  }

  @Override
  public String toString() {
    return "MixSettingsState(mixPath='" + myMixPath + "')";
  }
}
