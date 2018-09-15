package org.elixir_lang.mix.library

import com.intellij.openapi.roots.libraries.DummyLibraryProperties
import com.intellij.openapi.roots.libraries.PersistentLibraryKind

object Kind : PersistentLibraryKind<DummyLibraryProperties>("mix") {
    override fun createDefaultProperties(): DummyLibraryProperties = DummyLibraryProperties.INSTANCE
}
