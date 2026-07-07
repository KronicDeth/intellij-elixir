package org.elixir_lang.reference.mfa_tuple

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.find.usages.impl.searchTargets
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.MfaFunctionReference
import org.elixir_lang.reference.Module
import java.util.concurrent.Callable

@Suppress("UnstableApiUsage")
class MfaTupleReferenceTest : PlatformTestCase() {
    fun testFunctionResolvesIntegerArity() {
        val reference = mfaFunctionReferenceAtCaret("basic_mfa.ex")

        val resolveResults = reference.multiResolve(false)
        val resolvedName = (resolveResults.single().element as? PsiNamedElement)?.name

        assertEquals(1, resolveResults.size)
        assertTrue(resolveResults.single().isValidResult)
        assertEquals("map", resolvedName)
    }

    fun testResolveReturnsSingleElement() {
        val reference = mfaFunctionReferenceAtCaret("basic_mfa.ex")

        val resolved = reference.resolve()
        assertNotNull(resolved)
        assertEquals("map", (resolved as? PsiNamedElement)?.name)
    }

    fun testResolveHandlesMultiClause() {
        val reference = mfaFunctionReferenceAtCaret("multi_clause.ex")

        // multiResolve returns one result per clause
        val resolveResults = reference.multiResolve(false)
        assertTrue("Expected multiple clauses", resolveResults.size > 1)
        assertTrue(resolveResults.all { it.isValidResult })
        assertTrue(resolveResults.all { (it.element as? PsiNamedElement)?.name == "process" })

        // resolve() must return non-null even with multiple clauses - preferred target
        val resolved = reference.resolve()
        assertNotNull("resolve() must not return null for multi-clause functions", resolved)
        assertEquals("process", (resolved as? PsiNamedElement)?.name)
    }

    fun testGetVariantsReturnsEmpty() {
        val reference = mfaFunctionReferenceAtCaret("basic_mfa.ex")

        assertEmpty(reference.variants.toList())
    }

    fun testFunctionResolvesArgsListArity() {
        val reference = mfaFunctionReferenceAtCaret("supervisor_child_spec.ex")

        val resolveResults = reference.multiResolve(false)
        val resolvedName = (resolveResults.single().element as? PsiNamedElement)?.name

        assertEquals(1, resolveResults.size)
        assertTrue(resolveResults.single().isValidResult)
        assertEquals("start_link", resolvedName)
    }

    fun testFunctionResolvesEmptyArgsListArity() {
        val reference = mfaFunctionReferenceAtCaret("empty_args_list.ex")

        val resolveResults = reference.multiResolve(false)
        val resolvedName = (resolveResults.single().element as? PsiNamedElement)?.name

        assertEquals(1, resolveResults.size)
        assertTrue(resolveResults.single().isValidResult)
        assertEquals("run", resolvedName)
    }

    fun testFunctionResolvesInInlineDoBody() {
        val reference = mfaFunctionReferenceAtCaret("inline_do_body.ex")

        val resolveResults = reference.multiResolve(false)
        val resolvedName = (resolveResults.single().element as? PsiNamedElement)?.name

        assertEquals(1, resolveResults.size)
        assertTrue(resolveResults.single().isValidResult)
        assertEquals("map", resolvedName)
    }

    fun testFunctionResolvesInBlockDoBody() {
        val reference = mfaFunctionReferenceAtCaret("block_do_body.ex")

        val resolveResults = reference.multiResolve(false)
        val resolvedName = (resolveResults.single().element as? PsiNamedElement)?.name

        assertEquals(1, resolveResults.size)
        assertTrue(resolveResults.single().isValidResult)
        assertEquals("map", resolvedName)
    }

    fun testModuleAliasStillResolves() {
        myFixture.configureByFile("module_reference.ex")

        val reference = myFixture.getReferenceAtCaretPositionWithAssertion()

        assertInstanceOf(reference, Module::class.java)
        assertNotNull(reference.resolve())
    }

    fun testArityMismatchResolvesWithInvalidResult() {
        val reference = mfaFunctionReferenceAtCaret("arity_mismatch.ex")

        val resolveResults = reference.multiResolve(false)
        assertEquals(1, resolveResults.size)
        assertFalse(
            "Arity mismatch should resolve with isValidResult=false for navigation, not as a valid result",
            resolveResults.single().isValidResult
        )
    }

    fun testNonMfaTupleHasNoMfaReference() {
        myFixture.configureByFile("non_mfa_tuple.ex")

        assertNull(mfaFunctionReferenceAtCaretOrNull())
    }

    fun testQuotedAtomHasNoMfaReference() {
        myFixture.configureByFile("quoted_atom.ex")

        assertNull(mfaFunctionReferenceAtCaretOrNull())
    }

    fun testVariableModuleHasNoMfaReference() {
        myFixture.configureByFile("variable_module.ex")

        assertNull(mfaFunctionReferenceAtCaretOrNull())
    }

    fun testBareAtomHasNoMfaReference() {
        myFixture.configureByFile("bare_atom.ex")

        assertNull(mfaFunctionReferenceAtCaretOrNull())
    }

    fun testAtomInListHasNoMfaReference() {
        myFixture.configureByFile("atom_in_list.ex")

        assertNull(mfaFunctionReferenceAtCaretOrNull())
    }

