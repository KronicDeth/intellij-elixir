package org.elixir_lang.jps.model;

import com.intellij.util.xmlb.annotations.Tag;

/**
 * Created by zyuyou on 15/7/6.
 */
public class ElixirCompilerOptions {
  @Tag("useMixCompiler")
  public boolean myUseMixCompiler = false;

  @Tag("useDebugInfo")
  public boolean myAddDebugInfoEnabled = true;

  public ElixirCompilerOptions() {
  }

  public ElixirCompilerOptions(ElixirCompilerOptions options){
    myUseMixCompiler = options.myUseMixCompiler;
    myAddDebugInfoEnabled = options.myAddDebugInfoEnabled;
  }
}
