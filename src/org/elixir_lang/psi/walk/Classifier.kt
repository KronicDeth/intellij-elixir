package org.elixir_lang.psi.walk

import com.intellij.psi.PsiElement
import java.util.concurrent.ConcurrentHashMap

/**
 * Sorts a PSI element's shape into one of a walk's buckets: the first entry naming a class the shape is assignable
 * to wins, so a narrower interface must precede a wider one it extends. A shape no entry names gets [fallback].
 *
 * A scan of the entries costs a few hundred nanoseconds per ancestor, so the answer is cached per runtime class in a
 * map this object owns. The map dies with the plugin, where a `ClassValue` would outlive its class loader; in return
 * it pins every class it has seen for the plugin's life, which is bounded to the Elixir PSI and the files it sits in.
 */
class Classifier<B : Enum<B>>(val entries: List<Entry<B>>, val fallback: B) {
    class Entry<B>(val shape: Class<*>, val bucket: B)

    private val cache = ConcurrentHashMap<Class<*>, B>()
    private val compute = java.util.function.Function<Class<*>, B> { winner(it)?.bucket ?: fallback }

    fun classify(element: PsiElement): B = classify(element.javaClass)

    fun classify(shape: Class<*>): B = cache[shape] ?: cache.computeIfAbsent(shape, compute)

    /** The entry that decides [shape], or `null` when [fallback] applies. */
    fun winner(shape: Class<*>): Entry<B>? = entries.firstOrNull { it.shape.isAssignableFrom(shape) }
}
