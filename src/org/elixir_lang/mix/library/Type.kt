package org.elixir_lang.mix.library

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.DummyLibraryProperties
import com.intellij.openapi.roots.libraries.LibraryType
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.Icons
import javax.swing.Icon
import javax.swing.JComponent

class Type : LibraryType<DummyLibraryProperties>(Kind) {
    override fun getCreateActionName(): String? = null
    override fun createNewLibrary(parentComponent: JComponent, contextDirectory: VirtualFile?, project: Project): NewLibraryConfiguration? = null
    override fun createPropertiesEditor(editorComponent: LibraryEditorComponent<DummyLibraryProperties>): LibraryPropertiesEditor? = null

    override fun detect(classesRoots: List<VirtualFile>): DummyLibraryProperties? =
        if (classesRoots.any { it.name in CLASS_ROOT_NAMES }) {
            DummyLibraryProperties.INSTANCE
        } else {
            null
        }

    override fun getIcon(properties: DummyLibraryProperties?): Icon = Icons.LIBRARY
}

private val CLASS_ROOT_NAMES = arrayOf("consolidated", "ebin")
