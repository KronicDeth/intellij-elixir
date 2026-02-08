package org.elixir_lang.jps.builder;

import com.intellij.util.execution.ParametersListUtil;
import org.elixir_lang.jps.builder.execution.ExecutionException;
import org.elixir_lang.jps.builder.execution.GeneralCommandLine;
import org.elixir_lang.jps.builder.model.SdkProperties;
import org.elixir_lang.jps.builder.sdk_type.Elixir;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.library.sdk.JpsSdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

final class ElixirCliDryRun {
    private static final String DRY_RUN_ENV = "ELIXIR_CLI_DRY_RUN";
    private static final int DRY_RUN_TIMEOUT_MS = 10_000;

    private ElixirCliDryRun() {
    }

    @Nullable
    static List<String> baseArguments(@NotNull Map<String, String> environment,
                                      @Nullable String workingDirectory,
                                      @NotNull JpsSdk<SdkProperties> sdk) {
        String exePath = mixPath(sdk);
        if (exePath == null) {
            return null;
        }

        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withWorkDirectory(workingDirectory);
        commandLine.setExePath(exePath);
        commandLine.getEnvironment().putAll(environment);
        commandLine.getEnvironment().put(DRY_RUN_ENV, "1");
        commandLine.addParameter("--");

        ProcessOutput output = runProcess(commandLine);
        if (output == null || output.timedOut) {
            return null;
        }

        List<String> tokens = extractErlTokens(output.stdoutLines);
        if (tokens == null) {
            tokens = extractErlTokens(output.stderrLines);
        }
        if (tokens == null || tokens.size() < 2) {
            return null;
        }

        return stripDryRunMarkers(tokens.subList(1, tokens.size()));
    }

    @Nullable
    private static String mixPath(@NotNull JpsSdk<SdkProperties> sdk) {
        String homePath = sdk.getHomePath();
        if (homePath == null) {
            return null;
        }
        java.io.File file = Elixir.mixExecutableFile(homePath);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    @Nullable
    private static List<String> extractErlTokens(@NotNull List<String> lines) {
        for (String line : lines) {
            String[] tokens = ParametersListUtil.parseToArray(line);
            if (tokens.length == 0) {
                continue;
            }
            String first = tokens[0];
            if (first.endsWith("erl") || first.endsWith("erl.exe")) {
                return Arrays.asList(tokens);
            }
        }
        return null;
    }

    @NotNull
    private static List<String> stripDryRunMarkers(@NotNull List<String> arguments) {
        ArrayList<String> trimmed = new ArrayList<>(arguments);
        // retain compat with older Jvm versions
        //noinspection SequencedCollectionMethodCanBeUsed
        if (!trimmed.isEmpty() && Objects.equals(trimmed.get(trimmed.size() - 1), "--")) {
            // retain compat with older Jvm versions
            //noinspection SequencedCollectionMethodCanBeUsed
            trimmed.remove(trimmed.size() - 1);
        }
        return trimmed;
    }

    @Nullable
    private static ProcessOutput runProcess(@NotNull GeneralCommandLine commandLine) {
        Process process;
        try {
            process = commandLine.createProcess();
        } catch (ExecutionException executionException) {
            return null;
        }

        boolean finished;
        try {
            finished = process.waitFor(DRY_RUN_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return null;
        }

        if (!finished) {
            process.destroy();
            return new ProcessOutput(Collections.emptyList(), Collections.emptyList(), true);
        }

        List<String> stdoutLines = readLines(process.getInputStream());
        List<String> stderrLines = readLines(process.getErrorStream());
        return new ProcessOutput(stdoutLines, stderrLines, false);
    }

    @NotNull
    private static List<String> readLines(@NotNull InputStream stream) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {
        }
        return lines;
    }

    private static final class ProcessOutput {
        private final List<String> stdoutLines;
        private final List<String> stderrLines;
        private final boolean timedOut;

        private ProcessOutput(@NotNull List<String> stdoutLines,
                              @NotNull List<String> stderrLines,
                              boolean timedOut) {
            this.stdoutLines = stdoutLines;
            this.stderrLines = stderrLines;
            this.timedOut = timedOut;
        }
    }
}
