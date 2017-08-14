package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.elixir_lang.console.ElixirConsoleUtil;
import org.elixir_lang.exunit.ElixirModules;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunningState;
import org.elixir_lang.mix.runner.MixTestConsoleProperties;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MixExUnitRunningState extends MixRunningState {
    private static final Logger LOGGER = com.intellij.openapi.diagnostic.Logger.getInstance(MixExUnitRunningState.class);

    private final String TEST_FRAMEWORK_NAME = "ExUnit";

    MixExUnitRunningState(@NotNull ExecutionEnvironment environment, MixExUnitRunConfiguration configuration) {
        super(environment, configuration);
    }

    @NotNull
    private static ParametersList elixirParametersList(@Nullable Project project) throws IOException {
        Sdk sdk = null;
        boolean useCustomMixTask = false;

        if (project != null) {
            sdk = ProjectRootManager.getInstance(project).getProjectSdk();
            useCustomMixTask = !MixSettings.getInstance(project).getSupportsFormatterOption();
        }

        return elixirParametersList(sdk, useCustomMixTask);
    }

    @NotNull
    private static ParametersList elixirParametersList(@Nullable Sdk sdk, boolean useCustomMixTask) throws IOException {
        return ElixirModules.parametersList(ElixirSdkType.getRelease(sdk), useCustomMixTask);
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
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e);
            }
        } catch (NoSuchMethodException noSuchCreateAndAttachConsole3Method) {
            try {
                Method createAndAttachConsole = klass.getMethod("createAndAttachConsole", String.class, ProcessHandler.class, TestConsoleProperties.class, ExecutionEnvironment.class);

                try {
                    consoleView = (ConsoleView) createAndAttachConsole.invoke(null, testFrameworkName, processHandler, consoleProperties, getEnvironment());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOGGER.error(e);
                }
            } catch (NoSuchMethodException noSuchCreateAndAttachConsole4Method) {
                noSuchCreateAndAttachConsole4Method.printStackTrace();
            }
        }

        return consoleView;
    }

    /**
     * Unifies the interface for {@code SMTestRunnerConnectionUtil.createConsole} between 141 and later releases
     */
    private ConsoleView createConsole(@NotNull String name, @NotNull TestConsoleProperties testConsoleProperties) {
        ConsoleView consoleView = null;

        try {
            Method createConsole2 = SMTestRunnerConnectionUtil.class.getMethod(
                    "createConsole",
                    String.class,
                    TestConsoleProperties.class
            );

            try {
                // first argument is `null` because it's a static method
                consoleView = (ConsoleView) createConsole2.invoke(null, name, testConsoleProperties);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e);
            }
        } catch (NoSuchMethodException e) {
            try {
                Method createConsole3 = SMTestRunnerConnectionUtil.class.getMethod(
                        "createConsole",
                        String.class,
                        TestConsoleProperties.class,
                        ExecutionEnvironment.class
                );

                try {
                    // first argument is `null` because it's a static method
                    consoleView = (ConsoleView) createConsole3.invoke(null, name, testConsoleProperties, null);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    LOGGER.error(e1);
                }
            } catch (NoSuchMethodException e1) {
                LOGGER.error(e1);
            }
        }

        //noinspection ConstantConditions
        return consoleView;
    }

    @NotNull
    public ConsoleView createConsoleView(Executor executor) {
        TestConsoleProperties properties = new MixTestConsoleProperties(myConfiguration, TEST_FRAMEWORK_NAME, executor);

        return createConsole(TEST_FRAMEWORK_NAME, properties);
    }

    @NotNull
    public ParametersList elixirParametersList(@Nullable RunConfiguration runConfiguration) throws ExecutionException {
        Project project = null;

        if (runConfiguration != null) {
            project = runConfiguration.getProject();
        }

        try {
            return elixirParametersList(project);
        } catch (IOException ioException) {
            throw new ExecutionException(ioException);
        }
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();

        TestConsoleProperties properties = new MixTestConsoleProperties(myConfiguration, TEST_FRAMEWORK_NAME, executor);
        ConsoleView console = createAndAttachConsole(TEST_FRAMEWORK_NAME, processHandler, properties);
        ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), console);
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
    }
}
