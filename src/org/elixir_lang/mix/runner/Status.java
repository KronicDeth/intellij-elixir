package org.elixir_lang.mix.runner;

import com.google.common.base.CaseFormat;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Status {
    private static final String COMPILATION_ERROR_PREFIX = "    (elixir) lib/kernel/parallel_require.ex:";
    private static final String COMPILATION_FINISHED = compilationMessage("Finished");
    private static final String COMPILATION_STARTED = compilationMessage("Started");
    private static final Pattern TEST_MODULE_PATH_PATTERN = Pattern.compile("(?<file>test/.*_test.exs):(?<line>\\d+): \\(module\\)\\z");
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

    @NotNull
    private String fileToTestName(@NotNull String file) {
        String[] parts = file
                .substring(
                        0,
                        file.length() - ".exs".length()
                )
                .split("/");
        List<String> relativeModuleNames = new ArrayList<>(parts.length);

        for (String part : parts) {
            relativeModuleNames.add(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, part));
        }

        return StringUtils.join(relativeModuleNames, ".");
    }

    private boolean isCompilationError() {
        return stackTraceLines != null &&
                stackTraceLines
                        .get(stackTraceLines.size() - 1)
                        .startsWith(COMPILATION_ERROR_PREFIX);
    }

    @Nullable
    private Map<String, String> maybeTestStartedAttributes() {
        Map<String, String> attributes = null;

        if (stackTraceLines != null && stackTraceLines.size() > 0) {
            String firstStackTraceLine = stackTraceLines.get(0);

            Matcher matcher = TEST_MODULE_PATH_PATTERN.matcher(firstStackTraceLine);

            if (matcher.find()) {
                String file = matcher.group("file");
                String line = matcher.group("line");
                String locationHint = "file://" + file + ":" + line;
                attributes = new THashMap<>(2);
                attributes.put("locationHint", locationHint);

                String name = fileToTestName(file);
                attributes.put("name", name);
            }
        }

        return attributes;
    }

    @NotNull
    private String teamCityTestFinished(@NotNull Map<String, String> attributes) {
        return ServiceMessageBuilder
                .testFinished(attributes.get("name"))
                .toString();
    }

    @NotNull
    private String teamCityTestStarted(@NotNull Map<String, String> attributes) {
        return ServiceMessageBuilder
                .testStarted(attributes.get("name"))
                .addAttribute("locationHint", attributes.get("locationHint"))
                .toString();
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
    private String toTeamCityTestFailed(@NotNull String name) {
        String details = StringUtils.join(stackTraceLines, "\n");

        return ServiceMessageBuilder
                .testFailed(name)
                .addAttribute("details", details)
                .addAttribute("message", text)
                .toString();
    }

    @NotNull
    List<String> toTeamCityTestMessageList() {
        List<String> messageList;

        if (isCompilationError()) {
            messageList = new ArrayList<>();
            Map<String, String> maybeTestStartedAttributes = maybeTestStartedAttributes();
            String name;

            if (maybeTestStartedAttributes != null) {
                messageList.add(teamCityTestStarted(maybeTestStartedAttributes));
                name = maybeTestStartedAttributes.get("name");
            } else {
                name = "mix test";
            }

            messageList.add(toTeamCityTestFailed(name));

            if (maybeTestStartedAttributes != null) {
                messageList.add(teamCityTestFinished(maybeTestStartedAttributes));
            }
        } else {
            messageList = Collections.emptyList();
        }

        return messageList;
    }
}
