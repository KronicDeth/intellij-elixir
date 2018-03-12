package org.elixir_lang.psi.impl

import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.call.name.Function.__MODULE__
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.stripAccessExpression
import org.elixir_lang.psi.operation.Normalized
import org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall
import org.jetbrains.annotations.Contract

object QualifiableAliasImpl {
    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(alias: ElixirAlias): String {
        return alias.name
    }

    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(qualifiableAlias: QualifiableAlias): String? {
        var fullyQualifiedName: String? = null
        val children = qualifiableAlias.children
        val operatorIndex = Normalized.operatorIndex(children)
        val qualifier = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)

        var qualifierName: String? = null

        if (qualifier is Call) {
            val qualifierCall = qualifier as Call?

            if (qualifierCall!!.isCalling(KERNEL, __MODULE__, 0)) {
                val enclosingCall = enclosingModularMacroCall(qualifierCall)

                if (enclosingCall != null && enclosingCall is StubBased<*>) {
                    val enclosingStubBasedCall = enclosingCall as StubBased<*>
                    qualifierName = enclosingStubBasedCall.canonicalName()
                }
            }
        } else if (qualifier is QualifiableAlias) {
            val qualifiableQualifier = qualifier as QualifiableAlias?

            qualifierName = qualifiableQualifier!!.fullyQualifiedName()
        } else if (qualifier is ElixirAccessExpression) {
            val qualifierChild = stripAccessExpression(qualifier)

            if (qualifierChild is ElixirAlias) {
                qualifierName = qualifierChild.name
            }
        }

        if (qualifierName != null) {
            val rightOperand = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(
                    children,
                    operatorIndex
            )

            if (rightOperand is ElixirAlias) {
                val relativeAlias = rightOperand as ElixirAlias?

                fullyQualifiedName = qualifierName + "." + relativeAlias!!.name
            }
        }

        return fullyQualifiedName
    }
}
