package org.elixir_lang.psi.walk

import org.elixir_lang.psi.ElixirVisitor
import java.lang.reflect.Modifier

object GrammarShapes {
    /** Every generated PSI interface, read from the visitor GrammarKit maintains. */
    val INTERFACES: List<Class<*>> by lazy {
        ElixirVisitor::class.java.declaredMethods
            .filter { it.parameterCount == 1 }
            .map { it.parameterTypes[0] }
            .filter { it.isInterface && it.name.startsWith("org.elixir_lang.psi.Elixir") }
            .sortedBy { it.simpleName }
    }

    /**
     * The implementation class of every interface the parser can instantiate. Production classifies an element's
     * runtime class, so the implementations are what the tables are held against; a mixin adding an interface would
     * otherwise go unseen. The interfaces left out are in [DROPPED], which the coverage test pins.
     */
    val CONCRETE: List<Class<*>> by lazy {
        INTERFACES.mapNotNull { impl(it) }.filterNot { Modifier.isAbstract(it.modifiers) }
    }

    /** Interfaces with no implementation the parser can instantiate. */
    val DROPPED: List<Class<*>> by lazy {
        INTERFACES.filter { impl(it)?.let { impl -> Modifier.isAbstract(impl.modifiers) } ?: true }
    }

    /** The interface name a shape is known by, for messages. */
    fun name(impl: Class<*>): String = impl.simpleName.removeSuffix("Impl")

    private fun impl(shape: Class<*>): Class<*>? =
        try {
            Class.forName("org.elixir_lang.psi.impl.${shape.simpleName}Impl", false, shape.classLoader)
        } catch (e: ClassNotFoundException) {
            null
        }
}
