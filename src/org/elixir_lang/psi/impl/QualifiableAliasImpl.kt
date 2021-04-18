package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.refactoring.suggested.endOffset
import org.elixir_lang.Module.concat
import org.elixir_lang.errorreport.Logger
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

fun QualifiableAlias.computeReference(): PsiPolyVariantReference? =
    when (val parent = this.parent) {
        is QualifiableAlias ->
            // If the `parent` goes beyond this element then this element is the outermost Qualifiable alias that is still
            // ends in this element, so it represents the fully-qualified name.
            if (endOffset < parent.endOffset) {
                // The range in the element though should only be the final to match the guidance in
                // `com.intellij.psi.PsiReference#getRangeInElement`, which appears necessary to make completion to
                // work
                Module(this)
            } else {
                // If the parent ends at the same offset, then the parent should supply the reference
                null
            }
        else ->
            // There is no one module that defines the `Elixir` module.  It is only defined implicitly as the common
            // namespace to all Aliases.
            if (this.fullyQualifiedName() != "Elixir") {
                Module(this)
            } else {
                null
            }
    }

fun QualifiableAlias.getReference(): PsiPolyVariantReference? =
        CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(computeReference(), this)
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

fun QualifiableAlias.maybeModularNameToModulars(maxScope: PsiElement): List<Call> =
    if (!recursiveKernelImport(maxScope)) {
        /* need to construct reference directly as qualified aliases don't return a reference except for the
           outermost */
        reference?.toModulars()
    } else {
        null
    } ?: emptyList()

@Contract(pure = true)
private fun QualifiableAlias.recursiveKernelImport(maxScope: PsiElement): Boolean =
        maxScope is ElixirFile && maxScope.name == "kernel.ex" && name == KERNEL

private fun PsiReference.toModulars(): List<Call> =
        when (this) {
            is PsiPolyVariantReference -> {
                multiResolve(false).flatMap { resolveResult ->
                    resolveResult
                            .takeIf(ResolveResult::isValidResult)
                            ?.element
                            ?.let { resolved -> toModulars(resolved) }
                            ?: emptyList()
                }
            }
            else -> {
                resolve()
                        ?.let { resolved -> toModulars(resolved) }
                        ?: emptyList()
            }
        }

private fun PsiReference.toModulars(resolved: PsiElement): List<Call> =
    if (resolved is Call && isModular(resolved)) {
        listOf(resolved)
    } else if  (resolved.isEquivalentTo(element)) {
        // resolved to self, but not a modular, so stop looking
        emptyList()
    } else {
        resolved.reference?.toModulars() ?: emptyList()
    }

object QualifiableAliasImpl {
    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(alias: ElixirAlias): String = fullyQualifiedName(alias.parent, alias, listOf(alias.name))

    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(qualifiableAlias: QualifiableAlias): String =
            fullyQualifiedName(qualifiableAlias, listOf())

    private tailrec fun fullyQualifiedName(currentAncestor: PsiElement?, previousAncestor: PsiElement, nameTail: List<String>): String =
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

    private tailrec fun fullyQualifiedName(leftElement: PsiElement, rightNames: List<String>): String = when (leftElement) {
        is ElixirAccessExpression -> fullyQualifiedName(leftElement.stripAccessExpression(), rightNames)
        is ElixirAlias -> concat(listOf(leftElement.name) + rightNames)
        is QualifiedAlias -> {
            val children = leftElement.children
            val operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children)

            val qualifier = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)

            if (qualifier != null) {
                val relativeName = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex)?.let { relative ->
                    when (relative) {
                        is ElixirAlias -> relative.name
                        else -> {
                            Logger.error(this.javaClass, "Don't know how to calculate relative name", relative)

                            null
                        }
                    }
                } ?: "?"

                fullyQualifiedName(qualifier, listOf(relativeName) + rightNames)
            } else {
                concat(listOf("?") + rightNames)
            }
        }
        is Call -> {
            val qualifierName = if (leftElement.isCalling(KERNEL, __MODULE__, 0)) {
                val enclosingCall = enclosingModularMacroCall(leftElement)

                if (enclosingCall != null && enclosingCall is StubBased<*>) {
                    enclosingCall.canonicalName()
                } else {
                    null
                }
            } else {
                null
            } ?: "?"

            concat(listOf(qualifierName) + rightNames)
        }
        else -> concat(rightNames)
    }
}
