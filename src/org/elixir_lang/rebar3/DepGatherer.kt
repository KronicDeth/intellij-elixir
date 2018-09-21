package org.elixir_lang.rebar3

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.mix.Dep
import org.elixir_lang.package_manager.DepGatherer
import org.intellij.erlang.psi.*

class DepGatherer : DepGatherer() {
    override fun visitFile(file: PsiFile?) {
        if (file is ErlangFile) {
            file.acceptChildren(this)
        }
    }

    override fun visitElement(element: PsiElement) {
        if (element is ErlangTupleExpression) {
            val expressionList = element.expressionList

            if (expressionList.size == 2) {
                val firstExpression = expressionList[0]

                if (firstExpression is ErlangConfigExpression && firstExpression.text == "deps") {
                    val deps = expressionList[1]

                    if (deps is ErlangListExpression) {
                        deps.expressionList.forEach { expression ->
                            dep(expression)?.let { depSet.add(it) }
                        }
                    }
                }
            }
        }
    }

    private fun dep(expression: ErlangExpression): Dep? =
            when (expression) {
                is ErlangTupleExpression -> dep(expression)
                else -> {
                    Logger.error(
                            logger,
                            "Don't know if how to extract Mix.Dep from Rebar3 dep expression",
                            expression
                    )
                    null
                }
            }

    private fun dep(tuple: ErlangTupleExpression): Dep? {
        val expressionList = tuple.expressionList

        return if (expressionList.size >= 1) {
            val firstExpression = expressionList[0]

            if (firstExpression is ErlangConfigExpression) {
                val name = firstExpression.text
                Dep(name, path = "deps/$name")
            } else {
                Logger.error(
                        logger,
                        "Don't know if how to extract Mix.Dep from Rebar3 dep expression",
                        tuple
                )
                null
            }
        } else {
            Logger.error(
                    logger,
                    "Don't know if how to extract Mix.Dep from Rebar3 dep expression",
                    tuple
            )
            null
        }
    }
}

private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(DepGatherer::class.java) }
