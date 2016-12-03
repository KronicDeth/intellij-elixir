package org.elixir_lang.sdk;

import com.intellij.execution.ExecutionException;
import junit.framework.TestCase;

public class ElixirSystemUtilTest extends TestCase {
    /*
     * Tests
     */

    public void testIssue521() throws ExecutionException {
        assertEquals(
                -1,
                ElixirSystemUtil
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
