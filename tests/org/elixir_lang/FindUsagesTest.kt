package org.elixir_lang

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.find.FindManager
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.find.impl.FindManagerImpl
import com.intellij.lang.findUsages.LanguageFindUsages
import com.intellij.openapi.extensions.Extensions
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.usages.UsageTarget
import com.intellij.usages.UsageTargetUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProviderEx

class FindUsagesTest : BasePlatformTestCase() {
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

        val readWriteAccessDetector = readWriteAccessDetector(target)!!
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(1, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(4, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(
                "def function([], acc), do: acc",
                firstElement.parent.parent.text
        )
        assertEquals(29, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(
                "def function([h | t], acc) do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                secondElement.parent.parent.text
        )
        assertEquals(63, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(
                "do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                thirdElement.parent.parent.parent.text
        )
        assertEquals(93, thirdElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(thirdElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(thirdElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(thirdElement))

        assertEquals(UsageTypeProvider.CALL, getUsageType(thirdElement, usageTargets))

        val fourthElement = usages[3].element!!

        assertEquals(
                "do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                fourthElement.parent.parent.parent.text
        )
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

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(4, usages.size)

        val firstElement = usages[0].element!!
        val readWriteAccessDetector = readWriteAccessDetector(firstElement)!!

        assertEquals(
                "def function([], acc), do: acc",
                firstElement.parent.parent.text
        )
        assertEquals(29, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(
                "def function([h | t], acc) do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                secondElement.parent.parent.text
        )
        assertEquals(63, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(
                "do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                thirdElement.parent.parent.parent.text
        )
        assertEquals(93, thirdElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(thirdElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(thirdElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(thirdElement))

        assertEquals(UsageType.READ, getUsageType(thirdElement, usageTargets))

        val fourthElement = usages[3].element!!

        assertEquals(
                "do\n" +
                        "    function(t, [h | acc])\n" +
                        "  end",
                thirdElement.parent.parent.parent.text
        )
        assertEquals(93, fourthElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(fourthElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(fourthElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(fourthElement))

        assertEquals(UsageType.READ, getUsageType(fourthElement, usageTargets))
    }

    fun testFunctionSingleClauseUnused() {
        myFixture.configureByFiles("function_single_clause_unused.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("function", findUsagesProvider.getType(target))

        val readWriteAccessDetector = readWriteAccessDetector(target)!!
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(1, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(26, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))
    }

    fun testFunctionMultipleClausesUnused() {
        myFixture.configureByFiles("function_multiple_clauses_unused.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)!!
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(1, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(26, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(74, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(secondElement, usageTargets))
    }

    fun testFunctionMultipleModulesDeclaration() {
        myFixture.configureByFiles("function_multiple_modules_declaration_target.ex", "function_multiple_modules_declaration_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val findUsagesHandler = findUsagesHandler(target)
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!
        val secondElement = usages[1].element!!

        assertEquals(31, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        assertEquals(50, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL, getUsageType(secondElement, usageTargets))
    }

    fun testFunctionMultipleModulesUsage() {
        myFixture.configureByFiles("function_multiple_modules_usage_target.ex", "function_multiple_modules_usage_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(31, firstElement.textOffset)

        val readWriteAccessDetector = readWriteAccessDetector(firstElement)!!
        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(50, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    fun testFunctionImportDeclaration() {
        myFixture.configureByFiles("function_import_declaration_target.ex", "function_import_declaration_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val findUsagesHandler = findUsagesHandler(target)
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!
        val secondElement = usages[1].element!!

        assertEquals(
                "def declaration, do: :ok",
                firstElement.parent.parent.text
        )
        assertEquals(31, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        assertEquals(60, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.CALL, getUsageType(secondElement, usageTargets))
    }

    fun testFunctionImportUsage() {
        myFixture.configureByFiles("function_import_usage_target.ex", "function_import_usage_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(2, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!
        val secondElement = usages[1].element!!
        val readWriteAccessDetector = readWriteAccessDetector(firstElement)!!

        assertEquals(
                "def declaration, do: :ok",
                firstElement.parent.parent.text
        )
        assertEquals(31, firstElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(UsageTypeProvider.CALL_DEFINITION_CLAUSE, getUsageType(firstElement, usageTargets))

        assertEquals(
                "do\n" +
                "    declaration()\n" +
                "  end",
                secondElement.parent.parent.parent.text
        )
        assertEquals(60, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertFalse(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageType.READ, getUsageType(secondElement, usageTargets))
    }

    fun testParameterDeclaration() {
        myFixture.configureByFiles("parameter_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("parameter", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

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

    fun testModuleRecursiveDeclaration() {
        myFixture.configureByFiles("module_recursive_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("module", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(10, firstElement.textOffset)
        assertNull(readWriteAccessDetector(firstElement))
        assertEquals(UsageTypeProvider.MODULE_DEFINITION, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(33, secondElement.textOffset)
        assertNull(readWriteAccessDetector(secondElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(secondElement, usageTargets))
    }

    fun testModuleRecursiveUsage() {
        myFixture.configureByFiles("module_recursive_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("alias", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(2, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(3, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(10, firstElement.textOffset)
        assertNull(readWriteAccessDetector(firstElement))
        assertEquals(UsageTypeProvider.MODULE_DEFINITION, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(33, secondElement.textOffset)
        assertNull(readWriteAccessDetector(secondElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(33, thirdElement.textOffset)
        assertNull(readWriteAccessDetector(thirdElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(thirdElement, usageTargets))
    }

    fun testModuleNestedRecursiveDeclaration() {
        myFixture.configureByFiles("module_nested_recursive_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("module", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(3, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(
                "defmodule Parent.Declaration do\n" +
                        "  alias Parent.Declaration\n" +
                        "  alias Parent.{Declaration}\n" +
                        "end",
                firstElement.parent.parent.text
        )
        assertEquals(10, firstElement.textOffset)
        assertNull(readWriteAccessDetector(firstElement))
        assertEquals(UsageTypeProvider.MODULE_DEFINITION, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(
                "alias Parent.Declaration",
                secondElement.parent.parent.text
        )
        assertEquals(40, secondElement.textOffset)
        assertNull(readWriteAccessDetector(secondElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(
                "alias Parent.{Declaration}",
                thirdElement.parent.parent.parent.parent.parent.text
        )
        assertEquals(75, thirdElement.textOffset)
        assertNull(readWriteAccessDetector(thirdElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(thirdElement, usageTargets))
    }

    fun testModuleMultipleModulesDeclaration() {
        myFixture.configureByFiles("module_multiple_modules_declaration_target.ex", "module_multiple_modules_declaration_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("module", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements

        assertEquals(1, primaryElements.size)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(
                "defmodule Declaration do\n" +
                        "end",
                firstElement.parent.parent.parent.text
        )
        assertEquals(10, firstElement.textOffset)
        assertNull(readWriteAccessDetector(firstElement))
        assertEquals(UsageTypeProvider.MODULE_DEFINITION, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(
                "alias Declaration",
                secondElement.parent.parent.parent.text
        )
        assertEquals(27, secondElement.textOffset)
        assertNull(readWriteAccessDetector(secondElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(secondElement, usageTargets))
    }

    fun testModuleMultipleModulesUsage() {
        myFixture.configureByFiles("module_multiple_modules_usage_target.ex", "module_multiple_modules_usage_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!

        assertEquals("alias", findUsagesProvider.getType(target))

        val findUsagesHandler = findUsagesHandler(target)

        val primaryElements = findUsagesHandler.primaryElements.sortedBy { it.textOffset }

        assertEquals(2, primaryElements.size)

        val firstPrimaryElement = primaryElements[0]

        assertEquals(
                "defmodule Declaration do\n" +
                        "end",
                firstPrimaryElement.text
        )
        assertEquals(10, firstPrimaryElement.textOffset)

        val secondPrimaryElement = primaryElements[1]

        assertEquals(
                "alias Declaration",
                secondPrimaryElement.parent.parent.parent.text
        )
        assertEquals(27, secondPrimaryElement.textOffset)

        val secondaryElements = findUsagesHandler.secondaryElements

        assertEquals(0, secondaryElements.size)

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(3, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(
                "defmodule Declaration do\n" +
                        "end",
                firstElement.parent.parent.parent.text
        )
        assertEquals(10, firstElement.textOffset)
        assertNull(readWriteAccessDetector(firstElement))
        assertEquals(UsageTypeProvider.MODULE_DEFINITION, getUsageType(firstElement, usageTargets))

        val secondElement = usages[1].element!!

        assertEquals(
                "alias Declaration",
                secondElement.parent.parent.parent.text
        )
        assertEquals(27, secondElement.textOffset)
        assertNull(readWriteAccessDetector(secondElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(secondElement, usageTargets))

        val thirdElement = usages[2].element!!

        assertEquals(
                "alias Declaration",
                thirdElement.parent.parent.parent.text
        )
        assertEquals(27, thirdElement.textOffset)
        assertNull(readWriteAccessDetector(thirdElement))
        assertEquals(UsageTypeProvider.ALIAS, getUsageType(thirdElement, usageTargets))
    }

    fun testParameterUnused() {
        myFixture.configureByFiles("parameter_unused.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("parameter", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

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

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(39, firstElement.textOffset)

        val readWriteAccessDetector = readWriteAccessDetector(firstElement)!!
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
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("variable", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

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
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("variable", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

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

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(46, firstElement.textOffset)

        val readWriteAccessDetector = readWriteAccessDetector(firstElement)!!
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

    fun testModuleAttributeDeclaration() {
        myFixture.configureByFiles("module_attribute_declaration.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("module attribute", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(31, firstElement.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(
                UsageTypeProvider.MODULE_ATTRIBUTE_ACCUMULATE_OR_OVERRIDE,
                getUsageType(firstElement, usageTargets)
        )

        val secondElement = usages[1].element!!

        assertEquals(69, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.MODULE_ATTRIBUTE_READ, getUsageType(secondElement, usageTargets))
    }

    fun testModuleAttributeUsage() {
        myFixture.configureByFiles("module_attribute_usage.ex")

        val usageTargets = UsageTargetUtil.findUsageTargets(myFixture.editor, myFixture.file)

        assertEquals(1, usageTargets.size)

        val usageTarget = usageTargets[0]

        assertInstanceOf(usageTarget, PsiElement2UsageTargetAdapter::class.java)

        val target = (usageTarget as PsiElement2UsageTargetAdapter).element!!
        val readWriteAccessDetector = readWriteAccessDetector(target)!!

        assertEquals("module attribute", findUsagesProvider.getType(target))

        val usages = myFixture.findUsages(target).sortedBy { it.element!!.textOffset }

        assertEquals(2, usages.size)

        val firstElement = usages[0].element!!

        assertEquals(31, firstElement.textOffset)

        assertTrue(readWriteAccessDetector.isDeclarationWriteAccess(firstElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(firstElement))
        assertEquals(ReadWriteAccessDetector.Access.Write, readWriteAccessDetector.getExpressionAccess(firstElement))

        assertEquals(
                UsageTypeProvider.MODULE_ATTRIBUTE_ACCUMULATE_OR_OVERRIDE,
                getUsageType(firstElement, usageTargets)
        )

        val secondElement = usages[1].element!!

        assertEquals(69, secondElement.textOffset)

        assertFalse(readWriteAccessDetector.isDeclarationWriteAccess(secondElement))
        assertTrue(readWriteAccessDetector.isReadWriteAccessible(secondElement))
        assertEquals(ReadWriteAccessDetector.Access.Read, readWriteAccessDetector.getExpressionAccess(secondElement))

        assertEquals(UsageTypeProvider.MODULE_ATTRIBUTE_READ, getUsageType(secondElement, usageTargets))
    }

    private val findUsagesProvider by lazy { LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage) }

    private fun findUsagesHandler(element: PsiElement) =
        FindManager
                .getInstance(project)
                .let { it as FindManagerImpl }
                .findUsagesManager
                .getFindUsagesHandler(element, false)!!

    private fun readWriteAccessDetector(element: PsiElement) =
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.findDetector(element)

    private fun getUsageType(element: PsiElement, targets: Array<UsageTarget>): UsageType? =
            com.intellij.usages.impl.rules.UsageTypeProvider.EP_NAME
                    .extensionList
                    .mapNotNull { usageTypeProvider ->
                        when (usageTypeProvider) {
                            is UsageTypeProviderEx -> usageTypeProvider.getUsageType(element, targets)
                            else -> usageTypeProvider.getUsageType(element)
                        }
                    }
                    .firstOrNull()
}
