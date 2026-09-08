package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.operation.Pipe
import org.elixir_lang.psi.walk.ShapeTable

/**
 * Correlates a position in a match's pattern with the value at the same position on the right, so a name bound by
 * destructuring carries the one value it was bound to instead of the whole right-hand side.
 */
object Destructure {
    /**
     * The values [declaration] is bound to by `[pattern] = [value]`, empty when it is bound to nothing.
     *
     * A value this cannot take apart is answered whole to *every* position, so `[_, x] = fragments()` claims
     * everything `fragments/0` returns. Deliberate: refusing it would cost `[x] = fragments()`, which is how a
     * `__using__` usually reaches its fragments. A quote is the one exception, refused below. The `t` of `[h | t]`
     * binds nothing - issue #4067.
     */
    @RequiresReadLock
    fun valuesAt(pattern: PsiElement?, value: PsiElement?, declaration: PsiElement): List<PsiElement> =
        valuesAt(pattern, value, declaration, emptySet())

    private fun valuesAt(
        pattern: PsiElement?,
        value: PsiElement?,
        declaration: PsiElement,
        followed: Set<PsiElement>
    ): List<PsiElement> {
        val strippedPattern = pattern?.stripAccessExpression() ?: return emptyList()
        val strippedValue = value?.stripAccessExpression() ?: return emptyList()

        if (strippedPattern == declaration) {
            return listOf(strippedValue)
        }

        if (!PsiTreeUtil.isAncestor(strippedPattern, declaration, true)) {
            return emptyList()
        }

        if (!sameShapeAs(strippedPattern, strippedValue)) {
            // two containers of different kinds are a match that raises, so nothing is bound
            if (bucket(strippedValue) != Bucket.OPAQUE) {
                return emptyList()
            }

            val bounds = boundValues(strippedValue, followed)

            return if (bounds.isEmpty()) {
                listOf(strippedValue)
            } else {
                bounds.flatMap { bound -> valuesAt(strippedPattern, bound.value, declaration, bound.followed) }
            }
        }

        return descend(strippedPattern, strippedValue, declaration).flatMap { (nextPattern, nextValue) ->
            valuesAt(nextPattern, nextValue, declaration, followed)
        }
    }

    private data class Bound(val value: PsiElement, val followed: Set<PsiElement>)

    /**
     * What a name on the right of a match was itself bound to. [followed] bounds a chain of them; a read on the right
     * of `=` resolves to a strictly earlier binding, so it is insurance against that rule changing, not a live cycle.
     */
    private fun boundValues(value: PsiElement, followed: Set<PsiElement>): List<Bound> =
        // a variable read is the only shape whose binding is a match, and resolving anything else costs a stub-index
        // or decompiler round trip to reach the same empty answer
        (value as? UnqualifiedNoArgumentsCall<*>)
            ?.let { it.reference as? PsiPolyVariantReference }
            ?.multiResolve(false)
            ?.filter { it.isValidResult }
            ?.mapNotNull { it.element }
            ?.filterNot { it in followed }
            ?.flatMap { declaration ->
                PsiTreeUtil
                    .getParentOfType(declaration, Match::class.java)
                    ?.let { match ->
                        val stepped = followed + declaration

                        valuesAt(match.leftOperand(), match.rightOperand(), declaration, stepped)
                            .map { bound -> Bound(bound, stepped) }
                    }
                    .orEmpty()
            }
            .orEmpty()

    /**
     * How a shape can be taken apart by a match. Read from [ShapeTable], so a new grammar shape fails
     * `ShapeCoverageTest` until somebody classifies it rather than defaulting silently.
     */
    enum class Bucket {
        /** Positions matched by index; `[h | t]` matches any list at least as long as its heads. */
        LIST,
        /** Positions matched by index, counting a trailing keyword list as the single element it is. */
        TUPLE,
        /** Keys, of which the pattern may name a subset. */
        MAP,
        /** One key and its value, as a keyword list's elements are. */
        KEYWORD_PAIR,
        /** Cannot be taken apart here, so a name in the pattern may carry whatever it holds. */
        OPAQUE
    }

