package org.elixir_lang.inspection

import org.elixir_lang.PlatformTestCase

class TypeVariableUsedOnceTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(TypeVariableUsedOnce::class.java)
    }

    fun testTypeVariableUsedOnceFlagged() {
        myFixture.configureByFiles("type_variable_used_once_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testTypeVariableUsedTwiceNotFlagged() {
        myFixture.configureByFiles("type_variable_used_twice_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/inspection/type_variable_used_once"
}
