package org.elixir_lang.credo.inspection_tool

import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.ExternalLanguageAnnotators
import com.intellij.psi.PsiFile
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.credo.Annotator

/**
 * Inlines com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection, which does not exist in all supported versions
 */
class Local: LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val viewProvider = file.viewProvider
        val language = ElixirLanguage

        return viewProvider
                .getPsi(language)
                ?.let { psiRoot ->
                    ExternalLanguageAnnotators
                            .allForFile(language, psiRoot)
                            .filterIsInstance<Annotator>()
                            .firstOrNull()
                            ?.let { annotator ->
                                ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(
                                        file,
                                        manager,
                                        false,
                                        annotator
                                )
                            }
                }
                ?: emptyArray()
    }

    override fun getDisplayName(): String = "Credo"
    override fun getShortName(): String = "Credo"
}
