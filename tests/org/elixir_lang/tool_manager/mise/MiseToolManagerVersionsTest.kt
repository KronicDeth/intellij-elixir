package org.elixir_lang.tool_manager.mise

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.mise.MiseToolEntry
import org.elixir_lang.mise.MiseVersions

/**
 * Unit tests for [MiseToolManagerVersions].
 *
 * Verifies that the adapter correctly maps the three [MiseToolEntry] fields that
 * [org.elixir_lang.tool_manager.ToolManagerSdkChecker] actually uses
 * ([version][org.elixir_lang.tool_manager.ToolEntry.version],
 * [installPath][org.elixir_lang.tool_manager.ToolEntry.installPath],
 * [installed][org.elixir_lang.tool_manager.ToolEntry.installed]) and that the other
 * mise-specific fields are silently dropped at the adapter boundary.
 */
class MiseToolManagerVersionsTest : PlatformTestCase() {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Constructs a [MiseToolEntry] with only the three fields that survive into [MiseToolManagerVersions].
     * The remaining fields ([MiseToolEntry.requestedVersion], [MiseToolEntry.sourceType],
     * [MiseToolEntry.sourcePath], [MiseToolEntry.active]) are filled with stable placeholders so the
     * intent of each test stays focused on the mapped fields.
     */
    private fun miseEntry(
        version: String,
        installPath: String,
        installed: Boolean = true,
    ) = MiseToolEntry(
        version = version,
        requestedVersion = "latest",
        installPath = installPath,
        sourceType = "tool-versions",
        sourcePath = "/project/.tool-versions",
        installed = installed,
        active = true,
    )

    // -------------------------------------------------------------------------
    // toolManagerName
    // -------------------------------------------------------------------------

    fun testToolManagerName_isAlwaysMise() {
        val adapted = MiseToolManagerVersions(MiseVersions(elixir = null, erlang = null))
        assertEquals("mise", adapted.toolManagerName)
    }

    // -------------------------------------------------------------------------
    // Both entries present - field mapping
    // -------------------------------------------------------------------------

    fun testBothEntries_versionMappedVerbatim() {
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = miseEntry("1.17.3", "/i/elixir-1.17"),
                erlang = miseEntry("27.3.4", "/i/erlang-27"),
            )
        )
        assertEquals("1.17.3", adapted.elixir!!.version)
        assertEquals("27.3.4", adapted.erlang!!.version)
    }

    fun testBothEntries_installPathMappedVerbatim() {
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = miseEntry("1.17.3", "/i/elixir-1.17"),
                erlang = miseEntry("27.3.4", "/i/erlang-27"),
            )
        )
        assertEquals("/i/elixir-1.17", adapted.elixir!!.installPath)
        assertEquals("/i/erlang-27",   adapted.erlang!!.installPath)
    }

    fun testBothEntries_installedTruePreserved() {
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = miseEntry("1.17.3", "/i/elixir", installed = true),
                erlang = miseEntry("27.3.4",  "/i/erlang", installed = true),
            )
        )
        assertTrue("elixir.installed should be true",  adapted.elixir!!.installed)
        assertTrue("erlang.installed should be true",  adapted.erlang!!.installed)
    }

    fun testBothEntries_installedFalsePreserved() {
        // Verifies the flag is propagated, not assumed/defaulted to true.
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = miseEntry("1.17.3", "/i/elixir", installed = false),
                erlang = miseEntry("27.3.4",  "/i/erlang", installed = false),
            )
        )
        assertFalse("elixir.installed should be false", adapted.elixir!!.installed)
        assertFalse("erlang.installed should be false", adapted.erlang!!.installed)
    }

    // -------------------------------------------------------------------------
    // Null propagation
    // -------------------------------------------------------------------------

    fun testElixirNull_erlangPresent() {
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = null,
                erlang = miseEntry("27.3.4", "/i/erlang-27"),
            )
        )
        assertNull("elixir should be null", adapted.elixir)
        assertNotNull("erlang should not be null", adapted.erlang)
        assertEquals("27.3.4", adapted.erlang!!.version)
    }

    fun testElixirPresent_erlangNull() {
        val adapted = MiseToolManagerVersions(
            MiseVersions(
                elixir = miseEntry("1.17.3", "/i/elixir-1.17"),
                erlang = null,
            )
        )
        assertNotNull("elixir should not be null", adapted.elixir)
        assertNull("erlang should be null", adapted.erlang)
        assertEquals("1.17.3", adapted.elixir!!.version)
    }

    fun testBothNull_toolManagerNameStillMise() {
        val adapted = MiseToolManagerVersions(MiseVersions(elixir = null, erlang = null))
        assertNull(adapted.elixir)
        assertNull(adapted.erlang)
        assertEquals("mise", adapted.toolManagerName)
    }

    // -------------------------------------------------------------------------
    // Unconventional version strings - no parsing or normalisation
    // -------------------------------------------------------------------------

    fun testUnconventionalVersionStrings_copiedVerbatim() {
        // The adapter must not parse, normalise, or strip any part of the version string.
        val versions = listOf("", "1.17.3-otp-27", "main", "ref:abc1234")
        for (v in versions) {
            val adapted = MiseToolManagerVersions(
                MiseVersions(
                    elixir = miseEntry(v, "/i/elixir"),
                    erlang = null,
                )
            )
            assertEquals(
                "version '$v' should be copied verbatim without normalisation",
                v,
                adapted.elixir!!.version,
            )
        }
    }
}
