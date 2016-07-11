package org.elixir_lang.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Function;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Created by zyuyou on 2015/5/27.
 *
 */
public class ElixirSystemUtil {
  /*
   * CONSTANTS
   */

  public static final Logger LOGGER = Logger.getInstance(ElixirSystemUtil.class);
  public static final int STANDARD_TIMEOUT = 10 * 1000;

  @Nullable
  public static <T> T transformStdoutLine(@NotNull ProcessOutput output, @NotNull Function<String, T> lineTransformer) {
    List<String> lines;

    if (output.getExitCode() != 0 || output.isTimeout() || output.isCancelled()) {
      lines = ContainerUtil.emptyList();
    } else {
      lines = output.getStdoutLines();
    }

    T transformed = null;

    for (String line : lines) {
      transformed = lineTransformer.fun(line);

      if (transformed != null) {
        break;
      }
    }

    return transformed;
  }

  @Nullable
  public static <T> T transformStdoutLine(@NotNull Function<String, T> lineTransformer,
                                          int timeout,
                                          @NotNull String workDir,
                                          @NotNull String exePath,
                                          @NotNull String... arguments) {
    T transformed = null;

    try {
      ProcessOutput output = getProcessOutput(timeout, workDir, exePath, arguments);

      transformed = transformStdoutLine(output, lineTransformer);
    } catch (ExecutionException executionException) {
      LOGGER.warn(executionException);
    }

    return transformed;
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
