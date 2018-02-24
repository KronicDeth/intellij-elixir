package org.elixir_lang.debugger.line_breakpoint.availability_processor

import com.intellij.psi.PsiElement
import org.elixir_lang.debugger.line_breakpoint.AvailabilityProcessor
import org.elixir_lang.psi.impl.ElixirPsiImplUtil

class Elixir: AvailabilityProcessor() {
    override fun process(psiElement: PsiElement): Boolean =
            if (ElixirPsiImplUtil.getModuleName(psiElement) != null) {
                isAvailable = true

                false
            } else {
                true
            }
}
