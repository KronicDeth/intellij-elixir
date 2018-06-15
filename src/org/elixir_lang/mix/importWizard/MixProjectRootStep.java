package org.elixir_lang.mix.importWizard;

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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportWizardStep;
import org.elixir_lang.Mix;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

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

    private static void fetchDependencies(@NotNull final String workingDirectory, @NotNull final Sdk sdk) {
        mixTask(workingDirectory, sdk, "Fetching dependencies", "deps.get");
    }

    /**
     * private methods
     */

    private static void mixTask(@NotNull final String workingDirectory,
                                @NotNull final Sdk sdk,
                                @SuppressWarnings("SameParameterValue") @NotNull final String title,
                                @SuppressWarnings("SameParameterValue") @NotNull final String task,
                                @NotNull final String... taskParameters) {
        ProgressManager.getInstance().run(
                new Task.Modal(null, title, true) {
                    @Override
                    public void run(@NotNull final ProgressIndicator indicator) {
                        indicator.setIndeterminate(true);

                        GeneralCommandLine generalCommandLine = Mix.commandLine(
                                emptyMap(),
                                workingDirectory,
                                sdk,
                                emptyList(),
                                emptyList()
                        );
                        generalCommandLine.addParameter(task);
                        generalCommandLine.addParameters(taskParameters);

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

    private static void updateHex(@NotNull final String workingDirectory,
                                  @NotNull final Sdk sdk) {
        mixTask(workingDirectory, sdk, "Updating hex", "local.hex", "--force");
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
            String workingDirectory = projectRoot.getCanonicalPath();

            assert workingDirectory != null;

            Sdk sdk = getWizardContext().getProjectJdk();

            updateHex(workingDirectory, sdk);
            fetchDependencies(workingDirectory, sdk);
        }

        MixProjectImportBuilder builder = getBuilder();
        builder.setIsImportingProject(getWizardContext().isCreatingNewProject());

        return builder.setProjectRoot(projectRoot);
    }
}