    val classifier = ShapeTable.column(Bucket.OPAQUE) { it.destructure }

    private fun bucket(element: PsiElement): Bucket = classifier.classify(element)

    private fun sameShapeAs(pattern: PsiElement, value: PsiElement): Boolean =
        bucket(pattern).let { it != Bucket.OPAQUE && it == bucket(value) }

    /** The pair one level in from [pattern] and [value] that still contains [declaration], or none. */
    private fun descend(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): List<Pair<PsiElement, PsiElement>> =
        when (bucket(pattern)) {
            Bucket.MAP -> byKey(pattern, value, declaration)
            Bucket.LIST, Bucket.TUPLE -> byPosition(pattern, value, declaration)
            Bucket.KEYWORD_PAIR -> byPairedKey(pattern, value, declaration)
            // `sameShapeAs` has already answered false, so `valuesAt` never descends into one
            Bucket.OPAQUE -> emptyList()
        }

    private fun byPosition(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): List<Pair<PsiElement, PsiElement>> {
        val (heads, tail) = positions(pattern) ?: return emptyList()
        val (valueElements, valueTail) = positions(value) ?: return emptyList()

        // a cons on the right leaves the value's length unknown; conservative, since `[x] = [h | t]` matches only
        // when `t` is empty
        if (valueTail != null) {
            return emptyList()
        }

        // `[h | t]` matches any list at least as long as its heads; without a tail the lengths have to agree exactly
        val linesUp = if (tail == null) heads.size == valueElements.size else heads.size <= valueElements.size

        if (!linesUp) {
            return emptyList()
        }

        val index = heads.indexOfFirst { PsiTreeUtil.isAncestor(it, declaration, false) }

        if (index != -1) {
            return listOf(heads[index] to valueElements[index])
        }

        // `[h | t]`'s tail binds nothing - issue #4067; `valuesAt`'s KDoc records why, and a test pins it
        return emptyList()
    }

    /** The positions of a tuple or list: those matched by index, and the one `[h | t]` binds the rest to. */
    private data class Positions(val heads: List<PsiElement>, val tail: PsiElement?)

    private fun positions(container: PsiElement): Positions? {
        val elements = elements(container)

        if (bucket(container) != Bucket.LIST) {
            return Positions(elements, null)
        }

        // `[a, b | t]` parses as the elements before `b | t`, then one pipe operation carrying the last two positions
        val cons = elements.lastOrNull()?.stripAccessExpression() as? Pipe ?: return Positions(elements, null)
        // null, not a fixed-length reading: a half-typed cons would otherwise be less conservative than a whole one
        val head = cons.leftOperand() ?: return null
        val tail = cons.rightOperand() ?: return null

        return Positions(elements.dropLast(1) + head, tail)
    }

    /**
     * A container's elements as Elixir counts them: `containerArguments` is private, so they are its own child
     * expressions, except that a trailing keyword list is one child but one element per pair.
     */
    private fun elements(container: PsiElement): List<PsiElement> =
        container.childExpressions().toList().flatMap { child ->
            val stripped = child.stripAccessExpression()

            // `[q, k: 1, j: 2]` is a list of three but `{q, k: 1, j: 2}` a tuple of two, as `QuotableImpl.quote` has it
            if (bucket(container) == Bucket.LIST && stripped is ElixirKeywords) {
                stripped.keywordPairList
            } else {
                listOf(child)
            }
        }

    /** A map pattern names a subset of the value's keys, so the sides are paired by key rather than by position. */
    private fun byKey(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): List<Pair<PsiElement, PsiElement>> {
        val (key, patternElement) = entries(pattern)
            .firstOrNull { (_, element) -> PsiTreeUtil.isAncestor(element, declaration, false) }
            ?: return emptyList()
        // a duplicate key takes its last value - `%{a: 1, a: 2}` is `%{a: 2}` - so the last entry is the binding one
        val valueElement =
            entries(value).lastOrNull { (valueKey, _) -> valueKey == key }?.second ?: return emptyList()

        return listOf(patternElement to valueElement)
    }

