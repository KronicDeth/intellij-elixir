package org.elixir_lang.beam.psi

interface Module : BeamSymbol {
    /** Never null: the stub always carries one, which is what makes this indexable by name. */
    override fun getName(): String

    fun callDefinitions(): Array<out CallDefinition>
    fun typeDefinitions(): Array<out TypeDefinition>
}
