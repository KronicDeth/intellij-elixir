package org.elixir_lang.code

import junit.framework.TestCase

class IdentifierTest : TestCase() {
    fun testInspectAsFunctionEscapesBackslash() {
        assertEquals("\"\\\\\"", Identifier.inspectAsFunction("\\"))
    }

    fun testInspectAsKeyEscapesBackslash() {
        assertEquals("\"\\\\\":", Identifier.inspectAsKey("\\"))
    }

    fun testInspectAsKeyWithStartsWithDigit() {
        assertEquals( "\"4k\":", Identifier.inspectAsKey("4k"))
    }

    fun testInspectAsKeyWithContainsHyphen() {
        assertEquals("\"csrf-params\":", Identifier.inspectAsKey("csrf-params"))
    }

    private fun assertInspectAsKey(expected: String, atomValue: String) {
        assertEquals(expected, Identifier.inspectAsKey(atomValue))
    }

    fun testInspectAsKeyWithAtoms() {
        assertInspectAsKey("_12:", "_12")
        assertInspectAsKey("ola:", "ola")
        assertInspectAsKey("ólá:", "ólá")
        assertInspectAsKey("ólá?:", "ólá?")
        assertInspectAsKey("ólá!:", "ólá!")
        assertInspectAsKey("ól@:", "ól@")
        assertInspectAsKey("ól@!:", "ól@!")
        assertInspectAsKey("ó@@!:", "ó@@!")
        assertInspectAsKey("Ola:", "Ola")
        assertInspectAsKey("Ólá:", "Ólá")
        assertInspectAsKey("ÓLÁ:", "ÓLÁ")
        assertInspectAsKey("ÓLÁ?:", "ÓLÁ?")
        assertInspectAsKey("ÓLÁ!:", "ÓLÁ!")
        assertInspectAsKey("ÓL@!:", "ÓL@!")
        assertInspectAsKey("Ó@@!:", "Ó@@!")
        assertInspectAsKey("こんにちは世界:", "こんにちは世界")
        assertInspectAsKey( "\"123\":",  "123")
        assertInspectAsKey("\"@123\":", "@123")
    }
}
