package org.elixir_lang.beam

import com.intellij.lang.Language
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteIntentReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory

// The chunk viewer builds a tab when it is selected, and since 2026.1 a click holds no lock, so each of these takes
// the lock it needs and no more.

/** Creating the file parses [text]. */
internal fun scratchDocument(project: Project, language: Language, text: String): Document =
    ReadAction.computeBlocking<Document, RuntimeException> {
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText(language, text)

        PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    }

/** `EditorImpl` takes the write-intent lock to set its highlighter, which a read action forbids. */
@Suppress("UnstableApiUsage")
internal fun viewerEditor(document: Document, project: Project, fileType: FileType): Editor =
    WriteIntentReadAction.compute {
        EditorFactory.getInstance().createEditor(document, project, fileType, true)
    }
