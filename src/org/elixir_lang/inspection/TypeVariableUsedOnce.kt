package org.elixir_lang.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirVisitor
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.operation.Type as TypeOperation
import org.elixir_lang.psi.operation.When
import org.elixir_lang.structure_view.element.Type as TypeElement

class TypeVariableUsedOnce : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor =
        object : ElixirVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is AtUnqualifiedNoParenthesesCall<*> && TypeElement.`is`(element)) {
                    inspectTypeDefinition(element)
                }
            }

            private fun inspectTypeDefinition(typeDefinition: AtUnqualifiedNoParenthesesCall<*>) {
                val specification = TypeElement.specification(typeDefinition) ?: return
                val head = headOf(specification) ?: return
                val parameters = head.finalArguments()?.filterIsInstance<UnqualifiedNoArgumentsCall<*>>() ?: return

                if (parameters.isEmpty()) return

                val occurrencesByName = PsiTreeUtil
                    .collectElementsOfType(specification, UnqualifiedNoArgumentsCall::class.java)
                    .groupingBy { it.name }
                    .eachCount()

                parameters.forEach { parameter ->
                    val name = parameter.name ?: return@forEach
                    if (name.startsWith("_")) return@forEach

                    if ((occurrencesByName[name] ?: 0) < 2) {
                        holder.registerProblem(
                            parameter,
                            "Type variable '$name' is used only once; reference it at least twice or use term()",
                            ProblemHighlightType.ERROR
                        )
                    }
                }
            }

            private fun headOf(element: PsiElement?): Call? =
                when (element) {
                    is TypeOperation -> element.leftOperand() as? Call
                    is When -> headOf(element.leftOperand())
                    else -> null
                }
        }
}
