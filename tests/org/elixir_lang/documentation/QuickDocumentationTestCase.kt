package org.elixir_lang.documentation

import com.intellij.lang.documentation.ide.IdeDocumentationTargetProvider
import com.intellij.platform.backend.documentation.impl.computeDocumentationBlocking
import org.elixir_lang.PlatformTestCase

/**
 * Base class for Quick Documentation (Ctrl+Q) tests that drive the IDE's real documentation
 * pipeline the way a user does - by resolving the documentation target for the current caret
 * through the platform's target-selection chain and rendering it with the registered
 * documentation provider - rather than hand-resolving a [com.intellij.psi.PsiReference] in the test.
 *
 * [IdeDocumentationTargetProvider.documentationTargets] runs the exact offset-based chain Ctrl+Q
 * uses (custom documentation element -> `TargetElementUtil` -> reference at offset, bridged to a
 * [com.intellij.platform.backend.documentation.DocumentationTarget]). Because the test never
 * resolves references itself, it keeps guarding the *user-visible* behaviour across resolution
 * refactors (e.g. relocating or replacing the classic call reference) as long as the user still
 * sees the correct documentation - it locks behaviour, not implementation.
 */
@Suppress("UnstableApiUsage")
abstract class QuickDocumentationTestCase : PlatformTestCase() {
    /**
     * Renders Quick Documentation for the current caret exactly as pressing Ctrl+Q would, or returns
     * `null` when the IDE would show no documentation (no target resolved, or no docs for the
     * resolved target).
     *
     * Uses the v2 documentation-target API ([IdeDocumentationTargetProvider] +
     * [computeDocumentationBlocking]) - the same entry point the platform's own Quick Doc tests use
     * (e.g. Kotlin's `AbstractFirQuickDocTest`) - rather than the removed-for-`v2`
     * `DocumentationManager`.
     */
    protected fun quickDocumentationAtCaret(): String? {
        val editor = myFixture.editor
        val file = myFixture.file
        val offset = editor.caretModel.offset
        val target = IdeDocumentationTargetProvider.getInstance(project)
            .documentationTargets(editor, file, offset)
            .firstOrNull()
            ?: return null
        // DocumentationTarget is @ApiStatus.OverrideOnly, so calling createPointer() on the
        // interface-typed target trips the OverrideOnly inspection. The call is unavoidable and
        // sanctioned: it dispatches to the concrete platform target's override, and the @TestOnly
        // helper's contract is `computeDocumentationBlocking(target.createPointer())` - the exact
        // form the platform's own Quick Doc tests (Kotlin AbstractFirQuickDocTest, DevKit
        // XmlDescriptorDocumentationProviderTest) use.
        @Suppress("OverrideOnly")
        val pointer = target.createPointer()
        return computeDocumentationBlocking(pointer)?.html
    }
}
