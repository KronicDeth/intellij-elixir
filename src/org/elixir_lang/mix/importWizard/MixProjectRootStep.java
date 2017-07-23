package org.elixir_lang.mix.importWizard;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Platform;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportWizardStep;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.settings.MixConfigurationForm;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectRootStep.java
 */
public class MixProjectRootStep extends ProjectImportWizardStep {
    private static final Logger LOG = Logger.getInstance(MixProjectImportBuilder.class);
    private static final boolean ourEnabled = !SystemInfo.isWindows;
    private JCheckBox myGetDepsCheckbox;
    private MixConfigurationForm myMixConfigurationForm;
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
        myMixConfigurationForm.setPath(getMixPath(projectFileDirectory));
    }

    private static void fetchDependencies(@Nullable final Project project,
                                          @NotNull final String workingDirectory,
                                          @NotNull final String elixirPath,
                                          @NotNull final String mixPath) {
        mixTask(project, workingDirectory, elixirPath, mixPath, "Fetching dependencies", "deps.get");
    }

    /**
     * private methods
     */

    @NotNull
    private static String getMixPath(@Nullable String directory) {
        if (directory != null) {
            File mix = new File(directory, "mix");
            if (mix.exists() && mix.canExecute()) {
                return mix.getPath();
            }
        }

        String output = "";
        GeneralCommandLine generalCommandLine = null;

        if (SystemInfo.isWindows) {
            generalCommandLine = new GeneralCommandLine("where");
            generalCommandLine.addParameter("mix.bat");
        } else if (SystemInfo.isMac || SystemInfo.isLinux || SystemInfo.isUnix) {
            generalCommandLine = new GeneralCommandLine("which");
            generalCommandLine.addParameter("mix");
        }

        if (generalCommandLine != null) {
            try {
                output = ScriptRunnerUtil.getProcessOutput(generalCommandLine);
            } catch (Exception ignored) {
                LOG.warn(ignored);
            }
        }

        return output.trim();
    }

    @NotNull
    private static String maybeProjectToElixirPath(@Nullable Project project) {
        String sdkPath = maybeProjectToMaybeSdkPath(project);

        return maybeSdkPathToElixirPath(sdkPath);
    }

    @Nullable
    private static String maybeProjectToMaybeSdkPath(@Nullable Project project) {
        String sdkPath;

        if (project != null) {
            sdkPath = ElixirSdkType.getSdkPath(project);
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
                                @NotNull final String workingDirectory,
                                @NotNull final String elixirPath,
                                @NotNull final String mixPath,
                                @NotNull final String title,
                                @NotNull final String... parameters) {
        ProgressManager.getInstance().run(
                new Task.Modal(project, title, true) {
                    @Override
                    public void run(@NotNull final ProgressIndicator indicator) {
                        indicator.setIndeterminate(true);

                        GeneralCommandLine commandLine = new GeneralCommandLine();
                        commandLine.withWorkDirectory(workingDirectory);
                        commandLine.setExePath(elixirPath);
                        commandLine.addParameter(mixPath);

                        for (String parameter : parameters) {
                            commandLine.addParameter(parameter);
                        }

                        try {
                            OSProcessHandler handler = new OSProcessHandler(
                                    commandLine.createProcess(),
                                    commandLine.getPreparedCommandLine(Platform.current())
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
                                  @NotNull final String workingDirectory,
                                  @NotNull final String elixirPath,
                                  @NotNull final String mixPath) {
        mixTask(project, workingDirectory, elixirPath, mixPath, "Updating hex", "local.hex", "--force");
    }

    @Override
    @NotNull
    protected MixProjectImportBuilder getBuilder() {
        return (MixProjectImportBuilder) super.getBuilder();
    }

    @Override
    public JComponent getComponent() {
        myMixConfigurationForm.createComponent();
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
    public boolean validate() throws ConfigurationException {
        String projectRootPath = myProjectRootComponent.getText();
        if (StringUtil.isEmpty(projectRootPath)) {
            return false;
        }

        VirtualFile projectRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(projectRootPath);
        if (projectRoot == null) {
            return false;
        }

        String mixPath = myMixConfigurationForm.getPath();

        if (myGetDepsCheckbox.isSelected() && !ApplicationManager.getApplication().isUnitTestMode()) {
            if (!myMixConfigurationForm.isPathValid()) {
                return false;
            }

            final Project project = ProjectImportBuilder.getCurrentProject();
            String workingDirectory = projectRoot.getCanonicalPath();

            assert workingDirectory != null;

            String elixirPath = maybeProjectToElixirPath(project);

            updateHex(project, workingDirectory, elixirPath, mixPath);
            fetchDependencies(project, workingDirectory, elixirPath, mixPath);
        }

        MixProjectImportBuilder builder = getBuilder();
        builder.setMixPath(mixPath);
        builder.setIsImportingProject(getWizardContext().isCreatingNewProject());

        return builder.setProjectRoot(projectRoot);
    }
}
