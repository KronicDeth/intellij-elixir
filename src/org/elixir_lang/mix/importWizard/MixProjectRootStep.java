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
 * Created by zyuyou on 15/7/1.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectRootStep.java
 */
public class MixProjectRootStep extends ProjectImportWizardStep {
  private static final Logger LOG = Logger.getInstance(MixProjectImportBuilder.class);

  private JPanel myPanel;
  private TextFieldWithBrowseButton myProjectRootComponent;
  private JCheckBox myGetDepsCheckbox;
  private MixConfigurationForm myMixConfigurationForm;

  private static final boolean ourEnabled = !SystemInfo.isWindows;

  public MixProjectRootStep(WizardContext context) {
    super(context);

    String projectFileDirectory = context.getProjectFileDirectory();
    myProjectRootComponent.addBrowseFolderListener("Select mix.exs of a mix project to import", "", null,
        FileChooserDescriptorFactory.createSingleFolderDescriptor());

    myProjectRootComponent.setText(projectFileDirectory); // provide project path

    myGetDepsCheckbox.setVisible(ourEnabled);
    myMixConfigurationForm.setPath(getMixPath(projectFileDirectory));
  }

  @Override
  public JComponent getComponent() {
    myMixConfigurationForm.createComponent();
    return myPanel;
  }

  @Override
  public void updateDataModel() {
    String projectRoot = myProjectRootComponent.getText();
    if(!projectRoot.isEmpty()){
      suggestProjectNameAndPath(null, projectRoot);
    }
  }

  @Override
  public boolean validate() throws ConfigurationException {
    String projectRootPath = myProjectRootComponent.getText();
    if(StringUtil.isEmpty(projectRootPath)){
      return false;
    }

    VirtualFile projectRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(projectRootPath);
    if(projectRoot == null){
      return false;
    }

    if(myGetDepsCheckbox.isSelected() && !ApplicationManager.getApplication().isUnitTestMode()){
      if(!myMixConfigurationForm.isPathValid()){
        return false;
      }
      fetchDependencies(projectRoot, myMixConfigurationForm.getPath());
    }

    MixProjectImportBuilder builder = getBuilder();
    builder.setMixPath(myMixConfigurationForm.getPath());
    builder.setIsImportingProject(getWizardContext().isCreatingNewProject());

    return builder.setProjectRoot(projectRoot);
  }

  @Override
  @NotNull
  public JComponent getPreferredFocusedComponent() {
    return myProjectRootComponent.getTextField();
  }

  @Override
  @NotNull
  protected MixProjectImportBuilder getBuilder() {
    return (MixProjectImportBuilder) super.getBuilder();
  }

  /**
   * private methods
   * */

  @NotNull
  private static String getMixPath(@Nullable String directory){
    if(directory != null){
      File mix = new File(directory, "mix");
      if(mix.exists() && mix.canExecute()){
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

  private static void fetchDependencies(@NotNull final VirtualFile projectRoot, @NotNull final String mixPath){
    final Project project = ProjectImportBuilder.getCurrentProject();
    String sdkPath = project != null ? ElixirSdkType.getSdkPath(project) : null;
    final String elixirPath = sdkPath != null ?
        JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath() :
        JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);

    ProgressManager.getInstance().run(new Task.Modal(project, "Fetching dependencies", true){
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        indicator.setIndeterminate(true);

        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withWorkDirectory(projectRoot.getCanonicalPath());
        commandLine.setExePath(elixirPath);
        commandLine.addParameter(mixPath);
        commandLine.addParameter("deps.get");
        try{
          OSProcessHandler handler = new OSProcessHandler(commandLine.createProcess(), commandLine.getPreparedCommandLine(Platform.current()));
          handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
              String text = event.getText();
              indicator.setText2(text);
            }
          });
          ProcessTerminatedListener.attach(handler);
          handler.startNotify();
          handler.waitFor();
          indicator.setText2("Refreshing");
        }catch (ExecutionException e){
          LOG.warn(e);
        }
      }
    });
  }
}
