package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult

class ResolveResultOrderedSet : Collection<PsiElementResolveResult> {
   override val size: Int
      get() = resolvedResultList.size

   fun add(resolveResult: PsiElementResolveResult) {
      resolveResult.element.let { element ->
         if (element !in resolvedSet) {
            resolvedResultList.add(resolveResult)
            resolvedSet.add(element)
         }
      }
   }

   fun add(element: PsiElement, validResult: Boolean) {
      if (element !in resolvedSet) {
         resolvedResultList.add(PsiElementResolveResult(element, validResult))
         resolvedSet.add(element)
      }
   }

   fun addAll(resolveResultOrderedSet: ResolveResultOrderedSet) {
      addAll(resolveResultOrderedSet.resolvedResultList)
   }

   fun addAll(resolveResultList: List<PsiElementResolveResult>) {
      for (resolveResult in resolveResultList) {
         add(resolveResult)
      }
   }

   override fun contains(element: PsiElementResolveResult): Boolean = resolvedSet.contains(element.element)
   override fun containsAll(elements: Collection<PsiElementResolveResult>): Boolean = all { contains(it) }
   override fun isEmpty(): Boolean = resolvedResultList.isEmpty()
   override fun iterator(): Iterator<PsiElementResolveResult> = resolvedResultList.iterator()
   fun keepProcessing(incompleteCode: Boolean): Boolean = incompleteCode || !hasValidResult()
   fun toList(): List<PsiElementResolveResult> = resolvedResultList

   private val resolvedSet = mutableSetOf<PsiElement>()
   private val resolvedResultList = mutableListOf<PsiElementResolveResult>()

   private fun hasValidResult() = any { it.isValidResult }
}

private operator fun PsiElementResolveResult.component1(): PsiElement = this.element
private operator fun PsiElementResolveResult.component2(): Boolean = this.isValidResult
