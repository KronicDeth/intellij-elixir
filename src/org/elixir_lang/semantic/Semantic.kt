package org.elixir_lang.semantic

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.isAncestor
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Quote
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module.*
import org.elixir_lang.psi.impl.ElixirRelativeIdentifierImpl
import org.elixir_lang.psi.impl.ancestorSequence
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.operation.In
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.operation.When
import org.elixir_lang.semantic.Semantic.Companion.PARENT
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.def
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.defguard
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.defguardp
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.defmacro
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.defmacrop
import org.elixir_lang.semantic.call.definition.clause.Call.Companion.defp
import org.elixir_lang.semantic.module.Call.Companion.defmodule
import org.elixir_lang.semantic.module_attribute.definition.Literal
import org.elixir_lang.semantic.module_attribute.definition.dynamic.Put
import org.elixir_lang.semantic.module_attribute.definition.dynamic.Register
import org.elixir_lang.semantic.structure.Definition
import org.elixir_lang.semantic.type.definition.Binary
import org.elixir_lang.semantic.type.definition.source.Callback
import org.elixir_lang.semantic.type.definition.source.Head
import org.elixir_lang.semantic.type.definition.source.Specification
import org.elixir_lang.semantic.type.usage.Qualified
import org.elixir_lang.semantic.variable.Bang
import org.elixir_lang.semantic.variable.QuoteDeclared

/**
 * The semantic meaning of a `PsiElement`
 */
interface Semantic {
    val psiElement: PsiElement
    fun elementDescription(location: ElementDescriptionLocation): String?

