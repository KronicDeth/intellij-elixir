package org.elixir_lang.reference

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.elixir_lang.psi.ElixirAtom

class MfaTupleReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            psiElement(ElixirAtom::class.java),
            MfaTupleReferenceProvider()
        )
    }
}
