package org.elixir_lang.exunit;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import org.jetbrains.annotations.NotNull;

public class TestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
    public TestConsoleProperties(@NotNull RunConfiguration config,
                                 @NotNull String testFrameworkName,
                                 @NotNull Executor executor) {
        super(config, testFrameworkName, executor);
        setIdBasedTestTree(true);
    }

    @Override
    public OutputToGeneralTestEventsConverter createTestEventsConverter(
            @NotNull String testFrameworkName,
            @NotNull com.intellij.execution.testframework.TestConsoleProperties consoleProperties
    ) {
        return new MixOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
    }
}
