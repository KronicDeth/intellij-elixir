package org.elixir_lang.mix

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.NameArity
import org.elixir_lang.package_manager.DepGatherer
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.CallDefinitionClause.isFunction
import org.elixir_lang.psi.CallDefinitionClause.isPublicFunction
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.foldChildrenWhile
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.stripAccessExpression

class DepGatherer : DepGatherer() {
    override fun visitFile(file: PsiFile?) {
        if (file is ElixirFile) {
            file.acceptChildren(this)
        }
    }

    override fun visitElement(element: PsiElement?) {
        if (element is Call && org.elixir_lang.structure_view.element.modular.Module.`is`(element)) {
            val childCalls = element.macroChildCalls()

            childCalls
                    .foldDepsDefinersWhile(listOf<Dep>()) { depsDefiner, acc ->
                        AccumulatorContinue(acc + depsDefiner.deps(), true)
                    }
                    .accumulator
                    .let { depSet.addAll(it) }
        }
    }
}

private fun Call.deps(): List<Dep> = lastList()?.deps() ?: emptyList()

private fun Call.lastList(): ElixirList? =
    foldChildrenWhile(null as ElixirList?) { child, acc ->
        if (child is ElixirList) {
            AccumulatorContinue(child, true)
        } else {
            AccumulatorContinue(acc, true)
        }
    }.accumulator

private fun <R> Array<Call>.foldDepsDefinersWhile(
        initial: R,
        operation: (Call, acc: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> {
    var final = AccumulatorContinue(initial, true)

    depsNameArity()?.let { depsNameArity ->
        for (childCall in this) {
            if (isDefining(childCall, depsNameArity)) {
                final = operation(childCall, final.accumulator)

                if (!final.`continue`) {
                    break
                }
            }
        }
    }

    return final
}

private fun Array<Call>.depsNameArity(): NameArity? {
    var nameArity: NameArity? = null

    for (call in this) {
        if (isDefiningProject(call)) {
            nameArity = call.lastKeywordList()?.depsNameArity()

            if (nameArity != null) {
                break
            }
        }
    }

    return nameArity
}

private fun isDefining(call: Call, nameArity: NameArity): Boolean =
        if (isFunction(call)) {
            nameArityRange(call)?.let { definedNameArityRange ->
                if (definedNameArityRange.name == nameArity.name && definedNameArityRange.arityRange.contains(nameArity.arity)) {
                    true
                } else {
                    null
                }
            }
        } else {
            null
        } ?: false

private fun isDefiningProject(call: Call): Boolean =
        if (isPublicFunction(call)) {
            nameArityRange(call)?.let { nameArityRange ->
                if (nameArityRange.name == "project" && nameArityRange.arityRange.contains(0)) {
                    true
                } else {
                    null
                }
            }
        } else {
            null
        } ?: false

private fun Call.lastKeywordList(): QuotableKeywordList? =
        foldChildrenWhile(null as QuotableKeywordList?) { projectChild, acc ->
                    if (projectChild is ElixirList) {
                        val lastKeywordList = projectChild.children.last { it is QuotableKeywordList } as QuotableKeywordList?
                        AccumulatorContinue(lastKeywordList, true)
                    } else {
                        AccumulatorContinue(acc, true)
                    }
                }
                .accumulator

private fun QuotableKeywordList.depsNameArity(): NameArity? =
        keywordValue("deps")
                ?.let { it as? Call }
                ?.let { depsCall ->
                    depsCall
                            .functionName()
                            ?.let { name ->
                                NameArity(name, depsCall.resolvedFinalArity())
                            }
                }

private fun ElixirList.deps(): List<Dep> =
    children.map { it.stripAccessExpression() }.mapNotNull { Dep.from(it) }
