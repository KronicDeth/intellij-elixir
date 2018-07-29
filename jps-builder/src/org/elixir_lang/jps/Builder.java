package org.elixir_lang.jps;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.elixir_lang.jps.builder.ExecutionException;
import org.elixir_lang.jps.builder.GeneralCommandLine;
import org.elixir_lang.jps.builder.ProcessAdapter;
import org.elixir_lang.jps.builder.SourceRootDescriptor;
import org.elixir_lang.jps.compiler_options.Extension;
import org.elixir_lang.jps.model.ErlangSdkNameMissing;
import org.elixir_lang.jps.model.ModuleType;
import org.elixir_lang.jps.model.SdkProperties;
import org.elixir_lang.jps.sdk_type.Elixir;
import org.elixir_lang.jps.sdk_type.Erlang;
import org.elixir_lang.jps.sdk_type.MissingHomePath;
import org.elixir_lang.jps.target.BuilderUtil;
import org.elixir_lang.jps.target.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.resources.ResourcesBuilder;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.library.JpsLibrary;
import org.jetbrains.jps.model.library.JpsLibraryCollection;
import org.jetbrains.jps.model.library.JpsLibraryRoot;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.util.*;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/jps-plugin/src/org/intellij/erlang/jps/builder/ErlangBuilder.java
 * https://github.com/ignatov/intellij-erlang/tree/master/jps-plugin/src/org/intellij/erlang/jps/rebar
 */
public class Builder extends TargetBuilder<SourceRootDescriptor, Target> {
    public static final String BUILDER_NAME = "Elixir Builder";
    public static final String ELIXIR_SOURCE_EXTENSION = "ex";
    public static final String ELIXIR_SCRIPT_EXTENSION = "exs";
    private static final String URL_PREFIX = "file://";
    private static final String ELIXIR_TEST_SOURCE_EXTENSION = ELIXIR_SCRIPT_EXTENSION;

    private static final String ElIXIRC_NAME = "elixirc";
    private static final String MIX_NAME = "mix";
    private static final String ADD_PATH_TO_FRONT_OF_CODE_PATH = "-pa";
    private static final FileFilter ELIXIR_SOURCE_FILTER =
            file -> FileUtilRt.extensionEquals(file.getName(), ELIXIR_SOURCE_EXTENSION);
    private static final FileFilter ELIXIR_TEST_SOURCE_FILTER =
            file -> FileUtilRt.extensionEquals(file.getName(), ELIXIR_TEST_SOURCE_EXTENSION);
    // use JavaBuilderExtension?
    private static final Set<? extends JpsModuleType<?>> ourCompilableModuleTypes =
            Collections.singleton(ModuleType.INSTANCE);
    private static final String MIX_CONFIG_FILE_NAME = "mix." + ELIXIR_SCRIPT_EXTENSION;
    private final static Logger LOGGER = Logger.getInstance(Builder.class);

    public Builder() {
        super(Arrays.asList(Type.PRODUCTION, Type.TEST));

        // disables java resource builder for elixir modules
        ResourcesBuilder.registerEnabler(module -> !(module.getModuleType() instanceof ModuleType));
    }

    /**
     * Build With elixirc.
     * if "isMake": compile all files of the module and dependent module.
     * else: just for compile the target affected file.
     */
    private static void doBuildWithElixirc(Target target,
                                           CompileContext context,
                                           JpsModule module,
                                           CompilerOptions compilerOptions,
                                           Collection<File> filesToCompile) throws ProjectBuildException {

        // ensure compile output directory
        File outputDirectory = getBuildOutputDirectory(module, target.isTests(), context);

        runElixirc(target, context, compilerOptions, filesToCompile, outputDirectory);
    }

