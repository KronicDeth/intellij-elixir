package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.operation.Pipe

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
            if (isContainer(strippedValue)) {
                return emptyList()
            }

            val bounds = boundValues(strippedValue, followed)

            return if (bounds.isEmpty()) {
                listOf(strippedValue)
            } else {
                bounds.flatMap { bound -> valuesAt(strippedPattern, bound.value, declaration, bound.followed) }
            }
        }

        val (nextPattern, nextValue) = descend(strippedPattern, strippedValue, declaration) ?: return emptyList()

        return valuesAt(nextPattern, nextValue, declaration, followed)
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

    /** The shapes [descend] can take apart, and so the ones whose kind has to agree for the match to bind at all. */
    private fun isContainer(element: PsiElement): Boolean =
        element is ElixirTuple || element is ElixirList || element is ElixirMapOperation

    private fun sameShapeAs(pattern: PsiElement, value: PsiElement): Boolean =
        when (pattern) {
            is ElixirTuple -> value is ElixirTuple
            is ElixirList -> value is ElixirList
            is ElixirMapOperation -> value is ElixirMapOperation
            else -> false
        }

    /** The pair one level in from [pattern] and [value] that still contains [declaration]. */
    private fun descend(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): Pair<PsiElement, PsiElement>? =
        when (pattern) {
            is ElixirMapOperation -> (value as? ElixirMapOperation)?.let { byKey(pattern, it, declaration) }
            else -> byPosition(pattern, value, declaration)
        }

    private fun byPosition(
        pattern: PsiElement,
        value: PsiElement,
        declaration: PsiElement
    ): Pair<PsiElement, PsiElement>? {
        val (heads, tail) = positions(pattern)
        val (valueElements, valueTail) = positions(value)

        // a cons on the right leaves the value's length unknown; conservative, since `[x] = [h | t]` matches only
        // when `t` is empty
        if (valueTail != null) {
            return null
        }

        // `[h | t]` matches any list at least as long as its heads; without a tail the lengths have to agree exactly
        val linesUp = if (tail == null) heads.size == valueElements.size else heads.size <= valueElements.size

        if (!linesUp) {
            return null
        }

        // the rest of a list is not an element of it, so a name bound by `t` finds no index here and answers nothing
        val index = heads.indexOfFirst { PsiTreeUtil.isAncestor(it, declaration, false) }

        return if (index == -1) {
            null
        } else {
            heads[index] to valueElements[index]
        }
    }

    /** The positions of a tuple or list: those matched by index, and the one `[h | t]` binds the rest to. */
    private data class Positions(val heads: List<PsiElement>, val tail: PsiElement?)

    private fun positions(container: PsiElement): Positions {
        val elements = elements(container)
        // `[a, b | t]` parses as the elements before `b | t`, then one pipe operation carrying the last two positions
        val cons = elements.lastOrNull()?.stripAccessExpression() as? Pipe ?: return Positions(elements, null)
        val head = cons.leftOperand() ?: return Positions(elements, null)

        return Positions(elements.dropLast(1) + head, cons.rightOperand())
    }

    /**
     * A container's elements as Elixir counts them. `containerArguments` is a private rule, so they are its own child
     * expressions - except that it collapses a trailing keyword list into one `keywords` child, where Elixir sees one
     * element per pair: `[value, k: 1, j: 2]` is a list of three.
     */
    private fun elements(container: PsiElement): List<PsiElement> =
        container.childExpressions().toList().flatMap { child ->
            val stripped = child.stripAccessExpression()

            // The list rule only: `[q, k: 1, j: 2]` is a list of three, but `{q, k: 1, j: 2}` is a tuple of two, whose
            // second element is the whole keyword list. `QuotableImpl.quote` draws the same distinction.
            if (container is ElixirList && stripped is ElixirKeywords) {
                stripped.keywordPairList
            } else {
                listOf(child)
            }
        }

    /** A map pattern names a subset of the value's keys, so the sides are paired by key rather than by position. */
    private fun byKey(
        pattern: ElixirMapOperation,
        value: ElixirMapOperation,
        declaration: PsiElement
    ): Pair<PsiElement, PsiElement>? {
        val (key, patternElement) = entries(pattern)
            .firstOrNull { (_, element) -> PsiTreeUtil.isAncestor(element, declaration, false) }
            ?: return null
        // a duplicate key takes its last value - `%{a: 1, a: 2}` is `%{a: 2}` - so the last entry is the binding one
        val valueElement = entries(value).lastOrNull { (valueKey, _) -> valueKey == key }?.second ?: return null

        return patternElement to valueElement
    }

    /**
     * Every key of [element], with the expression it maps to, and none at all when it is not a map. An update,
     * `%{base | a: value}`, carries its associations and keywords the same way a construction does, and only the keys
     * it names are known here - the ones it inherits from `base` are not, so they pair with nothing, as an absent key
     * does.
     */
    private fun entries(mapOperation: ElixirMapOperation): List<Pair<String, PsiElement>> {
        val mapArguments = mapOperation.mapArguments
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
