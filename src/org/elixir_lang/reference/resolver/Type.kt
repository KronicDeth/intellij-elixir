package org.elixir_lang.reference.resolver

import com.intellij.openapi.project.Project
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.scope.type.MultiResolve
import org.elixir_lang.reference.Type
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.type.definition.Builtin

object Type : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Type> {
    override fun resolve(type: Type, incompleteCode: Boolean): Array<ResolveResult> {
        val element = type.element

        return if (element is Qualified) {
            resolve(element, incompleteCode)
        } else {
            resolve(element, incompleteCode)
        }
    }

    private fun resolve(qualified: Qualified, incompleteCode: Boolean): Array<ResolveResult> =
        qualified.qualifiedToModulars().flatMap { modular ->
            qualified.functionName()?.let { name ->
                val resolvedFinalArity = qualified.resolvedFinalArity()

                MultiResolve.resolveResults(name, resolvedFinalArity, incompleteCode, modular)
            } ?: emptyList()
        }.toTypedArray()

    private fun resolve(call: Call, incompleteCode: Boolean): Array<ResolveResult> =
            call
                    .functionName()
                    ?.let { name ->
                        val resolvedFinalArity = call.resolvedFinalArity()

                        val accumulator = MultiResolve.resolveResults(name, resolvedFinalArity, incompleteCode, call)

                        if (!accumulator.any(ResolveResult::isValidResult) && Builtin.`is`(name, resolvedFinalArity)) {
                            accumulator + resolveBuiltin(call.project, name, resolvedFinalArity)
                        } else {
                            accumulator
                        }.toTypedArray()
                    }
                    ?: emptyArray()

    private fun resolveBuiltin(project: Project, name: String, arity: Int): Array<ResolveResult> =
        Alias.modulars(project, ":erlang").flatMap { MultiResolve.resolveResults(name, arity, false, it) }.toTypedArray()
}
