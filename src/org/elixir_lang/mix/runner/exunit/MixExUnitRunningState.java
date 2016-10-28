package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.console.ElixirConsoleUtil;
import org.elixir_lang.exunit.ElixirModules;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.elixir_lang.mix.runner.MixRunningStateUtil.getWorkingDirectory;

final class MixExUnitRunningState extends CommandLineState{
  private final MixExUnitRunConfiguration myConfiguration;

  protected MixExUnitRunningState(@NotNull ExecutionEnvironment environment, MixExUnitRunConfiguration configuration) {
    super(environment);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();

    TestConsoleProperties properties = new SMTRunnerConsoleProperties(myConfiguration, "ExUnit", executor);
    ConsoleView console = SMTestRunnerConnectionUtil.createAndAttachConsole("ExUnit", processHandler, properties);
    ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), console);
    return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    GeneralCommandLine commandLine = getMixExunitCommandLine(myConfiguration);
    return MixRunningStateUtil.runMix(myConfiguration.getProject(), commandLine);
  }

  private static File createElixirModulesDirectory() throws IOException {
    return FileUtil.createTempDirectory("intellij_elixir_modules", null);
  }

  @NotNull
  public static GeneralCommandLine getMixExunitCommandLine(@NotNull MixExUnitRunConfiguration configuration) throws ExecutionException {
    Project project = configuration.getProject();
    MixSettings mixSettings = MixSettings.getInstance(project);

    // Copy Elixir modules to temp dir
    String elixirModulesFilePath = populateElixirModulesDirectory(!mixSettings.getSupportsFormatterOption());
    String sdkPath = ElixirSdkType.getSdkPath(project);

    String elixirPath = sdkPath != null ? JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath() :
        JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);

    GeneralCommandLine commandLine = new GeneralCommandLine();

    commandLine.withWorkDirectory(getWorkingDirectory(configuration));

    // Because we pass additional options to `elixir`, we can't use `mix.bat`. We assume there's a `mix` script in the
    // same directory if the user specified the .bat file in the "Elixir External Tools" config
    String mixPath = StringUtil.trimEnd(mixSettings.getMixPath(), ".bat");

    String task = mixSettings.getSupportsFormatterOption() ? "test" : "test_with_formatter";

    commandLine.setExePath(elixirPath);
    commandLine.addParameters("-r", elixirModulesFilePath, mixPath, task, "--formatter", "TeamCityExUnitFormatter");

    List<String> split = ContainerUtil.list(configuration.getMixTestArgs().split("\\s+"));
    if (!(split.size() == 1 && split.get(0).equals(""))) commandLine.addParameters(split);
    return commandLine;
  }

  @NotNull
  private static String populateElixirModulesDirectory(boolean useCustomMixTask) throws ExecutionException {
    try {
      File elixirModulesDir = createElixirModulesDirectory();
      ElixirModules.putFormatterTo(elixirModulesDir);

      // Support for the --formatter option was recently added to Mix. Older versions of Elixir will need to use the
      // custom task we've included in order to support this option
      if (useCustomMixTask) {
        ElixirModules.putMixTaskTo(elixirModulesDir);
      }

      return new File(elixirModulesDir.getCanonicalPath(), "*.ex").toString();
    } catch(IOException e) {
      throw new ExecutionException(e);
    }
  }

}
