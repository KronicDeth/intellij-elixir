package org.elixir_lang.junit;

import org.jetbrains.annotations.NotNull;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * Runs {@code @Parameterized} tests with control characters escaped out of the generated test names.
 *
 * <p>The lexer tests parameterise on raw character sequences, so display names built from them can contain
 * literal CR/LF (e.g. {@code token["\n" parses as EOL token and advances to state 0]}). Gradle escapes those
 * correctly in the JUnit XML, but EnricoMi/publish-unit-test-result-action -- which reports results in CI --
 * stores a commit's test-name list newline-joined in a check-run annotation and reads it back with
 * {@code raw_details.split('\n')}. Names containing a newline come back as fragments, so the comparison
 * against the base commit sees the same test as both removed and added, e.g. "This pull request removes 100
 * and adds 91 tests" on a pull request that touches no tests at all.
 *
 * <p>Escaping the names at the source keeps the reports stable. Applied via
 * {@code @Parameterized.UseParametersRunnerFactory} on the lexer test base class, which is {@code @Inherited}
 * and so covers every parameterised subclass.
 */
public class EscapedNameRunnerFactory implements ParametersRunnerFactory {
    @Override
    public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
        return new BlockJUnit4ClassRunnerWithParameters(test) {
            @Override
            protected String getName() {
                // testName(FrameworkMethod) delegates here, so overriding getName() covers both the runner
                // name and each individual test name.
                return escape(super.getName());
            }
        };
    }

    @NotNull
    private static String escape(@NotNull String name) {
        StringBuilder escaped = new StringBuilder(name.length());

        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);

            switch (character) {
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (Character.isISOControl(character)) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }

        return escaped.toString();
    }
}
