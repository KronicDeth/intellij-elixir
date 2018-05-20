package org.elixir_lang.psi.impl.operation.capture

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.ElixirDecimalWholeNumber
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.PsiNameIdentifierOwnerImpl
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.toBigInteger
import org.elixir_lang.psi.operation.Multiplication
import org.elixir_lang.psi.operation.capture.NonNumeric
import org.elixir_lang.psi.operation.prefix.Normalized
import org.elixir_lang.reference.Arity
import org.elixir_lang.reference.CaptureNameArity

private fun ElixirDecimalWholeNumber.toArity(): Arity? = toBigInteger()?.toInt()

private fun NonNumeric.computeReference(): PsiReference? =
    Normalized.operand(this)?.let { operand ->
        (operand as? Multiplication)?.let { multiplication ->
            val multiplicationChildren = multiplication.children
            val multiplicationOperatorIndex =
                    org.elixir_lang.psi.operation.Normalized.operatorIndex(multiplicationChildren)

            val operator = org.elixir_lang.psi.operation.Normalized.operator(
                    multiplicationChildren,
                    multiplicationOperatorIndex
            )

            operator.node.findChildByType(ElixirTypes.DIVISION_OPERATOR)?.let {
                org.elixir_lang.psi.operation.infix.Normalized.rightOperand(
                        multiplicationChildren,
                        multiplicationOperatorIndex
                )?.let { rightOperand ->
                    val strippedRightOperand = rightOperand.stripAccessExpression()

                    (strippedRightOperand as? ElixirDecimalWholeNumber)?.let { decimalWholeNumber ->
                        decimalWholeNumber.toArity()?.let { arity ->
                            org.elixir_lang.psi.operation.infix.Normalized.leftOperand(
                                    multiplicationChildren,
                                    multiplicationOperatorIndex
                            )?.let { leftOperand ->
                                (leftOperand as? Call)?.let { nameElement ->
                                    CaptureNameArity(element = this, nameElement = nameElement, arity = arity)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun getReference(nonNumeric: NonNumeric): PsiReference? =
        getCachedValue(nonNumeric) {
            CachedValueProvider.Result.create(nonNumeric.computeReference(), nonNumeric)
        }

fun getNameIdentifier(nonNumeric: NonNumeric): PsiElement? =
        when (nonNumeric.reference) {
            is CaptureNameArity -> Normalized.operand(nonNumeric)
            else -> PsiNameIdentifierOwnerImpl.getNameIdentifier(nonNumeric as Named)
        }
