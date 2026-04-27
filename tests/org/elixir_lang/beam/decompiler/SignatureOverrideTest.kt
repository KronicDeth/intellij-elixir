package org.elixir_lang.beam.decompiler

import junit.framework.TestCase
import org.elixir_lang.beam.MacroNameArity

class SignatureOverrideTest : TestCase() {
    fun testStructArityOneIncludesDefinitionMacro() {
        val decompiled = StringBuilder()

        SignatureOverride.append(decompiled, MacroNameArity("def", "__struct__", 1))

        assertEquals(
            "  def __struct__(kv) do\n    # body not decompiled\n  end\n",
            decompiled.toString()
        )
    }
}
