package org.elixir_lang.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirFile;

import java.io.IOException;

/**
 * @see <a href="https://confluence.jetbrains.com/display/IntelliJIDEA/Completion+Test">https://confluence.jetbrains.com/display/IntelliJIDEA/Completion+Test"</a>
 */
public class CreateElixirModuleActionTest extends LightCodeInsightFixtureTestCase {
    public static final String TEMPLATE_NAME = "Elixir Module";

    public void testCamelCaseAliasIsLowerCaseUnderscored() throws IOException {
        checkModuleFile("FooBar", "foo_bar.ex");
    }

    public void testAliasDotAliasIsDirectorySlashFile()  throws IOException {
        checkModuleFile("Foo.Bar", "foo/bar.ex");
    }

    public void testCamelCaseAliasDotCamelCaseAliasIsLowerCaseUnderscored() throws IOException {
        checkModuleFile("CamelCaseOne.CamelCaseTwo", "camel_case_one/camel_case_two.ex");
    }

    private void checkModuleFile(final String moduleName, String path) throws IOException {
        ActionManager actionManager = ActionManager.getInstance();
        final CreateElixirModuleAction elixirNewFileAction = (CreateElixirModuleAction) actionManager.getAction("Elixir.NewFile");

        // @see https://devnet.jetbrains.com/message/5539349#5539349
        VirtualFile directoryVirtualFile = myFixture.getTempDirFixture().findOrCreateDir("");
        final PsiDirectory directory = myFixture.getPsiManager().findDirectory(directoryVirtualFile);

        Application application = ApplicationManager.getApplication();
        application.runWriteAction(new Runnable() {
            @Override
            public void run() {
                ElixirFile file = elixirNewFileAction.createFile(moduleName, TEMPLATE_NAME, directory);
                assertNotNull("Expected CreateElixirModuleAction.createFile to create an ElixirFile", file);
            }
        });

        boolean ignoreTrailingWhitespaces = true;
        myFixture.checkResultByFile(path, path, ignoreTrailingWhitespaces);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/action/create_elixir_module_action_test";
    }
}
