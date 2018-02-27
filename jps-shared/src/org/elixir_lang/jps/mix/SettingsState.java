package org.elixir_lang.jps.mix;

import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class SettingsState {
  @Tag("mixPath")
  @NotNull
  private String myMixPath;
  private boolean supportsFormatterOption;

  SettingsState(){
    myMixPath = "";
    supportsFormatterOption = false;
  }

  SettingsState(SettingsState state){
    myMixPath = state.myMixPath;
    supportsFormatterOption = state.supportsFormatterOption;
  }

  @Override
  public String toString() {
    return "SettingsState(mixPath='" + myMixPath + "')";
  }
}
