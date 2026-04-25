package org.elixir_lang.psi.impl

import org.elixir_lang.PlatformTestCase

class GetModuleNameTest : PlatformTestCase() {
    fun testTopLevelElementReturnsNull() {
        myFixture.configureByFile("top_level.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        assertNull(elementAtCaret!!.getModuleName())
    }

    fun testInsideSingleModule() {
        myFixture.configureByFile("single_module.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        assertEquals("Foo", elementAtCaret!!.getModuleName())
    }

    fun testInsideNestedModule() {
        myFixture.configureByFile("nested_module.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        assertEquals("Foo.Bar", elementAtCaret!!.getModuleName())
    }

    fun testDefmoduleCallItself() {
        myFixture.configureByFile("defmodule_call.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        // When called on the defmodule Call node itself (withSelf = true scenario),
        // getModuleName() should return the module name.
        val moduleName = elementAtCaret!!.getModuleName()
        assertEquals("Foo", moduleName)
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/psi/impl/get_module_name"
}
