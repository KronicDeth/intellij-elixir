package org.elixir_lang

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.find.FindManager
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.find.impl.FindManagerImpl
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

    fun testFunctionRecursiveDeclaration() {
        myFixture.configureByFiles("function_recursive_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("function", findUsagesProvider.getType(target))

        val readWriteAccessDetector = readWriteAccessDetector(target)
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(1, secondaryElements.size)

        val usages = myFixture.findUsages(target).toList()

        assertEquals(4, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(63, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(93, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(29, thirdElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(thirdElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(thirdElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(thirdElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(thirdElement, usageTargets))

        val fourthElement = usages[3].element!!

        assertEquals(93, fourthElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(fourthElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(fourthElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(fourthElement))

        assertEquals(UsageTypeProvider.CALL, getUsageType(fourthElement, usageTargets))
    }

    fun testFunctionRecursiveUsage() {
        myFixture.configureByFiles("function_recursive_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("call", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(2, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).toList()

        assertEquals(4, usages.size)

        val firstElement = usages[0].element!!
        val readWriteAccessDetector = readWriteAccessDetector(firstElement)

        assertEquals(63, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(93, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(29, thirdElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(thirdElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(thirdElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(thirdElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(thirdElement, usageTargets))

        val fourthElement = usages[3].element!!

        assertEquals(93, fourthElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(fourthElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(fourthElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(fourthElement))

        assertEquals(UsageType.READ, getUsageType(fourthElement, usageTargets))
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

    private fun findUsagesHandler(element: PsiElement) =
        FindManager
                .getInstance(project)
                .let { it as FindManagerImpl }
                .findUsagesManager
                .getFindUsagesHandler(element, false)!!

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
