package org.elixir_lang.mix

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.NameArity
import org.elixir_lang.package_manager.DepGatherer
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.foldChildrenWhile
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Module
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic

class DepGatherer : DepGatherer() {
    override fun visitFile(file: PsiFile) {
        if (file is ElixirFile) {
            runReadAction {
                file.acceptChildren(this)
            }
        }
    }

    override fun visitElement(element: PsiElement) {
        element.semantic?.let { it as? Module }?.let { module ->
            val childCalls = module.psiElement.let { it as Call }.macroChildCalls()

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
    call.semantic?.let { it as? Clause }?.definition?.let { definition ->
        definition.time == Time.RUN && definition.nameArityInterval?.let { nameArityInterval ->
            nameArityInterval.name == nameArity.name && nameArityInterval.arityInterval.contains(nameArity.arity)
        } ?: false
    } ?: false


private fun isDefiningProject(call: Call): Boolean =
    call.semantic?.let { it as? Clause }?.definition?.nameArityInterval?.let { nameArityInterval ->
        nameArityInterval.name == "project" && nameArityInterval.arityInterval.contains(0)
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
    children.map { it.stripAccessExpression() }.asSequence().flatMap { Deps.from(it) }.toList()
