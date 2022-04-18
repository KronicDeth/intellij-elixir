package org.elixir_lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.beam.psi.stubs.ModuleStub
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.stub.index.ImplementedProtocolName

object Protocol {
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

    fun processImplementations(defprotocol: Call, consumer: Processor<in PsiElement>) {
        processImplementations(defprotocol.project, Module.name(defprotocol), consumer)
    }

    fun processImplementations(defprotocol: ModuleImpl<*>, consumer: Processor<in PsiElement>) {
        processImplementations(defprotocol.project, defprotocol.name!!, consumer)
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
