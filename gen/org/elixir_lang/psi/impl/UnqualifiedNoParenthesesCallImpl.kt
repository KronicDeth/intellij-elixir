package org.elixir_lang.psi.impl

import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.UnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.isExported
import org.jetbrains.annotations.Contract

@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedArity(): Int =
    if (isExported(this)) {
        CallDefinitionClause.nameArityRange(this)?.arityRange?.singleOrNull()
    } else {
        null
    } ?: MaybeExported.UNEXPORTED_ARITY

@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedName(): String? =
    if (isExported(this)) {
        CallDefinitionClause.nameArityRange(this)?.name
    } else {
        null
    }
