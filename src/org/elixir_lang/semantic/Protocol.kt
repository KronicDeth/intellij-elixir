package org.elixir_lang.semantic

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.ImplementedProtocolName
import org.elixir_lang.semantic.modular.CanonicallyNamedMaybeEnclosed

interface Protocol : CanonicallyNamedMaybeEnclosed, Modular {
    override val canonicalName: String
    val implementations: kotlin.collections.List<Implementation>

    companion object {
        fun computeImplementations(protocol: Protocol): kotlin.collections.List<Implementation> {
            val implementations = mutableListOf<Implementation>()
            val project = protocol.psiElement.project

            StubIndex
                .getInstance()
                .processElements(
                    ImplementedProtocolName.KEY,
                    protocol.canonicalName,
                    project,
                    GlobalSearchScope.everythingScope(project),
                    NamedElement::class.java
                ) { nameElement ->
                    nameElement.semantic?.let { it as? Implementation }?.let { implementation ->
                        implementations.add(implementation)
                    }

                    true
                }

            return implementations
        }
    }
}
