package org.elixir_lang.jps.model;

import com.intellij.util.xmlb.annotations.Tag;

/**
 * Created by zyuyou on 15/7/6.
 */
public class ElixirCompilerOptions {
  @Tag("useMixCompiler")
  public boolean myUseMixCompiler = false;

  @Tag("useDocs")
  public boolean myAttachDocsEnabled = false;

  @Tag("useDebugInfo")
  public boolean myAttachDebugInfoEnabled = true;

  @Tag("useWarningsAsErrors")
  public boolean myWarningsAsErrorsEnabled = false;

  @Tag("useIgnoreModuleConflict")
  public boolean myIgnoreModuleConflictEnabled = false;

  public ElixirCompilerOptions() {
  }

  public ElixirCompilerOptions(ElixirCompilerOptions options){
    myUseMixCompiler = options.myUseMixCompiler;
    myAttachDocsEnabled = options.myAttachDocsEnabled;
    myAttachDebugInfoEnabled = options.myAttachDebugInfoEnabled;
    myWarningsAsErrorsEnabled = options.myWarningsAsErrorsEnabled;
    myIgnoreModuleConflictEnabled = options.myIgnoreModuleConflictEnabled;
  }
}
