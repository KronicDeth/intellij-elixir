package org.elixir_lang.exunit;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputEventSplitter;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import org.elixir_lang.mix.runner.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MixOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    @NotNull
    private OutputEventSplitter splitter;
    @Nullable
    private Status stderrStatus = null;
    @Nullable
    private Status stdoutStatus = null;

    MixOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        super(testFrameworkName, consoleProperties);

        splitter = new OutputEventSplitter(consoleProperties.isEditable()) {
            @Override
            public void onTextAvailable(@NotNull String text, @NotNull Key outputType) {
                processConsistentText(text, outputType);
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
            super.processConsistentText(text, outputType);
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

    @Override
    public void processConsistentText(@NotNull String text, @NotNull Key outputType) {
        if (outputType == ProcessOutputTypes.STDERR) {
            if (stderrStatus != null) {
                if (text.startsWith("  ")) {
                    stderrStatus.addLine(text);
                } else if (text.equals("\n")) {
                    processStatus(stderrStatus, outputType);
                    stderrStatus = null;
                } else {
                    super.processConsistentText(text, outputType);
                }
            } else {
                stderrStatus = Status.fromStderrLine(text);

                if (stderrStatus == null) {
                    super.processConsistentText(text, outputType);
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
                    super.processConsistentText(text, outputType);
                }
            }
        } else {
            super.processConsistentText(text, outputType);
        }
    }
}
