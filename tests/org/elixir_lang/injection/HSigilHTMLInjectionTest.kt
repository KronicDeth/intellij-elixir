package org.elixir_lang.injection

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.injected.InjectedTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class HSigilHTMLInjectionTest : BasePlatformTestCase() {

    fun testDoBlockDiagnostic() {
        myFixture.configureByFile("html_injection.ex")
//        InjectedTestUtil.registerMockInjectedLanguageManager(getApplication(), project, getPluginDescriptor())

        // Step 1: Check if the file is loaded correctly
        assertNotNull("File should be loaded", myFixture.file)

        // test injection
        val context: PsiElement = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)

        context.text
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/injection"
    }
}
