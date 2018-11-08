package org.elixir_lang.create_module

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

import java.io.IOException

/**
 * @see [https://confluence.jetbrains.com/display/IntelliJIDEA/Completion+Test"](https://confluence.jetbrains.com/display/IntelliJIDEA/Completion+Test)
 */
class DialogTest : LightCodeInsightFixtureTestCase() {
    // TODO fix extra blank lines added by formatter
    @Throws(IOException::class)
    fun testCamelCaseAliasIsLowerCaseUnderscored() {
        checkModuleFile("FooBar", "foo_bar.ex")
    }

    // TODO fix extra blank lines added by formatter
    @Throws(IOException::class)
    fun testAliasDotAliasIsDirectorySlashFile() {
        checkModuleFile("Foo.Bar", "foo/bar.ex")
    }

    // TODO fix extra blank lines added by formatter
    @Throws(IOException::class)
    fun testCamelCaseAliasDotCamelCaseAliasIsLowerCaseUnderscored() {
        checkModuleFile("CamelCaseOne.CamelCaseTwo", "camel_case_one/camel_case_two.ex")
    }

    @Throws(IOException::class)
    private fun checkModuleFile(moduleName: String, path: String) {
        // @see https://devnet.jetbrains.com/message/5539349#5539349
        val directoryVirtualFile = myFixture.tempDirFixture.findOrCreateDir("")
        val directory = myFixture.psiManager.findDirectory(directoryVirtualFile)!!
        val dialog = Dialog(project, directory)
        dialog.name = moduleName
        dialog.templateName = TEMPLATE_NAME

        ApplicationManager.getApplication().runWriteAction {
            dialog.doOKAction()
        }

        val ignoreTrailingWhitespaces = true
        myFixture.checkResultByFile(path, path, ignoreTrailingWhitespaces)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/action/create_elixir_module_action_test"
}

private const val TEMPLATE_NAME = "Elixir Module"
