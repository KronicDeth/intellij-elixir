package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.CanonicallyNamed
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.structure_view.element.Timed.Time

interface CallDefinition : BeamSymbol, MaybeExported {
    /** The decompiled module this definition belongs to. */
    override fun getParent(): Module

    val time: Time
    val nameArityInterval: NameArityInterval
}
