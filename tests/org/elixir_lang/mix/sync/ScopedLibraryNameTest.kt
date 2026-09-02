package org.elixir_lang.mix.sync

import junit.framework.TestCase

/**
 * Unit tests for [scopedLibraryNameToken], the inverse of [scopedDepLibraryName].
 *
 * The parser guards stale-entry pruning: a wrong parse can misclassify an invalid placeholder
 * entry scoped to a CURRENT content root as stale and delete it, so the edge cases here matter.
 */
class ScopedLibraryNameTest : TestCase() {

    fun testRoundTripsScopedDepLibraryName() {
        val url = "file:///home/dev/workspace/my_project"
        assertEquals(url, scopedLibraryNameToken(scopedDepLibraryName(url, "phoenix")))
    }

    fun testRoundTripsWslUrl() {
        val url = "file:////wsl.localhost/Ubuntu-24.04/home/dev/workspace/my_project"
        assertEquals(url, scopedLibraryNameToken(scopedDepLibraryName(url, "ecto")))
    }

    /**
     * A directory named with `" ["` is legal on every OS.  The parser must split on the FIRST
     * `" ["` (dep names are Mix app atoms and can never contain one) so the embedded URL survives
     * intact; splitting on the last occurrence would truncate the URL and misclassify a
     * current-root placeholder entry as stale.
     */
    fun testRoundTripsUrlContainingBracketMarker() {
        val url = "file:///home/dev/work [old]/my_project"
        assertEquals(url, scopedLibraryNameToken(scopedDepLibraryName(url, "phoenix")))
    }

    /**
     * The relative form is what reaches version control, so a regression here is a regression in
     * whether `.idea` can be shared between clones.
     */
    fun testRoundTripsRelativeToken() {
        assertEquals("apps/child", scopedLibraryNameToken(scopedDepLibraryName("apps/child", "phoenix")))
    }

    fun testRoundTripsProjectRootToken() {
        assertEquals(".", scopedLibraryNameToken(scopedDepLibraryName(".", "phoenix")))
    }

    fun testRoundTripsSiblingToken() {
        assertEquals("../shared_lib", scopedLibraryNameToken(scopedDepLibraryName("../shared_lib", "phoenix")))
    }

    /**
     * The "no owning root known" fallback must stay distinct from the project root's `"."`, or
     * every rootless module's placeholder would be scoped to the project root.
     */
    fun testRoundTripsEmptyToken() {
        assertEquals("", scopedLibraryNameToken(scopedDepLibraryName("", "phoenix")))
    }

    fun testUnscopedNameReturnsNull() {
        assertNull(scopedLibraryNameToken("phoenix"))
    }

    fun testConsolidatedLibraryNameReturnsNull() {
        assertNull(scopedLibraryNameToken("my_project (consolidated)"))
    }

    fun testNameWithMarkerButNoClosingBracketReturnsNull() {
        assertNull(scopedLibraryNameToken("phoenix [file:///home/dev/my_project"))
    }

    fun testNameStartingWithMarkerReturnsNull() {
        // markerIndex == 0 means there is no dep-name prefix - not a scoped name.
        assertNull(scopedLibraryNameToken(" [file:///home/dev/my_project]"))
    }
}
