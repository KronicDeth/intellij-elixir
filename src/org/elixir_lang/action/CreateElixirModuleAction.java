package org.elixir_lang.action;

import com.google.common.base.CaseFormat;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 15/7/7.
 */
public class CreateElixirModuleAction extends CreateFromTemplateAction<ElixirFile> {
  private static final String NEW_ELIXIR_MODULE = "New Elixir Module";

  private static final String ALIAS_REGEXP = "[A-Z][0-9a-zA-Z_]*";
  private static final String MODULE_NAME_REGEXP = ALIAS_REGEXP + "(\\." + ALIAS_REGEXP + ")*";
  private static final Pattern MODULE_NAME_PATTERN = Pattern.compile(MODULE_NAME_REGEXP);
  private static final String INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Valid pattern for Elixir modules is dot sequences of aliases (" + MODULE_NAME_REGEXP + ")";

  public CreateElixirModuleAction() {
    super(NEW_ELIXIR_MODULE, "Nested Aliases, like Foo.Bar.Baz, are created in subdirectory for the parent Aliases, foo/bar/Baz.ex", ElixirIcons.FILE);
  }

  /**
   *
   * todo: the Application-template, Supervisor-template, GenServer-template, GenEvent-template should be improved
   * */
  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder.
        setTitle(NEW_ELIXIR_MODULE).
        addKind("Empty module", ElixirIcons.FILE, "Elixir Module").
        addKind("Elixir Application", ElixirIcons.ELIXIR_APPLICATION, "Elixir Application").
        addKind("Elixir Supervisor", ElixirIcons.ELIXIR_SUPERVISOR, "Elixir Supervisor").
        addKind("Elixir GenServer", ElixirIcons.ELIXIR_GEN_SERVER, "Elixir GenServer").
        addKind("Elixir GenEvent", ElixirIcons.ELIXIR_GEN_EVENT, "Elixir GenEvent").
        setValidator(new InputValidatorEx() {
          @Nullable
          @Override
          public String getErrorText(String inputString) {
            if (StringUtil.isEmpty(inputString)) return null;

            Matcher matcher = MODULE_NAME_PATTERN.matcher(inputString);

            if (matcher.matches()) {
              return null;
            }

            return String.format(INVALID_MODULE_MESSAGE_FMT, inputString);
          }

          @Override
          public boolean checkInput(String inputString) {
            return true;
          }

          @Override
          public boolean canClose(String inputString) {
            return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
          }
        });
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_ELIXIR_MODULE;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CreateElixirModuleAction;
  }

  @Override
  protected ElixirFile createFile(String name, String templateName, PsiDirectory dir) {
    return createDirectoryAndModuleFromTemplate(name, dir, templateName);
  }

  /**
   *
   * @param moduleName
   * @param directory
   * @param templateName
   * @return ElixirFile
   * @link com.intellij.ide.actions.CreateTemplateInPackageAction#checkOrCreate
   */
  private ElixirFile createDirectoryAndModuleFromTemplate(@NotNull String moduleName, @NotNull PsiDirectory directory, String templateName) {
    PsiDirectory currentDirectory = directory;
    String lastAlias;

    if (moduleName.contains(".")) {
      String[] aliases = moduleName.split("\\.");

      for (int i = 0; i < aliases.length - 1; i++) {
        String alias = aliases[i];
        String subdirectoryName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, alias);
        PsiDirectory subdirectory = currentDirectory.findSubdirectory(subdirectoryName);

        if (subdirectory == null) {
          subdirectory = currentDirectory.createSubdirectory(subdirectoryName);
        }

        currentDirectory = subdirectory;
      }

      lastAlias = aliases[aliases.length - 1];
    } else {
      lastAlias = moduleName;
    }

    String basename = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastAlias);

    return createModuleFromTemplate(currentDirectory, basename, moduleName, templateName);
  }

  /**
   *
   * @param directory
   * @param moduleName
   * @param basename
   * @param templateName
   * @return ElixirFile
   * @link com.intellij.ide.acitons.CreateTemplateInPackageAction#doCreate
   * @link com.intellij.ide.actions.CreateClassAction
   * @link com.intellij.psi.impl.file.JavaDirectoryServiceImpl.createClassFromTemplate
   */
  private ElixirFile createModuleFromTemplate(PsiDirectory directory, String basename, String moduleName, String templateName) {
    FileTemplateManager fileTemplateManager = FileTemplateManager.getDefaultInstance();
    FileTemplate template = fileTemplateManager.getInternalTemplate(templateName);

    Properties defaultProperties = fileTemplateManager.getDefaultProperties();
    Properties properties = new Properties(defaultProperties);
    properties.setProperty(FileTemplate.ATTRIBUTE_NAME, moduleName);

    String fileName = basename + ".ex";
    PsiElement element;

    try {
      element = FileTemplateUtil.createFromTemplate(template, fileName, properties, directory);
    } catch (Exception exception) {
      LOG.error(exception);

      return null;
    }

    if (element == null) {
      return null;
    }

    return (ElixirFile) element;
  }
}
