package org.elixir_lang.mix.runner;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class MixOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {

    private Status stderrStatus = null;
    private Status stdoutStatus = null;

    MixOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        super(testFrameworkName, consoleProperties);
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
    protected void processConsistentText(@NotNull String text, @NotNull Key outputType, boolean tcLikeFakeOutput) {
        if (outputType == ProcessOutputTypes.STDERR) {
            if (stderrStatus != null) {
                if (text.startsWith("  ")) {
                    stderrStatus.addLine(text);
                } else if (text.equals("\n")) {
                    processStatus(stderrStatus, outputType);
                    stderrStatus = null;
                } else {
                    super.processConsistentText(text, outputType, tcLikeFakeOutput);
                }
            } else {
                stderrStatus = Status.fromStderrLine(text);

                if (stderrStatus == null) {
                    super.processConsistentText(text, outputType, tcLikeFakeOutput);
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
                    super.processConsistentText(text, outputType, tcLikeFakeOutput);
                }
            }
        } else {
            super.processConsistentText(text, outputType, tcLikeFakeOutput);
        }
    }

    private void processStatus(@NotNull Status status, @NotNull Key outputType) {
        for (String text : status.toTeamCityCompilationMessageList()) {
            super.processConsistentText(text, outputType, false);
        }

        if (outputType == ProcessOutputTypes.STDERR) {
            for (String text : status.toTeamCityTestMessageList()) {
                super.processConsistentText(text, outputType, false);
            }
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
}
