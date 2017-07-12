package org.elixir_lang.mix.runner;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Status {
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

    @NotNull
    String toTeamCity() {
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
}
