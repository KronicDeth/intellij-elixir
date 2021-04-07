package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import org.elixir_lang.navigation.isDecompiled

class ResolveResultOrderedSet {
   fun add(element: PsiElement, name: String, validResult: Boolean) {
      if (element !in psiElementSet) {
         val existingDecompiledPsiElementResolveResultList = decompiledPsiElementResolveResultListByName[name]
         val decompiled = element.isDecompiled()

         if (existingDecompiledPsiElementResolveResultList != null) {
            val existingDecompiled = existingDecompiledPsiElementResolveResultList.decompiled

            if (existingDecompiled) {
               val psiElementResolveResult = PsiElementResolveResult(element, validResult)

               // add more decompiled
               if (decompiled) {
                  existingDecompiledPsiElementResolveResultList.psiElementResolveResultList.add(psiElementResolveResult)
                  psiElementSet.add(element)

               }
               // favor a source of the same name
               else {
                  val psiElemetResolveResultList = mutableListOf(psiElementResolveResult)
                  val decompiledPsiElemetResolveResultList = DecompiledPsiElemetResolveResultList(decompiled, psiElemetResolveResultList)
                  decompiledPsiElementResolveResultListByName[name] = decompiledPsiElemetResolveResultList
                  psiElementSet.add(element)

                  existingDecompiledPsiElementResolveResultList.psiElementResolveResultList.forEach { existingPsiElementResolveResult ->
                     psiElementSet.remove(existingPsiElementResolveResult.element)
                  }
               }
            } else {
               // ignore decompiled since source is favored

               // add more source
               if (!decompiled) {
                  val psiElementResolveResult = PsiElementResolveResult(element, validResult)
                  existingDecompiledPsiElementResolveResultList.psiElementResolveResultList.add(psiElementResolveResult)
                  psiElementSet.add(element)
               }
            }
         } else {
            psiElementSet.add(element)
            nameOrder.add(name)

            val psiElementResolveResult = PsiElementResolveResult(element, validResult)
            val psiElemetResolveResultList = mutableListOf(psiElementResolveResult)
            val decompiledPsiElemetResolveResultList = DecompiledPsiElemetResolveResultList(decompiled, psiElemetResolveResultList)
            decompiledPsiElementResolveResultListByName[name] = decompiledPsiElemetResolveResultList
         }
      }
   }

   fun addAll(other: ResolveResultOrderedSet) {
      other.nameOrder.forEach { name ->
         other.decompiledPsiElementResolveResultListByName[name]!!.psiElementResolveResultList.forEach { psiElementResolveResult ->
            add(psiElementResolveResult.element, name, psiElementResolveResult.isValidResult)
         }
      }
   }

   fun keepProcessing(incompleteCode: Boolean): Boolean = incompleteCode || !hasValidResult()

   fun toTypedArray(): Array<ResolveResult> = toList().toTypedArray()
           
   fun toList(): List<ResolveResult> =
           nameOrder
                   .flatMap { name ->
                      decompiledPsiElementResolveResultListByName[name]!!.psiElementResolveResultList
                   }

   private val psiElementSet = mutableSetOf<PsiElement>()
   private val decompiledPsiElementResolveResultListByName = mutableMapOf<String, DecompiledPsiElemetResolveResultList>()
   private val nameOrder = mutableListOf<String>()

   private fun hasValidResult() =
           decompiledPsiElementResolveResultListByName.values.any {
              it.psiElementResolveResultList.any { it.isValidResult }
           }
}

private data class DecompiledPsiElemetResolveResultList(val decompiled: Boolean, val psiElementResolveResultList: MutableList<PsiElementResolveResult>)
