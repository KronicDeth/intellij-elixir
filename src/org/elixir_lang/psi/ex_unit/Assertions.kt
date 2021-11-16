package org.elixir_lang.psi.ex_unit

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.Arity
import org.elixir_lang.ArityRange
import org.elixir_lang.psi.ModuleWalker
import org.elixir_lang.psi.NameArityRangeWalker
import org.elixir_lang.psi.call.Call

private abstract class Pattern(name: String, arityRange: ArityRange): NameArityRangeWalker(name, arityRange) {
    override fun walk(call: Call, arguments: Array<PsiElement>, resolvedFinalArity: Arity, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        // pattern is first argument, so...
        if (resolvedFinalArity == arguments.size) {
            keepProcessing(arguments[0], state)
        } else {
            // if there is a pipe the pattern is not in arguments
            true
        }
}

private object Assert: Pattern("assert", 1..2)
private object AssertReceive: Pattern("assert_receive", 1..3)
private object AssertReceived: Pattern("assert_received", 1..2)

object Assertions: ModuleWalker("ExUnit.Assertions", Assert, AssertReceive, AssertReceived)
