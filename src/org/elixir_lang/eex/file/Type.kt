package org.elixir_lang.eex.file

import com.google.common.collect.Iterables
import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.eex.Icons
import org.elixir_lang.eex.TemplateHighlighter
import java.util.*
import java.util.stream.Collectors
import javax.swing.Icon

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/file/HbFileType.java
open class Type protected constructor(lang: Language? = org.elixir_lang.eex.Language.INSTANCE) : LanguageFileType(lang!!), TemplateLanguageFileType {
    override fun getName(): String = "Embedded Elixir"
    override fun getDescription(): String = "Embedded Elixir file"
    override fun getDefaultExtension(): String = DEFAULT_EXTENSION
    override fun getIcon(): Icon? = Icons.FILE

    companion object {
        private const val DEFAULT_EXTENSION = "eex"

        @JvmField
        val INSTANCE: LanguageFileType = Type()
        private fun templateDataFileTypeSet(virtualFile: VirtualFile): Set<FileType> {
            val path = virtualFile.path
            val pathLength = path.length
            val fileTypeManager = FileTypeManager.getInstance()
            return fileTypeManager
                    .getAssociations(virtualFile.fileType)
                    .stream()
                    .filter { obj: FileNameMatcher? -> ExtensionFileNameMatcher::class.java.isInstance(obj) }
                    .map { obj: FileNameMatcher? -> ExtensionFileNameMatcher::class.java.cast(obj) }
                    .map { obj: ExtensionFileNameMatcher -> obj.extension }
                    .map { extension: String -> ".$extension" }
                    .filter { suffix: String? -> path.endsWith(suffix!!) }
                    .map { dotExtension: String -> path.substring(0, pathLength - dotExtension.length) }
                    .map { fileName: String? -> fileTypeManager.getFileTypeByFileName(fileName!!) }
                    .collect(Collectors.toSet())
        }

        @JvmStatic
        fun onlyTemplateDataFileType(virtualFile: VirtualFile): Optional<FileType> {
            val typeSet = templateDataFileTypeSet(virtualFile)
            val optionalType: Optional<FileType>
            optionalType = if (typeSet.size == 1) {
                val type = Iterables.getOnlyElement(typeSet)
                if (type === FileTypes.UNKNOWN) {
                    Optional.empty()
                } else {
                    Optional.of(type)
                }
            } else {
                Optional.empty()
            }
            return optionalType
        }
    }

    init {
        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(
                this,
                EditorHighlighterProvider { project: Project?, fileType: FileType?, virtualFile: VirtualFile?, editorColorsScheme: EditorColorsScheme? -> TemplateHighlighter(project, virtualFile, editorColorsScheme!!) }
        )
    }
}
