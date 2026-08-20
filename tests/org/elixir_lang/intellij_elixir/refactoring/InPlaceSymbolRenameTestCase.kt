package org.elixir_lang.intellij_elixir.refactoring

import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.PlatformTestUtil
import org.elixir_lang.PlatformTestCase

/**
 * Base class for rename tests that drive the IDE's **in-place (Shift+F6)** rename exactly the way a
 * user does - by dispatching the real editor actions and typing real keystrokes - rather than
 * calling the programmatic Symbol engine or manipulating the live template's internal state.
 *
 * The programmatic Symbol engine (`myFixture.renameTarget` → `RenameKt.renameAndWait`) is the engine
 * one layer *below* the handler: it never selects a rename handler, never builds an inline template,
 * and never runs the platform's asynchronous template-commit (`performRename`). As a result it cannot
 * catch defects in the in-place path - e.g. a symbol [com.intellij.model.Pointer] whose range marker
 * collapses when the inline template replaces the whole identifier, which makes the asynchronous
 * commit edit the wrong (empty) range even though the live template preview looks correct. These
 * tests are therefore the sole guard on that path.
 *
 * This case instead reproduces the real user gesture with nothing but user-level actions:
 *
 *  1. press **Shift+F6** (the Rename action), which starts the inline live template,
 *  2. **type** the new name (real keystrokes; the first replaces the selected identifier),
 *  3. press **Enter**, which commits the inline template,
 *  4. wait for the platform's asynchronous rename commit to settle.
 */
@Suppress("UnstableApiUsage")
abstract class InPlaceSymbolRenameTestCase : PlatformTestCase() {
    /**
     * Performs an in-place rename of the symbol under the caret by pressing Shift+F6, typing
     * [newName], and pressing Enter - then waits for the platform's asynchronous commit to settle.
     *
     * The only test-framework concession is [TemplateManagerImpl.setTemplateTesting], which runs the
     * live-template subsystem headlessly (no popup/focus). It does not alter rename behaviour: the
     * template, its segments, and the asynchronous commit all run exactly as they do in the IDE.
     */
    protected fun inPlaceRenameAtCaret(newName: String) {
        TemplateManagerImpl.setTemplateTesting(testRootDisposable)

        ApplicationManager.getApplication().invokeAndWait {
            // Press Shift+F6 - starts the inline rename live template with the identifier selected.
            myFixture.performEditorAction(IdeActions.ACTION_RENAME)

            // Type the new name - real keystrokes; the first character replaces the selected identifier.
            myFixture.type(newName)

            // Press Enter - commits the inline template, triggering the asynchronous rename commit.
            myFixture.type('\n')

            // The platform applies the rename to all usages on a background commit; let it settle.
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
            PlatformTestUtil.waitForAllBackgroundActivityToCalmDown()
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
        }
    }
}
