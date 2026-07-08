package org.elixir_lang.model.psi.atom

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.icons.AllIcons
import com.intellij.model.Pointer
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.ResolveState
import com.intellij.psi.search.SearchScope
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.ElixirSymbolWithUsages
import org.elixir_lang.navigation.ElixirClausePresentation
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.Timed
import java.util.*
import org.elixir_lang.beam.psi.CallDefinition as BeamCallDefinition

@Suppress("UnstableApiUsage")
class AtomSymbol(
    override val file: PsiFile,
    override val range: TextRange,
    val moduleName: String,
    val name: String,
    val arity: Int,
    val macro: Boolean,
    private val displayText: String? = null
) : ElixirSymbolWithUsages, NavigationTarget, SearchTarget {
    override val searchText: String get() = name

    override fun createPointer(): Pointer<out AtomSymbol> {
        val moduleName = this.moduleName
        val name = this.name
        val arity = this.arity
        val macro = this.macro
        val displayText = this.displayText
        return Pointer.fileRangePointer(file, range) { restoredFile, restoredRange ->
            AtomSymbol(restoredFile, restoredRange, moduleName, name, arity, macro, displayText)
        }
    }

    override fun computePresentation(): TargetPresentation = presentation()

    override fun navigationRequest(): NavigationRequest? =
        NavigationRequest.sourceNavigationRequest(file, range)

    override val maximalSearchScope: SearchScope? get() = null

    override val usageHandler: UsageHandler
        get() = UsageHandler.createEmptyUsageHandler("$name/$arity")

    override fun presentation(): TargetPresentation =
        TargetPresentation.builder(clausePresentationText() ?: "$moduleName.$name/$arity")
            .containerText(moduleName)
            .icon(if (macro) AllIcons.Nodes.AbstractMethod else AllIcons.Nodes.Method)
            .presentation()

    @RequiresReadLock
    private fun clausePresentationText(): String? =
        displayText ?: (declarationElement() as? Call)?.let(ElixirClausePresentation::elementText)

    override fun equals(other: Any?): Boolean =
        other is AtomSymbol &&
            other.moduleName == moduleName &&
            other.name == name &&
            other.arity == arity &&
            other.macro == macro &&
            other.file.virtualFile == file.virtualFile &&
            other.range == range

    override fun hashCode(): Int = Objects.hash(moduleName, name, arity, macro, file.virtualFile, range)

    override fun toString(): String = "AtomSymbol($moduleName.$name/$arity, macro=$macro)"

    @RequiresReadLock
    fun declarationElement(): PsiElement? =
        generateSequence(file.findElementAt(range.startOffset)) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }

    companion object {
        @RequiresReadLock
        fun fromClause(clause: Call): List<AtomSymbol> {
            if (!CallDefinitionClause.`is`(clause)) return emptyList()
            val enclosingModular = CallDefinitionClause.enclosingModularMacroCall(clause) ?: return emptyList()
            val moduleName = runCatching { org.elixir_lang.psi.Module.name(enclosingModular) }
                .getOrElse { if (it is ProcessCanceledException) throw it else null }
                ?: return emptyList()
            val nameArity = CallDefinitionClause.nameArityInterval(clause, ResolveState.initial()) ?: return emptyList()
            val nameId = CallDefinitionClause.nameIdentifier(clause) ?: return emptyList()
            val macro = CallDefinitionClause.isMacro(clause)
            return nameArity.arityInterval.closed().map { arity ->
                AtomSymbol(clause.containingFile, nameId.textRange, moduleName, nameArity.name, arity, macro)
            }
        }

        @RequiresReadLock
        fun fromBeamCallDefinition(callDefinition: BeamCallDefinition): List<AtomSymbol> {
            val navigationClause = callDefinition.navigationElement as? Call

            if (navigationClause != null && CallDefinitionClause.isMacro(navigationClause)) return emptyList()
            if (navigationClause == null && callDefinition.time == Timed.Time.COMPILE) return emptyList()

            val presentationText =
                if (navigationClause != null && CallDefinitionClause.`is`(navigationClause)) {
                    ElixirClausePresentation.elementText(navigationClause)
                } else {
                    null
                }
            val moduleName = (callDefinition.parent as? org.elixir_lang.beam.psi.Module)?.name ?: return emptyList()
            val nameArity = callDefinition.nameArityInterval
            return nameArity.arityInterval.closed().map { arity ->
                AtomSymbol(
                    callDefinition.containingFile,
                    callDefinition.textRange,
                    moduleName,
                    nameArity.name,
                    arity,
                    false,
                    presentationText
                )
            }
        }

        fun matches(symbol: AtomSymbol, moduleName: String, name: String, arity: Int, macro: Boolean): Boolean =
            symbol.moduleName == moduleName &&
                symbol.name == name &&
                symbol.arity == arity &&
                symbol.macro == macro
    }
}
