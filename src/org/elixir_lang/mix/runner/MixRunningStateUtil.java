package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {

  static final String SKIP_DEPENDENCIES_PARAMETER = "--no-deps-check";

  @NotNull
  public static GeneralCommandLine withEnvironment(@NotNull GeneralCommandLine commandLine,
                                                   @NotNull MixRunConfigurationBase configuration) {
    return commandLine.withEnvironment(configuration.getEnvs());
  }

  @NotNull
  public static GeneralCommandLine withWorkDirectory(@NotNull GeneralCommandLine commandLine,
                                                     @NotNull MixRunConfigurationBase configuration) {
    return commandLine.withWorkDirectory(getWorkingDirectory(configuration));
  }

  @NotNull
  public static String mixPath(@NotNull Project project) {
    MixSettings mixSettings = MixSettings.getInstance(project);
    return mixSettings.getMixPath();
  }

  @NotNull
  public static GeneralCommandLine addNewSkipDependencies(@NotNull GeneralCommandLine commandLine,
                                                          @NotNull MixRunConfigurationBase configuration) {
    if (configuration.isSkipDependencies()) {
      ParametersList parametersList = commandLine.getParametersList();

      if (!parametersList.hasParameter(SKIP_DEPENDENCIES_PARAMETER)) {
        parametersList.add(SKIP_DEPENDENCIES_PARAMETER);
      }
    }

    return commandLine;
  }

  @NotNull
  public static GeneralCommandLine addProgramParameters(@NotNull GeneralCommandLine commandLine,
                                                        @NotNull MixRunConfigurationBase configuration) {
    String programParameters = configuration.getProgramParameters();

    if (programParameters != null) {
      for (String programParameter : programParameters.split("\\s+")) {
        commandLine.addParameter(programParameter);
      }
    }

    return commandLine;
  }

  @NotNull
  public static GeneralCommandLine getBaseMixCommandLine(@NotNull MixRunConfigurationBase configuration) {
    GeneralCommandLine commandLine = withEnvironment(new GeneralCommandLine(), configuration);

    return withWorkDirectory(commandLine, configuration);
  }

  @NotNull
  public static String elixirPath(String sdkPath) {
    return sdkPath != null ? JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath() :
            JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);
  }

  @NotNull
  public static OSProcessHandler runMix(Project project, GeneralCommandLine commandLine) throws ExecutionException {
    try{
      return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
    }catch (ExecutionException e){
      String message = e.getMessage();
      boolean isEmpty = "Executable is not specified".equals(message);
      boolean notCorrect = message.startsWith("Cannot run program");
      if(isEmpty || notCorrect){
        Notifications.Bus.notify(
            new Notification(
                "Mix run configuration",  // groudDisplayId
                "Mix settings",           // title
                "Mix executable path is " + (isEmpty ? "empty" : "not specified correctly") + "<br><a href='configure'>Configure</a></br>",
                                          // content
                NotificationType.ERROR,   // errorType
                new ElixirExternalToolsNotificationListener(project)  // listener
            )
        );
      }
      throw e;
    }
  }

  @NotNull
  public static String getWorkingDirectory(@NotNull MixRunConfigurationBase configuration){
    String workingDirectory = configuration.getWorkingDirectory();

    if (isBlank(workingDirectory)) {
      Module module = configuration.getConfigurationModule().getModule();

      if (module != null) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();

        if (contentRoots.length >= 1) {
          workingDirectory = contentRoots[0].getPath();
        }
      }

      if (isBlank(workingDirectory)) {
        workingDirectory = ObjectUtils.assertNotNull(configuration.getProject().getBasePath());
      }
    }

    return workingDirectory;
  }
}
