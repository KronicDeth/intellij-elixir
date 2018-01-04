package org.elixir_lang.beam

import org.elixir_lang.beam.chunk.*

class Cache(private val beam: Beam) {
    val atoms: Atoms? by lazy { beam.atoms() }
    val attributes: Keyword? by lazy { beam.attributes() }
    val code: Code? by lazy { beam.code() }
    val compileInfo: Keyword? by lazy { beam.compileInfo() }
    val exports: CallDefinitions? by lazy { beam.exports(atoms) }
    val locals: CallDefinitions? by lazy { beam.locals(atoms) }
    val imports: Imports? by lazy { beam.imports(atoms) }
    val strings: Strings? by lazy { beam.strings() }

    fun chunkCollection(): Collection<Chunk> = beam.chunkCollection()
}
