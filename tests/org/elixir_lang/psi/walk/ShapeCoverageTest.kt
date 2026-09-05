package org.elixir_lang.psi.walk

import junit.framework.TestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirMatchedExpression
import org.elixir_lang.psi.ElixirTuple

/** Holds each walk's [Classifier] against the grammar, so a new rule fails here until it is given a bucket. */
class ShapeCoverageTest : TestCase() {
    fun testTheVisitorEnumeratesTheGeneratedSurface() {
        val shapes = GrammarShapes.CONCRETE

        assertTrue("only ${shapes.size} shapes; the visitor filter is wrong", shapes.size >= 150)
        assertTrue(ElixirTuple::class.java in shapes)
        assertFalse(ElixirMatchedExpression::class.java in shapes)
    }

    private fun <B : Enum<B>> assertCovers(classifier: Classifier<B>) {
        val shapes = GrammarShapes.CONCRETE + ElixirFile::class.java

        val unnamed = shapes.filter { classifier.winner(it) == null }.map { it.simpleName }
        assertTrue("no bucket names:\n  ${unnamed.joinToString("\n  ")}", unnamed.isEmpty())

        val winners = shapes.mapNotNull { classifier.winner(it) }.toSet()
        val dead = classifier.entries.filter { it !in winners }.map { "${it.shape.simpleName} (${it.bucket})" }
        assertTrue("no shape reaches these entries first:\n  ${dead.joinToString("\n  ")}", dead.isEmpty())
    }
}
