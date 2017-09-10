package org.elixir_lang.sdk;

import com.intellij.execution.ExecutionException;
import junit.framework.TestCase;

public class ProcessOutputTest extends TestCase {
    /*
     * Tests
     */

    public void testIssue521() throws ExecutionException {
        assertEquals(
                -1,
                ProcessOutput
                        .getProcessOutput(
                                1,
                                null,
                                "/exe-path",
                                new String[0]
                        )
                        .getExitCode()
        );
    }
}
