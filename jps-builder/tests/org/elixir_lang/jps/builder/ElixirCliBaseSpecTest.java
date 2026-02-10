package org.elixir_lang.jps.builder;

import com.intellij.testFramework.UsefulTestCase;
import org.elixir_lang.jps.builder.execution.GeneralCommandLine;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElixirCliBaseSpecTest extends UsefulTestCase {
    public void testElixirCliBaseMatchesSpecFeatures() throws Exception {
        Path specPath = findSpecPath();
        assertTrue("Spec file missing: " + specPath, Files.exists(specPath));

        List<String> lines = Files.readAllLines(specPath, StandardCharsets.UTF_8);
        List<String> errors = new ArrayList<>();
        String tool = null;

        List<String> actualTokens = buildActualTokens();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("#")) {
                tool = line.substring(1).trim();
                continue;
            }
            if ((!"elixir".equals(tool) && !"mix".equals(tool))) {
                continue;
            }

            String[] parts = line.split("\\|", 2);
            if (parts.length != 2) {
                continue;
            }

            String version = parts[0].trim();
            List<String> tokens = tokenize(parts[1]);
            tokens = dropWhile(tokens, "erl");
            tokens = dropTrailing(tokens, "--");

            if (tokens.contains("-noshell") && !actualTokens.contains("-noshell")) {
                errors.add(tool + " " + version + ": -noshell");
            }
            if (containsSequence(tokens, Arrays.asList("-s", "elixir", "start_cli")) &&
                !containsSequence(actualTokens, Arrays.asList("-s", "elixir", "start_cli"))) {
                errors.add(tool + " " + version + ": start_cli");
            }
            if (tokens.contains("-extra") && !actualTokens.contains("-extra")) {
                errors.add(tool + " " + version + ": -extra");
            }
        }

        if (!containsSequence(actualTokens, Arrays.asList("-elixir", "ansi_enabled", "true"))) {
            errors.add("base: -elixir ansi_enabled true");
        }

        assertTrue("Missing expected CLI features:\n" + String.join("\n", errors), errors.isEmpty());
    }

    @NotNull
    private static Path findSpecPath() {
        Path relative = Paths.get("testData", "org", "elixir_lang", "cli", "elixir_cli_base_args.txt");

        Path dir = Paths.get("").toAbsolutePath();
        while (dir != null) {
            Path candidate = dir.resolve(relative);
            if (Files.exists(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }

        return relative.toAbsolutePath();
    }

    private static List<String> buildActualTokens() {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        ElixirCliBase.addElixirFallbackArguments(commandLine, Collections.emptyList());
        return commandLine.getParametersList().getList();
    }

    private static List<String> tokenize(String command) {
        String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(trimmed.split("\\s+"));
    }

    private static boolean containsSequence(List<String> tokens, List<String> sequence) {
        if (sequence.isEmpty() || sequence.size() > tokens.size()) {
            return false;
        }
        for (int i = 0; i <= tokens.size() - sequence.size(); i++) {
            if (tokens.subList(i, i + sequence.size()).equals(sequence)) {
                return true;
            }
        }
        return false;
    }

    // Test helper keeps explicit parameter for readability.
    @SuppressWarnings("SameParameterValue")
    private static List<String> dropWhile(List<String> tokens, String token) {
        if (tokens.isEmpty()) {
            return tokens;
        }
        if (!tokens.getFirst().equals(token)) {
            return tokens;
        }
        return tokens.subList(1, tokens.size());
    }

    // Test helper keeps explicit parameter for readability.
    @SuppressWarnings("SameParameterValue")
    private static List<String> dropTrailing(List<String> tokens, String token) {
        if (tokens.isEmpty()) {
            return tokens;
        }
        int lastIndex = tokens.size() - 1;
        if (!tokens.get(lastIndex).equals(token)) {
            return tokens;
        }
        return tokens.subList(0, lastIndex);
    }
}
