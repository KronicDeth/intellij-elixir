package org.elixir_lang.action;

import com.google.common.base.CaseFormat;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.elixir_lang.Icons;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.Pair.pair;

/**
 * Created by zyuyou on 15/7/7.
 */
public class CreateElixirModuleAction extends CreateFromTemplateAction<ElixirFile> {
    /*
     * CONSTANTS
     */

    private static final String NEW_ELIXIR_MODULE = "New Elixir Module";

    private static final String ALIAS_REGEXP = "[A-Z][0-9a-zA-Z_]*";
    private static final String MODULE_NAME_REGEXP = ALIAS_REGEXP + "(\\." + ALIAS_REGEXP + ")*";
    private static final Pattern MODULE_NAME_PATTERN = Pattern.compile(MODULE_NAME_REGEXP);
    private static final String DESCRIPTION = "Nested Aliases, like Foo.Bar.Baz, are created in subdirectory for the " +
            "parent Aliases, foo/bar/Baz.ex";
    private static final String EXTENSION = ".ex";
    private static final String EXISTING_MODULE_MESSAGE_FMT = "'%s' already exists";
    private static final String INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Elixir module " +
            "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
            "capital letter. " + DESCRIPTION;

    /*
     * Static Methods
     */

    @NotNull
    private static Pair<List<String>, String> ancestorDirectoryNamesBaseNamePair(@NotNull String moduleName) {
        List<String> directoryList;
        String lastAlias;

        if (moduleName.contains(".")) {
            String[] aliases = moduleName.split("\\.");
            int directoryListSize = aliases.length - 1;
            directoryList = new ArrayList<String>(directoryListSize);

            for (int i = 0; i < directoryListSize; i++) {
                String alias = aliases[i];
                String subdirectoryName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, alias);
                directoryList.add(subdirectoryName);
            }

            lastAlias = aliases[aliases.length - 1];
        } else {
            directoryList = Collections.emptyList();
            lastAlias = moduleName;
        }

