package org.elixir_lang.psi.stub.type

import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.NamedStubBase
import org.elixir_lang.psi.Definition
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.definition
import org.elixir_lang.psi.stub.call.Stubbic
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.psi.stub.index.ModularName
import org.jetbrains.annotations.NonNls

abstract class Named<S : NamedStubBase<T>, T : PsiNameIdentifierOwner>(@NonNls debugName: String) : Element<S, T>(debugName) {
    override fun indexStub(stub: S, sink: IndexSink) {
        if (stub is Stubbic) {
            indexStubbic(stub as Stubbic, sink)
        } else {
            val name = stub.name

            if (name != null) {
                sink.occurrence<NamedElement, String>(AllName.KEY, name)

                if (stub is Call && definition(stub)?.type == Definition.Type.MODULAR) {
                    sink.occurrence<NamedElement, String>(ModularName.KEY, name)
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

            if (stubbic.definition?.type == Definition.Type.MODULAR) {
                nameSet.forEach { name ->
                    sink.occurrence<NamedElement, String>(ModularName.KEY, name)
                }
            }
        }
    }
}
