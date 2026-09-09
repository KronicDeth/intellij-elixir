package org.elixir_lang.code_insight.completion.contributor

import com.intellij.codeInsight.completion.CompletionType
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * A lookup element must point at the user's file, never at the completion copy.
 *
 * A modular in another file resolves through the stub index and cannot leak. A same-file sibling is mapped
 * by `getOriginalOrSelf` alone; only the enclosing and swallowed declarations need more than that.
 * Assert on `isPhysical`: a copy's `virtualFile` is null, not a `LightVirtualFile`, so the obvious check
 * passes on a leaked copy.
 */
class CompletionCopyLeakTest : PlatformTestCase() {
    fun testLookupElementPointsAtTheRealFileForASameFileModular() {
        myFixture.configureByFiles("same_file_nested_usage.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val element = myFixture.lookupElements.orEmpty().firstOrNull { it.lookupString == "inner_function" }

        assertNotNull(
            "Completion should offer inner_function from the same file, got: ${myFixture.lookupElementStrings}",
            element
        )

        val containingFile = element!!.psiElement?.containingFile

        assertNotNull("Lookup element lost its PSI element", containingFile)
        assertTrue(
            "Lookup element points at the throwaway completion copy rather than the user's file " +
                "(isPhysical=${containingFile!!.isPhysical}, virtualFile=${containingFile.virtualFile})",
            containingFile.isPhysical
        )
    }

    fun testLookupElementPointsAtTheRealFileForASameFileModularBelowTheCaret() {
        myFixture.configureByFiles("same_file_nested_after_caret_usage.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val element = myFixture.lookupElements.orEmpty().firstOrNull { it.lookupString == "inner_function" }

        assertNotNull(
            "Completion should offer inner_function from the same file, got: ${myFixture.lookupElementStrings}",
            element
        )

        val containingFile = element!!.psiElement?.containingFile

        assertNotNull("Lookup element lost its PSI element", containingFile)
        assertTrue(
            "Lookup element points at the throwaway completion copy rather than the user's file " +
                "(isPhysical=${containingFile!!.isPhysical}, virtualFile=${containingFile.virtualFile})",
            containingFile.isPhysical
        )
    }

    /** The enclosing modular, whose range spans the dummy identifier, unlike the two sibling cases above. */
    fun testLookupElementPointsAtTheRealFileForTheEnclosingModular() {
        myFixture.configureByFiles("same_file_enclosing_usage.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val offered = myFixture.lookupElementStrings.orEmpty()

        assertTrue(
            "Every function of the enclosing module should be offered, got: $offered",
            offered.containsAll(listOf("before_function", "run", "after_function", "delegated"))
        )

        myFixture.lookupElements.orEmpty().forEach { element ->
            val containingFile = element.psiElement?.containingFile

            assertNotNull("Lookup element ${element.lookupString} lost its PSI element", containingFile)
            assertTrue(
                "Lookup element ${element.lookupString} points at the throwaway completion copy rather " +
                    "than the user's file (isPhysical=${containingFile!!.isPhysical}, " +
                    "virtualFile=${containingFile.virtualFile})",
                containingFile.isPhysical
            )
        }

        // `run` encloses the caret, so it is the one that needs mapping rather than an early return. It
        // must come back as its clause, which is what the renderer and Quick Documentation read; the leaf
        // at the same offset would be physical too and tell us nothing.
        val enclosing = myFixture.lookupElements.orEmpty().first { it.lookupString == "run" }

        assertTrue(
            "run points at ${enclosing.psiElement?.javaClass?.simpleName}, not its declaration",
            (enclosing.psiElement as? Call)?.let { org.elixir_lang.psi.CallDefinitionClause.`is`(it) } == true
        )
    }

    /**
     * A declaration the trailing dot swallowed still points at its own position in the user's file.
     *
     * There is no clause node for it in the original tree, so the assertion is on where the element sits,
     * not what it is. `def abc` starts exactly one dummy identifier before `def b`, so an untranslated
     * copy offset lands on `b` and reports it as `abc`.
     */
    fun testLookupElementPointsAtTheSwallowedDeclarationsOwnPosition() {
        myFixture.configureByFiles("same_file_shifted_sibling_usage.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val element = myFixture.lookupElements.orEmpty().firstOrNull { it.lookupString == "abc" }

        assertNotNull("Completion should offer abc, got: ${myFixture.lookupElementStrings}", element)

        val target = element!!.psiElement

        assertNotNull("Lookup element lost its PSI element", target)
        assertTrue(
            "Lookup element for abc points at the throwaway completion copy " +
                "(isPhysical=${target!!.containingFile.isPhysical})",
            target.containingFile.isPhysical
        )

        val offset = target.textRange.startOffset

        assertTrue(
            "Lookup element for abc points at offset $offset, which is " +
                "\"${myFixture.file.text.drop(offset).take(16)}\"",
            myFixture.file.text.startsWith("def abc", offset)
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause"
}
