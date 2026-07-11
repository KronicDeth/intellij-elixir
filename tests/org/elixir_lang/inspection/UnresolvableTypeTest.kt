package org.elixir_lang.inspection

import org.elixir_lang.PlatformTestCase

class UnresolvableTypeTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(UnresolvableType::class.java)
    }

    fun testMissingTypeOnResolvedModuleFlagged() {
        myFixture.configureByFiles("missing_type_on_resolved_module_flagged.ex", "other.ex")
        myFixture.checkHighlighting()
    }

    fun testResolvedTypeNotFlagged() {
        myFixture.configureByFiles("resolved_type_not_flagged.ex", "other.ex")
        myFixture.checkHighlighting()
    }

    fun testMissingLocalTypeFlagged() {
        myFixture.configureByFiles("missing_local_type_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testTypeVariableNotFlagged() {
        myFixture.configureByFiles("type_variable_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSpecBareMissingTypeFlagged() {
        myFixture.configureByFiles("spec_bare_missing_type_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testTypeBodyUnboundVariableFlagged() {
        myFixture.configureByFiles("type_body_unbound_variable_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSpecWhenBoundVariableNotFlagged() {
        myFixture.configureByFiles("spec_when_bound_variable_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testBuiltinTypeNotFlagged() {
        myFixture.configureByFiles("builtin_type_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testUnresolvableModuleNotFlaggedByTypeInspection() {
        myFixture.configureByFiles("unresolvable_module_not_flagged_by_type.ex")
        myFixture.checkHighlighting()
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/inspection/unresolvable_type"
}
