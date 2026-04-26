package org.elixir_lang.beam.term

import junit.framework.TestCase

class InspectTest : TestCase() {
    fun testEscapesInterpolationMarkers() {
        assertEquals("'abc\\#{value}'", "'abc#{value}'".elixirEscape())
    }

    fun testPreservesEscapedInterpolationMarkers() {
        assertEquals("'abc\\#{value}'", "'abc\\#{value}'".elixirEscape())
    }

    fun testEscapesInterpolationAfterEscapedNewline() {
        assertEquals("'line\\n\\#{value}'", "'line\n#{value}'".elixirEscape())
    }
}
