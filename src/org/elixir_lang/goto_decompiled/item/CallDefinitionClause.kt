package org.elixir_lang.goto_decompiled.item

import com.intellij.navigation.GotoRelatedItem
import org.elixir_lang.semantic.call.definition.Clause

class CallDefinitionClause(clause: Clause) : GotoRelatedItem(clause.psiElement, "Decompiled BEAM")
