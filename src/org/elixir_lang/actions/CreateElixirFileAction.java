package org.elixir_lang.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by zyuyou on 15/7/7.
 */
public class CreateElixirFileAction extends CreateFileFromTemplateAction implements DumbAware {
  private static final String NEW_ELIXIR_FILE = "New Elixir File";

  public CreateElixirFileAction() {
    super(NEW_ELIXIR_FILE, "", ElixirIcons.FILE);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder.
        setTitle(NEW_ELIXIR_FILE).
        addKind("Empty module", ElixirIcons.FILE, "Elixir Module").
        addKind("Application", ElixirIcons.ELIXIR_APPLICATION, "Elixir Application").
        addKind("Supervisor", ElixirIcons.ELIXIR_SUPERVISOR, "Elixir Supervisor").
        addKind("GenServer", ElixirIcons.ELIXIR_GEN_SERVER, "Elixir GenServer").
        addKind("GenEvent", ElixirIcons.ELIXIR_GEN_EVENT, "Elixir GenEvent").
        setValidator(new InputValidatorEx() {
          @Nullable
          @Override
          public String getErrorText(String inputString) {
            return !StringUtil.isEmpty(inputString) && FileUtil.sanitizeFileName(inputString).equals(inputString) ? null : "'" + inputString + "'" + " is not a valid Elixir module name";
          }

          @Override
          public boolean checkInput(String inputString) {
            return getErrorText(inputString) == null;
          }

          @Override
          public boolean canClose(String inputString) {
            return getErrorText(inputString) == null;
          }
        });
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_ELIXIR_FILE;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CreateElixirFileAction;
  }
}
