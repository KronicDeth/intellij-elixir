package org.elixir_lang.mix.importWizard;

import com.google.common.base.Charsets;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Platform;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportWizardStep;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.elixir_lang.sdk.elixir.Type.mostSpecificSdk;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectRootStep.java
 */
public class MixProjectRootStep extends ProjectImportWizardStep {
    private static final Logger LOG = Logger.getInstance(MixProjectImportBuilder.class);
    private static final boolean ourEnabled = !SystemInfo.isWindows;
    private JCheckBox myGetDepsCheckbox;
    private JPanel myPanel;
    private TextFieldWithBrowseButton myProjectRootComponent;

    MixProjectRootStep(WizardContext context) {
        super(context);

        String projectFileDirectory = context.getProjectFileDirectory();
        //noinspection DialogTitleCapitalization
        myProjectRootComponent.addBrowseFolderListener(
                "Select mix.exs of a mix project to import",
                "",
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
        );

        myProjectRootComponent.setText(projectFileDirectory); // provide project path

        myGetDepsCheckbox.setVisible(ourEnabled);
    }

    private static void fetchDependencies(@Nullable final Project project,
                                          @NotNull final GeneralCommandLine workingDirectoryGeneralCommandLine,
                                          @Nullable final Sdk sdk) {
        mixTask(project, workingDirectoryGeneralCommandLine, sdk, "Fetching dependencies", "deps.get");
    }

    /**
     * private methods
     */


    @Nullable
    private static String maybeProjectToMaybeSdkPath(@Nullable Project project) {
        String sdkPath;

        if (project != null) {
            sdkPath = Type.getSdkPath(project);
        } else {
            sdkPath = null;
        }

        return sdkPath;
    }

    @NotNull
    private static String maybeSdkPathToElixirPath(@Nullable String sdkPath) {
        String elixirPath;

        if (sdkPath != null) {
            elixirPath = JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath();
        } else {
            elixirPath = JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);
        }

        return elixirPath;
    }

    private static void mixTask(@Nullable final Project project,
                                @NotNull final GeneralCommandLine workingDirectoryGeneralCommandLine,
                                @Nullable final Sdk sdk,
                                @NotNull final String title,
                                @NotNull final String... parameters) {
        ProgressManager.getInstance().run(
                new Task.Modal(project, title, true) {
                    @Override
                    public void run(@NotNull final ProgressIndicator indicator) {
                        indicator.setIndeterminate(true);

                        ParametersList mixParametersList = new ParametersList();
                        mixParametersList.addAll(parameters);

                        GeneralCommandLine generalCommandLine = MixRunningStateUtil.commandLine(
                                workingDirectoryGeneralCommandLine,
                                sdk,
                                new ParametersList(),
                                mixParametersList
                        );

                        try {
                            OSProcessHandler handler = new OSProcessHandler(
                                    generalCommandLine.createProcess(),
                                    generalCommandLine.getPreparedCommandLine(Platform.current())
                            );
                            handler.addProcessListener(
                                    new ProcessAdapter() {
                                        @Override
                                        public void onTextAvailable(ProcessEvent event, Key outputType) {
                                            String text = event.getText();
                                            indicator.setText2(text);
                                        }
                                    }
                            );
                            ProcessTerminatedListener.attach(handler);
                            handler.startNotify();
                            handler.waitFor();
                            indicator.setText2("Refreshing");
                        } catch (ExecutionException e) {
                            LOG.warn(e);
                        }
                    }
                }
        );
    }

    private static void updateHex(@Nullable final Project project,
                                  @NotNull final GeneralCommandLine workingDirectoryGeneralCommandLine,
                                  @Nullable final Sdk sdk) {
        mixTask(project, workingDirectoryGeneralCommandLine, sdk, "Updating hex", "local.hex", "--force");
    }

    @Override
    @NotNull
    protected MixProjectImportBuilder getBuilder() {
        return (MixProjectImportBuilder) super.getBuilder();
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    @NotNull
    public JComponent getPreferredFocusedComponent() {
        return myProjectRootComponent.getTextField();
    }

    @Override
    public void updateDataModel() {
        String projectRoot = myProjectRootComponent.getText();
        if (!projectRoot.isEmpty()) {
            suggestProjectNameAndPath(null, projectRoot);
        }
    }

    @Override
    public boolean validate() {
        String projectRootPath = myProjectRootComponent.getText();
        if (StringUtil.isEmpty(projectRootPath)) {
            return false;
        }

        VirtualFile projectRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(projectRootPath);
        if (projectRoot == null) {
            return false;
        }

        if (myGetDepsCheckbox.isSelected() && !ApplicationManager.getApplication().isUnitTestMode()) {
            final Project project = ProjectImportBuilder.getCurrentProject();
            String workingDirectory = projectRoot.getCanonicalPath();

            assert workingDirectory != null;

            GeneralCommandLine workingDirectoryGeneralCommandLine =
                    new GeneralCommandLine().withCharset(Charsets.UTF_8);
            workingDirectoryGeneralCommandLine.withWorkDirectory(workingDirectory);

            assert project != null : "Cannot find SDK for non-existent Project";

            Sdk sdk = mostSpecificSdk(project);

            updateHex(project, workingDirectoryGeneralCommandLine, sdk);
            fetchDependencies(project, workingDirectoryGeneralCommandLine, sdk);
        }

        MixProjectImportBuilder builder = getBuilder();
        builder.setIsImportingProject(getWizardContext().isCreatingNewProject());

        return builder.setProjectRoot(projectRoot);
    }
}
