package org.elixir_lang.psi.walk

import org.elixir_lang.psi.ElixirVisitor
import java.lang.reflect.Modifier

object GrammarShapes {
    /**
     * Every generated PSI interface the parser can instantiate, read from the visitor GrammarKit maintains. Its
     * overloads for hand-written markers and the two abstract `extends` bases are dropped.
     */
    val CONCRETE: List<Class<*>> by lazy {
        ElixirVisitor::class.java.declaredMethods
            .filter { it.parameterCount == 1 }
            .map { it.parameterTypes[0] }
            .filter { it.isInterface && it.name.startsWith("org.elixir_lang.psi.Elixir") }
            .filter { shape ->
                val impl = Class.forName("org.elixir_lang.psi.impl.${shape.simpleName}Impl", false, shape.classLoader)
                !Modifier.isAbstract(impl.modifiers)
            }
            .sortedBy { it.simpleName }
    }
}
