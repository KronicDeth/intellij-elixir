package org.elixir_lang.psi.impl

import com.intellij.psi.ResolveState
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.UnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.isExported
import org.jetbrains.annotations.Contract

@RequiresReadLock
@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedArity(state: ResolveState): Int =
    if (isExported(this)) {
        CallDefinitionClause.nameArityInterval(this, state)?.arityInterval?.singleOrNull()
    } else {
        null
    } ?: MaybeExported.UNEXPORTED_ARITY

@RequiresReadLock
@Contract(pure = true)
fun UnqualifiedNoParenthesesCall<*>.exportedName(): String? =
    if (isExported(this)) {
        CallDefinitionClause.nameArityInterval(this, ResolveState.initial())?.name
    } else {
        null
    }
