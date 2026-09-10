package org.elixir_lang.beam

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.impl.TestOnlyThreading
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.BinaryLightVirtualFile
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.ui.PrevNextActionsDescriptor
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.tabs.impl.JBTabsImpl
import com.intellij.util.ui.UIUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.BeamBytes.elixirSystemBeam
import javax.swing.SwingConstants
import org.elixir_lang.beam.chunk.code.Component as CodeComponent
import org.elixir_lang.beam.FileEditor as ChunkViewer
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.junit.Assert
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Model as DefinitionsModel
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Tree as DefinitionsTree
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.clause.Panel as ClausePanel
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.Model as TypeSpecificationsModel
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.Panel as TypeSpecificationsPanel
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.Tree as TypeSpecificationsTree
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Model as AbstractCodeModel
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Panel as AbstractCodePanel
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Tree as AbstractCodeTree
import org.elixir_lang.beam.chunk.elixir_documentation.Panel as ElixirDocumentationPanel
import org.elixir_lang.beam.chunk.elixir_documentation.Tree as ElixirDocumentationTree

/**
 * The chunk viewer builds a tab's contents when the tab is selected, on the EDT outside the write-intent lock that
 * the test framework otherwise holds, so without read access. Every one of these parses a scratch file.
 */
class ChunkViewerPanelTest : PlatformTestCase() {
    /** Its controls and its editor are built separately, so the tab is built whole. */
    fun testTheCodeTabBuildsWithoutReadAccess() {
        val cached = CachedBeamReader.from(BinaryLightVirtualFile(PATH, elixirSystemBeam()))!!
        val tabs = TabbedPaneWrapper.createJbTabs(
            project,
            SwingConstants.TOP,
            PrevNextActionsDescriptor(IdeActions.ACTION_NEXT_EDITOR_TAB, IdeActions.ACTION_PREVIOUS_EDITOR_TAB),
            testRootDisposable,
        )
        val code = CodeComponent(cached, project, tabs)

        assertBuildsWithoutReadAccess { code.apply { ensureChildrenAdded() } }
    }

    fun testTheElixirDefinitionsTabBuildsWithoutReadAccess() {
        val tree = DefinitionsTree(DefinitionsModel(elixirDebugInfo()))

        assertBuildsWithoutReadAccess { ClausePanel(tree, project) }
    }

    fun testTheElixirTypeSpecificationsTabBuildsWithoutReadAccess() {
        val tree = TypeSpecificationsTree(TypeSpecificationsModel(elixirDebugInfo()))

        assertBuildsWithoutReadAccess { TypeSpecificationsPanel(tree, project) }
    }

    fun testTheErlangAbstractCodeTabBuildsWithoutReadAccess() {
        val tree = AbstractCodeTree(AbstractCodeModel(erlangDebugInfo()))

        assertBuildsWithoutReadAccess { AbstractCodePanel(tree, project) }
    }

    /** `ExDc` stopped being written in Elixir 1.7, so no SDK the tests run against has one to read. The panel only listens to its tree. */
    fun testTheElixirDocumentationTabBuildsWithoutReadAccess() {
        val tree = ElixirDocumentationTree(null, DefaultTreeModel(DefaultMutableTreeNode()))

        assertBuildsWithoutReadAccess { ElixirDocumentationPanel(tree, project, null) }
    }

    /** The platform calls `getComponent` repeatedly, and what is on screen is the first one. */
    fun testTheViewerKeepsShowingOneComponent() {
        val viewer = ChunkViewer(BinaryLightVirtualFile(PATH, elixirSystemBeam()), project)

        try {
            Assert.assertSame(viewer.component, viewer.component)
        } finally {
            Disposer.dispose(viewer)
        }
    }

    /** The platform reports an editor still open when its project closes, and each tab builds its own. */
    fun testClosingTheViewerReleasesTheEditorsItsTabsCreated() {
        val editorFactory = EditorFactory.getInstance()
        val editorsBefore = editorFactory.allEditors.toSet()
        val viewer = ChunkViewer(BinaryLightVirtualFile(PATH, elixirSystemBeam()), project)

        val created = try {
            val shown = viewer.component
            // The platform asks again while the file stays open, many times per open.
            viewer.component
            val tabs = UIUtil.findComponentOfType(shown, JBTabsImpl::class.java)!!
            tabs.tabs.forEach { tabs.select(it, false) }
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()

            editorFactory.allEditors.toSet() - editorsBefore
        } finally {
            Disposer.dispose(viewer)
        }
        val leaked = created.filterNot { it.isDisposed }
        leaked.forEach(editorFactory::releaseEditor)

        Assert.assertTrue("selecting every tab must build at least the Code and Dbgi editors", created.size >= 2)
        Assert.assertEquals("closing the viewer must release every editor its tabs created", emptyList<Editor>(), leaked)
    }

    private fun assertBuildsWithoutReadAccess(build: () -> JComponent) {
        val editorFactory = EditorFactory.getInstance()
        val editorsBefore = editorFactory.allEditors.toSet()

        try {
            TestOnlyThreading.releaseTheAcquiredWriteIntentLockThenExecuteActionAndTakeWriteIntentLockBack {
                Assert.assertFalse(
                    "a tab must be built as a click builds it, with no read access",
                    ApplicationManager.getApplication().isReadAccessAllowed,
                )

                build()
            }
        } finally {
            (editorFactory.allEditors.toSet() - editorsBefore).forEach(editorFactory::releaseEditor)
        }
    }

    private fun elixirDebugInfo(): V1 =
        BeamReader.read(elixirSystemBeam(), PATH) { it.debugInfo as? V1 }
            ?: throw AssertionError("Elixir.System.beam has no Elixir debug info")

    private fun erlangDebugInfo(): AbstractCodeCompileOptions =
        SdkBeams.forSdk(System.getenv("ERLANG_SDK_HOME"), "erlang").firstNotNullOfOrNull { (_, file) ->
            BeamReader.read(file.readBytes(), file.path) { it.debugInfo as? AbstractCodeCompileOptions }
        } ?: throw AssertionError("no module in ERLANG_SDK_HOME has Erlang abstract code")

    private companion object {
        const val PATH = "Elixir.System.beam"
    }
}
