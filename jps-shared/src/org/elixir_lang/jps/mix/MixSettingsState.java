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

  public MixSettingsState(){
    myMixPath = "";
  }

  public MixSettingsState(MixSettingsState state){
    myMixPath = state.myMixPath;
  }

  @Override
  public String toString() {
    return "MixSettingsState(mixPath='" + myMixPath + "')";
  }
}
