package org.elixir_lang.psi.walk

import junit.framework.TestCase
import org.elixir_lang.psi.ElixirEexTag
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirInterpolation
import org.elixir_lang.psi.ElixirKeywordKey
import org.elixir_lang.psi.ElixirMapUpdateArguments
import org.elixir_lang.psi.ElixirMatchedExpression
import org.elixir_lang.psi.ElixirTuple
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.psi.QualifiedAlias
import org.elixir_lang.annotator.ParameterWalk
import org.elixir_lang.psi.UnquotedVariableWalk
import org.elixir_lang.reference.VariableUseScopeWalk
import org.elixir_lang.reference.VariableWalk

/** Holds each walk's [Classifier] against the grammar, so a new rule fails here until it is given a bucket. */
class ShapeCoverageTest : TestCase() {
    fun testTheVisitorEnumeratesTheGeneratedSurface() {
        val shapes = GrammarShapes.CONCRETE

        assertTrue("only ${shapes.size} shapes; the visitor filter is wrong", shapes.size >= 150)
        assertTrue(ElixirTuple::class.java in shapes)
        assertFalse(ElixirMatchedExpression::class.java in shapes)
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
            // update arguments cannot be a pattern, so nothing is declared through them
            ElixirMapUpdateArguments::class.java,
            // an element `isVariable` starts from, never an ancestor
            ElixirVariable::class.java,
            // a variable qualifying an alias is searched inside the alias only
            QualifiedAlias::class.java
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

    private fun assertAgree(
        variable: VariableWalk.Bucket,
        useScope: Set<VariableUseScopeWalk.Bucket>,
        exceptions: List<Class<*>>
    ) {
        val shapes = GrammarShapes.CONCRETE.filter { VariableWalk.classifier.classify(it) == variable }
        val (excepted, checked) = shapes.partition { shape -> exceptions.any { it.isAssignableFrom(shape) } }

        val disagreeing = checked.filter { VariableUseScopeWalk.classifier.classify(it) !in useScope }.map { it.simpleName }
        assertTrue("$variable in isVariable but not ${useScope.joinToString("/")} in variableUseScope:\n  ${disagreeing.joinToString("\n  ")}", disagreeing.isEmpty())

        val stale = excepted.filter { VariableUseScopeWalk.classifier.classify(it) in useScope }.map { it.simpleName }
        assertTrue("no longer exceptions:\n  ${stale.joinToString("\n  ")}", stale.isEmpty())
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
