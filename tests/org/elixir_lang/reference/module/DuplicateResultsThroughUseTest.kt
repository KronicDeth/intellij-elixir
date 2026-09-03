package org.elixir_lang.reference.module

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.util.IdempotenceChecker
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call

/**
 * A module reachable by more than one path through a `use` must resolve to each element once, and to
 * the same elements every time it is resolved.
 *
 * The resolver expands each result into its terminal element plus the `alias`, `require` and `use`
 * calls walked to reach it, so a module defined in two files shares those calls between both paths.
 * Without the deduplication the shared calls come back once per path.
 */
class DuplicateResultsThroughUseTest : PlatformTestCase() {
    fun testEachElementResolvedOnce() {
        val elements = resolvedElements()

        assertContainsUseCall(elements)

        val duplicated = elements.groupBy { it }.filterValues { it.size > 1 }.keys

        assertTrue(
            "Resolved more than once: ${duplicated.joinToString { it.text }} in ${describe(elements)}",
            duplicated.isEmpty()
        )
    }

    fun testResolvedElementsSurviveDroppingTheResolveCache() {
        val first = resolvedElements()

        assertContainsUseCall(first)

        // Without this the second call reads the ResolveCache and cannot re-enter the `use` scope walk.
        PsiManager.getInstance(project).dropResolveCaches()

        val second = resolvedElements()

        assertEquals(describe(first), describe(second))
        assertEquals(first, second)
    }

    /**
     * The comparison `ResolveCache` makes for itself: hand a cached result and a fresh recompute to
     * the same checker that reports a non-idempotent computation, and let it decide they agree.
     */
    fun testCachedAndFreshlyComputedResultsAreEquivalent() {
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference
        val cached = reference.multiResolve(false)

        PsiManager.getInstance(project).dropResolveCaches()

        val fresh = reference.multiResolve(false)

        IdempotenceChecker.checkEquivalence(cached, fresh, reference.javaClass) { fresh }
    }

    /*
     * Private Instance Methods
     */

    private fun resolvedElements(): List<PsiElement> {
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset)

        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        return (reference as PsiPolyVariantReference).multiResolve(false).mapNotNull { it.element }
    }

    /**
     * Guards the fixture itself: if the reference stops resolving through `use SharedWeb`, the paths
     * no longer share anything and the test would pass without exercising what it is written for.
     */
    private fun assertContainsUseCall(elements: List<PsiElement>) {
        assertTrue(
            "Did not resolve through `use`: ${describe(elements)}",
            elements.any { it is Call && Use.`is`(it) }
        )
    }

    private fun describe(elements: List<PsiElement>): String =
        elements.joinToString(prefix = "[", postfix = "]") { it.text.replace('\n', ' ') }

    /*
     * Protected Instance Methods
     */

    override fun setUp() {
        super.setUp()
        myFixture.configureByFiles("reference.ex", "web.ex", "query_one.ex", "query_two.ex")
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/use_shared_alias"
}
