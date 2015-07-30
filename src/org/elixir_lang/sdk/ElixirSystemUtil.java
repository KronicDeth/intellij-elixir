package org.elixir_lang.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by zyuyou on 2015/5/27.
 * 
 */
public class ElixirSystemUtil {
  public static final int STANDARD_TIMEOUT = 10 * 1000;

  @NotNull
  public static ProcessOutput getProcessOutput(@NotNull String workDir,
                                               @NotNull String exePath,
                                               @NotNull String... arguments) throws ExecutionException{
    return getProcessOutput(STANDARD_TIMEOUT, workDir, exePath, arguments);
  }

  @NotNull
  public static ProcessOutput getProcessOutput(int timeout,
                                               @NotNull String workDir,
                                               @NotNull String exePath,
                                               @NotNull String... arguments) throws ExecutionException{
    if(!new File(workDir).isDirectory() || !new File(exePath).canExecute()){
      return new ProcessOutput();
    }

    GeneralCommandLine cmd = new GeneralCommandLine();
    cmd.withWorkDirectory(workDir);
    cmd.setExePath(exePath);
    cmd.addParameters(arguments);

    return execute(cmd, timeout);
  }

  @NotNull
  public static ProcessOutput execute(@NotNull GeneralCommandLine cmd) throws ExecutionException {
    return execute(cmd, STANDARD_TIMEOUT);
  }

  @NotNull
  public static ProcessOutput execute(@NotNull GeneralCommandLine cmd, int timeout) throws ExecutionException {
    CapturingProcessHandler processHandler = new CapturingProcessHandler(cmd.createProcess());
    return timeout < 0 ? processHandler.runProcess() : processHandler.runProcess(timeout);
  }

  public static boolean isSmallIde(){
    return PlatformUtils.isRubyMine()
        || PlatformUtils.isPyCharm()
        || PlatformUtils.isPhpStorm()
        || PlatformUtils.isWebStorm()
        || PlatformUtils.isAppCode()
        || PlatformUtils.isCLion();
  }
}
