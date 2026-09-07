package org.elixir_lang.psi.walk

import org.elixir_lang.psi.ElixirVisitor
import java.io.File
import java.lang.reflect.Modifier

object GrammarShapes {
    private val LOADER = ElixirVisitor::class.java.classLoader

    /** Every generated PSI interface, read from the files GrammarKit writes under `gen/`. */
    val INTERFACES: List<Class<*>> by lazy {
        (File("gen/org/elixir_lang/psi").listFiles { file -> file.name.matches(Regex("Elixir\\w+\\.java")) }
            ?.takeIf { it.isNotEmpty() }
            ?: error("no generated PSI under ${System.getProperty("user.dir")}; generate the parser from Elixir.bnf"))
            .map { Class.forName("org.elixir_lang.psi.${it.nameWithoutExtension}", false, LOADER) }
            .filter { it.isInterface && it != org.elixir_lang.psi.ElixirTypes::class.java }
            .sortedBy { it.simpleName }
    }

    /** Every interface the visitor GrammarKit maintains has an overload for. */
    private val VISITED: List<Class<*>> by lazy {
        ElixirVisitor::class.java.declaredMethods
            .filter { it.parameterCount == 1 }
            .map { it.parameterTypes[0] }
            .filter { it.isInterface }
            .sortedBy { it.simpleName }
    }

    /** The visited interfaces that are not generated: the hand-written markers rules implement, and the platform's. */
    val SKIPPED: List<Class<*>> by lazy { VISITED.filterNot { it in INTERFACES } }

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
        } catch (_: ClassNotFoundException) {
            null
        }
}
