package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ThrowableRunnable;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.elixir_lang.intellij_elixir.Quoter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * One test per snippet of Elixir's own tests, from {@code elixir_snippets/snippets.jsonl}, that the reference quoter
 * accepts: it must parse without error and quote as the quoter does. Snippets the quoter rejects are left out, since
 * what the plugin does with invalid Elixir is not asserted here. Line separators are converted, as IntelliJ converts a
 * file's text on load, so the quoter judges the text the plugin would parse; nothing is trimmed, because some snippets
 * are only whitespace or line endings.
 */
public class ElixirSnippetParsingTestCase extends ParsingTestCase {
    private static final Path SNIPPETS =
            Path.of("testData", "org", "elixir_lang", "parser_definition", "elixir_snippets", "snippets.jsonl");
    private static final Path KNOWN_FAILURES =
            Path.of("testData", "org", "elixir_lang", "parser_definition", "snippet_known_failures.tsv");

    private final String hash;
    private final String source;
    private final KnownFailures knownFailures;

    private ElixirSnippetParsingTestCase(@NotNull JsonObject snippet, @NotNull KnownFailures knownFailures) {
        hash = snippet.get("hash").getAsString();
        source = source(snippet);
        this.knownFailures = knownFailures;

        JsonObject origin = snippet.getAsJsonObject("origin");
        setName(hash + " " + origin.get("file").getAsString() + ":" + origin.get("line").getAsInt());
    }

    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite(ElixirSnippetParsingTestCase.class.getName());
        KnownFailures knownFailures = KnownFailures.forElixirUnderTest(KNOWN_FAILURES);
        List<String> hashes = new ArrayList<>();

        for (String line : Files.readAllLines(SNIPPETS, StandardCharsets.UTF_8)) {
            JsonObject snippet = JsonParser.parseString(line).getAsJsonObject();
            String status;

            try {
                OtpErlangTuple quoted = Quoter.INSTANCE.quote(source(snippet));
                status = quoted == null ? null : ((OtpErlangAtom) quoted.elementAt(0)).atomValue();
            } catch (Throwable e) {
                suite.addTest(TestSuite.warning("The reference quoter could not judge the snippets: " + e));
                return suite;
            }

            if ("ok".equals(status)) {
                ElixirSnippetParsingTestCase test = new ElixirSnippetParsingTestCase(snippet, knownFailures);
                suite.addTest(test);
                hashes.add(test.hash);
            } else if (status == null) {
                suite.addTest(TestSuite.warning("The reference quoter did not answer for snippet " + snippet.get("hash")));
                return suite;
            }
        }

        knownFailures.checkStale(suite, hashes);

        return suite;
    }

    private static String source(@NotNull JsonObject snippet) {
        return StringUtil.convertLineSeparators(snippet.get("source").getAsString());
    }

    @Override
    protected void runBare(@NotNull ThrowableRunnable<Throwable> testRunnable) throws Throwable {
        if (knownFailures.contains(hash)) {
            super.runBare(() -> knownFailures.expectFailure(hash, this::assertParsed));
        } else {
            super.runBare(this::assertParsed);
        }
    }

    private void assertParsed() {
        myFile = createPsiFile("snippet", source);
        ensureParsed(myFile);

        assertWithoutLocalError();
        assertQuotedCorrectly();
    }
}
