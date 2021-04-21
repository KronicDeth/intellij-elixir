package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.isAncestor
import com.intellij.refactoring.suggested.endOffset
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.operation.Normalized.operatorIndex
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.psi.stub.type.call.Stub.isModular
import org.elixir_lang.reference.Module
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

fun QualifiableAlias.maybeModularNameToModulars(maxScope: PsiElement): Set<Call> =
    if (!recursiveKernelImport(maxScope)) {
        /* need to construct reference directly as qualified aliases don't return a reference except for the
           outermost */
        reference?.toModulars()
    } else {
        null
    } ?: emptySet()

@Contract(pure = true)
private fun QualifiableAlias.recursiveKernelImport(maxScope: PsiElement): Boolean =
        maxScope is ElixirFile && maxScope.name == "kernel.ex" && name == KERNEL

private fun PsiReference.toModulars(): Set<Call> =
        when (this) {
            is PsiPolyVariantReference -> {
                multiResolve(false).flatMap { resolveResult ->
                    resolveResult
                            .takeIf(ResolveResult::isValidResult)
                            ?.element
                            ?.let { resolved -> toModulars(resolved) }
                            ?: emptySet()
                }.toSet()
            }
            else -> {
                resolve()
                        ?.let { resolved -> toModulars(resolved) }
                        ?: emptySet()
            }
        }

private fun PsiReference.toModulars(resolved: PsiElement): Set<Call> =
    if (resolved is Call && isModular(resolved)) {
        setOf(resolved)
    } else if  (resolved.isEquivalentTo(element)) {
        // resolved to self, but not a modular, so stop looking
        emptySet()
    } else {
        resolved.reference?.toModulars() ?: emptySet()
    }

object QualifiableAliasImpl {
    @Contract(pure = true)
    @JvmStatic
    fun fullyQualifiedName(qualifiableAlias: QualifiableAlias): String =
            prependQualifiers(qualifiableAlias.parent, qualifiableAlias, qualifiableAlias.name ?: "?")

    private fun prependQualifiers(ancestor: PsiElement, previousAncestor: PsiElement, accumulator: String): String =
        when (ancestor) {
            // being inside arguments to a call end qualifiers
            is Arguments,
            is Operation,
            is QuotableKeywordPair,
            // containers
            is ElixirList, is ElixirStructOperation, is ElixirTuple -> accumulator
            is ElixirAccessExpression, is ElixirMultipleAliases ->
                prependQualifiers(ancestor.parent, ancestor, accumulator)
            is QualifiedAlias, is Qualified, is QualifiedMultipleAliases -> {
                val children = ancestor.children
                val operatorIndex = operatorIndex(children)
                val qualifier = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)

                if (qualifier != null) {
                    if (qualifier.isAncestor(previousAncestor)) {
                        // ancestor was qualifier, so it is only the qualifier's name
                        accumulator
                    } else {
                        val qualifiedName = when (val strippedQualifier = qualifier.stripAccessExpression()) {
                            is QualifiableAlias -> strippedQualifier.name
                            else -> {
                                TODO()
                            }
                        }

                        "${qualifiedName}.${accumulator}"
                    }
                } else {
                    TODO()
                }
            }
            else -> {
                Logger.error(QualifiableAlias::class.java, "Don't know how to prepend qualifier", ancestor)

                "?.${accumulator}"
            }
        }
}
