package org.elixir_lang.mix.runner;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Status {
    private static final String COMPILATION_ERROR_PREFIX = "    (elixir) lib/kernel/parallel_require.ex:";
    private static final String COMPILATION_FINISHED = compilationMessage("Finished");
    private static final String COMPILATION_STARTED = compilationMessage("Started");
    @NotNull
    private final String status;
    @NotNull
    private final String text;
    @Nullable
    private List<String> stackTraceLines;
    private Status(@NotNull String status, @NotNull String text) {
        this.status = status;
        this.text = text;
    }

    private static String compilationMessage(@NotNull String commandSuffix) {
        return new ServiceMessageBuilder("compilation" + commandSuffix)
                .addAttribute("compile", "mix")
                .toString();
    }

    @NotNull
    private static Status fromLine(@NotNull String status, @NotNull String line) {
        String text = line.substring(0, line.length() - 1);
        return new Status(status, text);
    }

    @Nullable
    private static Status fromMaybeStatusLine(@Nullable String status, @NotNull String line) {
        Status maybeStatus = null;

        if (status != null) {
            maybeStatus = fromLine(status, line);
        }

        return maybeStatus;
    }

    @Nullable
    static Status fromStderrLine(@NotNull String line) {
        String status = null;

        if (line.startsWith("warning: ")) {
            status = "WARNING";
        } else if (line.startsWith("** ")) {
            status = "ERROR";
        }

        return fromMaybeStatusLine(status, line);
    }

    @Nullable
    static Status fromStdoutLine(@NotNull String line) {
        String status = null;

        if (line.contains("[error]")) {
            status = "ERROR";
        }

        return fromMaybeStatusLine(status, line);
    }

    void addLine(@NotNull String line) {
        String withoutNewline = line.substring(0, line.length() - 1);

        if (stackTraceLines == null) {
            stackTraceLines = new ArrayList<>();
        }

        stackTraceLines.add(withoutNewline);
    }

    private boolean isCompilationError() {
        return stackTraceLines != null &&
                stackTraceLines
                        .get(stackTraceLines.size() - 1)
                        .startsWith(COMPILATION_ERROR_PREFIX);
    }

    @NotNull
    List<String> toTeamCityCompilationMessageList() {
        return Arrays.asList(
                COMPILATION_STARTED,
                toTeamCityMessage(),
                COMPILATION_FINISHED
        );
    }

    @NotNull
    private String toTeamCityMessage() {
        ServiceMessageBuilder serviceMessageBuilder = new ServiceMessageBuilder("message");

        if (stackTraceLines != null && stackTraceLines.size() > 0) {
            String errorDetails = StringUtils.join(stackTraceLines, "\n");
            serviceMessageBuilder.addAttribute("errorDetails", errorDetails);
        }

        return serviceMessageBuilder
                .addAttribute("status", status)
                .addAttribute("text", text)
                .toString();
    }

    @NotNull
    private String toTeamCityTestFailure() {
        String details = StringUtils.join(stackTraceLines, "\n");

        return ServiceMessageBuilder
                .testFailed("mix test")
                .addAttribute("details", details)
                .addAttribute("message", text)
                .toString();
    }

    @NotNull
    List<String> toTeamCityTestMessageList() {
        List<String> messageList;

        if (isCompilationError()) {
            messageList = Collections.singletonList(
                    toTeamCityTestFailure()
            );
        } else {
            messageList = Collections.emptyList();
        }

        return messageList;
    }
}
