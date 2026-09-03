package org.elixir_lang.documentation

import com.intellij.lang.documentation.ide.IdeDocumentationTargetProvider
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
    /** @see quickDocumentationAtCaret */
    protected fun quickDocumentationAtCaret(): String? = myFixture.quickDocumentationAtCaret(project)
}
