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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.jps.model.JpsErlangSdkType;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.elixir.Type;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {
    private static final Logger LOGGER = Logger.getInstance(MixRunningStateUtil.class);
    private static final String SKIP_DEPENDENCIES_PARAMETER = "--no-deps-check";

    private static void prependCodePaths(@NotNull GeneralCommandLine commandLine, @NotNull Sdk sdk) {
        for (VirtualFile virtualFile : sdk.getRootProvider().getFiles(OrderRootType.CLASSES)) {
            commandLine.addParameters("-pa", virtualFile.getCanonicalPath());
        }
    }

    @NotNull
    private static GeneralCommandLine addNewSkipDependencies(@NotNull GeneralCommandLine commandLine,
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

    @NotNull
    private static String elixirPath(String sdkPath) {
        return sdkPath != null ? JpsElixirSdkType.getScriptInterpreterExecutable(sdkPath).getAbsolutePath() :
                JpsElixirSdkType.getExecutableFileName(JpsElixirSdkType.SCRIPT_INTERPRETER);
    }

    @NotNull
    private static GeneralCommandLine getBaseMixCommandLine(@NotNull MixRunConfigurationBase configuration) {
        GeneralCommandLine commandLine = withEnvironment(new GeneralCommandLine(), configuration);

        return withWorkDirectory(commandLine, configuration);
    }

    @NotNull
    private static String getWorkingDirectory(@NotNull MixRunConfigurationBase configuration) {
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
    private static String mixPath(@NotNull Project project) {
        MixSettings mixSettings = MixSettings.getInstance(project);
        return mixSettings.getMixPath();
    }

    @NotNull
    static OSProcessHandler runMix(Project project, GeneralCommandLine commandLine) throws ExecutionException {
        try {
            return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
        } catch (ExecutionException e) {
            String message = e.getMessage();
            boolean isEmpty = "Executable is not specified".equals(message);
            boolean notCorrect = message.startsWith("Cannot run program");
            if (isEmpty || notCorrect) {
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

    private static void setElixir(@NotNull GeneralCommandLine commandLine,
                                  @NotNull Project project,
                                  @NotNull ParametersList parametersList) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if (sdk != null) {
            SdkTypeId sdkType = sdk.getSdkType();

            if (sdkType == Type.getInstance()) {
                org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData sdkAdditionalData =
                        (org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData) sdk.getSdkAdditionalData();

                if (sdkAdditionalData != null) {
                    Sdk erlangSdk = sdkAdditionalData.getErlangSdk();

                    if (erlangSdk != null) {
                        String erlangHomePath = erlangSdk.getHomePath();

                        if (erlangHomePath != null) {
                            File erlFile = JpsErlangSdkType.getByteCodeInterpreterExecutable(erlangHomePath);

                            if (erlFile.exists() && erlFile.canExecute()) {
                                // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
                                commandLine.setExePath(erlFile.getAbsolutePath());
                                // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L96-L102
                                prependCodePaths(commandLine, sdk);
                                // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L106
                                commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
                                // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
                                commandLine.addParameter("-extra");
                            }
                        }
                    } else {
                        String erl = JpsErlangSdkType.getExecutableFileName("erl");

                        // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
                        commandLine.setExePath(erl);
                        // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L96-L102
                        prependCodePaths(commandLine, sdk);
                        // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L106
                        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
                        // See https://github.com/elixir-lang/elixir/blob/v1.5.1/bin/elixir.bat#L111
                        commandLine.addParameter("-extra");
                    }
                }
            }
        } else {
            String elixir = JpsElixirSdkType.getExecutableFileName("elixir");
            commandLine.setExePath(elixir);
        }

        commandLine.addParameters(parametersList.getList());
    }

    @NotNull
    private static GeneralCommandLine withEnvironment(@NotNull GeneralCommandLine commandLine,
                                                      @NotNull MixRunConfigurationBase configuration) {
        return commandLine.withEnvironment(configuration.getEnvs());
    }

    @NotNull
    private static GeneralCommandLine withWorkDirectory(@NotNull GeneralCommandLine commandLine,
                                                        @NotNull MixRunConfigurationBase configuration) {
        return commandLine.withWorkDirectory(getWorkingDirectory(configuration));
    }
}
