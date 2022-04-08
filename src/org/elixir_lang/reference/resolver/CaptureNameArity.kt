package org.elixir_lang.reference.resolver

import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.reference.CaptureNameArity

object CaptureNameArity : ResolveCache.PolyVariantResolver<CaptureNameArity> {
    override fun resolve(reference: CaptureNameArity, incompleteCode: Boolean): Array<ResolveResult> =
            Callable.resolve(reference.nameElement, reference.arity, incompleteCode)
}
