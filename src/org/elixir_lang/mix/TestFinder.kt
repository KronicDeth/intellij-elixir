package org.elixir_lang.mix

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinder
import com.intellij.util.Function
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.semantic

class TestFinder : TestFinder {
    override fun findSourceElement(from: PsiElement): Call? = sourceElement(from)

    override fun findTestsForClass(element: PsiElement): Collection<PsiElement> {
        return corresponding(
            element,
            { canonicalName: String -> canonicalName + TEST_SUFFIX }
        ) { call: Call -> call.semantic is Modular }
    }

    override fun findClassesForTest(element: PsiElement): Collection<PsiElement> {
        return corresponding(
            element,
            { canonicalName: String ->
                var correspondingCanonicalName: String? = null
                if (canonicalName.endsWith(TEST_SUFFIX)) {
                    correspondingCanonicalName = canonicalName.substring(0, canonicalName.length - TEST_SUFFIX.length)
                }
                correspondingCanonicalName
            }) { call: Call -> call.semantic is Modular }
    }

    override fun isTest(element: PsiElement): Boolean = isTestElement(element)

    companion object {
        private const val TEST_SUFFIX = "Test"

        private fun corresponding(
            element: PsiElement,
            correspondingName: Function<String, String?>,
            correspondingCallCondition: Condition<Call>
        ): Collection<PsiElement> {
            val sourceElement = sourceElement(element)
            val correspondingCollection: MutableCollection<PsiElement> = ArrayList()
            if (sourceElement is StubBased<*>) {
                val canonicalNameSet = sourceElement.canonicalNameSet
                if (!canonicalNameSet.isEmpty()) {
                    val project = element.project
                    val scope = GlobalSearchScope.projectScope(project)
                    for (canonicalName in canonicalNameSet) {
                        val correspondingCanonicalName = correspondingName.`fun`(canonicalName)
                        if (correspondingCanonicalName != null) {
                            val correspondingElements = StubIndex.getElements(
                                AllName.KEY,
                                correspondingCanonicalName,
                                project,
                                scope,
                                NamedElement::class.java
                            )
                            for (correspondingElement in correspondingElements) {
                                if (correspondingElement is Call) {
                                    val correspondingCall = correspondingElement as Call
                                    if (correspondingCallCondition.value(correspondingCall)) {
                                        correspondingCollection.add(correspondingCall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return correspondingCollection
        }

        private fun parentCallSourceElement(from: PsiElement): Call? =
            PsiTreeUtil.getParentOfType(from, Call::class.java)?.let { sourceElement(it) }

        private fun sourceElement(from: Call): Call? = if (from.semantic is Modular) {
            from
        } else {
            parentCallSourceElement(from)
        }

        private fun sourceElement(from: PsiElement): Call? = if (from is Call) {
            sourceElement(from)
        } else {
            parentCallSourceElement(from)
        }

        /**
         * Check if the given element is a test or not.
         */
        fun isTestElement(element: PsiElement): Boolean =
            sourceElement(element)
                ?.let { it as? StubBased<*> }?.canonicalNameSet?.any { canonicalName ->
                    canonicalName.endsWith("Test")
                }
                ?: false
    }
}
