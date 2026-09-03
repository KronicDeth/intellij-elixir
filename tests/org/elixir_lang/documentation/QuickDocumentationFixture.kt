package org.elixir_lang.documentation

import com.intellij.lang.documentation.ide.IdeDocumentationTargetProvider
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.impl.computeDocumentationBlocking
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

/**
 * Renders Quick Documentation for the fixture's caret exactly as pressing Ctrl+Q would, or returns
 * `null` when the IDE would show no documentation (no target resolved, or no docs for the resolved
 * target).
 *
 * Uses the v2 documentation-target API ([IdeDocumentationTargetProvider] +
 * [computeDocumentationBlocking]) - the same entry point the platform's own Quick Doc tests use
 * (e.g. Kotlin's `AbstractFirQuickDocTest`) - rather than the removed-for-`v2` `DocumentationManager`.
 *
 * An extension on the fixture rather than a method on [QuickDocumentationTestCase] so tests that
 * need a different base class - [org.elixir_lang.heex.reference.HeexHostTestCase], which enables
 * `~H` injection - can drive the same pipeline without duplicating it.
 */
@Suppress("UnstableApiUsage")
fun CodeInsightTestFixture.quickDocumentationAtCaret(project: Project): String? {
    val target = IdeDocumentationTargetProvider.getInstance(project)
        .documentationTargets(editor, file, editor.caretModel.offset)
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
