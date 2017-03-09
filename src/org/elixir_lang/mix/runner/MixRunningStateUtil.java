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
import org.elixir_lang.sdk.ElixirSdkType;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.stripEnd;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {

  private static final String SKIP_DEPENDENCIES_PARAMETER = "--no-deps-check";

//  @NotNull
//  public static GeneralCommandLine mixCommandLine(@NotNull MixRunConfigurationBase configuration,
//                                                  @Nullable List<String> elixirParams,
//                                                  @Nullable List<String> mixParams) {
//
//    GeneralCommandLine commandLine = getBaseMixCommandLine(configuration);
//
//    Project project = configuration.getProject();
//    String mixPath = mixPath(project);
//
//    String sdkPath = ElixirSdkType.getSdkPath(project);
//    String elixirPath = elixirPath(sdkPath);
//
//    commandLine.setExePath(elixirPath);
//    if (elixirParams != null) commandLine.addParameters(elixirParams);
//    commandLine.addParameter(mixPath);
//    addNewSkipDependencies(commandLine, configuration);
//    if (mixParams != null) commandLine.addParameters(mixParams);
//
//    return commandLine;
//  }

  @NotNull
  private static GeneralCommandLine withEnvironment(@NotNull GeneralCommandLine commandLine,
                                                    @NotNull MixRunConfigurationBase configuration) {
    return commandLine.withEnvironment(configuration.getEnvs());
  }

  @NotNull
  public static GeneralCommandLine withWorkDirectory(@NotNull GeneralCommandLine commandLine,
                                                      @NotNull MixRunConfigurationBase configuration) {
    return commandLine.withWorkDirectory(getWorkingDirectory(configuration));
  }

  @NotNull
  static String mixPath(@NotNull Project project) {
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
  private static String getWorkingDirectory(@NotNull MixRunConfigurationBase configuration){
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

  public static GeneralCommandLine commandLine(@NotNull MixRunConfigurationBase configuration,
                                               @NotNull List<String> elixirParams,
                                               @NotNull List<String> mixParams) {

    GeneralCommandLine commandLine = getBaseMixCommandLine(configuration);

    Project project = configuration.getProject();
    String mixPath = mixPath(project);

    if (mixPath.endsWith(".bat") && elixirParams.isEmpty()) {
      commandLine.setExePath(mixPath);
    } else {
      mixPath = stripEnd(mixPath, ".bat");
      String sdkPath = ElixirSdkType.getSdkPath(project);
      String elixirPath = elixirPath(sdkPath);

      commandLine.setExePath(elixirPath);
      commandLine.addParameters(elixirParams);
      commandLine.addParameter(mixPath);
    }

    commandLine.addParameters(mixParams);

    return addNewSkipDependencies(commandLine, configuration);
  }
}
