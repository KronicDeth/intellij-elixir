package org.elixir_lang.semantic.call.definition.clause

import com.intellij.psi.PsiElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause

open class Call(
    override val enclosingModular: Modular,
    val call: Call,
    private val visibility: Visibility,
    private val time: Time,
    private val computeHead:
        (org.elixir_lang.semantic.call.definition.clause.Call) -> Head? = { callDefinitionClause ->
        headPsiElement(call)?.let { Head.get(callDefinitionClause, it) }
    }
) : Clause {
    val nameIdentifier: PsiElement?
        get() = head?.nameIdentifier

    override val psiElement: PsiElement
        get() = call
    override val definition: Definition?
        get() = nameArityInterval?.let { enclosingModular.callDefinitions[it] }

    override val compiled: Boolean = false
    override val nameArityInterval: NameArityInterval?
        get() = head?.nameArityInterval
    val head: Head? by lazy {
        computeHead(this)
    }

    companion object {
        private fun headPsiElement(call: Call): Call? =
            call.primaryArguments()?.firstOrNull()?.let { it as? Call }

        fun defmacro(modular: Modular, call: Call): Semantic {
            val headPsiElement = headPsiElement(call)
            val nameArityInterval = if (headPsiElement != null) {
                Head.nameArityInterval(headPsiElement, modular.canonicalName)
            } else {
                null
            }

            return when (nameArityInterval) {
                org.elixir_lang.semantic.call.definition.clause.using.Call.NAME_ARITY_INTERVAL ->
                    org.elixir_lang.semantic.call.definition.clause.using.Call(modular, call)
                else -> Call(
                    modular,
                    call,
                    visibility = Visibility.PUBLIC,
                    time = Time.COMPILE
                ) { callDefinitionClause ->
                    headPsiElement?.let { Head(callDefinitionClause, it) }
                }
            }
        }

        fun defmacrop(modular: Modular, call: Call) =
            Call(modular, call, visibility = Visibility.PRIVATE, time = Time.COMPILE)

        fun defguard(modular: Modular, call: Call) =
            Call(modular, call, visibility = Visibility.PUBLIC, time = Time.GUARD)

        fun defguardp(modular: Modular, call: Call) =
            Call(modular, call, visibility = Visibility.PRIVATE, time = Time.GUARD)

        fun def(modular: Modular, call: Call) = Call(modular, call, visibility = Visibility.PUBLIC, time = Time.RUN)
        fun defp(modular: Modular, call: Call) = Call(modular, call, visibility = Visibility.PRIVATE, time = Time.RUN)
    }
}
