package org.elixir_lang.model.psi.module

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.model.psi.PsiSymbolReferenceService
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.QualifiableAlias

/**
 * Regression for [ModuleReferenceProvider]: a no-block `alias Foo` (and `import`/`use`) must expose a
 * module reference over the aliased name so navigation and rename can be initiated from the caret.
 * The provider previously used `isCallingMacro` (which requires a do-block/keyword), so these common
 * no-block forms produced no reference at all.
 *
 * References are hosted on the outermost [QualifiableAlias] itself (not the `alias ...` Call), so the
 * same provider covers bare aliased names, multi-alias braces, struct literals, and `@behaviour`.
 */
@Suppress("UnstableApiUsage")
class ModuleAliasReferenceTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testNoBlockAliasExposesModuleReference() {
        myFixture.configureByFiles("alias_reference.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        val alias = generateSequence(element!!) { it.parent }
            .filterIsInstance<QualifiableAlias>()
            .last()

        val references = PsiSymbolReferenceService.getService().getReferences(alias)
        assertTrue(
            "alias Sample.Target should expose a ModuleReference over the aliased name",
            references.any { it is ModuleReference }
        )
    }
}
