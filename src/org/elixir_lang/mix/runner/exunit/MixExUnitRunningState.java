package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.elixir_lang.console.ElixirConsoleUtil;
import org.elixir_lang.exunit.ElixirModules;
import org.elixir_lang.mix.runner.MixRunningState;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.mix.settings.MixSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class MixExUnitRunningState extends MixRunningState {
  private final String TEST_FRAMEWORK_NAME = "ExUnit";

  MixExUnitRunningState(@NotNull ExecutionEnvironment environment, MixExUnitRunConfiguration configuration) {
    super(environment, configuration);
  }

  @NotNull
  @Override
  public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();

    TestConsoleProperties properties = new SMTRunnerConsoleProperties(myConfiguration, TEST_FRAMEWORK_NAME, executor);
    ConsoleView console = createAndAttachConsole(TEST_FRAMEWORK_NAME, processHandler, properties);
    ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), console);
    return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
  }

  @NotNull
  public ConsoleView createConsoleView(Executor executor) {
    TestConsoleProperties properties = new SMTRunnerConsoleProperties(myConfiguration, TEST_FRAMEWORK_NAME, executor);
    return SMTestRunnerConnectionUtil.createConsole(TEST_FRAMEWORK_NAME, properties);
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

  private boolean useCustomMixTask() {
    return !MixSettings.getInstance(myConfiguration.getProject()).getSupportsFormatterOption();
  }

  @NotNull
  private static List<String> elixirParams(@NotNull Collection<File> elixirModuleFileCollection) {
    ArrayList<String> elixirParams = new ArrayList<>();
    for (File elixirModuleFile : elixirModuleFileCollection) {
      elixirParams.add("-r");
      elixirParams.add(String.valueOf(elixirModuleFile));
    }

    return elixirParams;
  }

  @NotNull
  public List<String> setupElixirParams() throws ExecutionException {
    List<File> elixirModuleFileList = new ArrayList<File>();

    try {
      File elixirModulesDir = createElixirModulesDirectory();

      elixirModuleFileList.add(ElixirModules.putFormatterTo(elixirModulesDir));

      // Support for the --formatter option was recently added to Mix. Older versions of Elixir will need to use the
      // custom task we've included in order to support this option
      if (useCustomMixTask()) {
        elixirModuleFileList.add(ElixirModules.putMixTaskTo(elixirModulesDir));
      }
    } catch(IOException e) {
      throw new ExecutionException(e);
    }

    return elixirParams(elixirModuleFileList);
  }
}
