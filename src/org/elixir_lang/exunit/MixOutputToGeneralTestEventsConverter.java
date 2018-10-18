package org.elixir_lang.exunit;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputLineSplitter;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import org.elixir_lang.mix.runner.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MixOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    private static final Logger LOGGER = Logger.getInstance(MixOutputToGeneralTestEventsConverter.class);
    @NotNull
    private static final Method SUPER_PROCESS_CONSISTENT_TEXT_METHOD;

    static {
        try {
            SUPER_PROCESS_CONSISTENT_TEXT_METHOD = OutputToGeneralTestEventsConverter.class.getDeclaredMethod(
                    "processConsistentText",
                    String.class,
                    Key.class,
                    boolean.class
            );
            // counter `private` in IntelliJ IDEA prior to 2016.2
            SUPER_PROCESS_CONSISTENT_TEXT_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private OutputLineSplitter splitter;
    @Nullable
    private Status stderrStatus = null;
    @Nullable
    private Status stdoutStatus = null;

    MixOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        super(testFrameworkName, consoleProperties);

        splitter = new OutputLineSplitter(consoleProperties.isEditable()) {
            @Override
            protected void onLineAvailable(@NotNull String text, @NotNull Key outputType, boolean tcLikeFakeOutput) {
                subProcessConsistentText(text, outputType, tcLikeFakeOutput);
            }
        };
    }

    /**
     * Flashes the rest of stdout text buffer after output has been stopped
     */
    @Override
    public void flushBufferBeforeTerminating() {
        super.flushBufferBeforeTerminating();
        processStatuses();
    }

    @Override
    public void flushBufferOnProcessTermination(int code) {
        super.flushBufferOnProcessTermination(code);
        processStatuses();
    }

    @Override
    public void process(String text, Key outputType) {
        splitter.process(text, outputType);
    }

    private void processStatus(@NotNull Status status, @NotNull Key outputType) {
        for (String text : status.toTeamCityMessageList()) {
            superProcessConsistentText(text, outputType, false);
        }
    }

    private void processStatuses() {
        if (stderrStatus != null) {
            processStatus(stderrStatus, ProcessOutputTypes.STDERR);
            stderrStatus = null;
        }

        if (stdoutStatus != null) {
            processStatus(stdoutStatus, ProcessOutputTypes.STDOUT);
            stdoutStatus = null;
        }
    }

    private void subProcessConsistentText(@NotNull String text, @NotNull Key outputType, boolean tcLikeFakeOutput) {
        if (outputType == ProcessOutputTypes.STDERR) {
            if (stderrStatus != null) {
                if (text.startsWith("  ")) {
                    stderrStatus.addLine(text);
                } else if (text.equals("\n")) {
                    processStatus(stderrStatus, outputType);
                    stderrStatus = null;
                } else {
                    superProcessConsistentText(text, outputType, tcLikeFakeOutput);
                }
            } else {
                stderrStatus = Status.fromStderrLine(text);

                if (stderrStatus == null) {
                    superProcessConsistentText(text, outputType, tcLikeFakeOutput);
                }
            }
        } else if (outputType == ProcessOutputTypes.STDOUT) {
            if (stdoutStatus != null) {
                if (text.equals("\n")) {
                    processStatus(stdoutStatus, outputType);
                    stdoutStatus = null;
                } else {
                    stdoutStatus.addLine(text);
                }
            } else {
                stdoutStatus = Status.fromStdoutLine(text);

                if (stdoutStatus == null) {
                    superProcessConsistentText(text, outputType, tcLikeFakeOutput);
                }
            }
        } else {
            superProcessConsistentText(text, outputType, tcLikeFakeOutput);
        }
    }

    private void superProcessConsistentText(@NotNull String text, @NotNull Key outputType, boolean tcLikeFakeOutput) {
        try {
            SUPER_PROCESS_CONSISTENT_TEXT_METHOD.invoke(this, text, outputType, tcLikeFakeOutput);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e);
        }
    }
}
