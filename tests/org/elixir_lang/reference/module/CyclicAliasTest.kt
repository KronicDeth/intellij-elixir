package org.elixir_lang.reference.module

import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.testFramework.PlatformTestUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias

/**
 * `resolver/Module.resolveAll` catches the overflow and logs it, so empty results alone would pass
 * whether the walk terminated or overflowed - hence the assertions on logged errors.
 */
class CyclicAliasTest : PlatformTestCase() {
    /** A single-segment `alias` of a module that does not exist re-enters the fallback on itself. */
    fun testSelfReferentialAlias() {
        val reference = aliasReferenceAtCaret("self_referential.ex")

        val (resolveResults, loggedErrors) = captureLoggedErrors { reference.multiResolve(false) }

        assertEmpty(
            "resolving an alias of a module that does not exist logged an error",
            loggedErrors
        )
        assertEmpty(resolveResults)
    }

    /** Two turns rather than one, through the `__using__` quotes of two mutually `use`d modules. */
    fun testMutuallyUsedAlias() {
        val reference = aliasReferenceAtCaret("mutual_use.ex", "cycle_a.ex", "cycle_b.ex")

        val (resolveResults, loggedErrors) = captureLoggedErrors { reference.multiResolve(false) }

        assertEmpty(
            "resolving an alias through mutually `use`d modules logged an error",
            loggedErrors
        )
        assertEmpty(resolveResults)
    }

    /** Every `alias Missing` asks the same question; keying on the name answers it once. */
    fun testRepeatedUnresolvableAliasesTerminatePromptly() {
        val reference = aliasReferenceAtCaret("repeated_alias.ex")
        lateinit var resolveResults: Array<ResolveResult>
        lateinit var loggedErrors: List<LoggedError>

        PlatformTestUtil.assertTiming("the same name is being resolved more than once", 6_000, 4) {
            // Each attempt must do the work: `ResolveCache` would serve the first result to the rest.
            PsiManager.getInstance(project).dropResolveCaches()
            val captured = captureLoggedErrors { reference.multiResolve(false) }
            resolveResults = captured.first
            loggedErrors = captured.second
        }

        assertEmpty("resolving repeated unresolvable aliases logged an error", loggedErrors)
        assertEmpty(resolveResults)
    }

    /** A rename chain, which the name-keyed guard must not cut short. */
    fun testDeepChainStillResolves() {
        val resolveResults = aliasReferenceAtCaret("deep_chain.ex", "referenced.ex").multiResolve(false)

        assertContainsElements(
            resolveResults.map { it.element!!.text },
            "defmodule MyNamespace.Referenced do\nend"
        )
    }

    /** The control: the chain the fallback exists for, which a guard that over-fires would break. */
    fun testTransitiveAlias() {
        val resolveResults = aliasReferenceAtCaret("transitive.ex", "referenced.ex").multiResolve(false)

        assertEquals(
            listOf(
                // only the fallback can reach this one, by following Refd -> Referenced ->
                // MyNamespace.Referenced
                "defmodule MyNamespace.Referenced do\nend",
                "alias Referenced, as: Refd",
                "alias MyNamespace.Referenced"
            ),
            resolveResults.map { it.element!!.text }
        )
    }

    /** Returns the reference unresolved, so a caller can time or capture the resolve itself. */
    private fun aliasReferenceAtCaret(vararg fileNames: String): PsiPolyVariantReference {
        myFixture.configureByFiles(*fileNames)

        val alias = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent
        assertInstanceOf(alias, ElixirAlias::class.java)

        return assertInstanceOf(alias.reference, PsiPolyVariantReference::class.java)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/cyclic_alias"
}