    fun testVariableArityResolvesWithInvalidResult() {
        val reference = mfaFunctionReferenceAtCaret("variable_arity.ex")

        val resolveResults = reference.multiResolve(false)
        assertTrue("Expected variable arity to produce resolve results", resolveResults.isNotEmpty())
        assertFalse(
            "Variable arity (arity=-1 sentinel) should resolve with isValidResult=false for navigation",
            resolveResults.first().isValidResult
        )
    }

    fun testDynamicThirdElementResolvesWithInvalidResult() {
        val reference = mfaFunctionReferenceAtCaret("dynamic_third_element.ex")

        val resolveResults = reference.multiResolve(false)
        assertTrue("Expected dynamic third element to produce resolve results via name-only fallback", resolveResults.isNotEmpty())
        assertFalse(
            "Dynamic third element (arity=-1 sentinel) should resolve with isValidResult=false for navigation",
            resolveResults.first().isValidResult
        )
        assertEquals("by_id", (resolveResults.first().element as? PsiNamedElement)?.name)
    }

    fun testWildcardThirdElementResolvesWithInvalidResult() {
        val reference = mfaFunctionReferenceAtCaret("wildcard_third_element.ex")

        val resolveResults = reference.multiResolve(false)
        assertTrue("Expected wildcard third element to produce resolve results via name-only fallback", resolveResults.isNotEmpty())
        assertFalse(
            "Wildcard third element (arity=-1 sentinel) should resolve with isValidResult=false for navigation",
            resolveResults.first().isValidResult
        )
        assertEquals("start_link", (resolveResults.first().element as? PsiNamedElement)?.name)
    }

    fun testMissingTargetReferenceIsSoft() {
        val reference = mfaFunctionReferenceAtCaret("unresolvable_module.ex")

        assertTrue(reference.isSoft)
        assertEmpty(reference.multiResolve(false))
    }

    /**
     * `{:ok, :error, \[reason]}` is a common Elixir tagged-value tuple, not an MFA tuple.
     * However, with Erlang-style support (unquoted atom at element[0]), the provider accepts
     * `:ok` as a potential Erlang module name.  Since `:ok` resolves to no module, the
     * reference is soft and returns empty results - no false error highlighting.
     *
     * This documents the accepted false-positive shape: syntactically matches Erlang-style
     * MFA but silently produces no results.  `isSoft = true` is the guard against UI noise.
     */
    fun testTaggedValueTupleIsAcceptedAsSoftErlangStyleMfa() {
        val reference = mfaFunctionReferenceAtCaret("tagged_value_tuple.ex")

        assertTrue("Tagged-value MFA-shaped tuple reference must be soft", reference.isSoft)
        assertTrue(
            "Tagged-value tuple {ok, error, [reason]} must resolve to nothing - :ok is not a real module",
            reference.multiResolve(false).isEmpty()
        )
    }

    fun testFindUsagesIncludesMfaTupleReference() {
        val reference = mfaFunctionReferenceAtCaret("find_usages.ex")

        // Resolve the MFA reference to the function definition
        val resolveResults = reference.multiResolve(false)
        assertTrue("Expected MFA reference to resolve", resolveResults.isNotEmpty())
        assertTrue(resolveResults.first().isValidResult)
        val functionDefinition = resolveResults.first().element!!

        // Find usages through SearchTarget routing (Symbol API path).
        val usages = findUsages(functionDefinition)

        // The MFA tuple atom should be among them
        val usageElements = usages.mapNotNull { usage -> usage.file.findElementAt(usage.range.startOffset) }
        val hasMfaTupleAtomUsage = usages.any { usage ->
            if (usage.file.name != "find_usages.ex") return@any false
            val start = usage.range.startOffset
            if (start <= 0) return@any false
            usage.file.text.getOrNull(start - 1) == ':'
        }
        assertTrue(
            "Expected MFA tuple reference in Find Usages results, got: ${usageElements.map { it.text }}",
            hasMfaTupleAtomUsage
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/mfa_tuple"

    private fun mfaFunctionReferenceAtCaret(fileName: String): MfaFunctionReference {
        myFixture.configureByFile(fileName)

        return mfaFunctionReferenceAtCaretOrNull() ?: error("Expected MfaFunctionReference at caret")
    }

    private fun mfaFunctionReferenceAtCaretOrNull(): MfaFunctionReference? {
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset) ?: return null
        val atom = PsiTreeUtil.getParentOfType(elementAtCaret, ElixirAtom::class.java, false) ?: return null

        return PsiReferenceService.getService().getReferences(atom, PsiReferenceService.Hints.NO_HINTS)
            .filterIsInstance<MfaFunctionReference>()
            .singleOrNull()
    }

    private fun findUsages(element: PsiElement): List<PsiUsage> {
        val file = element.containingFile
        val offset = (element as? Call)
            ?.takeIf { CallDefinitionClause.`is`(it) }
            ?.let { CallDefinitionClause.nameIdentifier(it)?.textRange?.startOffset }
            ?: element.textRange.startOffset
        val allOptions = AllSearchOptions(
            UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
            textSearch = false
        )

        return ApplicationManager.getApplication().executeOnPooledThread(Callable<List<PsiUsage>> {
            ReadAction.nonBlocking(Callable<List<PsiUsage>> {
                val targets = searchTargets(file, offset)
                assertTrue("Expected at least one search target at offset $offset", targets.isNotEmpty())
                targets
                    .flatMap { buildQuery(project, it, allOptions).findAll() }
                    .filterIsInstance<PsiUsage>()
            }).executeSynchronously()
        }).get()
    }
}
