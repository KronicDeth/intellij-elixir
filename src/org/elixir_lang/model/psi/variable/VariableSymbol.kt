package org.elixir_lang.model.psi.variable

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.icons.AllIcons
import com.intellij.model.Pointer
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.ElixirSymbolWithUsages
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
) : ElixirSymbolWithUsages, NavigationTarget, SearchTarget {
    enum class Kind {
        PARAMETER,
        VARIABLE,
        IGNORED
    }

    override val searchText: String get() = name

    override fun createPointer(): Pointer<out VariableSymbol> {
        val name = this.name
        val kind = this.kind
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
            return UseScopeImpl.get(declaration)
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
                    org.elixir_lang.reference.Callable.isParameter(element) ||
                        org.elixir_lang.reference.Callable.isParameterWithDefault(element) ||
                        isVariableDeclaration(element)
                Kind.VARIABLE -> isVariableDeclaration(element)
                null -> false
            }
        }

        @RequiresReadLock
        private fun isVariableDeclaration(element: PsiElement): Boolean {
            if (isPinnedSite(element)) return false

            val match = generateSequence(element) { it.parent }
                .filterIsInstance<Match>()
                .firstOrNull()
                ?: return false

            return match.leftOperand()?.let { left -> PsiTreeUtil.isAncestor(left, element, false) } ?: false
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
