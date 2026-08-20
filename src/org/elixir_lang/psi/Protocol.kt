package org.elixir_lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.beam.psi.stubs.ModuleStub
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.stub.index.ImplementedProtocolName
import org.jetbrains.annotations.Contract

object Protocol {
    @RequiresReadLock
    @JvmStatic
    fun `is`(call: Call): Boolean =
        call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DEFPROTOCOL, 2)

    fun `is`(moduleImpl: ModuleImpl<*>): Boolean =
        moduleImpl.callDefinitions().any { callDefinitionImpl ->
            callDefinitionImpl.name == "__protocol__" && callDefinitionImpl.exportedArity(ResolveState.initial()) == 1
        }

    fun `is`(moduleStub: ModuleStub<*>): Boolean =
        moduleStub
            .getChildrenByType(ModuleStubElementTypes.CALL_DEFINITION, emptyArray<CallDefinitionImpl<*>>())
            .any { callDefinition ->
                callDefinition.name == "__protocol__" && callDefinition.exportedArity(ResolveState.initial()) == 1
            }

    /**
     * `true` if [element] is (within) the name/head of a definition clause inside a `defprotocol`,
     * e.g. `foo` or `foo()` in `def foo(); ... end` inside `defprotocol MyProtocol do ... end`.
     *
     * Used so the Symbol model (the `ProtocolFunction` symbol) owns Find Usages / navigation for protocol
     * function names instead of the legacy `PsiReference`-based find-usages, which otherwise contributes a
     * redundant second target.
     */
    @RequiresReadLock
    @Contract(pure = true)
    fun isHead(element: PsiElement): Boolean {
        // Walk up through ALL Call ancestors to find the nearest CallDefinitionClause.
        // `getParentOfType` alone is insufficient because intermediate non-CDC Calls (e.g. the inner
        // `perform()` argument call inside `def perform()`) would be found first and rejected.
        val defClause = generateSequence(element) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
            ?: return false

        // Check if this clause is inside a defprotocol
        val defprotocol = CallDefinitionClause.enclosingModularMacroCall(defClause) ?: return false
        if (!`is`(defprotocol)) return false

        // Check if element is at or within the name/head of the clause.
        // Use bidirectional isAncestor: either element is inside nameIdentifier (element IS the identifier or
        // deeper), or element contains nameIdentifier (element is the defClause or an intermediate parent).
        val nameIdentifier = CallDefinitionClause.nameIdentifier(defClause) ?: return false
        return PsiTreeUtil.isAncestor(nameIdentifier, element, false) ||
               PsiTreeUtil.isAncestor(element, nameIdentifier, false)
    }

    fun processImplementations(defprotocol: Call, consumer: Processor<in PsiElement>) {
        processImplementations(defprotocol.project, Module.name(defprotocol), consumer)
    }

    fun processImplementations(defprotocol: ModuleImpl<*>, consumer: Processor<in PsiElement>) {
        processImplementations(defprotocol.project, defprotocol.name, consumer)
    }

    private fun processImplementations(project: Project, name: String, consumer: Processor<in PsiElement>) {
        StubIndex
            .getInstance()
            .processElements(
                ImplementedProtocolName.KEY,
                name,
                project,
                GlobalSearchScope.everythingScope(project),
                NamedElement::class.java,
                consumer
            )
    }
}
