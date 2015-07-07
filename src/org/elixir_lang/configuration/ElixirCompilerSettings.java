package org.elixir_lang.configuration;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.elixir_lang.jps.model.ElixirCompilerOptions;
import org.elixir_lang.jps.model.JpsElixirCompilerOptionsSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/6.
 */
@State(
    name = JpsElixirCompilerOptionsSerializer.COMPILER_OPTIONS_COMPONENT_NAME,
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/compile.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class ElixirCompilerSettings implements PersistentStateComponent<ElixirCompilerOptions>{
  private ElixirCompilerOptions myCompilerOptions = new ElixirCompilerOptions();

  @Nullable
  @Override
  public ElixirCompilerOptions getState() {
    return myCompilerOptions;
  }

  @Override
  public void loadState(ElixirCompilerOptions state) {
    myCompilerOptions = state;
  }

  @NotNull
  public static ElixirCompilerSettings getInstance(@NotNull Project project){
    ElixirCompilerSettings persisted = ServiceManager.getService(project, ElixirCompilerSettings.class);
    return persisted != null ? persisted : new ElixirCompilerSettings();
  }

  public boolean isUseMixCompilerEnabled(){
    return myCompilerOptions.myUseMixCompiler;
  }

  public void setUseMixCompilerEnabled(boolean useMixCompiler){
    myCompilerOptions.myUseMixCompiler = useMixCompiler;
  }

  public boolean isAddDebugInfoEnabled(){
    return myCompilerOptions.myAddDebugInfoEnabled;
  }

  public void setAddDebugInfoEnabled(boolean useDebugInfo){
    myCompilerOptions.myAddDebugInfoEnabled = useDebugInfo;
  }

}
