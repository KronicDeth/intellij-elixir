package org.elixir_lang.psi

import org.elixir_lang.PlatformTestCase

class ElixirVisitorTest : PlatformTestCase() {
    fun testvisitLiteralSigilLine() {
        myFixture.configureByFile("autogenerate_test.exs")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val literalSigilLine = elementAtCaret!!.parent


        assertInstanceOf(literalSigilLine, ElixirLiteralSigilLine::class.java)

        literalSigilLine.accept(ElixirVisitor())
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/elixir_visitor"
}
