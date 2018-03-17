package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call

fun ElixirAtom.maybeModularNameToModular(maxScope: PsiElement): Call? =
    reference?.let { reference ->
        (reference.resolve() as? Call)?.let { resolved ->
            if (org.elixir_lang.psi.stub.type.call.Stub.isModular(resolved)) {
                resolved
            } else {
                null
            }
        }
    }
