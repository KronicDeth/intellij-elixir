package org.elixir_lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.startOffset
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.reference.resolver.Type

class Type(typeSpec: AtUnqualifiedNoParenthesesCall<*>, type: Call) : PsiPolyVariantReferenceBase<Call>(type, textRange(typeSpec, type)) {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
            ResolveCache
                    .getInstance(this.myElement.project)
                    .resolveWithCaching(
                            this,
                            Type,
                            false,
                            incompleteCode
                    )

    companion object {
        fun typeHead(typeSpec: AtUnqualifiedNoParenthesesCall<*>): Call? =
            typeSpec
                    .finalArguments()
                    ?.singleOrNull()
                    ?.let { it as? org.elixir_lang.psi.operation.Type }
                    ?.leftOperand()
                    ?.let { it as? Call }

        private fun textRange(typeSpec: AtUnqualifiedNoParenthesesCall<*>, type: Call): TextRange =
            if (typeSpec.isEquivalentTo(type)) {
                typeHead(typeSpec)
                        ?.let { typeHead ->
                            val typeStartOffset = type.startOffset
                            val typeHeadStartOffset = typeHead.startOffset
                            val startOffset = typeHeadStartOffset - typeStartOffset
                            val endOffset = startOffset + typeHead.textLength

                            TextRange(startOffset, endOffset)
                        }
            } else {
                null
            } ?: TextRange(0, type.textLength)
    }
}
