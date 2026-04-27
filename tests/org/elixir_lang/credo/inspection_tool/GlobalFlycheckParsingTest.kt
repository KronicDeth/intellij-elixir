package org.elixir_lang.credo.inspection_tool

import org.elixir_lang.PlatformTestCase

class GlobalFlycheckParsingTest : PlatformTestCase() {
    fun testParsesLineAndColumnFinding() {
        val finding = parseFlycheckFinding(
            "lib/level_web/ui/empty_state.ex:1:11: R: Modules should have a @moduledoc tag."
        )

        assertNotNull(finding)
        assertEquals("lib/level_web/ui/empty_state.ex", finding!!.path)
        assertEquals(1, finding.line)
        assertEquals(11, finding.column)
        assertEquals("Modules should have a @moduledoc tag.", finding.message)
    }

    fun testParsesLineWithoutColumnFinding() {
        val finding = parseFlycheckFinding(
            "lib/level_web/ui/empty_state.ex:1: W: Prefer explicit module aliases"
        )

        assertNotNull(finding)
        assertEquals("lib/level_web/ui/empty_state.ex", finding!!.path)
        assertEquals(1, finding.line)
        assertNull(finding.column)
        assertEquals("Prefer explicit module aliases", finding.message)
    }

    fun testParsesLineWithoutColumnFindingInNestedPath() {
        val finding = parseFlycheckFinding(
            "apps/ic_core/lib/ic_core/dispatch/randomization.ex:6: C: Prefer Enum.random/1"
        )

        assertNotNull(finding)
        assertEquals("apps/ic_core/lib/ic_core/dispatch/randomization.ex", finding!!.path)
        assertEquals(6, finding.line)
        assertNull(finding.column)
        assertEquals("Prefer Enum.random/1", finding.message)
    }

    fun testParsesFileLevelFinding() {
        val finding = parseFlycheckFinding(
            "apps/smoke_test/lib/smoke_test.ex: W: Test files should end with .exs"
        )

        assertNotNull(finding)
        assertEquals("apps/smoke_test/lib/smoke_test.ex", finding!!.path)
        assertNull(finding.line)
        assertNull(finding.column)
        assertEquals("Test files should end with .exs", finding.message)
    }

    fun testDoesNotParseSummaryLines() {
        assertNull(parseFlycheckFinding("Please report incorrect results: https://example.test"))
    }
}
