package org.elixir_lang

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.lang.findUsages.LanguageFindUsages
import com.intellij.openapi.extensions.Extensions
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.usages.UsageTarget
import com.intellij.usages.UsageTargetUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProviderEx

class FindUsagesTest : LightCodeInsightFixtureTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/find_usages"
    }

    fun testParameterDeclaration() {
        myFixture.configureByFiles("parameter_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)

        assertEquals("parameter", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).toList()

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(39, firstElement.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.FUNCTION_PARAMETER, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(70, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    fun testParameterUnused() {
        myFixture.configureByFiles("parameter_unused.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)

        assertEquals("parameter", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).toList()

        assertEquals(1, usages.size)

        val element = usages[0].element!!

        assertEquals(39, element.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(element))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(element))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(element))

        assertEquals(UsageTypeProvider.FUNCTION_PARAMETER, getUsageType(element, usageTargets))
    }

    fun testParameterUsage() {
        myFixture.configureByFiles("parameter_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        val usages = myFixture.findUsages(target).toList()

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(39, firstElement.textOffset)

        val readWriteAccessDetector = readWriteAccessDetector(firstElement)
        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.FUNCTION_PARAMETER, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(70, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    fun testVariableDeclaration() {
        myFixture.configureByFiles("variable_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)

        assertEquals("variable", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).toList()

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(46, firstElement.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageType.WRITE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(99, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    fun testVariableUnused() {
        myFixture.configureByFiles("variable_unused.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)

        assertEquals("variable", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).toList()

        assertEquals(1, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(46, firstElement.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageType.WRITE, getUsageType(firstElement, usageTargets))
    }

    fun testVariableUsage() {
        myFixture.configureByFiles("variable_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("call", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).toList()

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(46, firstElement.textOffset)

        val readWriteAccessDetector = readWriteAccessDetector(firstElement)
        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageType.WRITE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(99, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    private val findUsagesProvider by lazy { LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage) }

    private fun readWriteAccessDetector(element: PsiElement) =
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.findDetector(element)!!

    private fun getUsageType(element: PsiElement, targets: Array<UsageTarget>): UsageType? =
            Extensions
                    .getExtensions(com.intellij.usages.impl.rules.UsageTypeProvider.EP_NAME)
                    .asSequence()
                    .mapNotNull { usageTypeProvider ->
                        when (usageTypeProvider) {
                            is UsageTypeProviderEx -> usageTypeProvider.getUsageType(element, targets)
                            else -> usageTypeProvider.getUsageType(element)
                        }
                    }
                    .firstOrNull()
}
