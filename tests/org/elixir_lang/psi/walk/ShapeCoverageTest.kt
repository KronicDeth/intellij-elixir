package org.elixir_lang.psi.walk

import junit.framework.TestCase
import org.elixir_lang.psi.ElixirEexTag
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirInterpolation
import org.elixir_lang.psi.ElixirKeywordKey
import org.elixir_lang.psi.ElixirMatchedExpression
import org.elixir_lang.psi.ElixirUnmatchedExpression
import org.elixir_lang.psi.impl.ElixirTupleImpl
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.annotator.ParameterWalk
import org.elixir_lang.psi.UnquotedVariableWalk
import org.elixir_lang.reference.VariableUseScopeWalk
import org.elixir_lang.reference.VariableWalk

/** Holds each walk's [Classifier] against the grammar, so a new rule fails here until it is given a bucket. */
class ShapeCoverageTest : TestCase() {
    fun testTheVisitorEnumeratesTheGeneratedSurface() {
        val shapes = GrammarShapes.CONCRETE

        assertTrue("only ${shapes.size} shapes; the visitor filter is wrong", shapes.size >= 150)
        assertTrue(ElixirTupleImpl::class.java in shapes)
        // the two `extends` bases are the only interfaces the parser never instantiates
        assertEquals(
            listOf(ElixirMatchedExpression::class.java, ElixirUnmatchedExpression::class.java),
            GrammarShapes.DROPPED
        )
    }

    fun testVariableWalkNamesEveryShape() = assertCovers(VariableWalk.classifier)

    fun testVariableUseScopeWalkNamesEveryShape() = assertCovers(VariableUseScopeWalk.classifier)

    fun testParameterWalkNamesEveryShape() = assertCovers(ParameterWalk.classifier)

    fun testUnquotedVariableWalkNamesEveryShape() = assertCovers(UnquotedVariableWalk.classifier)

    /** A shape `isVariable` looks through must carry the use scope walk too, or Find Usages loses the variable. */
    fun testUseScopeWalksThroughWhatIsVariableLooksThrough() = assertAgree(
        VariableWalk.Bucket.TRANSPARENT,
        setOf(VariableUseScopeWalk.Bucket.PARENT),
        listOf(
            // a tag's variables are not searched above the tag, pinned by Issue1831Test
            ElixirEexTag::class.java,
            // an element `isVariable` starts from, never an ancestor
            ElixirVariable::class.java
        )
    )

    /** A shape that makes an identifier a variable must give it a scope. */
    fun testUseScopeScopesWhatIsVariableDeclares() = assertAgree(
        VariableWalk.Bucket.DECLARES,
        VariableUseScopeWalk.Bucket.entries.toSet() - VariableUseScopeWalk.Bucket.EMPTY - VariableUseScopeWalk.Bucket.LEAF,
        listOf(
            // identifiers in an interpolation are uses; nothing is declared there
            ElixirInterpolation::class.java,
            // an element `isVariable` starts from, never an ancestor
            ElixirKeywordKey::class.java
        )
    )

    /** An unquote container left unfollowed is only open if a variable can be declared through it. */
    fun testUnfollowedUnquoteContainersCanDeclareAVariable() {
        val closed = GrammarShapes.CONCRETE.filter {
            UnquotedVariableWalk.classifier.classify(it) == UnquotedVariableWalk.Bucket.UNFOLLOWED &&
                VariableWalk.classifier.classify(it) !in setOf(VariableWalk.Bucket.TRANSPARENT, VariableWalk.Bucket.DECLARES)
        }.map { GrammarShapes.name(it) }

        assertTrue("stops, not unfollowed:\n  ${closed.joinToString("\n  ")}", closed.isEmpty())
    }

    private fun assertAgree(
        variable: VariableWalk.Bucket,
        useScope: Set<VariableUseScopeWalk.Bucket>,
        exceptions: List<Class<*>>
    ) {
        val shapes = GrammarShapes.CONCRETE.filter { VariableWalk.classifier.classify(it) == variable }
        val (excepted, checked) = shapes.partition { shape -> exceptions.any { it.isAssignableFrom(shape) } }

        val disagreeing = checked.filter { VariableUseScopeWalk.classifier.classify(it) !in useScope }.map { GrammarShapes.name(it) }
        val expected = useScope.joinToString("/")
        assertTrue("$variable in isVariable but not $expected in variableUseScope:\n  ${disagreeing.joinToString("\n  ")}", disagreeing.isEmpty())

        val stale = excepted.filter { VariableUseScopeWalk.classifier.classify(it) in useScope }.map { GrammarShapes.name(it) }
        assertTrue("no longer exceptions:\n  ${stale.joinToString("\n  ")}", stale.isEmpty())
    }

    private fun <B : Enum<B>> assertCovers(classifier: Classifier<B>) {
        val shapes = GrammarShapes.CONCRETE + ElixirFile::class.java

        val unnamed = shapes.filter { classifier.winner(it) == null }.map { GrammarShapes.name(it) }
        assertTrue("no bucket names:\n  ${unnamed.joinToString("\n  ")}", unnamed.isEmpty())

        val winners = shapes.mapNotNull { classifier.winner(it) }.toSet()
        val dead = classifier.entries.filter { it !in winners }.map { "${GrammarShapes.name(it.shape)} (${it.bucket})" }
        assertTrue("no shape reaches these entries first:\n  ${dead.joinToString("\n  ")}", dead.isEmpty())
    }
}
