package org.elixir_lang.mix.runner;

import com.google.common.base.Charsets;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.jps.model.JpsErlangSdkType;
import org.elixir_lang.utils.SetupElixirSDKNotificationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.elixir_lang.jps.model.JpsElixirSdkType.ELIXIR_TOOL_MIX;
import static org.elixir_lang.sdk.elixir.Type.mostSpecificSdk;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {
    private static final String SKIP_DEPENDENCIES_PARAMETER = "--no-deps-check";

    private static void prependCodePaths(@NotNull GeneralCommandLine commandLine, @NotNull Sdk sdk) {
        for (VirtualFile codePath : sdk.getRootProvider().getFiles(OrderRootType.CLASSES)) {
            commandLine.addParameters("-pa", codePath.getCanonicalPath());
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
        RunConfigurationModule runConfigurationModule = configuration.getConfigurationModule();
        Module module = runConfigurationModule.getModule();

        if (module == null) {
            File workingDirectory = commandLine.getWorkDirectory();
            VirtualFile virtualFile = VfsUtil.findFileByIoFile(workingDirectory, true);

            assert virtualFile != null :
                    "Working directory (" + workingDirectory + ") could not be mapped to a VirtualFile";

            module = ModuleUtilCore.findModuleForFile(virtualFile, configuration.getProject());

            assert module != null :
                    "Working directory (" + workingDirectory + ") VirtualFile could not be mapped to a Module";
        }

        commandLine(commandLine, module, elixirParametersList, mixParametersList);

        return addNewSkipDependencies(commandLine, configuration);
    }

    @NotNull
    public static GeneralCommandLine commandLine(@NotNull GeneralCommandLine baseMixCommandLine,
                                                 @NotNull Module module,
                                                 @NotNull ParametersList elixirParametersList,
                                                 @NotNull ParametersList mixParametersList) {
        Sdk sdk = mostSpecificSdk(module);

        return commandLine(baseMixCommandLine, sdk, elixirParametersList, mixParametersList);
    }

    @NotNull
    public static GeneralCommandLine commandLine(@NotNull GeneralCommandLine baseMixCommandLine,
                                                 @Nullable Sdk sdk,
                                                 @NotNull ParametersList elixirParametersList,
                                                 @NotNull ParametersList mixParametersList) {
        setElixir(baseMixCommandLine, sdk, elixirParametersList);

        String mixPath = mixPath(sdk);
        baseMixCommandLine.addParameter(mixPath);

        baseMixCommandLine.addParameters(mixParametersList.getList());

        return baseMixCommandLine;
    }

    @NotNull
    private static GeneralCommandLine getBaseMixCommandLine(@NotNull MixRunConfigurationBase configuration) {
        GeneralCommandLine commandLine =
                withEnvironment(new GeneralCommandLine().withCharset(Charsets.UTF_8), configuration);

        return withWorkDirectory(commandLine, configuration);
    }

    @Nullable
    public static Module module(@NotNull MixRunConfigurationBase mixRunConfigurationBase) {
        Module module = mixRunConfigurationBase.getConfigurationModule().getModule();

        if (module == null) {
            String workingDirectory = workingDirectory(mixRunConfigurationBase);

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(workingDirectory);

            if (virtualFile != null) {
                Project project = mixRunConfigurationBase.getProject();
                module = ModuleUtilCore.findModuleForFile(virtualFile, project);
            }
        }

        return module;
    }

    @NotNull
    public static String workingDirectory(@NotNull Project project) {
        return ObjectUtils.assertNotNull(project.getBasePath());
    }

    @Nullable
    public static String workingDirectory(@NotNull Module module) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        String workingDirectory;

        if (contentRoots.length >= 1) {
            workingDirectory = contentRoots[0].getPath();
        } else {
            workingDirectory = null;
        }

        return workingDirectory;
    }

    @NotNull
    public static String workingDirectory(@NotNull MixRunConfigurationBase configuration) {
        String workingDirectory = configuration.getWorkingDirectory();

        if (isBlank(workingDirectory)) {
            Module module = configuration.getConfigurationModule().getModule();

            if (module != null) {
                workingDirectory = workingDirectory(module);
            }

            if (isBlank(workingDirectory)) {
                workingDirectory = workingDirectory(configuration.getProject());
            }
        }

        return workingDirectory;
    }

    private static String mixPath(@Nullable Sdk sdk) {
        String mixPath;

        if (sdk != null) {
            String homePath = sdk.getHomePath();

            if (homePath != null) {
                mixPath = JpsElixirSdkType.getMixScript(homePath).getPath();
            } else {
                mixPath = ELIXIR_TOOL_MIX;
            }
        } else {
            mixPath = ELIXIR_TOOL_MIX;
        }

        return mixPath;
    }

    @NotNull
    static OSProcessHandler runMix(Project project, GeneralCommandLine commandLine) throws ExecutionException {
        try {
            return new ColoredProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
        } catch (ExecutionException e) {
            String message = e.getMessage();
            boolean isEmpty = "Executable is not specified".equals(message);
            boolean notCorrect = message.startsWith("Cannot run program");
            if (isEmpty || notCorrect) {
                Notifications.Bus.notify(
                        new Notification(
                                "Mix run configuration",
                                "Mix settings",
                                "Mix executable path, elixir executable path, or erl executable path is " +
                                        (isEmpty ? "empty" : "not specified correctly") +
                                        "<br><a href='configure'>Configure</a></br>",
                                NotificationType.ERROR,
                                new SetupElixirSDKNotificationListener(project, commandLine.getWorkDirectory())
                        )
                );
            }
            throw e;
        }
    }

    private static void setElixir(@NotNull GeneralCommandLine commandLine,
                                  @Nullable Sdk sdk,
                                  @NotNull ParametersList parametersList) {
        if (sdk != null) {
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
        return commandLine.withWorkDirectory(workingDirectory(configuration));
    }
}
