package org.elixir_lang.code_insight

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.Implementation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.semantic


class GotoSuper : CodeInsightActionHandler {
    override fun startInWriteAction(): Boolean = false

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        file
                .findElementAt(editor.caretModel.offset)
                ?.let { focusedElement -> PsiTreeUtil.getNonStrictParentOfType(focusedElement, Call::class.java) }
                ?.let { invoke(editor, it) }
    }

    fun invoke(editor: Editor, call: Call) {
        val name = call.functionName()
        val arity = call.resolvedFinalArity()

        val targets = call
                .reference
                ?.let { it as PsiPolyVariantReference }
                ?.multiResolve(false)
                ?.asSequence()
                .orEmpty()
                .mapNotNull(ResolveResult::getElement)
                .mapNotNull(PsiElement::semantic)
                .filterIsInstance<Clause>()
                .map(Clause::definition)
                .map(Definition::enclosingModular)
                .filterIsInstance<Implementation>()
                .flatMap { it.protocols.asSequence() }
                .flatMap { it.exportedCallDefinitions.asSequence() }
                .filter { definition ->
                    definition.nameArityInterval?.let { nameArityInterval ->
                        nameArityInterval.name == name && nameArityInterval.arityInterval.contains(arity)
                    } ?: false
                }
                .flatMap { definition ->
                    definition.clauses.asSequence()
                }
                .map(Clause::psiElement)
                .map(PsiElement::getNavigationElement)
                .filterIsInstance<NavigatablePsiElement>()
                .toSet()
                .toTypedArray()

        if (targets.isNotEmpty()) {
            PsiElementListNavigator.openTargets(
                    editor,
                    targets,
                    "Choose Protocol Function",
                    "Protocol Functions of ${call.functionName()}/${call.resolvedFinalArity()}",
                    DefaultPsiElementCellRenderer()
            )
        }
    }
}
