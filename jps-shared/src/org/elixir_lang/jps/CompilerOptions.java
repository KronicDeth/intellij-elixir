package org.elixir_lang.jps;

import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

public class CompilerOptions {
  @Tag("useMixCompiler")
  public boolean useMixCompiler = true;

  @Tag("useDocs")
  public boolean attachDocsEnabled = true;

  @Tag("useDebugInfo")
  public boolean attachDebugInfoEnabled = true;

  @Tag("useWarningsAsErrors")
  public boolean warningsAsErrorsEnabled = false;

  @Tag("useIgnoreModuleConflict")
  public boolean ignoreModuleConflictEnabled = false;

  public CompilerOptions() {
  }

  public CompilerOptions(@NotNull CompilerOptions options){
    useMixCompiler = options.useMixCompiler;
    attachDocsEnabled = options.attachDocsEnabled;
    attachDebugInfoEnabled = options.attachDebugInfoEnabled;
    warningsAsErrorsEnabled = options.warningsAsErrorsEnabled;
    ignoreModuleConflictEnabled = options.ignoreModuleConflictEnabled;
  }
}
