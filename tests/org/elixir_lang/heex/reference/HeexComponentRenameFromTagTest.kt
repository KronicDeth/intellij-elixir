package org.elixir_lang.heex.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiManager
import org.elixir_lang.code_insight.renameTargetAtCaret
import org.elixir_lang.code_insight.renameTargetsAtCaret
import org.elixir_lang.model.psi.function.FunctionSymbol

/**
 * Rename (Shift+F6) with the caret on a HEEx component tag, in a `.heex` file and inside a `~H`
 * sigil: the tag's [FunctionSymbol] is the single rename target, and renaming updates tag and
 * declaration. Slot and unresolved tags have no target.
 */
class HeexComponentRenameFromTagTest : HeexHostTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/reference/rename_from_tag"

    /** A resolved local component tag (`<.button>`) has exactly one rename target: its `FunctionSymbol`. */
    fun testLocalTagResolvesRenameTarget() {
        myFixture.configureByFiles("local/page_live.html.heex", "local/page_live.ex")
        assertSingleFunctionSymbolTarget()
    }

    /** Same, for a fully-qualified remote tag (`<MyAppWeb.CoreComponents.button>`). */
    fun testRemoteTagResolvesRenameTarget() {
        myFixture.configureByFiles("remote/page_live.html.heex", "remote/core_components.ex")
        assertSingleFunctionSymbolTarget()
    }

    /** Same, for a local tag reached through a `~H` sigil rather than a top-level `.heex` file. */
    fun testLocalTagInsideHSigilResolvesRenameTarget() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("local/page_live.ex"),
                heexBody = myFixture.fixtureText("local/page_live.html.heex")
            )
        )
        assertSingleFunctionSymbolTarget()
    }

    /**
     * Same, for a remote tag reached through a `~H` sigil in a minimal synthetic entrance module -
     * fully-qualified resolution needs no particular entrance.
     */
    fun testRemoteTagInsideHSigilResolvesRenameTarget() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = "defmodule Test do\nend",
                heexBody = myFixture.fixtureText("remote/page_live.html.heex"),
                myFixture.fixtureText("remote/core_components.ex")
            )
        )
        assertSingleFunctionSymbolTarget()
    }

    /** A `<:slot>` tag names a `slot` macro, not a function - no rename target at all. */
    fun testSlotTagYieldsNoRenameTarget() {
        myFixture.configureByFiles("slot_negative/page_live.html.heex", "slot_negative/page_live.ex")
        assertNoRenameTarget()
    }

    /** Same negative case, with the slot tag inside a `~H` sigil instead of a separate `.heex` file. */
    fun testSlotTagInsideHSigilYieldsNoRenameTarget() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("slot_negative/page_live.ex"),
                heexBody = myFixture.fixtureText("slot_negative/page_live.html.heex")
            )
        )
        assertNoRenameTarget()
    }

    /** An unresolved component tag (no matching `def`) yields no rename target either. */
    fun testUnresolvedComponentTagYieldsNoRenameTarget() {
        myFixture.configureByText("unresolved.html.heex", unresolvedTagText)
        assertNoRenameTarget()
    }

    /** Same negative case, with the unresolved tag inside a `~H` sigil in a synthetic entrance module. */
    fun testUnresolvedComponentTagInsideHSigilYieldsNoRenameTarget() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(entranceModuleText = "defmodule Test do\nend", heexBody = unresolvedTagText)
        )
        assertNoRenameTarget()
    }

    /** Renaming from the tag updates both the tag (keeping its `.` prefix) and the declaration. */
    fun testRenamingFromTagUpdatesBothTagAndDeclaration() {
        val files = myFixture.configureByFiles("local/page_live.html.heex", "local/page_live.ex")
        val heexFile = files[0]
        val exFile = files[1]

        myFixture.renameTargetAtCaret("submit_button")

        val renamedHeex = PsiManager.getInstance(project).findFile(heexFile.virtualFile)
        assertNotNull(renamedHeex)
        assertTrue(
            "Expected the tag to read <.submit_button>, got: ${renamedHeex!!.text}",
            renamedHeex.text.contains(".submit_button")
        )

        val renamedEx = PsiManager.getInstance(project).findFile(exFile.virtualFile)
        assertNotNull(renamedEx)
        assertTrue(
            "Expected the declaration to read def submit_button, got: ${renamedEx!!.text}",
            renamedEx.text.contains("def submit_button")
        )
    }

    /** `myFixture.file` may be the injected fragment after the rename; the host file holds both edits. */
    fun testRenamingFromTagInsideHSigilUpdatesBothTagAndDeclaration() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("local/page_live.ex"),
                heexBody = myFixture.fixtureText("local/page_live.html.heex")
            )
        )

        myFixture.renameTargetAtCaret("submit_button")

        val hostText = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file).text
        assertTrue("Expected the tag to read <.submit_button>, got: $hostText", hostText.contains(".submit_button"))
        assertTrue("Expected the declaration to read def submit_button, got: $hostText", hostText.contains("def submit_button"))
    }

    private fun assertSingleFunctionSymbolTarget() {
        val targets = myFixture.renameTargetsAtCaret()
        assertEquals("Expected exactly one rename target at the caret", 1, targets.size)
        assertTrue("Expected the rename target to be a FunctionSymbol, got: ${targets.single()}", targets.single() is FunctionSymbol)
    }

    private companion object {
        const val unresolvedTagText = "<.non<caret>existent>Click</.nonexistent>"
    }
}