    companion object {
        private val SEMANTIC: Key<CachedValue<Semantic?>> = Key("elixir.semantic")
        internal val PARENT: Key<CachedValue<Semantic?>> = Key("elixir.semantic.parent")

        fun semantic(psiElement: PsiElement): Semantic? =
            when (psiElement) {
                is ElixirAccessExpression, is PsiFileSystemItem -> null
                else -> CachedValuesManager.getCachedValue(psiElement, SEMANTIC) {
                    CachedValueProvider.Result.create(computeSemantic(psiElement), psiElement)
                }
            }

        fun <S : Semantic> semantic(psiElement: PsiElement, compute: () -> S): S {
            val value = psiElement.getUserData(SEMANTIC)

            TODO()
        }

        private fun computeSemantic(psiElement: PsiElement): Semantic? =
            when (psiElement) {
                is ModuleDefinition -> org.elixir_lang.semantic.module.Binary(psiElement)
                is TypeDefinition -> Binary(psiElement)
                is CallDefinition -> org.elixir_lang.semantic.call.definition.clause.Binary(
                    psiElement
                )
                is AtUnqualifiedNoParenthesesCall<*> -> computeSemantic(psiElement)
                is QualifiedMultipleAliases -> org.elixir_lang.semantic.multiple_aliases.Qualified(psiElement)
                is QualifiedAlias -> org.elixir_lang.semantic.alias.Qualified(psiElement)
                is ElixirAlias -> org.elixir_lang.semantic.alias.Alias(psiElement)
                is ElixirAtom -> computeSemantic(psiElement)
                is ElixirFile -> org.elixir_lang.semantic.file.Source(psiElement)
                is ElixirKeywordKey -> computeSemantic(psiElement)
                is ElixirList -> List(psiElement)
                is ElixirTuple -> Tuple(psiElement)
                is ElixirVariable -> org.elixir_lang.semantic.variable.Variable(psiElement)
                is Quote -> org.elixir_lang.semantic.textual.Quote(psiElement)
                is Sigil -> org.elixir_lang.semantic.textual.Sigil(psiElement)
                is In -> computeParentDependentSemantic(psiElement)
                is Arguments, is Body, is ElixirAnonymousFunction, is ElixirAssociations,
                is ElixirAssociationsBase, is ElixirAtIdentifier, is ElixirAtomKeyword, is ElixirBlockIdentifier,
                is ElixirBlockItem, is ElixirBlockList, is ElixirBracketArguments,
                is ElixirContainerAssociationOperation, is ElixirDecimalDigits, is ElixirDecimalWholeNumber, is
                ElixirDoBlock, is
                ElixirEndOfExpression,
                is ElixirHeredocLinePrefix, is ElixirHeredocPrefix, is ElixirIdentifier, is ElixirInterpolation,
                is ElixirMapArguments, is ElixirMapConstructionArguments, is ElixirMapOperation,
                is ElixirMapUpdateArguments, is ElixirMatchedParenthesesArguments, is ElixirNoParenthesesArguments,
                is ElixirRelativeIdentifierImpl, is ElixirSigilModifiers, is ElixirStab, is ElixirStabBody,
                is ElixirStabOperation, is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature,
                is ElixirStructOperation, is EscapeSequence, is HeredocLine, is LeafPsiElement, is Operator, is
                Operation,
                is QuotableKeywordList, is QuotableKeywordPair -> null
                is Call -> computeSemantic(psiElement)
                else -> null
            }

        private fun computeSemantic(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Semantic =
            when (val identifierName = atUnqualifiedNoParenthesesCall.atIdentifier.identifierName()) {
                "behaviour" -> org.elixir_lang.semantic.module_attribute.Behaviour(atUnqualifiedNoParenthesesCall)
                "callback", "macrocallback" ->
                    Callback(atUnqualifiedNoParenthesesCall, identifierName)
                "moduledoc" -> org.elixir_lang.semantic.documentation.module.Source(atUnqualifiedNoParenthesesCall)
                "typedoc" -> org.elixir_lang.semantic.documentation.Type(atUnqualifiedNoParenthesesCall)
                "doc" -> org.elixir_lang.semantic.documentation.CallDefinition(atUnqualifiedNoParenthesesCall)
                "opaque", "type", "typep" -> org.elixir_lang.semantic.type.definition.source.ModuleAttribute(
                    atUnqualifiedNoParenthesesCall,
                    identifierName
                )
                "spec" -> Specification(atUnqualifiedNoParenthesesCall)
                else -> Literal(atUnqualifiedNoParenthesesCall)
            }

        private fun computeSemantic(atom: ElixirAtom): Semantic =
            atom
                .semanticParent
                ?.let { semanticParent ->
                    if (semanticParent is org.elixir_lang.semantic.structure.Definition) {
                        org.elixir_lang.semantic.structure.definition.Field(semanticParent, atom)
                    } else {
                        null
                    }
                }
                ?: Atom(atom)

        private fun computeSemantic(call: Call): Semantic? =
            call.resolvedModuleName()
                ?.let { module -> computeSemantic(call, module) }
                ?: computeParentDependentSemantic(call)

        private fun computeSemantic(call: Call, module: String): Semantic? =
            when (module) {
                // Kernel is most common, so put it first even if not alphabetical
                KERNEL -> computeSemanticFromKernel(call)
                EEX -> computeSemanticFromEEx(call)
                MODULE -> computeSemanticFromModule(call)
                else -> null
            }

        private fun computeSemanticFromEEx(call: Call): Semantic? =
            call.functionName()?.let { function ->
                computeSemanticFromEEx(call, function)
            }

        private fun computeSemanticFromEEx(call: Call, function: String): Semantic? =
            if (call.resolvedFinalArity() in 3..5) {
                when (function) {
                    org.elixir_lang.semantic.call.definition.eex.FunctionFromFile.FUNCTION ->
                        enclosingModular(call)?.let { enclosingModular ->
                            org.elixir_lang.semantic.call.definition.eex.FunctionFromFile(enclosingModular, call)
                        }
                    org.elixir_lang.semantic.call.definition.eex.FunctionFromString.FUNCTION ->
                        enclosingModular(call)?.let { enclosingModular ->
                            org.elixir_lang.semantic.call.definition.eex.FunctionFromString(enclosingModular, call)
                        }
                    else -> null
                }
            } else {
                null
            }

        private fun computeSemanticFromKernel(call: Call): Semantic? =
            call.functionName()?.let { function ->
                computeSemanticFromKernel(call, function)
            }

        private fun computeSemanticFromKernel(call: Call, function: String): Semantic? {
            val arity = call.resolvedFinalArity()
            val hasDoBlockOrKeyword = call.hasDoBlockOrKeyword()

            return when (arity) {
                2 -> if (hasDoBlockOrKeyword) {
                    when (function) {
                        Function.COND -> org.elixir_lang.semantic.branching.Cond(call)
                        Function.IF -> org.elixir_lang.semantic.branching.conditional.If(call)
                        Function.QUOTE -> org.elixir_lang.semantic.quote.Call(
                            enclosingModular(call),
                            call
                        )
                        Function.RECEIVE -> org.elixir_lang.semantic.branching.Receive(call)
                        Function.UNLESS -> org.elixir_lang.semantic.branching.conditional.Unless(call)
                        else -> defs(
                            enclosingModular(call),
                            call,
                            function
                        )
                    }
                } else {
                    when (function) {
                        Function.ALIAS -> org.elixir_lang.semantic.aliasing.Call(call)
                        Function.DESTRUCTURE -> Destructure(call)
                        Function.IMPORT -> org.elixir_lang.semantic.import.Call(call)
                        Function.MATCH_QUESTION_MARK -> MatchQuestionMark(call)
                        Function.REQUIRE -> org.elixir_lang.semantic.require.Call(call)
                        Function.USE -> org.elixir_lang.semantic.use.Call(call)
                        else -> null
                    }
                }
                1 -> {
                    @Suppress("UNCHECKED_CAST")
                    when (function) {
                        Function.ALIAS -> org.elixir_lang.semantic.aliasing.Call(call)
                        Function.DEFOVERRIDABLE -> Overridable(call)
                        Function.DEFSTRUCT -> Definition(
                            enclosingModular(call)!!,
                            call
                        )
                        Function.IMPORT -> org.elixir_lang.semantic.import.Call(call)
                        Function.QUOTE -> org.elixir_lang.semantic.quote.Call(enclosingModular(call), call)
                        Function.UNQUOTE -> org.elixir_lang.semantic.unquote.Call(call)
                        Function.USE -> org.elixir_lang.semantic.use.Call(call)
                        Function.REQUIRE -> org.elixir_lang.semantic.require.Call(call)
                        Function.VAR_BANG -> Bang(call)
                        else ->
                            enclosingModular(call)
                                ?.let { enclosingModular ->
                                    defs(enclosingModular, call, function)
                                }
                    }
                }
                0 -> when (function) {
                    Function.__MODULE__ -> org.elixir_lang.semantic.alias.Module(
                        enclosingModular(call),
                        call
                    )
                    else -> null
                }
                else -> null
            }
        }

        private fun computeSemanticFromModule(call: Call): Semantic? =
            call.functionName()?.let { function ->
                val arity = call.resolvedFinalArity()
                val hasDoBlockOrKeyword = call.hasDoBlockOrKeyword()

                when (arity) {
                    3 -> if (hasDoBlockOrKeyword) {
                        null
                    } else {
                        when (function) {
                            Function.CREATE -> org.elixir_lang.semantic.module.Create(call)
                            Function.PUT_ATTRIBUTE ->
                                Put(call)
                            Function.REGISTER_ATTRIBUTE ->
                                Register(call)
                            else -> null
                        }
                    }
                    else -> null
                }
            }

        private fun computeSemantic(keywordKey: ElixirKeywordKey): Semantic =
            if (keywordKey
                    .parent.let { it as? ElixirKeywordPair }
                    ?.parent?.let { it as? ElixirKeywords }
                    ?.parent?.let { it as? ElixirList }
                    ?.parent?.let { it as ElixirAccessExpression }
                    ?.parent?.let { it as? QuotableKeywordPair }
                    ?.keywordKey?.semantic?.let { it as? KeywordKey }?.name == "bind_quoted"
            ) {
                org.elixir_lang.semantic.variable.QuoteBound(keywordKey)
            } else {
                KeywordKey(keywordKey)
            }

        private val ENCLOSING_MODULAR: Key<CachedValue<Modular?>> = Key("elixir.enclosing_modular")

        fun enclosingModular(element: PsiElement): Modular? =
            CachedValuesManager.getCachedValue(element, ENCLOSING_MODULAR) {
                CachedValueProvider.Result.create(computeEnclosingModular(element), element)
            }

        private fun computeEnclosingModular(element: PsiElement): Modular? =
            element
                .ancestorSequence()
                .drop(1)
                .filterIsInstance<Call>()
                .map { semantic(it) }
                .filterIsInstance<Modular>()
                .firstOrNull()

        private fun defs(
            modular: Modular?,
            call: Call,
            function: String
        ): Semantic? =
            when (function) {
                Function.DEFMODULE -> defmodule(modular, call)
                else -> if (modular != null) {
                    when (function) {
                        Function.DEF -> def(modular, call)
                        Function.DEFP -> defp(modular, call)
                        Function.DEFMACRO -> defmacro(modular, call)
                        Function.DEFMACROP -> defmacrop(modular, call)
                        Function.DEFGUARD -> defguard(modular, call)
                        Function.DEFGUARDP -> defguardp(modular, call)
                        else -> null
                    }
                } else {
                    null
                }
            }

        private fun computeParentDependentSemantic(operation: Operation): Semantic? =
            operation.semanticParent?.let { semanticParent ->
                when (semanticParent) {
                    is org.elixir_lang.semantic.branching.Conditional ->
                        semanticParent.psiElement.let { it as? Call }?.finalArguments()?.singleOrNull()
                            ?.let { conditionPsiElement ->
                                if (operation == conditionPsiElement) {
                                    org.elixir_lang.semantic.branching.conditional.Condition(operation)
                                } else if (conditionPsiElement.isAncestor(operation)) {
                                    TODO()
                                } else {
                                    TODO()
                                }
                            }
                            ?: TODO()
                    else -> null
                }
            }

        private fun computeParentDependentSemantic(call: Call): Semantic? =
            call.semanticParent?.let { semanticParent ->
                when (semanticParent) {
                    is Specification -> {
                        semanticParent
                            .value
                            ?.let { value ->
                                when (value) {
                                    is Type -> if (value.leftOperand() == call) {
                                        Head(semanticParent, call)
                                    } else {
                                        org.elixir_lang.semantic.type.definition.specification.Body(
                                            semanticParent,
                                            call
                                        )
                                    }
                                    is When -> {
                                        val leftOperand = value.leftOperand()

                                        if (leftOperand.isAncestor(call)) {
                                            when (leftOperand) {
                                                is Type -> if (leftOperand.leftOperand() == call) {
                                                    Head(semanticParent, call)
                                                } else {
                                                    org.elixir_lang.semantic.type.definition.specification.Body(
                                                        semanticParent,
                                                        call
                                                    )
                                                }
                                                else -> TODO()
                                            }
                                        } else {
                                            TODO()
                                        }
                                    }
                                    else -> TODO()
                                }
                            }
                    }
                    is Head -> {
                        when (call) {
                            is QualifiedNoArgumentsCall<*> -> Qualified(call)
                            is UnqualifiedNoArgumentsCall<*> -> {
                                org.elixir_lang.semantic.type.definition.source.head.Parameter(semanticParent, call)
                            }
                            else -> TODO()
                        }
                    }
                    is org.elixir_lang.semantic.Quote -> {
                        if (call is UnqualifiedNoArgumentsCall<*> && call.resolvedFinalArity() == 0) {
                            QuoteDeclared(call)
                        } else {
                            TODO()
                        }
                    }
                    is Patternable -> if (call is UnqualifiedNoArgumentsCall<*> && call.resolvedFinalArity() == 0) {
                        when (semanticParent.outerMostPattern) {
                            Pattern.Match -> {
                                TODO()
                            }
                            Pattern.Parameter -> {
                                TODO()
                            }
                            null -> org.elixir_lang.semantic.ambiguous.Call(call)
                        }
                    } else {
                        org.elixir_lang.semantic.call.usage.Call(call)
                    }
                    is org.elixir_lang.semantic.call.Usage ->
                        if (call is UnqualifiedNoArgumentsCall<*> && call.resolvedFinalArity() == 0) {
                            org.elixir_lang.semantic.ambiguous.Call(call)
                        } else {
                            org.elixir_lang.semantic.call.usage.Call(call)
                        }
                    is org.elixir_lang.semantic.branching.Conditional -> {
                        semanticParent.psiElement.let { it as? Call }?.finalArguments()?.singleOrNull()
                            ?.let { conditionPsiElement ->
                                if (call == conditionPsiElement) {
                                    TODO()
                                } else if (conditionPsiElement.isAncestor(call)) {
                                    TODO()
                                } else {
                                    TODO()
                                }
                            }
                            ?: TODO()
                    }
                    else -> TODO()
                }
            }
    }
}

val <P : PsiElement> P.semantic: Semantic?
    get() = Semantic.semantic(this)

val PsiElement.semanticParent: Semantic?
    get() = CachedValuesManager.getCachedValue(this, PARENT) {
        CachedValueProvider.Result.create(
            // drop(1) to drop `this`, so that `semantic` isn't recursive
            this
                .ancestorSequence()
                .drop(1)
                .filter { it !is ElixirAccessExpression }
                .mapNotNull(PsiElement::semantic)
                .firstOrNull(),
            this
        )
    }

val PsiElement.enclosingModular: Modular?
    get() = Semantic.enclosingModular(this)
