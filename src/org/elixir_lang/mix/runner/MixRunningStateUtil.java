package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkType;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {
  private static final Logger LOGGER = Logger.getInstance(MixRunningStateUtil.class);
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
      com.intellij.execution.configurations.ParametersList parametersList = commandLine.getParametersList();

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
  private static GeneralCommandLine getBaseMixCommandLine(@NotNull MixRunConfigurationBase configuration) {
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

  @NotNull
  public static GeneralCommandLine commandLine(@NotNull MixRunConfigurationBase configuration,
                                               @NotNull ParametersList elixirParametersList,
                                               @NotNull ParametersList mixParametersList) {
    GeneralCommandLine commandLine = getBaseMixCommandLine(configuration);
    Project project = configuration.getProject();
    setElixir(commandLine, project, elixirParametersList);

    String mixPath = mixPath(project);
    commandLine.addParameter(mixPath);

    commandLine.addParameters(mixParametersList.getList());

    return addNewSkipDependencies(commandLine, configuration);
  }

  private static void setElixir(@NotNull GeneralCommandLine commandLine,
                                @NotNull Project project,
                                @NotNull ParametersList parametersList) {
    String sdkPath = ElixirSdkType.getSdkPath(project);
    String elixirPath = elixirPath(sdkPath);

    /* replace elixir.bat with direct call to erl.exe, to work around quoting problem
       See https://github.com/KronicDeth/intellij-elixir/issues/603 */
    if (elixirPath.endsWith(".bat") && sdkPath != null) {
      // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
      commandLine.setExePath("erl.exe");
      // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L96-L102
      addEbinPaths(commandLine, sdkPath);
      // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L106
      commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
      // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
      commandLine.addParameter("-extra");
    } else {
      commandLine.setExePath(elixirPath);
    }

    commandLine.addParameters(parametersList.getList());
  }

  private static void addEbinPaths(@NotNull GeneralCommandLine commandLine, @NotNull String sdkPath) {
    try (DirectoryStream<Path>  libStream = Files.newDirectoryStream(Paths.get(sdkPath, "lib"))) {
      libStream.forEach(
              path -> {
                try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(path, "*")) {
                  pathStream.forEach(ebinPath -> commandLine.addParameters("-pa", ebinPath.toString()));
                } catch (IOException ioException) {
                  LOGGER.error(ioException);
                }
              }
      );
    } catch (IOException ioException) {
      LOGGER.error(ioException);
    }
  }
}
