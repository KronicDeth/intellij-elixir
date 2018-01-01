package org.elixir_lang.beam

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.atoms.ScrollPane
import java.beans.PropertyChangeListener
import java.nio.charset.Charset
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane

private fun addTab(tabbedPane: JBTabbedPane, chunk: Chunk) {
    val typeID = chunk.typeID
    val component: JComponent = when (typeID) {
        Chunk.TypeID.ATOM.toString() ->
            ScrollPane(Atoms.from(chunk, Chunk.TypeID.ATOM, Charset.forName("LATIN1")))
        Chunk.TypeID.ATU8.toString() ->
            ScrollPane(Atoms.from(chunk, Chunk.TypeID.ATU8, Charset.forName("UTF-8")))
        else ->
            JPanel()
    }

    tabbedPane.addTab(typeID, component)
}

class FileEditor(private val virtualFile: VirtualFile): UserDataHolderBase(), FileEditor {
    private var isActive: Boolean = false

    // GUI
    private lateinit var rootTabbedPane: JBTabbedPane
    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = null
    override fun getComponent(): JComponent {
        rootTabbedPane = JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT)

        Beam.from(virtualFile).orElse(null)?.let { beam ->
            beam.chunkCollection().forEach { chunk ->
                addTab(rootTabbedPane, chunk)
            }
        }

        return rootTabbedPane
    }

    override fun getCurrentLocation(): FileEditorLocation? = null
    override fun getName(): String = "BEAM Chunks"
    override fun getPreferredFocusedComponent(): JComponent? = rootTabbedPane
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
        Disposer.dispose(this)
    }

    override fun setState(state: FileEditorState) {}
}