        String basename = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastAlias) + EXTENSION;

        return pair(directoryList, basename);
    }

    /**
     * @link com.intellij.ide.actions.CreateTemplateInPackageAction#checkOrCreate
     */
    @Nullable
    private static ElixirFile createDirectoryAndModuleFromTemplate(@NotNull String moduleName,
                                                                   @NotNull PsiDirectory directory,
                                                                   @NotNull String templateName) {
        PsiDirectory currentDirectory = directory;

        Pair<List<String>, String> ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(moduleName);
        List<String> ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first;

        for (String ancestorDirectoryName : ancestorDirectoryNames) {
            PsiDirectory subdirectory = currentDirectory.findSubdirectory(ancestorDirectoryName);

            if (subdirectory == null) {
                subdirectory = currentDirectory.createSubdirectory(ancestorDirectoryName);
            }

            currentDirectory = subdirectory;
        }

        String basename = ancestorDirectoryNamesBaseNamePair.second;

        return createModuleFromTemplate(currentDirectory, basename, moduleName, templateName);
    }

    /**
     * @link com.intellij.ide.acitons.CreateTemplateInPackageAction#doCreate
     * @link com.intellij.ide.actions.CreateClassAction
     * @link com.intellij.psi.impl.file.JavaDirectoryServiceImpl.createClassFromTemplate
     */
    @Nullable
    private static ElixirFile createModuleFromTemplate(@NotNull PsiDirectory directory,
                                                       @NotNull String basename,
                                                       @NotNull String moduleName,
                                                       @NotNull String templateName) {
        FileTemplateManager fileTemplateManager = FileTemplateManager.getDefaultInstance();
        FileTemplate template = fileTemplateManager.getInternalTemplate(templateName);

        Properties defaultProperties = fileTemplateManager.getDefaultProperties();
        Properties properties = new Properties(defaultProperties);
        properties.setProperty(FileTemplate.ATTRIBUTE_NAME, moduleName);

        PsiElement element;

        try {
            element = FileTemplateUtil.createFromTemplate(template, basename, properties, directory);
        } catch (Exception exception) {
            LOG.error(exception);

            return null;
        }

        if (element == null) {
            return null;
        }

        return (ElixirFile) element;
    }

    @Contract(pure = true)
    @NotNull
    private static String fullPath(@NotNull PsiDirectory directory,
                                   @NotNull Pair<List<String>, String> ancestorDirectoryNamesBaseNamePair) {
        return directory.getVirtualFile().getCanonicalPath() + "/" + path(ancestorDirectoryNamesBaseNamePair);
    }

    @Contract(pure = true)
    @NotNull
    private static String path(@NotNull Pair<List<String>, String> ancestorDirectoryNamesBaseNamePair) {
        List<String> ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first;
        String directoryPath = StringUtil.join(ancestorDirectoryNames, "/");

        return directoryPath + ancestorDirectoryNamesBaseNamePair.second;
    }

    /*
     * Constructors
     */

    public CreateElixirModuleAction() {
        super(NEW_ELIXIR_MODULE, DESCRIPTION, Icons.FILE);
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CreateElixirModuleAction;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /*
     * Protected Instance Methods
     */

    /**
     * todo: the Application-template, Supervisor-template, GenServer-template, GenEvent-template should be improved
     */
    @Override
    protected void buildDialog(@NotNull Project project,
                               @NotNull final PsiDirectory directory,
                               @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder.
                setTitle(NEW_ELIXIR_MODULE).
                addKind("Empty module", Icons.FILE, "Elixir Module").
                addKind("Elixir Application", Icons.File.APPLICATION, "Elixir Application").
                addKind("Elixir Supervisor", Icons.File.SUPERVISOR, "Elixir Supervisor").
                addKind("Elixir GenServer", Icons.File.GEN_SERVER, "Elixir GenServer").
                addKind("Elixir GenEvent", Icons.File.GEN_EVENT, "Elixir GenEvent").
                setValidator(new InputValidatorEx() {
                    /*
                     * Public Instance Methods
                     */

                    @Override
                    public boolean canClose(String inputString) {
                        return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
                    }

                    @Override
                    public boolean checkInput(String inputString) {
                        return checkFormat(inputString) && checkDoesNotExist(inputString);
                    }

                    @Nullable
                    @Override
                    public String getErrorText(String inputString) {
                        String errorText = null;

                        if (!StringUtil.isEmpty(inputString)) {
                            if (!checkFormat(inputString)) {
                                errorText = String.format(INVALID_MODULE_MESSAGE_FMT, inputString);
                            } else if (!checkDoesNotExist(inputString)) {
                                String fullPath = fullPath(directory, ancestorDirectoryNamesBaseNamePair(inputString));
                                errorText = String.format(EXISTING_MODULE_MESSAGE_FMT, fullPath);
                            }
                        }

                        return errorText;
                    }

                    /*
                     * Private Instance Methods
                     */

                    private boolean checkDoesNotExist(@NotNull String moduleName) {
                        Pair<List<String>, String> ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(
                                moduleName
                        );
                        List<String> ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first;
                        PsiDirectory currentDirectory = directory;
                        boolean doesNotExists = false;

                        for (String ancestorDirectoryName : ancestorDirectoryNames) {
                            PsiDirectory subdirectory = currentDirectory.findSubdirectory(ancestorDirectoryName);

                            if (subdirectory == null) {
                                doesNotExists = true;

                                break;
                            }

                            currentDirectory = subdirectory;
                        }

                        // if all the directories exist
                        if (!doesNotExists) {
                            String baseName = ancestorDirectoryNamesBaseNamePair.second;
                            doesNotExists = currentDirectory.findFile(baseName) == null;
                        }

                        return doesNotExists;
                    }

                    private boolean checkFormat(@NotNull String inputString) {
                        Matcher matcher = MODULE_NAME_PATTERN.matcher(inputString);

                        return matcher.matches();
                    }
                });
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return NEW_ELIXIR_MODULE;
    }

    @Override
    protected ElixirFile createFile(String name, String templateName, PsiDirectory dir) {
        return createDirectoryAndModuleFromTemplate(name, dir, templateName);
    }
}
