package org.elixir_lang.psi.walk

import com.intellij.psi.PsiElement

/**
 * Sorts a PSI element's shape into one of a walk's buckets. The first bucket naming a class the shape is assignable
 * to wins, so a narrower interface must precede a wider one it extends: every infix operation is a `Call`. A shape no
 * bucket names gets [fallback].
 *
 * No per-class cache: a `ClassValue` outlives the plugin class loader, and the scan costs what the `instanceof`
 * chain it replaces cost.
 */
class Classifier<B : Enum<B>>(buckets: List<Pair<B, List<Class<*>>>>, val fallback: B) {
    class Entry<B>(val shape: Class<*>, val bucket: B)

    /** Every named class with its bucket, in order. */
    val entries: List<Entry<B>> = buckets.flatMap { (bucket, shapes) -> shapes.map { Entry(it, bucket) } }

    fun classify(element: PsiElement): B = classify(element.javaClass)

    fun classify(shape: Class<*>): B = winner(shape)?.bucket ?: fallback

    /** The entry that decides [shape], or `null` when [fallback] applies. */
    fun winner(shape: Class<*>): Entry<B>? = entries.firstOrNull { it.shape.isAssignableFrom(shape) }
}
