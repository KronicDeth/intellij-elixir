package org.elixir_lang.psi.stub.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import org.elixir_lang.psi.NamedElement

/**
 * Keys: The protocol being implemented.
 * Values: The `defimpl` call implementimg the key.
 *
 * Used to lookup all implementations of a protocol for "Go To Definitions".
 */
class ImplementedProtocolName : StringStubIndexExtension<NamedElement>() {
    override fun getVersion(): Int = super.getVersion() + VERSION

    override fun getKey(): StubIndexKey<String, NamedElement> = KEY

    companion object {
        @JvmField
        val KEY = StubIndexKey.createIndexKey<String, NamedElement>("elixir.implemented_protocol.name")
    }
}

private const val VERSION = 0
