package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.CanonicallyNamed
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.structure_view.element.Timed.Time

interface CallDefinition : CanonicallyNamed, MaybeExported, NamedElement, PsiCompiledElement {
    val time: Time
    val nameArityInterval: NameArityInterval
}