    /**
     * A keyword list is a list of pairs, so its pairs line up by position and must then agree on the key. A pair
     * spelled as a tuple, `[{:a, x}] = [a: value]`, does match in Elixir but binds nothing here.
     */
    private fun byPairedKey(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): List<Pair<PsiElement, PsiElement>> {
        val patternPair = pattern as? ElixirKeywordPair ?: return emptyList()
        val valuePair = value as? ElixirKeywordPair ?: return emptyList()

        if (key(patternPair.keywordKey) != key(valuePair.keywordKey)) {
            return emptyList()
        }

        val patternValue = patternPair.keywordValue

        return if (PsiTreeUtil.isAncestor(patternValue, declaration, false)) {
            listOf(patternValue as PsiElement to valuePair.keywordValue as PsiElement)
        } else {
            emptyList()
        }
    }

    /**
     * Every key of [element], with the expression it maps to, and none at all when it is not a map. An update,
     * `%{base | a: value}`, carries its associations and keywords the same way a construction does, and only the keys
     * it names are known here - the ones it inherits from `base` are not, so they pair with nothing, as an absent key
     * does.
     */
    private fun entries(element: PsiElement): List<Pair<String, PsiElement>> {
        // the only shape in the `MAP` bucket; a wider one would answer no keys rather than throw
        val mapArguments = (element as? ElixirMapOperation)?.mapArguments ?: return emptyList()
        val constructionArguments = mapArguments.mapConstructionArguments
        val updateArguments = mapArguments.mapUpdateArguments
        val associationsBase =
            constructionArguments?.let { it.associations?.associationsBase ?: it.associationsBase }
                ?: updateArguments?.let { it.associations?.associationsBase ?: it.associationsBase }
        val associationEntries = associationsBase
            ?.containerAssociationOperationList
            ?.mapNotNull { association ->
                // `associationInfixOperator` is a private rule, so `=>` is a leaf and the operands are what is left
                val children = association.childExpressions().toList()
                val keyElement = children.firstOrNull()
                val valueElement = children.lastOrNull()

                if (keyElement != null && valueElement != null && keyElement !== valueElement) {
                    key(keyElement) to valueElement
                } else {
                    null
                }
            }
            .orEmpty()
        val keywordEntries = (constructionArguments?.keywords ?: updateArguments?.keywords)
            ?.keywordPairList
            ?.map { keywordPair -> key(keywordPair.keywordKey) to keywordPair.keywordValue as PsiElement }
            .orEmpty()

        return associationEntries + keywordEntries
    }

    /**
     * A key's identity for pairing. `a:`, `:a`, `:"a"` and `"a":` are one atom; `"a" =>` is a binary and distinct. An
     * escape sequence answers as written, so `"\x61":` fails to pair with `a:` though Elixir says they are equal.
     */
    private fun key(element: PsiElement): String =
        when (val stripped = element.stripAccessExpression()) {
            is ElixirAtom -> stripped.line?.let(::quotedName) ?: "atom ${stripped.text.removePrefix(":")}"
            is ElixirKeywordKey -> stripped.line?.let(::quotedName) ?: "atom ${stripped.text}"
            else -> "term ${stripped.text}"
        }

    /** The atom a quoted key spells. Interpolation is unknowable here, so it gets an identity pairing with nothing. */
    private fun quotedName(line: ElixirLine): String {
        val body = line.lineBody

        return if (body == null || PsiTreeUtil.findChildOfType(body, ElixirInterpolation::class.java) != null) {
            "interpolated ${line.textOffset}"
        } else {
            "atom ${body.text}"
        }
    }
}
