package org.elixir_lang.structure_view.sorter;

import junit.framework.TestCase;
import org.elixir_lang.structure_view.element.Timed;

import java.util.Comparator;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TimeTest extends TestCase {
    /*
     * Fixtures
     */

    private static Timed compileTimed() { return () -> Timed.Time.COMPILE; }
    private static Timed runTimed()     { return () -> Timed.Time.RUN; }

    private static final Object NON_TIMED_A = new Object();
    private static final Object NON_TIMED_B = new Object();

    private Comparator comparator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comparator = Time.INSTANCE.getComparator();
    }

    /*
     * Tests - both Timed
     */

    public void testCompileVsCompile() {
        Timed c1 = compileTimed(), c2 = compileTimed();
        assertEquals(0, comparator.compare(c1, c2));
    }

    public void testRunVsRun() {
        Timed r1 = runTimed(), r2 = runTimed();
        assertEquals(0, comparator.compare(r1, r2));
    }

    public void testCompileBeforeRun() {
        assertTrue(comparator.compare(compileTimed(), runTimed()) < 0);
    }

    public void testRunAfterCompile() {
        assertTrue(comparator.compare(runTimed(), compileTimed()) > 0);
    }

    /*
     * Tests - one Timed, one non-Timed
     */

    public void testCompileBeforeNonTimed() {
        assertTrue(comparator.compare(compileTimed(), NON_TIMED_A) < 0);
    }

    public void testRunAfterNonTimed() {
        assertTrue(comparator.compare(runTimed(), NON_TIMED_A) > 0);
    }

    public void testNonTimedAfterCompile() {
        assertTrue(comparator.compare(NON_TIMED_A, compileTimed()) > 0);
    }

    public void testNonTimedBeforeRun() {
        assertTrue(comparator.compare(NON_TIMED_A, runTimed()) < 0);
    }

    /*
     * Tests - both non-Timed
     */

    public void testNonTimedVsNonTimed() {
        assertEquals(0, comparator.compare(NON_TIMED_A, NON_TIMED_B));
    }
}
