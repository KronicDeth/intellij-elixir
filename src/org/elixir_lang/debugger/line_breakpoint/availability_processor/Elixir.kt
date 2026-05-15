package org.elixir_lang.debugger.line_breakpoint.availability_processor

import com.intellij.psi.PsiElement
import org.elixir_lang.debugger.line_breakpoint.AvailabilityProcessor
import org.elixir_lang.psi.impl.isInsideModule

class Elixir: AvailabilityProcessor() {
    override fun process(psiElement: PsiElement): Boolean =
            // DO NOT call getModuleName() here — isInsideModule() avoids recursive name assembly and VFS traversal risk
            if (psiElement.isInsideModule()) {
                isAvailable = true

                false
            } else {
                true
            }
}
