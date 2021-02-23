package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Module.concat
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.call.name.Function.__MODULE__
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.operation.Normalized
import org.elixir_lang.psi.stub.type.call.Stub.isModular
import org.elixir_lang.reference.Module
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.jetbrains.annotations.Contract

fun QualifiableAlias.computeReference(): PsiPolyVariantReference = Module(this)

fun QualifiableAlias.getReference(): PsiPolyVariantReference =
        CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(computeReference(), this)
        }

fun QualifiableAlias.fullyResolve(startingReference: PsiReference?): PsiElement {
    val fullyResolved: PsiElement
    var currentResolved: PsiElement = this
    var reference = startingReference

    do {
        if (reference == null) {
            reference = currentResolved.reference
        }

        if (reference != null) {
            if (reference is PsiPolyVariantReference) {
                val resolveResults = reference.multiResolve(false)
                val resolveResultCount = resolveResults.size

                if (resolveResultCount == 0) {
                    fullyResolved = currentResolved

                    break
                } else if (resolveResultCount == 1) {
                    val resolveResult = resolveResults[0]

                    val nextResolved = resolveResult.element

                    if (nextResolved != null && nextResolved is Call && isModular(nextResolved)) {
                        fullyResolved = nextResolved
                        break
                    }

                    if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                        fullyResolved = currentResolved
                        break
                    } else {
                        currentResolved = nextResolved
                    }
                } else {
                    var nextResolved: PsiElement? = null

                    for (resolveResult in resolveResults) {
                        val resolveResultElement = resolveResult.element

                        if (resolveResultElement != null &&
                                resolveResultElement is Call &&
                                isModular(resolveResultElement)) {
                            nextResolved = resolveResultElement

                            break
                        }
                    }

                    fullyResolved = if (nextResolved == null) {
                        currentResolved
                    } else {
                        nextResolved
                    }

                    break
                }
            } else {
                val nextResolved = reference.resolve()

                if (nextResolved == null || nextResolved.isEquivalentTo(currentResolved)) {
                    fullyResolved = currentResolved
                    break
                } else {
                    currentResolved = nextResolved
                }
            }
        } else {
            fullyResolved = currentResolved

            break
        }

        reference = null
    } while (true)

    return fullyResolved
}


@Contract(pure = true)
fun QualifiableAlias.isOutermostQualifiableAlias(): Boolean {
    val parent = parent
    var outermost = false

    /* prevents individual Aliases or tail qualified aliases of qualified chain from having reference separate
           reference from overall chain */
    if (parent !is QualifiableAlias) {
        val grandParent = parent.parent

        // prevents first Alias of a qualified chain from having a separate reference from overall chain
        if (grandParent !is QualifiableAlias) {
            outermost = true
        }
    }

    return outermost
}

fun QualifiableAlias.maybeModularNameToModular(maxScope: PsiElement): Call? =
    if (!recursiveKernelImport(maxScope)) {
        /* need to construct reference directly as qualified aliases don't return a reference except for the
           outermost */
        getReference()?.let { this.toModular(it) }
    } else {
        null
    }


@Contract(pure = true)
private fun QualifiableAlias.recursiveKernelImport(maxScope: PsiElement): Boolean =
        maxScope is ElixirFile && maxScope.name == "kernel.ex" && name == KERNEL

@Contract(pure = true)
fun QualifiableAlias.toModular(startingReference: PsiReference): Call? {
    val fullyResolvedAlias = fullyResolve(startingReference)

    return if (fullyResolvedAlias is Call && isModular(fullyResolvedAlias)) {
        fullyResolvedAlias
    } else {
        null
    }
}

object QualifiableAliasImpl {
    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(alias: ElixirAlias): String = fullyQualifiedName(alias.parent, alias, listOf(alias.name))

    tailrec fun fullyQualifiedName(currentAncestor: PsiElement?, previousAncestor: PsiElement, nameTail: List<String>): String =
        when (currentAncestor) {
            is ElixirAccessExpression, is ElixirMultipleAliases -> fullyQualifiedName(currentAncestor.parent, currentAncestor, nameTail)
            is QualifiedAlias, is QualifiedMultipleAliases -> {
                val children = currentAncestor.children
                val operatorIndex = Normalized.operatorIndex(children)
                val previousAncestorIndex = children.indexOf(previousAncestor)
                // if `previousAncestor` was the left operand, then the `accNameList` is complete
                if (previousAncestorIndex < operatorIndex) {
                    concat(nameTail)
                // if the `previousAncestor` was the right operand, then the `accNameList` needs to
                } else {
                    val leftOperand = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)!!
                    fullyQualifiedName(leftOperand, nameTail)
                }
            }
            else -> concat(nameTail)
        }

    tailrec fun fullyQualifiedName(leftElement: PsiElement, rightNames: List<String>): String = when (leftElement) {
        is ElixirAccessExpression -> fullyQualifiedName(leftElement.stripAccessExpression(), rightNames)
        is ElixirAlias -> concat(listOf(leftElement.name) + rightNames)
        else -> concat(rightNames)
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
                    qualifierName = enclosingCall.canonicalName()
                }
            }
        } else if (qualifier is QualifiableAlias) {
            val qualifiableQualifier = qualifier as QualifiableAlias?

            qualifierName = qualifiableQualifier!!.fullyQualifiedName()
        } else if (qualifier is ElixirAccessExpression) {
            val qualifierChild = qualifier.stripAccessExpression()

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
