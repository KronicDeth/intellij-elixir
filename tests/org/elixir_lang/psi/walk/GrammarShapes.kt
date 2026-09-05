package org.elixir_lang.psi.walk

import org.elixir_lang.psi.ElixirVisitor
import java.lang.reflect.Modifier

object GrammarShapes {
    /** Every interface the visitor GrammarKit maintains has an overload for. */
    private val VISITED: List<Class<*>> by lazy {
        ElixirVisitor::class.java.declaredMethods
            .filter { it.parameterCount == 1 }
            .map { it.parameterTypes[0] }
            .filter { it.isInterface }
            .sortedBy { it.simpleName }
    }

    /** The generated PSI interfaces: the visited interfaces the grammar's class prefix names. */
    val INTERFACES: List<Class<*>> by lazy { VISITED.filter { isGenerated(it) } }

    /** The visited interfaces that are not generated: the hand-written markers rules implement, and the platform's. */
    val SKIPPED: List<Class<*>> by lazy { VISITED.filterNot { isGenerated(it) } }

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

    private fun isGenerated(shape: Class<*>): Boolean = shape.name.startsWith("org.elixir_lang.psi.Elixir")

    private fun impl(shape: Class<*>): Class<*>? =
        try {
            Class.forName("org.elixir_lang.psi.impl.${shape.simpleName}Impl", false, shape.classLoader)
        } catch (e: ClassNotFoundException) {
            null
        }
}
