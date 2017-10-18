package org.elixir_lang.mix.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.jps.model.JpsErlangSdkType;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.elixir.Type;
import org.elixir_lang.utils.ElixirExternalToolsNotificationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Paths;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.elixir_lang.sdk.elixir.Type.addDocumentationPaths;
import static org.elixir_lang.sdk.elixir.Type.addSourcePaths;
import static org.elixir_lang.sdk.elixir.Type.putDefaultErlangSdk;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningStateUtil.java
 */
public class MixRunningStateUtil {
    private static final String SKIP_DEPENDENCIES_PARAMETER = "--no-deps-check";

    private static void prependCodePaths(@NotNull GeneralCommandLine commandLine, @NotNull Sdk sdk) {
        VirtualFile[] virtualFiles = updatedCodePaths(sdk);

        for (VirtualFile codePath : virtualFiles) {
            commandLine.addParameters("-pa", codePath.getCanonicalPath());
        }
    }

    /**
     * Ensures code path are ebin directories that can be passed to `-pa`.
     * <p>
     * Updates all Roots in SDK as it assumes that if the Class Paths are out-of-date then the Source and Documentation
     * Paths are likely out-of-date too.
     */
    @NotNull
    private static VirtualFile[] updatedCodePaths(@NotNull Sdk sdk) {
        updateRoots(sdk);

        return sdk.getRootProvider().getFiles(OrderRootType.CLASSES);
    }

    @NotNull
    private static String oldClassPathPath(@NotNull String homePath) {
        return Paths.get(homePath, "lib").toString();
    }

    @NotNull
    private static String oldSourcePathPath(@NotNull String homePath) {
        return Paths.get(homePath, "lib").toString();
    }

    private static boolean updateClassPaths(@NotNull SdkModificator sdkModificator, @NotNull String homePath) {
        final String oldClassPathPath = oldClassPathPath(homePath);
        boolean modified = false;

        for (VirtualFile classPath : sdkModificator.getRoots(OrderRootType.CLASSES)) {
            String classPathPath = classPath.getPath();

            if (classPathPath.equals(oldClassPathPath)) {
                sdkModificator.removeRoot(classPath, OrderRootType.CLASSES);
                org.elixir_lang.sdk.Type.addCodePaths(sdkModificator);
                modified = true;
            }
        }

        return modified;
    }

    private static void updateRoots(@NotNull Sdk sdk) {
        String homePath = sdk.getHomePath();

        if (homePath != null) {
            final SdkModificator sdkModificator = sdk.getSdkModificator();
            boolean modified = updateClassPaths(sdkModificator, homePath);
            modified = updateDocumentationPaths(sdkModificator) || modified;
            modified = updateSourcePaths(sdkModificator, homePath) || modified;

            if (modified) {
                sdkModificator.commitChanges();
            }
        }
    }

    private static boolean updateSourcePaths(SdkModificator sdkModificator, String homePath) {
        final String oldSourcePathPath = oldSourcePathPath(homePath);
        boolean modified = false;

        for (VirtualFile classPath : sdkModificator.getRoots(OrderRootType.SOURCES)) {
            String classPathPath = classPath.getPath();

            if (classPathPath.equals(oldSourcePathPath)) {
                sdkModificator.removeRoot(classPath, OrderRootType.SOURCES);
                addSourcePaths(sdkModificator);
                modified = true;
            }
        }

        return modified;
    }

    private static boolean updateDocumentationPaths(@NotNull SdkModificator sdkModificator) {
        OrderRootType documentationRootType = JavadocOrderRootType.getInstance();
        final String elixirLangDotOrgDocsUrl = "http://elixir-lang.org/docs/stable/elixir/";
        boolean modified = false;

        for (VirtualFile documentationPath : sdkModificator.getRoots(documentationRootType)) {
            if (documentationPath.getUrl().equals(elixirLangDotOrgDocsUrl)) {
                VirtualFile elixirLangDotOrgDocsUrlVirtualFile =
                        VirtualFileManager.getInstance().findFileByUrl(elixirLangDotOrgDocsUrl);
                sdkModificator.removeRoot(elixirLangDotOrgDocsUrlVirtualFile, documentationRootType);
                addDocumentationPaths(sdkModificator);
                modified = true;
            }
        }

        return modified;
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
        commandLine(commandLine, project, elixirParametersList, mixParametersList);

        return addNewSkipDependencies(commandLine, configuration);
    }

    @NotNull
    public static GeneralCommandLine commandLine(@NotNull GeneralCommandLine baseMixCommandLine,
                                                 @NotNull Project project,
                                                 @NotNull ParametersList elixirParametersList,
                                                 @NotNull ParametersList mixParametersList) {
        setElixir(baseMixCommandLine, project, elixirParametersList);

        String mixPath = mixPath(project);
        baseMixCommandLine.addParameter(mixPath);

        baseMixCommandLine.addParameters(mixParametersList.getList());

        return baseMixCommandLine;
    }

    @NotNull
    private static GeneralCommandLine getBaseMixCommandLine(@NotNull MixRunConfigurationBase configuration) {
        GeneralCommandLine commandLine = withEnvironment(new GeneralCommandLine(), configuration);

        return withWorkDirectory(commandLine, configuration);
    }

    @NotNull
    public static String workingDirectory(@NotNull Project project) {
        return ObjectUtils.assertNotNull(project.getBasePath());
    }

    @Nullable
    private static String workingDirectory(@NotNull Module module) {
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
    private static String workingDirectory(@NotNull MixRunConfigurationBase configuration) {
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
                                "Mix run configuration",
                                "Mix settings",
                                "Mix executable path is " + (isEmpty ? "empty" : "not specified correctly") + "<br><a href='configure'>Configure</a></br>",
                                NotificationType.ERROR,
                                new ElixirExternalToolsNotificationListener(project)
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

                    if (erlangSdk == null) {
                        erlangSdk = putDefaultErlangSdk(sdk);
                    }

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
        return commandLine.withWorkDirectory(workingDirectory(configuration));
    }
}
