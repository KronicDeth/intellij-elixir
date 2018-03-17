package org.elixir_lang.psi.impl

import org.elixir_lang.psi.UnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.isExported
import org.elixir_lang.structure_view.element.CallDefinitionClause
import org.jetbrains.annotations.Contract

@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedArity(): Int =
    if (isExported(this)) {
        CallDefinitionClause.nameArityRange(this)?.let { nameArityRange ->
            nameArityRange.second?.let { arityRange ->
                val minimumArity = arityRange.minimumInteger
                val maximumArity = arityRange.maximumInteger

                if (minimumArity == maximumArity) {
                    minimumArity
                } else {
                    null
                }
            }
        }
    } else {
        null
    } ?: MaybeExported.UNEXPORTED_ARITY

@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedName(): String? =
    if (isExported(this)) {
        CallDefinitionClause.nameArityRange(this)?.first
    } else {
        null
    }
