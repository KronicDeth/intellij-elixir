package org.elixir_lang.beam.file_editor

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.FileEditor
import org.elixir_lang.beam.FileType
import org.elixir_lang.beam.Language

class Provider: com.intellij.openapi.fileEditor.FileEditorProvider {
    override fun getEditorTypeId(): String = "BEAM"

    override fun accept(project: Project, file: VirtualFile): Boolean =
            file.fileType is FileType ||
                    try {
                        ScratchFileService.getInstance().scratchesMapping.getMapping(file) is Language
                    } catch (e: Throwable) {
                        false
                    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return FileEditor(file)
    }

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
