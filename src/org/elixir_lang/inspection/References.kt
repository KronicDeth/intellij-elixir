package org.elixir_lang.inspection

import com.intellij.codeInspection.*
import com.intellij.psi.*
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.ElixirVisitor
import org.elixir_lang.psi.call.Call

class References : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : ElixirVisitor() {
            override fun visitElement(element: PsiElement) {
                when (element) {
                    is ElixirAlias -> visitAlias(element)
                    is Call -> visitCall(element)
                }
            }

            override fun visitAlias(alias: ElixirAlias) {
                alias.reference?.let { reference -> registerProblem(alias, reference) }
            }

            override fun visitAtNonNumericOperation(atNonNumericOperation: AtNonNumericOperation) {
                atNonNumericOperation.reference?.let { reference -> registerProblem(atNonNumericOperation, reference) }
            }

            private fun visitCall(call: Call) {
                call.reference?.let { reference -> registerProblem(call, reference) }
            }

            private fun registerProblem(element: PsiElement, reference: PsiReference) {
                if (reference is PsiPolyVariantReference) {
                    registerProblem(element, reference)
                } else {
                    val resolved = reference.resolve()

                    if (resolved == null) {
                        holder.registerProblem(element, "Does not resolve to anything", ProblemHighlightType.ERROR)
                    }
                }
            }

            private fun registerProblem(element: PsiElement, reference: PsiPolyVariantReference) {
                val resolveResults = reference.multiResolve(false)

                if (resolveResults.isEmpty()) {
                    holder.registerProblem(element, "Does not resolve to anything", ProblemHighlightType.ERROR)
                } else if (!resolveResults.any { it.isValidResult }) {
                    holder.registerProblem(element, "Only resolves to invalid results", ProblemHighlightType.ERROR)
                }
            }
        }
    }
}
