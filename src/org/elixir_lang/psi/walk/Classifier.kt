package org.elixir_lang.psi.walk

import com.intellij.psi.PsiElement
import java.util.concurrent.ConcurrentHashMap

/**
 * Sorts a PSI element's shape into one of a walk's buckets. The first bucket naming a class the shape is assignable
 * to wins, so a narrower interface must precede a wider one it extends: every infix operation is a `Call`. A shape no
 * bucket names gets [fallback].
 *
 * A scan of the lists costs a few hundred nanoseconds per ancestor, so the answer is cached per runtime class in a
 * map this object owns. The map dies with the plugin, where a `ClassValue` would outlive its class loader; in return
 * it pins every class it has seen for the plugin's life, which is bounded to the Elixir PSI and the files it sits in.
 */
class Classifier<B : Enum<B>>(buckets: List<Pair<B, List<Class<*>>>>, val fallback: B) {
    class Entry<B>(val shape: Class<*>, val bucket: B)

    /** Every named class with its bucket, in order. */
    val entries: List<Entry<B>> = buckets.flatMap { (bucket, shapes) -> shapes.map { Entry(it, bucket) } }

    private val cache = ConcurrentHashMap<Class<*>, B>()

    fun classify(element: PsiElement): B = classify(element.javaClass)

    fun classify(shape: Class<*>): B = cache.computeIfAbsent(shape) { winner(it)?.bucket ?: fallback }

    /** The entry that decides [shape], or `null` when [fallback] applies. */
    fun winner(shape: Class<*>): Entry<B>? = entries.firstOrNull { it.shape.isAssignableFrom(shape) }
}
