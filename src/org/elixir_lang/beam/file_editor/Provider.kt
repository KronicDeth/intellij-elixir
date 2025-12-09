package org.elixir_lang.beam.file_editor

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.FileEditor
import org.elixir_lang.beam.FileType
import org.elixir_lang.beam.Language

class Provider : FileEditorProvider, DumbAware {
    override fun getEditorTypeId(): String = "BEAM"

    override fun accept(project: Project, file: VirtualFile): Boolean =
        file.fileType is FileType ||
                try {
                    ScratchFileService.getInstance().scratchesMapping.getMapping(file) is Language
                } catch (_: Throwable) {
                    false
                }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return FileEditor(file, project)
    }

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
