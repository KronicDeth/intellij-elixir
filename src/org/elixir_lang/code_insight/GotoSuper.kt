package org.elixir_lang.code_insight

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.Implementation
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallList
import org.elixir_lang.psi.stub.index.ModularName


class GotoSuper : CodeInsightActionHandler {
    override fun startInWriteAction(): Boolean = false

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        file
                .findElementAt(editor.caretModel.offset)
                ?.let { focusedElement -> PsiTreeUtil.getNonStrictParentOfType(focusedElement, Call::class.java) }
                ?.let { invoke(project, editor, it) }
    }

    fun invoke(project: Project, editor: Editor, call: Call) {
        val enclosingModularMacroCallSet = call
                .reference
                ?.let { it as PsiPolyVariantReference }
                ?.multiResolve(false)
                ?.asSequence()
                .orEmpty()
                .mapNotNull(ResolveResult::getElement)
                .filterIsInstance<Call>()
                .filter { CallDefinitionClause.`is`(it) }
                .mapNotNull(::enclosingModularMacroCall)
                .toSet()

        val protocolNameSet = enclosingModularMacroCallSet
                .asSequence()
                .filter(Implementation::`is`)
                .mapNotNull(Implementation::protocolName)
                .toSet()

        if (protocolNameSet.isNotEmpty()) {
            val globalSearchScope = GlobalSearchScope.everythingScope(project)

            val targets = protocolNameSet
                    .asSequence()
                    .flatMap { protocolName ->
                        StubIndex
                                .getElements(
                                        ModularName.KEY,
                                        protocolName,
                                        project,
                                        globalSearchScope,
                                        NamedElement::class.java
                                )
                                .asSequence()
                                .filterIsInstance<Call>()
                    }
                    .flatMap { defprotocol ->
                        defprotocol.macroChildCallList().asSequence()
                    }
                    .filter(CallDefinitionClause::`is`)
                    .filter { protocolCallDefinitionClause ->
                        nameArityInterval(protocolCallDefinitionClause, ResolveState.initial())
                                ?.let { protocolNameArityInterval ->
                                    protocolNameArityInterval.name == call.functionName() &&
                                            protocolNameArityInterval.arityInterval.contains(call.resolvedFinalArity())
                                }
                                ?: false
                    }
                    .toList()
                    .toTypedArray()

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
