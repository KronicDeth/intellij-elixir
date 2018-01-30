package org.elixir_lang.beam.assembly

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.chunk.Code
import java.awt.event.ItemEvent.DESELECTED
import java.awt.event.ItemEvent.SELECTED
import javax.swing.BoxLayout
import javax.swing.JPanel

private const val DEFAULT_TEXT = "# Could not disassemble Code Chunk"

class Controls(val cache: Cache, val project: Project): JBScrollPane() {
    private var assemblyOptions: Code.Options = Code.Options(
            inline = Code.Options.Inline(
                    atoms = true,
                    functions = true,
                    imports = true,
                    integers = true,
                    labels = true,
                    literals = true,
                    localCalls = true,
                    strings = true
            ),
            showArgumentNames = true
    )
    val document: Document

    init {
        val text = computeDocumentText()
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText(Language, text)

        document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!

        val scrollable = JPanel()
        scrollable.layout = BoxLayout(scrollable, BoxLayout.LINE_AXIS)

        val inlineAtomsCheckBox = JBCheckBox("Inline Atoms", assemblyOptions.inline.atoms)
        inlineAtomsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(atoms = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(atoms = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineAtomsCheckBox)

        val inlineFunctionsCheckBox = JBCheckBox("Inline Functions", assemblyOptions.inline.functions)
        inlineFunctionsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(functions = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(functions = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineFunctionsCheckBox)

        val inlineImportsCheckBox = JBCheckBox("Inline Imports", assemblyOptions.inline.imports)
        inlineImportsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(imports = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(imports = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineImportsCheckBox)

        val inlineIntegersCheckBox = JBCheckBox("Inline Integers", assemblyOptions.inline.integers)
        inlineIntegersCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(integers = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(integers = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineIntegersCheckBox)

        val inlineLabelsCheckBox = JBCheckBox("Inline Labels", assemblyOptions.inline.labels)
        inlineLabelsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(labels = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(labels = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineLabelsCheckBox)

        val inlineLiteralsCheckBox = JBCheckBox("Inline Literals", assemblyOptions.inline.literals)
        inlineLiteralsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(literals = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(literals = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineLiteralsCheckBox)

        val inlineLocalCallsCheckBox = JBCheckBox("Inline Local Calls", assemblyOptions.inline.localCalls)
        inlineLocalCallsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(localCalls = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(localCalls = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineLocalCallsCheckBox)

        val inlineStringsCheckBox = JBCheckBox("Inline Strings", assemblyOptions.inline.strings)
        inlineStringsCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(strings = false))
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(inline = assemblyOptions.inline.copy(strings = true))
            }

            setDocumentText()
        }
        scrollable.add(inlineStringsCheckBox)


        val showArgumentNamesCheckBox = JBCheckBox("Show Argument Names", assemblyOptions.showArgumentNames)
        showArgumentNamesCheckBox.addItemListener { itemEvent ->
            when (itemEvent.stateChange) {
                DESELECTED ->
                    assemblyOptions = assemblyOptions.copy(showArgumentNames = false)
                SELECTED ->
                    assemblyOptions = assemblyOptions.copy(showArgumentNames = true)
            }

            setDocumentText()
        }
        scrollable.add(showArgumentNamesCheckBox)

        setViewportView(scrollable)
    }

    private fun computeDocumentText() = cache.code?.assembly(cache, assemblyOptions) ?: DEFAULT_TEXT

    private fun setDocumentText() {
        ApplicationManager.getApplication().runWriteAction {
            document.setText(computeDocumentText())
        }
    }
}
