package org.elixir_lang.heex.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.xml.XmlTag
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.Implementation
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.psi.scope.call_definition_clause.MultiResolve
import org.elixir_lang.reference.resolver.Module as ModuleResolver

/**
 * Resolves a `<.button>` / `<MyAppWeb.CoreComponents.button>` component tag to the `def`/`defp`
 * it names - an adapter over the existing call-definition and module resolution machinery
 * (`MultiResolve`, `reference.resolver.Module`), not new resolution logic. Local components enter
 * through the same view provider's Elixir root - which, once [ElixirFile.viewFile] knows about
 * HEEx, resolves `getContext()` into the surrounding view module the same way it already does for
 * `.eex`/`.leex`.
 */
object HeexComponentResolver {
    fun resolveDeclaration(tag: XmlTag): PsiElement? =
        CachedValuesManager.getCachedValue(tag) {
            CachedValueProvider.Result.create(doResolveDeclaration(tag), PsiModificationTracker.MODIFICATION_COUNT)
        }

    /** The module's arity-1 call definitions, local-component candidates for tag-name completion. */
    fun localComponentDefinitions(tag: XmlTag): List<Call> {
        val module = elixirRoot(tag)?.viewFile()?.modulars()?.singleOrNull() as? Call ?: return emptyList()

        return module.stabBodyChildExpressions()
            ?.filterIsInstance<Call>()
            ?.filter(CallDefinitionClause::isFunction)
            ?.filter { call -> arity1(call) }
            ?.toList()
            ?: emptyList()
    }

    private fun arity1(call: Call): Boolean =
        CallDefinitionClause.nameArityInterval(call, ResolveState.initial())?.arityInterval?.let { 1 in it } == true

    private fun doResolveDeclaration(tag: XmlTag): PsiElement? {
        val component = ComponentTagName.parse(tag.name) ?: return null
        val elixirRoot = elixirRoot(tag) ?: return null

        return when (component) {
            is ComponentTagName.Local -> resolveLocal(component.functionName, localEntrance(elixirRoot))
            is ComponentTagName.Remote -> resolveRemote(component.aliasChain, component.functionName, elixirRoot)
        }
    }

    // `psi.scope.maxScope()` bounds a treeWalkUp at `entrance.containingFile` - which, when
    // `entrance` already IS that file (as the bare Elixir root is), equals `entrance` itself, so the
    // walk stops after one level and never reaches `entrance.getContext()`. Taking the context
    // ourselves first - the injection host for a `~H` sigil, the surrounding view module's own file
    // for a real `.heex` file - gives `maxScope` a nested element to walk up from instead, so it
    // actually climbs into the module that defines the local component.
    //
    // Only local resolution needs this: remote resolution goes through `reference.resolver.Module`,
    // whose project-wide stub-index fallback finds a fully-qualified module regardless of scope and
    // whose `alias ... as:` scope-walk must start from the bare Elixir root.
    private fun localEntrance(elixirRoot: ElixirFile): PsiElement = elixirRoot.context ?: elixirRoot

    // `.originalFile` recovers the real, on-disk file (with a real parent directory to search) when
    // `tag` comes from completion's throwaway dummy-identifier copy of the file; it is a no-op
    // otherwise.
    private fun elixirRoot(tag: XmlTag): ElixirFile? =
        tag.containingFile.originalFile.viewProvider.getPsi(ElixirLanguage) as? ElixirFile

    private fun resolveLocal(functionName: String, entrance: PsiElement): PsiElement? =
        MultiResolve
            .resolveResults(functionName, 1, false, entrance)
            .firstOrNull { it.isValidResult }
            ?.element
            ?.let(::declarationTarget)

    private fun resolveRemote(aliasChain: String, functionName: String, entrance: PsiElement): PsiElement? =
        ModuleResolver
            .resolve(entrance, aliasChain, false)
            .filter { it.isValidResult }
            .mapNotNull { it.element }
            .filter(::isModular)
            .firstNotNullOfOrNull { modular ->
                MultiResolve.resolveResults(functionName, 1, false, modular).firstOrNull { it.isValidResult }?.element
            }
            ?.let(::declarationTarget)

    private fun isModular(element: PsiElement): Boolean =
        element is Call && (Module.`is`(element) || Protocol.`is`(element) || Implementation.`is`(element))

    // Navigation should land on the def's name, not the whole clause - mirroring how the rest of
    // the plugin's own Go To Declaration behaves for a regular Elixir call site.
    private fun declarationTarget(element: PsiElement): PsiElement =
        (element as? Call)
            ?.takeIf(CallDefinitionClause::isFunction)
            ?.let { CallDefinitionClause.nameIdentifier(it) }
            ?: element
}
