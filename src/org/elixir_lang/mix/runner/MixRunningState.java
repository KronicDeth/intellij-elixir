package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import org.elixir_lang.console.ElixirConsoleUtil;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.mix.runner.MixRunningStateUtil.*;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningState.java
 */
final class MixRunningState extends CommandLineState {
    private final MixRunConfigurationBase myConfiguration;

    MixRunningState(@NotNull ExecutionEnvironment environment, MixRunConfigurationBase configuration) {
        super(environment);
        myConfiguration = configuration;
    }

    @NotNull
    private static GeneralCommandLine commandLine(@NotNull MixRunConfigurationBase configuration) throws ExecutionException {
        GeneralCommandLine commandLine = getBaseMixCommandLine(configuration);

        Project project = configuration.getProject();
        String mixPath = mixPath(project);

        if (mixPath.endsWith(".bat")) {
            commandLine.setExePath(mixPath);
        } else {
            String sdkPath = ElixirSdkType.getSdkPath(project);
            String elixirPath = elixirPath(sdkPath);

            commandLine.setExePath(elixirPath);
            commandLine.addParameter(mixPath);
        }

        commandLine = addProgramParameters(commandLine, configuration);

        return addNewSkipDependencies(commandLine, configuration);
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        TextConsoleBuilder consoleBuilder = new TextConsoleBuilderImpl(myConfiguration.getProject()) {
            @Override
            public ConsoleView getConsole() {
                ConsoleView consoleView = super.getConsole();
                ElixirConsoleUtil.attachFilters(myConfiguration.getProject(), consoleView);
                return consoleView;
            }
        };
        setConsoleBuilder(consoleBuilder);
        return super.execute(executor, runner);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = commandLine(myConfiguration);
        return runMix(myConfiguration.getProject(), commandLine);
    }
}
