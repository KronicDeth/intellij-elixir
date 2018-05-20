package org.elixir_lang.reference

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Prefix
import org.elixir_lang.psi.operation.capture.NonNumeric
import org.elixir_lang.reference.resolver.CaptureNameArity as Resolver

typealias Arity = Int

class CaptureNameArity(element: NonNumeric, val nameElement: Call, val arity: Arity) :
        PsiReferenceBase<Prefix>(element), PsiPolyVariantReference {
    init {
        rangeInElement = TextRange(0, element.textLength)
    }

    override fun getVariants(): Array<Any> = emptyArray()

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
       resolveWithCaching(myElement.project, this, incompleteCode)

    override fun resolve(): PsiElement? = multiResolve(false).singleOrNull()?.element

    private fun resolveWithCaching(
            project: Project,
            captureNameArity: CaptureNameArity,
            incompleteCode: Boolean
    ): Array<ResolveResult> =
            ReadAction.compute<Array<ResolveResult>, Throwable> {
                ResolveCache
                        .getInstance(project)
                        .resolveWithCaching(captureNameArity, Resolver, false, incompleteCode)
            }
}
