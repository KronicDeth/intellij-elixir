package org.elixir_lang.debugger.line_breakpoint

import com.intellij.psi.PsiElement
import com.intellij.util.Processor

abstract class AvailabilityProcessor: Processor<PsiElement> {
    var isAvailable: Boolean = false
        protected set
}
