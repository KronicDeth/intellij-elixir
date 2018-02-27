package org.elixir_lang.configuration;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.elixir_lang.jps.CompilerOptions;
import org.elixir_lang.jps.compiler_options.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/6.
 */
@State(
    name = Serializer.COMPILER_OPTIONS_COMPONENT_NAME,
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class ElixirCompilerSettings implements PersistentStateComponent<CompilerOptions>{
  private CompilerOptions myCompilerOptions = new CompilerOptions();

  @Nullable
  @Override
  public CompilerOptions getState() {
    return myCompilerOptions;
  }

  @Override
  public void loadState(CompilerOptions state) {
    myCompilerOptions = state;
  }

  @NotNull
  public static ElixirCompilerSettings getInstance(@NotNull Project project){
    ElixirCompilerSettings persisted = ServiceManager.getService(project, ElixirCompilerSettings.class);
    return persisted != null ? persisted : new ElixirCompilerSettings();
  }

  /* use mix-compiler */
  public boolean isUseMixCompilerEnabled(){
    return myCompilerOptions.useMixCompiler;
  }

  public void setUseMixCompilerEnabled(boolean useMixCompiler){
    myCompilerOptions.useMixCompiler = useMixCompiler;
  }

  /* attach docs */
  public boolean isAttachDocsEnabled(){
    return myCompilerOptions.attachDocsEnabled;
  }

  public void setAttachDocsEnabled(boolean useDocs){
    myCompilerOptions.attachDocsEnabled = useDocs;
  }

  /* attach debug-info */
  public boolean isAttachDebugInfoEnabled(){
    return myCompilerOptions.attachDebugInfoEnabled;
  }

  public void setAttachDebugInfoEnabled(boolean useDebugInfo){
    myCompilerOptions.attachDebugInfoEnabled = useDebugInfo;
  }

  /* warnings-as-errors */
  public boolean isWarningsAsErrorsEnabled(){
    return myCompilerOptions.warningsAsErrorsEnabled;
  }

  public void setWarningsAsErrorsEnabled(boolean useWarningsAsErrors){
    myCompilerOptions.warningsAsErrorsEnabled = useWarningsAsErrors;
  }

  /* ignore-module-conflict */
  public boolean isIgnoreModuleConflictEnabled(){
    return myCompilerOptions.ignoreModuleConflictEnabled;
  }

  public void setIgnoreModuleConflictEnabled(boolean useIgnoreModuleConflict){
    myCompilerOptions.ignoreModuleConflictEnabled = useIgnoreModuleConflict;
  }

}
