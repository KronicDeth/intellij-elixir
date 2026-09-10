package org.elixir_lang.beam

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.PrevNextActionsDescriptor
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.Table
import org.elixir_lang.beam.chunk.lines.TabbedPane
import java.beans.PropertyChangeListener
import javax.swing.*

class FileEditor(
        private val virtualFile: VirtualFile,
        private val project: Project
): UserDataHolderBase(), FileEditor {
    private var isActive: Boolean = false

    // The platform asks for the component many times per open, so the tabs are built once.
    private val rootTabbedPaneLazy = lazy {
        val descriptor = PrevNextActionsDescriptor(IdeActions.ACTION_NEXT_EDITOR_TAB, IdeActions.ACTION_PREVIOUS_EDITOR_TAB)

        TabbedPaneWrapper.createJbTabs(project, SwingConstants.TOP, descriptor, this).also { tabbedPane ->
            CachedBeamReader.from(virtualFile)?.let { cache ->
                cache.chunkCollection().forEach { chunk ->
                    addTab(tabbedPane, cache, chunk)
                }
            }
        }
    }
    private val rootTabbedPane by rootTabbedPaneLazy

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = null
    override fun getComponent(): JComponent = rootTabbedPane.component

    override fun getCurrentLocation(): FileEditorLocation? = null
    override fun getFile(): VirtualFile = virtualFile
    override fun getName(): String = "BEAM Chunks"
    override fun getPreferredFocusedComponent(): JComponent? = rootTabbedPane.component
    override fun isModified(): Boolean = false
    override fun isValid() = true

    override fun selectNotify() {
        isActive = true
//
//        if (isObsolete) {
//            update()
//        }
    }

    override fun deselectNotify() {
        isActive = true
    }

    override fun dispose() {
        if (rootTabbedPaneLazy.isInitialized()) {
            releaseTabEditors()
        }

        Disposer.dispose(this)
    }

    /** Each tab creates its editors when first selected, and a tab that is not selected is detached from the hierarchy. */
    private fun releaseTabEditors() {
        val tabComponents = (0 until rootTabbedPane.tabCount).map(rootTabbedPane::getComponentAt)
        val editorFactory = EditorFactory.getInstance()

        editorFactory.allEditors
            .filter { editor -> tabComponents.any { SwingUtilities.isDescendingFrom(editor.component, it) } }
            .forEach(editorFactory::releaseEditor)
    }

    override fun setState(state: FileEditorState) {}

    private fun addTab(tabbedPaneWrapper: TabbedPaneWrapper, cache: CachedBeamReader, chunk: Chunk) {
        val typeID = chunk.typeID
        val component: JComponent = when (typeID) {
            Chunk.TypeID.ATOM.toString(), Chunk.TypeID.ATU8.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.atoms.Model(cache.atoms)))
            Chunk.TypeID.ATTR.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.keyword.Model(cache.attributes)))
            Chunk.TypeID.CINF.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.keyword.Model(cache.compileInfo)))
            Chunk.TypeID.CODE.toString() ->
                org.elixir_lang.beam.chunk.code.Component(cache, project, tabbedPaneWrapper)
            Chunk.TypeID.DBGI.toString() ->
                org.elixir_lang.beam.chunk.debug_info.term.component(cache.debugInfo, project, tabbedPaneWrapper)
            Chunk.TypeID.EXDC.toString() ->
                org.elixir_lang.beam.chunk.elixir_documentation.component(
                        cache.elixirDocumentation,
                        project,
                        cache.atoms?.moduleName(),
                        tabbedPaneWrapper
                )
            Chunk.TypeID.EXPT.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.call_definitions.Model(cache.exports)))
            Chunk.TypeID.FUNT.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.functions.Model(cache.functions)))
            Chunk.TypeID.IMPT.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.imports.Model(cache.imports)))
            Chunk.TypeID.LINE.toString() ->
                TabbedPane(cache.lines)
            Chunk.TypeID.LITT.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.literals.Model(cache.literals)))
            Chunk.TypeID.LOCT.toString() ->
                JBScrollPane(Table(org.elixir_lang.beam.chunk.call_definitions.Model(cache.locals)))
            Chunk.TypeID.STRT.toString() -> {
                val table = Table(org.elixir_lang.beam.chunk.strings.Model(cache.strings, cache.code))
                table.emptyText.text = "String pool is empty"
                JBScrollPane(table)
            }
            else ->
                JBScrollPane(JPanel())
        }

        tabbedPaneWrapper.addTab(typeID, component)
    }
}
