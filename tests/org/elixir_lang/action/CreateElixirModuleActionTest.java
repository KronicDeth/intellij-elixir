package org.elixir_lang.action;

import com.intellij.openapi.actionSystem.ActionManager;
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
        ActionManager actionManager = ActionManager.getInstance();
        CreateElixirModuleAction elixirNewFileAction = (CreateElixirModuleAction) actionManager.getAction("Elixir.NewFile");

        // @see https://devnet.jetbrains.com/message/5539349#5539349
        VirtualFile directoryVirtualFile = myFixture.getTempDirFixture().findOrCreateDir("");
        PsiDirectory directory = myFixture.getPsiManager().findDirectory(directoryVirtualFile);

        ElixirFile file = elixirNewFileAction.createFile("FooBar", TEMPLATE_NAME, directory);
        assertNotNull("Expected CreateElixirModuleAction.createFile to create an ElixirFile", file);

        boolean ignoreTrailingWhitespaces = true;

        myFixture.checkResultByFile(
                "foo_bar.ex",
                "foo_bar.ex",
                ignoreTrailingWhitespaces
        );
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/action/create_elixir_module_action_test";
    }
}
