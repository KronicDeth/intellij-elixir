package org.elixir_lang.parser_definition;

import com.intellij.util.ThrowableRunnable;
import junit.framework.TestSuite;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.fail;

/**
 * The tests a list names as failing on the Elixir under test. A listed test must still fail, and a listed name must
 * still be a test, so the list only ever shrinks to what is true. Each suite needs a list of its own: a name only
 * another suite produces fails this suite's stale check.
 */
final class KnownFailures {
    private final Path file;
    private final String elixirVersion;
    private final Set<String> names;

    private KnownFailures(@NotNull Path file, @NotNull String elixirVersion, @NotNull Set<String> names) {
        this.file = file;
        this.elixirVersion = elixirVersion;
        this.names = names;
    }

    /** Lines are {@code <test name>\t<Elixir versions, comma-separated>}; {@code #} starts a comment. */
    static KnownFailures forElixirUnderTest(@NotNull Path file) {
        String elixirVersion = String.valueOf(System.getenv("ELIXIR_VERSION")).split("-otp-")[0];
        List<String> lines;

        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Set<String> names = lines
                .stream()
                .filter(line -> !line.isBlank() && !line.startsWith("#"))
                .map(line -> line.split("\t"))
                .filter(columns -> Arrays.asList(columns[1].split(",")).contains(elixirVersion))
                .map(columns -> columns[0])
                .collect(Collectors.toSet());

        return new KnownFailures(file, elixirVersion, names);
    }

    boolean contains(@NotNull String name) {
        return names.contains(name);
    }

    /** Adds a failing test to {@code suite} for every listed name that is not one of {@code testNames}. */
    void checkStale(@NotNull TestSuite suite, @NotNull Collection<String> testNames) {
        List<String> stale = names.stream().filter(name -> !testNames.contains(name)).sorted().toList();

        if (!stale.isEmpty()) {
            suite.addTest(TestSuite.warning(
                    file + " lists tests that do not exist for Elixir " + elixirVersion + ": " + String.join(", ", stale)
            ));
        }
    }

    void expectFailure(@NotNull String name, @NotNull ThrowableRunnable<Throwable> test) {
        try {
            test.run();
        } catch (Throwable expected) {
            return;
        }

        fail(name + " passes on Elixir " + elixirVersion + ", so remove it from " + file);
    }
}
