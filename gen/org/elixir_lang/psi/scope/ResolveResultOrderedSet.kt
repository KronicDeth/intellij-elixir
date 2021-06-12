package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import org.elixir_lang.navigation.isDecompiled

class ResolveResultOrderedSet {
   fun add(element: PsiElement, name: String, validResult: Boolean) {
      if (element !in psiElementSet) {
         psiElementSet.add(element)
         val psiElementResolveResult = PsiElementResolveResult(element, validResult)
         val existingDecompiledPsiElementResolveResultList = psiElementResolveResultListByName[name]

         if (existingDecompiledPsiElementResolveResultList != null) {
            existingDecompiledPsiElementResolveResultList.add(psiElementResolveResult)
         } else {
            nameOrder.add(name)

            val psiElementResolveResultList = mutableListOf(psiElementResolveResult)
            psiElementResolveResultListByName[name] = psiElementResolveResultList
         }
      }
   }

   fun addAll(other: ResolveResultOrderedSet) {
      other.nameOrder.forEach { name ->
         other.psiElementResolveResultListByName[name]!!.forEach { psiElementResolveResult ->
            add(psiElementResolveResult.element, name, psiElementResolveResult.isValidResult)
         }
      }
   }

   fun keepProcessing(incompleteCode: Boolean): Boolean = incompleteCode || !hasValidResult()

   fun toTypedArray(): Array<ResolveResult> = toList().toTypedArray()

   fun toList(): List<ResolveResult> =
           nameOrder
                   .flatMap { name ->
                      psiElementResolveResultListByName[name]!!
                   }

   private val psiElementSet = mutableSetOf<PsiElement>()
   private val psiElementResolveResultListByName = mutableMapOf<String, MutableList<PsiElementResolveResult>>()
   private val nameOrder = mutableListOf<String>()

   private fun hasValidResult() =
           psiElementResolveResultListByName.values.any {
              it.any { it.isValidResult }
           }
}
