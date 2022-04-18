package org.elixir_lang.beam.chunk

import gnu.trove.THashSet
import org.elixir_lang.call.Visibility
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.chunk.Chunk.Companion.unsignedInt
import org.elixir_lang.beam.chunk.call_definitions.CallDefinition.Companion.from
import org.elixir_lang.beam.chunk.Chunk.TypeID
import org.elixir_lang.beam.MacroNameArity
import org.elixir_lang.beam.chunk.call_definitions.CallDefinition
import java.util.*

class CallDefinitions(private val typeID: TypeID, var callDefinitionCollection: Collection<CallDefinition>) {
    /**
     * Map of sets of [MacroNameArity] sorted as
     * 1. [MacroNameArity.name] is sorted alphabetically
     * 2. [MacroNameArity.arity] is sorted ascending
     *
     * @return The sorted sets collectively will be the same size as [.callDefinitionCollection] unless
     * [CallDefinition.name] returns `null` for some [CallDefinition]s.
     */
    fun macroNameAritySortedSetByMacro(): Map<String, SortedSet<MacroNameArity>> =
        callDefinitionCollection.fold(mutableMapOf()) { acc, callDefinition ->
            val exportName = callDefinition.name
            if (exportName != null) {
                val macroNameArity = MacroNameArity(visibility(), exportName, callDefinition.arity.toInt())
                val macro = macroNameArity.macro
                val macroNameAritySortedSet = acc.computeIfAbsent(macro) { TreeSet() }
                macroNameAritySortedSet.add(macroNameArity)
            }

            acc
        }

    private fun visibility(): Visibility = VISIBILITY_BY_TYPE_ID[typeID]!!

    /**
     * The number of call_definitions
     */
    fun size(): Int = callDefinitionCollection.size

    companion object {
        private val VISIBILITY_BY_TYPE_ID: Map<TypeID, Visibility> =
            mapOf(TypeID.EXPT to Visibility.PUBLIC, TypeID.LOCT to Visibility.PRIVATE)

        fun from(chunk: Chunk, typeID: TypeID, atoms: Atoms?): CallDefinitions? =
            if (chunk.typeID == typeID.toString() && chunk.data.size >= 4) {
                val callDefinitionCollection: MutableCollection<CallDefinition> = THashSet()
                var offset = 0
                val exportCountByteCount = unsignedInt(chunk.data, 0)
                val exportCount = exportCountByteCount.first
                offset += exportCountByteCount.second
                for (i in 0 until exportCount) {
                    val (first, second) = from(chunk, offset, atoms)
                    callDefinitionCollection.add(first)
                    offset += second
                }
                CallDefinitions(typeID, callDefinitionCollection)
            } else {
                null
            }

        fun macroNameAritySortedSetByMacro(beam: Beam, atoms: Atoms): Map<String, SortedSet<MacroNameArity>> =
            beam.callDefinitionsList(atoms).fold(mutableMapOf()) { acc, callDefinitions ->
                val macroNameAritySortedSetByMacro = callDefinitions.macroNameAritySortedSetByMacro()

                for ((macro, macroNameAritySortedSet) in macroNameAritySortedSetByMacro) {
                    val accMacroNameAritySortedSet = acc.computeIfAbsent(macro) { TreeSet() }

                    accMacroNameAritySortedSet.addAll(macroNameAritySortedSet)
                }

                acc
            }
    }
}
