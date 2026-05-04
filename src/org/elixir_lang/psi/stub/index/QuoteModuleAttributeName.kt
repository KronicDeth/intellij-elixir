package org.elixir_lang.psi.stub.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import org.elixir_lang.psi.NamedElement

class QuoteModuleAttributeName : StringStubIndexExtension<NamedElement>() {
    override fun getVersion(): Int = super.getVersion() + VERSION

    override fun getKey(): StubIndexKey<String, NamedElement> = KEY

    companion object {
        @JvmField
        val KEY = StubIndexKey.createIndexKey<String, NamedElement>("elixir.quote.module_attribute.name")
    }
}

private const val VERSION = 0
