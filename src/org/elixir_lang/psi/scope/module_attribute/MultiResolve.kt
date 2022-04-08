package org.elixir_lang.psi.scope.module_attribute

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.scope.ModuleAttribute
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.visitedElementSet

class MultiResolve(private val name: String) : ModuleAttribute() {
    override fun execute(
        semantic: org.elixir_lang.semantic.module_attribute.definition.Literal,
        state: ResolveState
    ): Boolean {
        semantic.moduleAttributeName?.let { declaredName ->
            if (declaredName.startsWith(name)) {
                val validResult = declaredName == name
                val psiElement = semantic.psiElement

                resolveResultOrderedSet.add(psiElement, psiElement.text, validResult, state.visitedElementSet())
            }
        }

        // keep searching in case there the module attribute is accumulating
        return true
    }

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    companion object {
        fun resolveResultOrderedSet(name: String, entrance: PsiElement): ResolveResultOrderedSet {
            val multiResolve = MultiResolve(name)

            val resolveState = ResolveState.initial().put(ENTRANCE, entrance).putInitialVisitedElement(entrance)
            val maxScope = entrance.containingFile

            PsiTreeUtil.treeWalkUp(multiResolve, entrance, maxScope, resolveState)

            return multiResolve.resolveResultOrderedSet
        }
    }
}
