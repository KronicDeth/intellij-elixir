package org.elixir_lang.semantic.call.definition.eex

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Arity
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Named
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.documentation.CallDefinition
import org.elixir_lang.semantic.modular.Enclosed

abstract class FunctionFrom(override val enclosingModular: Modular, val call: Call) : Named, Enclosed, Semantic,
                                                                                      Definition {
    override val decompiled: List<Definition>
        get() = TODO("Not yet implemented")
    override val visibility: Visibility
        get() = CachedValuesManager.getCachedValue(call, VISIBILITY) {
            CachedValueProvider.Result.create(computeVisibility(call), call)
        }
    override val nameArityInterval: NameArityInterval?
        get() = CachedValuesManager.getCachedValue(call, NAME_ARITY_INTERVAL) {
            CachedValueProvider.Result.create(computeNameArityInterval(this), call)
        }
    override val name: String?
        get() = CachedValuesManager.getCachedValue(call, NAME) {
            CachedValueProvider.Result.create(computeName(call), call)
        }
    private val arity: Arity?
        get() = CachedValuesManager.getCachedValue(call, ARITY) {
            CachedValueProvider.Result.create(computeArity(call), call)
        }
    override val docs: List<CallDefinition>
        get() = TODO("Not yet implemented")
    override val clauses: List<Clause>
        get() = TODO("Not yet implemented")
    override val psiElement: PsiElement
        get() = call

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    override val time: Time = Time.RUN

    companion object {
        private val VISIBILITY: Key<CachedValue<Visibility>> =
            Key("elixir.semantic.call.definition.eex.function_from.visibility")

        private fun computeVisibility(call: Call): Visibility =
            call.finalArguments()?.get(0)?.stripAccessExpression()
                ?.let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { macro ->
                    when (macro) {
                        Function.DEF -> Visibility.PUBLIC
                        Function.DEFP -> Visibility.PRIVATE
                        else -> null
                    }
                }
                ?: Visibility.PUBLIC

        private val NAME_ARITY_INTERVAL: Key<CachedValue<NameArityInterval?>> =
            Key("elixir.semantic.call.definition.eex.function_from.name_arity_interval")

        private fun computeNameArityInterval(functionFrom: FunctionFrom): NameArityInterval? =
            functionFrom.name?.let { name ->
                functionFrom.arity?.let { arity ->
                    NameArityInterval(name, ArityInterval(arity, arity))
                }
            }

        private val NAME: Key<CachedValue<String?>> =
            Key("elixir.semantic.call.definition.eex.function_from.name")

        private fun computeName(call: Call): String? =
            call.finalArguments()?.get(1)?.stripAccessExpression()?.let { it as? ElixirAtom }?.node?.lastChildNode?.text

        private val ARITY: Key<CachedValue<Arity?>> =
            Key("elixir.semantic.call.definition.eex.function_from.name")

        private fun computeArity(call: Call): Arity? =
            call.finalArguments()?.let { arguments ->
                if (arguments.size >= 4) {
                    // function_from_file(kind, name, file, args)
                    // function_from_file(kind, name, file, args, options)
                    // function_from_string(kind, name, template, args)
                    // function_from_string(kind, name, template, args, options)
                    arguments[3].stripAccessExpression().let { it as? ElixirList }?.children?.size
                } else {
                    // function_from_file(kind, name, file) where args defaults to `[]`
                    // function_from_string(kind, name, template) where args defaults to `[]`
                    0
                }
            }
    }
}
