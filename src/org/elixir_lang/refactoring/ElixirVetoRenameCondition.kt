package org.elixir_lang.refactoring

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import org.elixir_lang.ElixirLanguage

/**
 * Vetoes the platform's legacy [com.intellij.refactoring.rename.PsiElementRenameHandler] for every
 * Elixir PSI element.
 *
 * Elixir rename is fully owned by the Symbol API ([com.intellij.refactoring.rename.api.RenameTarget]
 * via `SymbolRenameTargetRenamerFactory`). Without this veto the platform *also* offers the legacy
 * `PsiElementRenameHandler` whenever `TargetElementUtil` resolves a `PsiNamedElement` at the caret -
 * either from a legacy `Call.getReference()` (usage carets) or from the named-element fallback
 * (declaration carets). Two available `Renamer`s make `RenameElementAction` show the
 * "What would you like to do?" chooser popup instead of starting the inline Symbol rename, which is
 * the Shift+F6 regression reported during IDE validation.
 *
 * Vetoing all Elixir elements guarantees the Symbol renamer is the sole `Renamer`, so inline rename
 * starts directly. This is the platform-sanctioned replacement for the rename-suppression that
 * [org.elixir_lang.TargetElementEvaluator] previously provided via `isAcceptableNamedParent`, and it
 * survives that evaluator's eventual removal.
 */
internal class ElixirVetoRenameCondition : Condition<PsiElement> {
    override fun value(element: PsiElement?): Boolean =
        element != null && element.language == ElixirLanguage
}
