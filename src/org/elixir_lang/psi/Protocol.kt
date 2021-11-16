package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.stub.index.ImplementedProtocolName

object Protocol {
    @JvmStatic
    fun `is`(call: Call): Boolean =
            call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DEFPROTOCOL, 2)

    fun processImplementations(defprotocol: Call, consumer: Processor<in PsiElement>) {
        val project = defprotocol.project

        StubIndex
                .getInstance()
                .processElements(
                        ImplementedProtocolName.KEY,
                        Module.name(defprotocol),
                        project,
                        GlobalSearchScope.everythingScope(project),
                        NamedElement::class.java,
                        consumer
                )
    }
}
