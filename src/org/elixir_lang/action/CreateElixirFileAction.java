package org.elixir_lang.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import org.elixir_lang.ElixirIcon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides
 */
public class CreateElixirFileAction extends CreateFileFromTemplateAction {
  private static final String ALIAS_REGEXP = "[A-Z][0-9a-zA-Z_]*";
  private static final String MODULE_NAME_REGEXP = ALIAS_REGEXP + "(\\." + ALIAS_REGEXP + ")*";
  private static final Pattern MODULE_NAME_PATTERN = Pattern.compile(MODULE_NAME_REGEXP);
  private static final String NEW_ELIXIR_FILE = "New Elixir File";
  private static final String INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Valid pattern for Elixir modules is dot sequences of aliases (" + MODULE_NAME_REGEXP + ")";

  public CreateElixirFileAction() {
    super(NEW_ELIXIR_FILE, "", ElixirIcon.FILE);
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_ELIXIR_FILE;
  }

  @Override
  protected void buildDialog(final Project project,
                             PsiDirectory directory,
                             CreateFileFromTemplateDialog.Builder builder) {
    builder.
        setTitle(NEW_ELIXIR_FILE).
        addKind("Empty module", ElixirIcon.FILE, "Elixir Module").
        setValidator(new InputValidatorEx() {
          @Override
          public boolean checkInput(String inputString) {
            return true;
          }

          @Override
          public boolean canClose(String inputString) {
            return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
          }

          @Override
          public String getErrorText(String inputString) {
            if (StringUtil.isEmpty(inputString))
              return null;

            Matcher matcher = MODULE_NAME_PATTERN.matcher(inputString);

            if (matcher.matches()) {
              return null;
            }

            return String.format(INVALID_MODULE_MESSAGE_FMT, inputString);
          }
        })
    ;
  }
}
