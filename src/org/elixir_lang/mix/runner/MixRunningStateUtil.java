package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkType;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {
  public MixRunningStateUtil() {
  }

  @NotNull
  public static GeneralCommandLine getMixCommandLine(@NotNull MixRunConfigurationBase configuration) throws ExecutionException{
    Project project = configuration.getProject();
    MixSettings mixSettings = MixSettings.getInstance(project);
    String sdkPath = ElixirSdkType.getSdkPath(project);

    String elixirPath = sdkPath != null? JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath():
        JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);

    GeneralCommandLine commandLine = new GeneralCommandLine();

    commandLine.withWorkDirectory(getWorkingDirectory(configuration));

    String mixPath = mixSettings.getMixPath();

    if (mixPath.endsWith(".bat")) {
      commandLine.setExePath(mixPath);
    } else {
      commandLine.setExePath(elixirPath);
      commandLine.addParameter(mixPath);
    }

    List<String> split = ContainerUtil.list(configuration.getCommand().split("\\s+"));
    if(configuration.isSkipDependencies() && !split.contains("--no-deps-check")){
      commandLine.addParameter("--no-deps-check");
    }
    commandLine.addParameters(split);
    return commandLine;
  }

  @NotNull
  public static OSProcessHandler runMix(Project project, GeneralCommandLine commandLine) throws ExecutionException {
    try{
      return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
    }catch (ExecutionException e){
      String message = e.getMessage();
      boolean isEmpty = message.equals("Executable is not specified");
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
    Module module = configuration.getConfigurationModule().getModule();
    if(module != null){
      VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
      if(contentRoots.length >= 1){
        return contentRoots[0].getPath();
      }
    }
    return ObjectUtils.assertNotNull(configuration.getProject().getBasePath());
  }
}
