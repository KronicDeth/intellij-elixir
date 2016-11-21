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
import org.elixir_lang.console.ElixirConsoleUtil;
import org.elixir_lang.exunit.ElixirModules;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elixir_lang.mix.runner.MixRunningStateUtil.addNewSkipDependencies;
import static org.elixir_lang.mix.runner.MixRunningStateUtil.addProgramParameters;

final class MixExUnitRunningState extends CommandLineState{
  private final MixExUnitRunConfiguration myConfiguration;

  MixExUnitRunningState(@NotNull ExecutionEnvironment environment, MixExUnitRunConfiguration configuration) {
    super(environment);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();

    TestConsoleProperties properties = new SMTRunnerConsoleProperties(myConfiguration, "ExUnit", executor);
    ConsoleView console = createAndAttachConsole("ExUnit", processHandler, properties);
    ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), console);
    return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    GeneralCommandLine commandLine = commandLine(myConfiguration);
    return MixRunningStateUtil.runMix(myConfiguration.getProject(), commandLine);
  }

  /**
   * Unifies the interface for {@code SMTestRunnerConnectionUtil.createAndAttachConsole} between 141 and later releases
   */
  private ConsoleView createAndAttachConsole(@NotNull String testFrameworkName,
                                             @NotNull ProcessHandler processHandler,
                                             @NotNull TestConsoleProperties consoleProperties) throws ExecutionException {
    Class<SMTestRunnerConnectionUtil> klass = SMTestRunnerConnectionUtil.class;
    ConsoleView consoleView = null;

    try {
      Method createAndAttachConsole = klass.getMethod("createAndAttachConsole", String.class, ProcessHandler.class, TestConsoleProperties.class);

      try {
        consoleView = (ConsoleView) createAndAttachConsole.invoke(null, testFrameworkName, processHandler, consoleProperties);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    } catch (NoSuchMethodException noSuchCreateAndAttachConsole3Method) {
      try {
        Method createAndAttachConsole = klass.getMethod("createAndAttachConsole", String.class, ProcessHandler.class, TestConsoleProperties.class, ExecutionEnvironment.class);

        try {
          consoleView = (ConsoleView) createAndAttachConsole.invoke(null, testFrameworkName, processHandler, consoleProperties, getEnvironment());
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      } catch (NoSuchMethodException noSuchCreateAndAttachConsole4Method) {
        noSuchCreateAndAttachConsole4Method.printStackTrace();
      }
    }

    return consoleView;
  }

  private static File createElixirModulesDirectory() throws IOException {
    return FileUtil.createTempDirectory("intellij_elixir_modules", null);
  }

  private static String elixirPath(@NotNull Project project) {
    String sdkPath = ElixirSdkType.getSdkPath(project);

    return MixRunningStateUtil.elixirPath(sdkPath);
  }

  @NotNull
  private static GeneralCommandLine commandLine(@NotNull MixExUnitRunConfiguration configuration) throws ExecutionException {
    GeneralCommandLine commandLine = MixRunningStateUtil.getBaseMixCommandLine(configuration);

    Project project = configuration.getProject();
    MixSettings mixSettings = MixSettings.getInstance(project);

    String elixirPath = elixirPath(project);

    // Because we pass additional options to `elixir`, we can't use `mix.bat`. We assume there's a `mix` script in the
    // same directory if the user specified the .bat file in the "Elixir External Tools" config
    String mixPath = StringUtil.trimEnd(mixSettings.getMixPath(), ".bat");

    String task = mixSettings.getSupportsFormatterOption() ? "test" : "test_with_formatter";

    commandLine.setExePath(elixirPath);

    // Copy Elixir modules to temp dir
    List<File> elixirModuleFileList = populateElixirModulesDirectory(!mixSettings.getSupportsFormatterOption());
    commandLine = addRequireParameters(commandLine, elixirModuleFileList);

    commandLine.addParameters(mixPath, task, "--formatter", "TeamCityExUnitFormatter");

    commandLine = addProgramParameters(commandLine, configuration);

    return addNewSkipDependencies(commandLine, configuration);
  }

  @NotNull
  private static GeneralCommandLine addRequireParameters(@NotNull GeneralCommandLine commandLine,
                                                         @NotNull Collection<File> elixirModuleFileCollection) {
    for (File elixirModuleFile : elixirModuleFileCollection) {
      commandLine.addParameters("-r", String.valueOf(elixirModuleFile));
    }

    return commandLine;
  }

  @NotNull
  private static List<File> populateElixirModulesDirectory(boolean useCustomMixTask) throws ExecutionException {
    List<File> elixirModuleFileList = new ArrayList<File>();

    try {
      File elixirModulesDir = createElixirModulesDirectory();

      elixirModuleFileList.add(ElixirModules.putFormatterTo(elixirModulesDir));

      // Support for the --formatter option was recently added to Mix. Older versions of Elixir will need to use the
      // custom task we've included in order to support this option
      if (useCustomMixTask) {
        elixirModuleFileList.add(ElixirModules.putMixTaskTo(elixirModulesDir));
      }
    } catch(IOException e) {
      throw new ExecutionException(e);
    }

    return elixirModuleFileList;
  }
}
