package org.elixir_lang.beam.psi

import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.CanonicallyNamed

/**
 * A named element of a decompiled `.beam`: the module itself, or one of its call or type definitions.
 * These are exactly the elements whose stub types write into `AllName.KEY`, so a `when` over this is a
 * complete account of what the symbol index can hand back from a decompiled file.
 *
 * Sealed, so adding a fourth fails the build at every dispatch site. [BeamFileImpl] is not a member
 * because a file is not a symbol.
 */
sealed interface BeamSymbol : CanonicallyNamed, NamedElement, NavigatablePsiElement, PsiCompiledElement
