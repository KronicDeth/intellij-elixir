package org.elixir_lang.jps.builder;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import org.apache.commons.lang3.StringUtils;
import org.elixir_lang.jps.model.ElixirCompilerOptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElixirCompilerProcessAdapter extends ProcessAdapter {
    private static final Pattern COMPILATION_ERROR_PATTERN =
            Pattern.compile("^== Compilation error in file (?<file>.+) ==\n");
    private static final String INDENT = "  ";
    private static final long DEFAULT_COLUMN = -1L;
    private static final long DEFAULT_PROBLEM_BEGIN_OFFSET = -1L;
    private static final long DEFAULT_PROBLEM_END_OFFSET = -1L;
    private static final long DEFAULT_PROBLEM_LOCATION_OFFSET = -1L;
    private static final long DEFAULT_LINE = -1L;
    private static final Pattern EXCEPTION_PATTERN =
            Pattern.compile("^\\*\\* (?<exception>.+?).*", Pattern.DOTALL);
    private static final Pattern STACK_TRACE_LINE =
            Pattern.compile("\\s+(?<problem>(?<relativePath>.*):(?<line>\\d+))");
    private static final Pattern WARNING_PATTERN = Pattern.compile("^warning:\\s*(.*)", Pattern.DOTALL);

    private final String builderName;
    private final ElixirCompilerOptions compilerOptions;
    private final String compileTargetRootPath;
    private final CompileContext context;
    private final StringBuilder text = new StringBuilder();
    private long column = DEFAULT_COLUMN;
    private long line = DEFAULT_LINE;
    private String sourcePath = null;
    private State state = State.INITIAL;

    public ElixirCompilerProcessAdapter(@NotNull CompileContext context,
                                        @NotNull String builderName,
                                        @NotNull String compileTargetRootPath,
                                        ElixirCompilerOptions compilerOptions) {
        this.context = context;
        this.builderName = builderName;
        this.compileTargetRootPath = compileTargetRootPath;
        this.compilerOptions = compilerOptions;
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        switch (state) {
            case INITIAL:
                onTextAvailableInInitial(event, outputType);
                break;

            case COMPILATION_ERROR:
                onTextAvailableInCompilationError(event, outputType);
                break;

            case WARNING:
                onTextAvailableInWarning(event, outputType);
                break;
        }
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
        switch (state) {
            case INITIAL:
                processWillTerminateInInitial();
                break;

            case COMPILATION_ERROR:
                processWillTerminateInCompilationError();
                break;

            case WARNING:
                processWillTerminateInWarning();
                break;
        }
    }

    private void onTextAvailableInAny(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String eventText = event.getText();

        if (StringUtils.isNotBlank(eventText)) {
            text.append(eventText);
        }
    }

    // Private Methods

    private void onTextAvailableInCompilationError(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();
        Matcher exceptionMatcher = EXCEPTION_PATTERN.matcher(text);

        if (exceptionMatcher.matches() || text.startsWith(INDENT)) {
            this.text.append(text);
        } else {
            onTransitionFromCompilationError();
            this.state = State.INITIAL;
            onTextAvailable(event, outputType);
        }
    }

    private void onTextAvailableInInitial(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();
        Matcher compilationErrorMatcher = COMPILATION_ERROR_PATTERN.matcher(text);

        if (compilationErrorMatcher.matches()) {
            onTransitionFromInitial();

            state = State.COMPILATION_ERROR;
            sourcePath = compilationErrorMatcher.group("file");
            this.text.append(text);
        } else {
            Matcher warningMatcher = WARNING_PATTERN.matcher(text);

            if (warningMatcher.matches()) {
                onTransitionFromInitial();

                state = State.WARNING;
                this.text.append(warningMatcher.group(1));
            } else {
                onTextAvailableInAny(event, outputType);
            }
        }
    }

    private void onTextAvailableInWarning(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();

        if (text.startsWith(INDENT)) {
            String unindented = text.substring(INDENT.length(), text.length());
            this.text.append(unindented);
        } else {
            onTransitionFromWarning();
            this.state = State.INITIAL;
            onTextAvailable(event, outputType);
        }
    }

    private void onTransitionFromCompilationError() {
        processMessage(BuildMessage.Kind.ERROR);
    }

    private void onTransitionFromInitial() {
        processMessage(BuildMessage.Kind.INFO);
    }

    private void onTransitionFromWarning() {
        processMessage(warningKind());
    }

    private void processMessage(@NotNull BuildMessage.Kind kind) {
        if (text.length() > 0) {
            String messageText = text.toString();
            Matcher stackTraceLineMatcher = STACK_TRACE_LINE.matcher(messageText);
            long problemBeginOffset = DEFAULT_PROBLEM_BEGIN_OFFSET;
            long problemEndOffset = DEFAULT_PROBLEM_END_OFFSET;

            if (stackTraceLineMatcher.find()) {
                sourcePath = compileTargetRootPath + '/' + stackTraceLineMatcher.group("relativePath");
                problemBeginOffset = stackTraceLineMatcher.start("problem");
                problemEndOffset = stackTraceLineMatcher.end("problem");
                line = Long.parseLong(stackTraceLineMatcher.group("line"));
                column = 0;
            }

            CompilerMessage compilerMessage = new CompilerMessage(
                    builderName,
                    kind,
                    messageText,
                    sourcePath,
                    problemBeginOffset,
                    problemEndOffset,
                    DEFAULT_PROBLEM_LOCATION_OFFSET,
                    line,
                    column
            );
            context.processMessage(compilerMessage);
        }

        reset();
    }

    private void processWillTerminateInCompilationError() {
        onTransitionFromCompilationError();
    }

    private void processWillTerminateInInitial() {
    }

    private void processWillTerminateInWarning() {
        onTransitionFromWarning();
    }

    private void reset() {
        this.column = DEFAULT_COLUMN;
        this.line = DEFAULT_LINE;
        this.sourcePath = null;
        this.text.setLength(0);
    }

    @Contract(pure = true)
    @NotNull
    private BuildMessage.Kind warningKind() {
        BuildMessage.Kind kind;

        if (compilerOptions.myWarningsAsErrorsEnabled) {
            kind = BuildMessage.Kind.ERROR;
        } else {
            kind = BuildMessage.Kind.WARNING;
        }

        return kind;
    }

    private enum State {
        INITIAL,
        COMPILATION_ERROR,
        WARNING
    }
}
