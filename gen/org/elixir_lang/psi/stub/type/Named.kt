package org.elixir_lang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.NamedStubBase
import org.elixir_lang.psi.Definition
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.definition
import org.elixir_lang.psi.stub.call.Stubbic
import org.elixir_lang.psi.stub.index.*
import org.elixir_lang.semantic.semantic
import org.jetbrains.annotations.NonNls

abstract class Named<S : NamedStubBase<T>, T : PsiNameIdentifierOwner>(@NonNls debugName: String) : Element<S, T>(debugName) {
    override fun indexStub(stub: S, sink: IndexSink) {
        if (stub is Stubbic) {
            indexStubbic(stub as Stubbic, sink)
        } else {
            val name = stub.name

            if (name != null) {
                sink.occurrence<NamedElement, String>(AllName.KEY, name)

                when (val semantic = stub.let { it as? PsiElement }?.semantic) {
                    is org.elixir_lang.semantic.Modular -> {
                        sink.occurrence<NamedElement, String>(ModularName.KEY, name)

                        when (semantic) {
                            is org.elixir_lang.semantic.Implementation -> {
                                sink
                                    .occurrence<NamedElement, String>(ImplementedProtocolName.KEY, semantic.protocolName)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getExternalId(): String = "elixir." + super.toString()

    companion object {
        @JvmStatic
        fun <T : Stubbic> indexStubbic(stubbic: T, sink: IndexSink) {
            val nameSet = stubbic.canonicalNameSet()

            nameSet.forEach { name ->
                sink.occurrence<NamedElement, String>(AllName.KEY, name)
            }

            stubbic.definition?.let { definition ->
                if (definition.type == Definition.Type.MODULAR) {
                    nameSet.forEach { name ->
                        sink.occurrence<NamedElement, String>(ModularName.KEY, name)
                    }

                    if (definition == Definition.IMPLEMENTATION) {
                        stubbic.implementedProtocolName?.let { implementedProtocolName ->
                            sink.occurrence<NamedElement, String>(ImplementedProtocolName.KEY, implementedProtocolName)
                        }
                    }
                } else if (definition == Definition.MODULE_ATTRIBUTE) {
                    nameSet.forEach { name ->
                        sink.occurrence<NamedElement, String>(QuoteModuleAttributeName.KEY,  name)
                    }
                } else if (definition == Definition.VARIABLE) {
                    nameSet.forEach { name ->
                        sink.occurrence<NamedElement, String>(QuoteVariableName.KEY, name)
                    }
                }
            }
        }
    }
}
