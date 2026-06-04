package org.elixir_lang.mix.library

import com.intellij.openapi.roots.libraries.DummyLibraryProperties
import com.intellij.openapi.roots.libraries.PersistentLibraryKind

object Kind : PersistentLibraryKind<DummyLibraryProperties>("mix") {
    override fun createDefaultProperties(): DummyLibraryProperties = DummyLibraryProperties.INSTANCE
}

/** Suffix appended to the project-wide consolidated-protocols library name, e.g. `mymodule (consolidated)`. */
internal const val CONSOLIDATED_LIBRARY_SUFFIX = "(consolidated)"
