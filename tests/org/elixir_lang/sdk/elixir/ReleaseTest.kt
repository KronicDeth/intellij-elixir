package org.elixir_lang.sdk.elixir

import org.elixir_lang.PlatformTestCase
import org.junit.Assert.assertNotEquals

/**
 * Tests for Elixir Release parsing and formatting.
 */
class ReleaseTest: PlatformTestCase() {

    fun testFromString_simpleVersion() {
        val release = Release.fromString("1.15.7")
        assertNotNull(release)
        assertEquals("1", release!!.major)
        assertEquals("15", release.minor)
    }

    fun testFromString_versionWithOtp() {
        val release = Release.fromString("1.15.7-otp-26")
        assertNotNull(release)
        assertEquals("1", release!!.major)
        assertEquals("15", release.minor)
    }

    fun testFromString_majorOnly() {
        val release = Release.fromString("1")
        assertNotNull(release)
        assertEquals("1", release!!.major)
        assertNull(release.minor)
    }

    fun testFromString_invalid() {
        assertNull(Release.fromString("invalid"))
        assertNull(Release.fromString(""))
        assertNull(Release.fromString(null))
    }

    fun testVersion_simple() {
        val release = Release.fromString("1.15.7")
        assertEquals("1.15.7", release!!.version())
    }

    fun testVersion_withOtp() {
        val release = Release.fromString("1.15.7-otp-26")
        assertEquals("1.15.7-otp-26", release!!.version())
    }

    fun testToString_includesElixirPrefix() {
        val release = Release.fromString("1.15.7")
        assertEquals("Elixir 1.15.7", release!!.toString())
    }

    fun testToString_vs_version() {
        // toString() includes "Elixir" prefix, version() does not
        val release = Release.fromString("1.15.7")
        assertNotEquals(release!!.toString(), release.version())
        assertTrue(release.toString().startsWith("Elixir "))
        assertFalse(release.version().contains("Elixir"))
    }

    fun testCompareTo_greaterMajor() {
        val older = Release.fromString("1.14.0")
        val newer = Release.fromString("2.0.0")
        assertTrue(newer!! > older!!)
    }

    fun testCompareTo_greaterMinor() {
        val older = Release.fromString("1.14.0")
        val newer = Release.fromString("1.15.0")
        assertTrue(newer!! > older!!)
    }

    fun testCompareTo_greaterPatch() {
        val older = Release.fromString("1.15.6")
        val newer = Release.fromString("1.15.7")
        assertTrue(newer!! > older!!)
    }

    fun testCompareTo_equal() {
        val a = Release.fromString("1.15.7")
        val b = Release.fromString("1.15.7")
        assertEquals(0, a!!.compareTo(b!!))
    }
}
