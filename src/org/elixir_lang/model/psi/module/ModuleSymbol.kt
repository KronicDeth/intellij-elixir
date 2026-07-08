package org.elixir_lang.model.psi.module

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
import com.intellij.psi.search.SearchScope
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.ElixirSymbolWithUsages
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import java.util.*

/**
 * Symbol representing a `defmodule` declaration.
 */
@Suppress("UnstableApiUsage")
class ModuleSymbol(
    override val file: PsiFile,
    override val range: TextRange,
    val moduleName: String
) : ElixirSymbolWithUsages, NavigationTarget, SearchTarget {
    override val searchText: String get() = moduleName.substringAfterLast('.')

    override fun createPointer(): Pointer<out ModuleSymbol> {
        val moduleName = this.moduleName
        return Pointer.fileRangePointer(file, range) { restoredFile, restoredRange ->
            ModuleSymbol(restoredFile, restoredRange, moduleName)
        }
    }

    override fun computePresentation(): TargetPresentation = presentation()

    override fun navigationRequest(): NavigationRequest? =
        NavigationRequest.sourceNavigationRequest(file, range)

    override val maximalSearchScope: SearchScope? get() = null

    override val usageHandler: UsageHandler
        get() = UsageHandler.createEmptyUsageHandler(moduleName)

    override fun presentation(): TargetPresentation =
        TargetPresentation.builder(moduleName)
            .icon(AllIcons.Nodes.Module)
            .presentation()

    override fun equals(other: Any?): Boolean =
        other is ModuleSymbol && other.moduleName == moduleName

    override fun hashCode(): Int = Objects.hash(moduleName)

    override fun toString(): String = "ModuleSymbol($moduleName)"

    companion object {
        @RequiresReadLock
        fun fromModular(call: Call): ModuleSymbol? {
            if (!Module.`is`(call)) return null
            val nameElement = moduleNameElement(call) ?: return null
            val moduleName = moduleNameText(call)?.removeElixirPrefix() ?: return null

            return ModuleSymbol(call.containingFile, nameElement.textRange, moduleName)
        }

        @RequiresReadLock
        fun moduleNameElement(call: Call): PsiElement? {
            val firstArgument = call.primaryArguments()
                ?.firstOrNull()
                ?: call.finalArguments()?.firstOrNull()
                ?: return null
            val stripped = firstArgument.stripAccessExpression()

            return when (stripped) {
                is QualifiableAlias -> stripped
                is Call -> stripped.functionNameElement() ?: stripped
                else -> stripped
            }
        }

        @RequiresReadLock
        fun moduleNameText(call: Call): String? {
            val firstArgument = call.primaryArguments()
                ?.firstOrNull()
                ?: call.finalArguments()?.firstOrNull()
                ?: return null
            val stripped = firstArgument.stripAccessExpression()

            return when (stripped) {
                is QualifiableAlias -> stripped.fullyQualifiedName()
                else -> stripped.text
            }
        }

        private fun String.removeElixirPrefix(): String =
            if (startsWith("Elixir.")) removePrefix("Elixir.") else this
    }
}
