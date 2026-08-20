package org.elixir_lang.model.psi.variable

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.icons.AllIcons
import com.intellij.model.Pointer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.rename.api.RenameTarget
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.ElixirSymbolWithUsages
import org.elixir_lang.psi.ElixirAnonymousFunction
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.ElixirStabNoParenthesesSignature
import org.elixir_lang.psi.ElixirStabParenthesesSignature
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.UnaryOperation
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.declarations.UseScopeImpl
import java.util.*

@Suppress("UnstableApiUsage")
class VariableSymbol(
    override val file: PsiFile,
    override val range: TextRange,
    val name: String,
    val kind: Kind
) : ElixirSymbolWithUsages, NavigationTarget, SearchTarget, RenameTarget {
    enum class Kind {
        PARAMETER,
        VARIABLE,
        IGNORED
    }

    override val searchText: String get() = name
    override val targetName: String get() = name

    override fun createPointer(): Pointer<out VariableSymbol> {
        val name = this.name
        val kind = this.kind
        // Anchor to the enclosing call-definition clause (the `def`/`defp`/... a large, structurally
        // stable ancestor) and recompute the occurrence by its offset within that clause. A variable's
        // own name element is essentially the identifier leaf, and a plain file-range marker collapses:
        // an in-place (Shift+F6) rename replaces the whole identifier, which invalidates a pointer
        // anchored to that leaf/marker. The programmatic commit then reverts the live edit and
        // re-dereferences the target pointer; if it is null the whole rename is dropped (a no-op). The
        // enclosing clause survives the identifier replacement, so the occurrence range is recomputed
        // from the (restored) clause on dereference.
        val clause = generateSequence(file.findElementAt(range.startOffset)) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
        if (clause != null && clause.textRange.contains(range)) {
            val clausePointer = SmartPointerManager.getInstance(file.project)
                .createSmartPsiElementPointer(clause, file)
            val relativeStart = range.startOffset - clause.textRange.startOffset
            val length = range.length
            return Pointer {
                val restoredClause = clausePointer.dereference() ?: return@Pointer null
                val start = restoredClause.textRange.startOffset + relativeStart
                if (start + length > restoredClause.textRange.endOffset) return@Pointer null
                VariableSymbol(restoredClause.containingFile, TextRange(start, start + length), name, kind)
            }
        }
        return Pointer.fileRangePointer(file, range) { restoredFile, restoredRange ->
            VariableSymbol(restoredFile, restoredRange, name, kind)
        }
    }

    override fun computePresentation(): TargetPresentation = presentation()

    override fun navigationRequest(): NavigationRequest? =
        NavigationRequest.sourceNavigationRequest(file, range)

    override val maximalSearchScope: SearchScope?
        @RequiresReadLock get() {
            val declaration = declarationCall() as? UnqualifiedNoArgumentsCall<*> ?: return null
            return UseScopeImpl.get(chainRootDeclaration(declaration))
        }

    /**
     * The earliest same-named declaration in this declaration's rebinding chain - possibly itself.
     *
     * A rebinding (`x = x + 1` after `x = input`) SHADOWS the earlier binding, but the chain
     * reads and writes one user-facing variable, so search and rename must span the whole chain.
     * A use scope anchored at a later rebinding is `SELF_AND_FOLLOWING_SIBLINGS` and would miss
     * the earlier bindings and the reads that resolve to them. The chain root is the earliest of:
     * this declaration, any same-named declaration in a PRECEDING statement of an enclosing stab
     * body, or a same-named parameter of the enclosing definition clause head. The walk stops at
     * the definition-clause and anonymous-function boundaries: a same-named variable in another
     * function (or in the enclosing scope of an `fn` that rebinds locally) is a different
     * variable.
     */
    @RequiresReadLock
    private fun chainRootDeclaration(declaration: UnqualifiedNoArgumentsCall<*>): UnqualifiedNoArgumentsCall<*> {
        val candidates = mutableListOf<UnqualifiedNoArgumentsCall<*>>(declaration)

        val ancestorsInClause = generateSequence(declaration as PsiElement) { it.parent }
            .takeWhile {
                it !is PsiFile &&
                    it !is ElixirAnonymousFunction &&
                    !(it is Call && CallDefinitionClause.`is`(it))
            }

        // Same-named declarations in preceding statements of every enclosing stab body.
        ancestorsInClause
            .filter { it.parent is ElixirStabBody }
            .forEach { statement ->
                var sibling = statement.prevSibling
                while (sibling != null) {
                    ProgressManager.checkCanceled()
                    PsiTreeUtil.findChildrenOfType(sibling, UnqualifiedNoArgumentsCall::class.java)
                        .filterTo(candidates) { variableName(it) == name && isDeclaration(it) }
                    sibling = sibling.prevSibling
                }
            }

        // A same-named parameter of the enclosing definition clause is the outermost chain root.
        generateSequence(declaration as PsiElement) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
            ?.let { clause -> CallDefinitionClause.head(clause) }
            ?.let { head ->
                PsiTreeUtil.findChildrenOfType(head, UnqualifiedNoArgumentsCall::class.java)
                    .filterTo(candidates) { variableName(it) == name && isDeclaration(it) }
            }

        return candidates.minByOrNull { it.textRange.startOffset } ?: declaration
    }

    override val usageHandler: UsageHandler
        get() = UsageHandler.createEmptyUsageHandler(name)

    override fun presentation(): TargetPresentation =
        TargetPresentation.builder(name)
            .icon(AllIcons.Nodes.Variable)
            .containerText(kind.name.lowercase())
            .presentation()

    override fun equals(other: Any?): Boolean =
        other is VariableSymbol &&
            other.name == name &&
            other.kind == kind &&
            other.file.virtualFile == file.virtualFile &&
            other.range == range

    override fun hashCode(): Int = Objects.hash(name, kind, file.virtualFile, range)

    override fun toString(): String = "VariableSymbol($name, $kind, $range)"

    @RequiresReadLock
    private fun declarationCall(): Call? =
        generateSequence(file.findElementAt(range.startOffset)) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { call ->
                call.functionNameElement()?.textRange == range
            }

    companion object {
        @RequiresReadLock
        fun fromDeclaration(call: Call): VariableSymbol? {
            if (!isDeclaration(call)) return null
            return fromElement(call)
        }

        @RequiresReadLock
        fun fromDeclaration(element: PsiElement): VariableSymbol? {
            if (!isDeclaration(element)) return null
            return fromElement(element)
        }

        @RequiresReadLock
        fun fromCall(call: Call): VariableSymbol? {
            return fromElement(call)
        }

        @RequiresReadLock
        fun fromElement(element: PsiElement): VariableSymbol? {
            val kind = classify(element) ?: return null
            val nameElement = nameIdentifierElement(element) ?: return null
            val name = variableName(element) ?: return null

            return VariableSymbol(element.containingFile, nameElement.textRange, name, kind)
        }

        @RequiresReadLock
        fun classify(element: PsiElement): Kind? {
            if (element !is UnqualifiedNoArgumentsCall<*> && element !is ElixirVariable) return null
            variableName(element) ?: return null

            return when {
                org.elixir_lang.reference.Callable.isIgnored(element) ->
                    Kind.IGNORED
                org.elixir_lang.reference.Callable.isParameter(element) || org.elixir_lang.reference.Callable.isParameterWithDefault(element) ->
                    Kind.PARAMETER
                org.elixir_lang.reference.Callable.isVariable(element) ->
                    Kind.VARIABLE
                element is ElixirVariable ->
                    if ((variableName(element) ?: "").startsWith("_")) Kind.IGNORED else Kind.VARIABLE
                else -> null
            }
        }

        @RequiresReadLock
        fun isDeclaration(call: Call): Boolean = isDeclaration(call as PsiElement)

        @RequiresReadLock
        fun isDeclaration(element: PsiElement): Boolean {
            if (CallDefinitionClause.isHead(element)) return false
            if (isInMatchRightOperand(element)) return false

            return when (classify(element)) {
                Kind.PARAMETER, Kind.IGNORED ->
                    ((org.elixir_lang.reference.Callable.isParameter(element) ||
                        org.elixir_lang.reference.Callable.isParameterWithDefault(element)) &&
                        !isInsideAnonymousFunctionBody(element)) ||
                        isVariableDeclaration(element)
                Kind.VARIABLE -> isVariableDeclaration(element)
                null -> false
            }
        }

        @RequiresReadLock
        private fun isVariableDeclaration(element: PsiElement): Boolean {
            if (isPinnedSite(element)) return false

            // A binding in a stab-clause PATTERN (`{:ok, value} -> ...` in case/receive/with-else)
            // declares a variable for the clause body without any `=` Match.
            if (isInsideStabSignature(element)) return true

            val match = generateSequence(element) { it.parent }
                .filterIsInstance<Match>()
                .firstOrNull()
                ?: return false

            return match.leftOperand()?.let { left -> PsiTreeUtil.isAncestor(left, element, false) } ?: false
        }

        /**
         * The nearest stab-related container of [element]: a body (clause/function body) or a
         * signature (clause pattern / `fn` parameter list), or `null` outside any stab.
         */
        private fun nearestStabContainer(element: PsiElement): PsiElement? =
            generateSequence(element) { it.parent }
                .takeWhile { it !is PsiFile }
                .firstOrNull {
                    it is ElixirStabBody ||
                        it is ElixirStabNoParenthesesSignature ||
                        it is ElixirStabParenthesesSignature
                }

        /**
         * True when [element] sits in the BODY of an anonymous function (`fn ... -> body end`).
         *
         * The legacy parameter classifier ([org.elixir_lang.annotator.Parameter]) marks EVERYTHING
         * inside an `ElixirAnonymousFunction` as a parameter - including reads in the `fn` BODY -
         * which would make a closure read of an outer variable its own "declaration" and break
         * rename from inside the closure. Parameter-ness is discounted exactly there and nowhere
         * else ([org.elixir_lang.reference.Callable.isParameter] walks to the definition clause
         * for genuine parameters, `<-` bindings, and clause patterns, all of which really are
         * declarations).
         */
        private fun isInsideAnonymousFunctionBody(element: PsiElement): Boolean {
            var nearestIsBody: Boolean? = null
            for (ancestor in generateSequence(element) { it.parent }.takeWhile { it !is PsiFile }) {
                when (ancestor) {
                    is ElixirAnonymousFunction -> return nearestIsBody == true
                    is ElixirStabBody -> if (nearestIsBody == null) nearestIsBody = true
                    is ElixirStabNoParenthesesSignature, is ElixirStabParenthesesSignature ->
                        if (nearestIsBody == null) nearestIsBody = false
                    else -> {}
                }
            }
            return false
        }

        private fun isInsideStabSignature(element: PsiElement): Boolean =
            nearestStabContainer(element).let {
                it is ElixirStabNoParenthesesSignature || it is ElixirStabParenthesesSignature
            }

        @RequiresReadLock
        private fun isInMatchRightOperand(element: PsiElement): Boolean =
            generateSequence(element) { it.parent }
                .filterIsInstance<Match>()
                .any { match ->
                    match.rightOperand()?.let { right -> PsiTreeUtil.isAncestor(right, element, false) } == true
                }

        @RequiresReadLock
        private fun isPinnedSite(element: PsiElement): Boolean =
            generateSequence(element) { it.parent }
                .filterIsInstance<UnaryOperation>()
                .any { unary ->
                    unary.operator().text == "^" &&
                        unary.operand()?.let { operand -> PsiTreeUtil.isAncestor(operand, element, false) } == true
                }

        @RequiresReadLock
        fun isHead(element: PsiElement): Boolean {
            val call = when (element) {
                is Call -> element
                else -> generateSequence(element) { it.parent }
                    .filterIsInstance<Call>()
                    .firstOrNull()
            } ?: return false
            return fromDeclaration(call) != null
        }

        @RequiresReadLock
        fun variableName(element: PsiElement): String? =
            when (element) {
                is Call -> element.functionName()
                is ElixirVariable -> element.name
                else -> null
            }

        @RequiresReadLock
        fun nameIdentifierElement(element: PsiElement): PsiElement? =
            when (element) {
                is Call -> element.functionNameElement()
                is PsiNameIdentifierOwner -> element.nameIdentifier ?: element
                else -> null
            }
    }
}
