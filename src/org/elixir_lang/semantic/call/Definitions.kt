package org.elixir_lang.semantic.call

import org.elixir_lang.NameArityInterval
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility

class Definitions(val enclosingModular: Modular) {
    fun get(time: Time, visibility: Visibility, nameArityInterval: NameArityInterval): Definition {
        val name = nameArityInterval.name
        val arityInterval = nameArityInterval.arityInterval
        val definitions = definitionsByName.getOrPut(name, ::mutableListOf)
        var existingDefinition: Definition? = null

        for (definition in definitions) {
            val definitionArityInterval = definition.nameArityInterval.arityInterval

            if (definitionArityInterval.overlaps(arityInterval)) {
                existingDefinition = definition.expandArityInterval(arityInterval)
                break
            }
        }

        return if (existingDefinition != null) {
            existingDefinition
        } else {
            val newDefinition = Definition(enclosingModular, time, visibility, nameArityInterval)
            var inserted = false

            for ((index, definition) in definitions.withIndex()) {
                if (arityInterval < definition.nameArityInterval.arityInterval) {
                    definitions.add(index, newDefinition)
                    inserted = true
                    break
                }
            }

            if (!inserted) {
                definitions.add(newDefinition)
            }

            newDefinition
        }
    }

    private val definitionsByName: MutableMap<String, MutableList<Definition>> = mutableMapOf()

    fun merge(definitions: Definitions): Definitions {
        for (definition in definitions) {
            put(definition)
        }

        return this;
    }

    fun put(definition: Definition): Definition {
        val nameArityInterval = definition.nameArityInterval
        val name = nameArityInterval.name
        val arityInterval = nameArityInterval.arityInterval


    }
}