    private static void doBuildWithMix(Target target,
                                       CompileContext context,
                                       JpsModule module,
                                       CompilerOptions compilerOptions) throws ProjectBuildException, IOException {
        JpsSdk<SdkProperties> sdk = BuilderUtil.getSdk(context, module);

        for (String contentRootUrl : module.getContentRootsList().getUrls()) {
            String contentRootPath = new URL(contentRootUrl).getPath();
            File contentRootDir = new File(contentRootPath);
            File mixConfigFile = new File(contentRootDir, MIX_CONFIG_FILE_NAME);
            if (!mixConfigFile.exists()) continue;

            String task = target.isTests() ? "test" : "compile";

            runMix(sdk, module, contentRootPath, task, compilerOptions, context);
        }
    }

    /*** doBuildWithElixirc releated private methods */

    @NotNull
    private static File getBuildOutputDirectory(@NotNull JpsModule module,
                                                boolean forTests,
                                                @NotNull CompileContext context) throws ProjectBuildException {

        JpsJavaExtensionService instance = JpsJavaExtensionService.getInstance();
        File outputDirectory = instance.getOutputDirectory(module, forTests);
        if (outputDirectory == null) {
            String errorMessage = "No output directory for module " + module.getName();
            context.processMessage(new CompilerMessage(ElIXIRC_NAME, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }

        if (!outputDirectory.exists()) {
            FileUtil.createDirectory(outputDirectory);
        }
        return outputDirectory;
    }

    private static void runElixirc(Target target,
                                   CompileContext context,
                                   CompilerOptions compilerOptions,
                                   Collection<File> files,
                                   File outputDirectory) throws ProjectBuildException {
        GeneralCommandLine commandLine = getElixircCommandLine(target, context, compilerOptions, files, outputDirectory);

        run(commandLine, context, ElIXIRC_NAME, compilerOptions);
    }

    private static GeneralCommandLine getElixircCommandLine(Target target,
                                                            CompileContext context,
                                                            CompilerOptions compilerOptions,
                                                            Collection<File> files,
                                                            File outputDirectory) throws ProjectBuildException {

        GeneralCommandLine commandLine = new GeneralCommandLine();

        // get executable
        JpsModule module = target.getModule();
        JpsSdk<SdkProperties> sdk = BuilderUtil.getSdk(context, module);
        File executable = Elixir.getByteCodeCompilerExecutable(sdk.getHomePath());

        List<String> compileFilePaths = getCompileFilePaths(module, target, context, files);

        commandLine.withWorkDirectory(outputDirectory);
        commandLine.setExePath(executable.getAbsolutePath());
        addDependentModuleCodePath(commandLine, module, target, context);
        addCompileOptions(commandLine, compilerOptions);
        commandLine.addParameters(compileFilePaths);

        return commandLine;
    }

    @NotNull
    private static List<String> getCompileFilePaths(@NotNull JpsModule module,
                                                    @NotNull Target target,
                                                    @NotNull CompileContext context,
                                                    Collection<File> files) {
        // make
        if (context.getScope().isBuildIncrementally(target.getTargetType())) {
            return getCompileFilePathsDefault(module, target);
        }

        // force build files
        return ContainerUtil.map(files, File::getAbsolutePath);
    }

    @NotNull
    private static List<String> getCompileFilePathsDefault(@NotNull JpsModule module, @NotNull Target target) {
        CommonProcessors.CollectProcessor<File> exFilesCollector = new CommonProcessors.CollectProcessor<File>() {
            @Override
            protected boolean accept(File file) {
                return !file.isDirectory() && FileUtilRt.extensionEquals(file.getName(), ELIXIR_SOURCE_EXTENSION);
            }
        };

        List<JpsModuleSourceRoot> sourceRoots = new ArrayList<>();
        ContainerUtil.addAll(sourceRoots, module.getSourceRoots(JavaSourceRootType.SOURCE));
        if (target.isTests()) {
            ContainerUtil.addAll(sourceRoots, module.getSourceRoots(JavaSourceRootType.TEST_SOURCE));
        }

        for (JpsModuleSourceRoot root : sourceRoots) {
            FileUtil.processFilesRecursively(root.getFile(), exFilesCollector);
        }

        return ContainerUtil.map(exFilesCollector.getResults(), File::getAbsolutePath);
    }

    private static void addDependentModuleCodePath(@NotNull GeneralCommandLine commandLine,
                                                   @NotNull JpsModule module,
                                                   @NotNull Target target,
                                                   @NotNull CompileContext context) throws ProjectBuildException {

        ArrayList<JpsModule> codePathModules = new ArrayList<>();
        collectDependentModules(module, codePathModules, new HashSet<>());

        addModuleToCodePath(commandLine, module, target.isTests(), context);
        for (JpsModule codePathModule : codePathModules) {
            if (codePathModule != module) {
                addModuleToCodePath(commandLine, codePathModule, false, context);
            }
        }
    }

    private static void collectDependentModules(@NotNull JpsModule module,
                                                @NotNull Collection<JpsModule> addedModules,
                                                @NotNull Set<String> addedModuleNames) {

        String moduleName = module.getName();
        if (addedModuleNames.contains(moduleName)) return;
        addedModuleNames.add(moduleName);
        addedModules.add(module);

        for (JpsDependencyElement dependency : module.getDependenciesList().getDependencies()) {
            if (!(dependency instanceof JpsModuleDependency)) continue;
            JpsModuleDependency moduleDependency = (JpsModuleDependency) dependency;
            JpsModule depModule = moduleDependency.getModule();
            if (depModule != null) {
                collectDependentModules(depModule, addedModules, addedModuleNames);
            }
        }
    }

    private static void addModuleToCodePath(@NotNull GeneralCommandLine commandLine,
                                            @NotNull JpsModule module,
                                            boolean forTests,
                                            @NotNull CompileContext context) throws ProjectBuildException {

        File outputDirectory = getBuildOutputDirectory(module, forTests, context);
        commandLine.addParameters(ADD_PATH_TO_FRONT_OF_CODE_PATH, outputDirectory.getPath());
        for (String rootUrl : module.getContentRootsList().getUrls()) {
            try {
                String path = new URL(rootUrl).getPath();
                commandLine.addParameters(ADD_PATH_TO_FRONT_OF_CODE_PATH, path);
            } catch (MalformedURLException e) {
                context.processMessage(new CompilerMessage(ElIXIRC_NAME, BuildMessage.Kind.ERROR, "Failed to find content root for module: " + module.getName()));
            }
        }
    }

    private static void addCompileOptions(@NotNull GeneralCommandLine commandLine, CompilerOptions compilerOptions) {
        if (!compilerOptions.attachDocsEnabled) {
            commandLine.addParameter("--no-docs");
        }

        if (!compilerOptions.attachDebugInfoEnabled) {
            commandLine.addParameter("--no-debug-info");
        }

        if (compilerOptions.warningsAsErrorsEnabled) {
            commandLine.addParameter("--warnings-as-errors");
        }

        if (compilerOptions.ignoreModuleConflictEnabled) {
            commandLine.addParameter("--ignore-module-conflict");
        }
    }

    /*** doBuildWithMix related private methods */

    @Nullable
    private static String elixirSdkToElixirExePath(@NotNull JpsSdk elixirSdk) {
        String elixirHomePath = elixirSdk.getHomePath();
        String elixirExePath = null;

        if (elixirHomePath != null) {
            elixirExePath = Elixir.homePathToElixirExePath(elixirHomePath);
        }

        return elixirExePath;
    }

    @NotNull
    private static String erlangSdkLibraryToErlExePath(@NotNull JpsLibrary erlangSdkLibrary) throws FileNotFoundException, AccessDeniedException {
        return erlangJpsSdkToErlExePath((JpsSdk) erlangSdkLibrary.getProperties());
    }

    @NotNull
    private static String erlangSdkNameToErlExePath(@NotNull String erlangSdkName, @NotNull JpsModule module) throws LibraryNotFound, FileNotFoundException, AccessDeniedException {
        JpsLibraryCollection libraryCollection = module.getProject().getModel().getGlobal().getLibraryCollection();
        JpsLibrary erlangSdkLibrary = libraryCollection.findLibrary(erlangSdkName);

        String erlExePath;

        if (erlangSdkLibrary != null) {
            erlExePath = erlangSdkLibraryToErlExePath(erlangSdkLibrary);
        } else {
            throw new LibraryNotFound(erlangSdkName);
        }

        return erlExePath;
    }

    @NotNull
    public static String erlangHomePathToErlExePath(@Nullable String erlangHomePath) throws
            AccessDeniedException, FileNotFoundException {
        String erlExePath;

        if (erlangHomePath != null) {
            erlExePath = Erlang.homePathToErlExePath(erlangHomePath);
        } else {
            throw new FileNotFoundException("Erlang SDK home path is not set");
        }

        return erlExePath;
    }

    @NotNull
    private static String erlangJpsSdkToErlExePath(@NotNull JpsSdk erlangSdk) throws FileNotFoundException, AccessDeniedException {
        String erlangHomePath = erlangSdk.getHomePath();
        return erlangHomePathToErlExePath(erlangHomePath);
    }

    private static void prependCodePaths(@NotNull GeneralCommandLine commandLine, @NotNull JpsSdk sdk) {
        JpsLibrary jpsLibrary = sdk.getParent();
        List<JpsLibraryRoot> compiledRoots = jpsLibrary.getRoots(JpsOrderRootType.COMPILED);

        for (JpsLibraryRoot compiledRoot : compiledRoots) {
            String url = compiledRoot.getUrl();

            assert url.startsWith(URL_PREFIX);

            String path = url.substring(URL_PREFIX.length(), url.length());

            commandLine.addParameters("-pa", path);
        }
    }

    @NotNull
    private static String sdkPropertiesToErlExePath(@NotNull SdkProperties sdkProperties, @NotNull JpsModule module) throws ErlangSdkNameMissing, FileNotFoundException, AccessDeniedException, LibraryNotFound {
        String erlangSdkName = sdkProperties.ensureErlangSdkName();

        return erlangSdkNameToErlExePath(erlangSdkName, module);
    }

    @NotNull
    private static SdkProperties ensureSdkProperties(@NotNull JpsSdk<SdkProperties> sdk) throws MissingSdkProperties {
        SdkProperties sdkProperties = sdk.getSdkProperties();

        if (sdkProperties == null) {
            throw new MissingSdkProperties();
        }

        return sdkProperties;
    }

    private static void setErl(@NotNull GeneralCommandLine commandLine,
                               @NotNull JpsSdk<SdkProperties> sdk,
                               @NotNull SdkProperties sdkProperties,
                               @NotNull JpsModule module) throws
            FileNotFoundException, AccessDeniedException, LibraryNotFound, ErlangSdkNameMissing {
        String erlExePath = sdkPropertiesToErlExePath(sdkProperties, module);
        setErl(commandLine, erlExePath, sdk);
    }

    private static void setErl(@NotNull GeneralCommandLine generalCommandLine,
                               @NotNull String exePath,
                               @NotNull JpsSdk sdk) {
        generalCommandLine.setExePath(exePath);
        prependCodePaths(generalCommandLine, sdk);
    }

    private static void addElixir(@NotNull GeneralCommandLine commandLine) {
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
        commandLine.addParameter("-extra");
    }

    private static void addMix(@NotNull GeneralCommandLine commandLine, @NotNull JpsSdk<SdkProperties> sdk) throws MissingHomePath {
        String mixPath = Elixir.mixPath(sdk);
        commandLine.addParameter(mixPath);
    }

    private static GeneralCommandLine erlCommandLine(@NotNull JpsSdk<SdkProperties> sdk,
                                                     @NotNull JpsModule module,
                                                     @Nullable String workingDirectory)
            throws AccessDeniedException, ErlangSdkNameMissing, FileNotFoundException, LibraryNotFound, MissingSdkProperties {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withWorkDirectory(workingDirectory);
        SdkProperties sdkProperties = ensureSdkProperties(sdk);
        setErl(commandLine, sdk, sdkProperties, module);

        return commandLine;
    }

    private static GeneralCommandLine elixirCommandLine(@NotNull JpsSdk<SdkProperties> sdk,
                                                        @NotNull JpsModule module,
                                                        @Nullable String workingDirectory)
            throws AccessDeniedException, ErlangSdkNameMissing, FileNotFoundException, LibraryNotFound, MissingSdkProperties {
        GeneralCommandLine commandLine = erlCommandLine(sdk, module, workingDirectory);
        addElixir(commandLine);

        return commandLine;
    }

    private static GeneralCommandLine mixCommandLine(@NotNull JpsSdk<SdkProperties> sdk,
                                                     @NotNull JpsModule module,
                                                     @Nullable String workingDirectory,
                                                     @NotNull String task,
                                                     @NotNull CompilerOptions compilerOptions) throws
            FileNotFoundException, ErlangSdkNameMissing, LibraryNotFound, AccessDeniedException, MissingHomePath, MissingSdkProperties {
        GeneralCommandLine commandLine = elixirCommandLine(sdk, module, workingDirectory);
        addMix(commandLine, sdk);
        commandLine.addParameter(task);
        addCompileOptions(commandLine, compilerOptions);

        return commandLine;
    }

    private static void runMix(@NotNull JpsSdk<SdkProperties> sdk,
                               @NotNull JpsModule module,
                               @Nullable String contentRootPath,
                               @NotNull String task,
                               @NotNull CompilerOptions compilerOptions,
                               @NotNull CompileContext context) throws ProjectBuildException {
        GeneralCommandLine commandLine;

        try {
            commandLine = mixCommandLine(sdk, module, contentRootPath, task, compilerOptions);
        } catch (AccessDeniedException |
                ErlangSdkNameMissing |
                FileNotFoundException |
                LibraryNotFound |
                MissingHomePath |
                MissingSdkProperties exception) {
            throw new ProjectBuildException(
                    "Couldn't construct command line for mix: " + exception.getMessage(),
                    exception
            );
        }

        run(commandLine, context, MIX_NAME, compilerOptions);
    }

    private static void run(@NotNull GeneralCommandLine commandLine,
                            @NotNull CompileContext context,
                            @NotNull String builderName,
                            @NotNull CompilerOptions compilerOptions) throws ProjectBuildException {
        Process process;

        try {
            process = commandLine.createProcess();
        } catch (ExecutionException executionException) {
            throw new ProjectBuildException("Failed to run " + builderName, executionException);
        }

        BaseOSProcessHandler handler = new BaseOSProcessHandler(
                process,
                commandLine.getCommandLineString(),
                Charset.defaultCharset()
        );
        com.intellij.execution.process.ProcessAdapter adapter = new ProcessAdapter(
                context,
                builderName,
                commandLine.getWorkDirectory().getPath(),
                compilerOptions
        );
        handler.addProcessListener(adapter);
        handler.startNotify();
        handler.waitFor();
    }

    @Override
    public void build(@NotNull Target target,
                      @NotNull DirtyFilesHolder<SourceRootDescriptor, Target> holder,
                      @NotNull BuildOutputConsumer outputConsumer,
                      @NotNull CompileContext context) throws ProjectBuildException, IOException {
        LOGGER.info(target.getPresentableName());
        final Set<File> filesToCompile = new THashSet<>(FileUtil.FILE_HASHING_STRATEGY);

        holder.processDirtyFiles((target1, file, root) -> {
            boolean isAcceptFile = target1.isTests() ? ELIXIR_TEST_SOURCE_FILTER.accept(file) : ELIXIR_SOURCE_FILTER.accept(file);
            if (isAcceptFile && ourCompilableModuleTypes.contains(target1.getModule().getModuleType())) {
                filesToCompile.add(file);
            }
            return true;
        });

        if (filesToCompile.isEmpty() && !holder.hasRemovedFiles()) return;

        JpsModule module = target.getModule();
        JpsProject project = module.getProject();
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(project).getOptions();

        if (compilerOptions.useMixCompiler) {
            doBuildWithMix(target, context, module, compilerOptions);
        } else {
            // elixirc can not compile tests now.
            if (!target.isTests()) {
                doBuildWithElixirc(target, context, module, compilerOptions, filesToCompile);
            }
        }
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return BUILDER_NAME;
    }
}
