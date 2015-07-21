package org.elixir_lang.jps.builder;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

/**
 * Created by zyuyou on 15/7/12.
 */
public class ElixirCompilerProcessAdapter extends ProcessAdapter {
  private static final long DEFAULT_PROBLEM_BEGIN_OFFSET = -1L;
  private static final long DEFAULT_PROBLEM_END_OFFSET = -1L;
  private static final long DEFAULT_PROBLEM_LOCATION_OFFSET = -1L;
  private static final long DEFAULT_LOCATION_COLUMN = -1L;

  private final CompileContext myContext;
  private final String myBuilderName;
  private final String myCompileTargetRootPath;

  public ElixirCompilerProcessAdapter(@NotNull CompileContext context,
                                      @NotNull String builderName,
                                      @NotNull String compileTargetRootPath){

    myContext = context;
    myBuilderName = builderName;
    myCompileTargetRootPath = compileTargetRootPath;
  }

  @Override
  public void onTextAvailable(@NotNull ProcessEvent event, Key outputType) {
    ElixirCompilerError error = ElixirCompilerError.create(myCompileTargetRootPath, event.getText());
    if(error != null){
      boolean isError = error.getCategory() == CompilerMessageCategory.ERROR;
      BuildMessage.Kind kind = isError ? BuildMessage.Kind.ERROR : BuildMessage.Kind.WARNING;
      CompilerMessage msg = new CompilerMessage(myBuilderName, kind, error.getErrorMessage(),
          VirtualFileManager.extractPath(error.getUrl()), DEFAULT_PROBLEM_BEGIN_OFFSET, DEFAULT_PROBLEM_END_OFFSET,
          DEFAULT_PROBLEM_LOCATION_OFFSET, error.getLine(), DEFAULT_LOCATION_COLUMN);
      myContext.processMessage(msg);
    }
  }
}
