package org.elixir_lang.jps.builder;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.jps.builder.compiler_options.Extension;
import org.elixir_lang.jps.builder.execution.ExecutionException;
import org.elixir_lang.jps.builder.execution.GeneralCommandLine;
import org.elixir_lang.jps.builder.execution.ProcessAdapter;
import org.elixir_lang.jps.builder.execution.SourceRootDescriptor;
import org.elixir_lang.jps.builder.model.ErlangSdkNameMissing;
import org.elixir_lang.jps.builder.model.ModuleType;
import org.elixir_lang.jps.builder.model.SdkProperties;
import org.elixir_lang.jps.builder.sdk_type.Elixir;
import org.elixir_lang.jps.builder.sdk_type.Erlang;
import org.elixir_lang.jps.builder.sdk_type.MissingHomePath;
import org.elixir_lang.jps.builder.target.BuilderUtil;
import org.elixir_lang.jps.builder.target.Type;
import org.elixir_lang.jps.shared.CompilerOptions;
import org.elixir_lang.jps.shared.mix.MixToolingBootstrapSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.logging.ProjectBuilderLogger;
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
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.intellij.util.io.URLUtil.extractPath;

/**
 * <a href="https://github.com/ignatov/intellij-erlang/blob/master/jps-plugin/src/org/intellij/erlang/jps/builder/ErlangBuilder.java">...</a>
 * <a href="https://github.com/ignatov/intellij-erlang/tree/master/jps-plugin/src/org/intellij/erlang/jps/rebar">...</a>
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
    private final static Logger logger = Logger.getInstance(Builder.class);
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
                                           Set<String> absolutePathsToCompile) throws ProjectBuildException {

        // ensure compile output directory
        File outputDirectory = getBuildOutputDirectory(module, target.isTests(), context);

        runElixirc(target, context, compilerOptions, absolutePathsToCompile, outputDirectory);
    }

    private static void doBuildWithMix(Target target,
                                       CompileContext context,
                                       JpsModule module,
                                       CompilerOptions compilerOptions) throws ProjectBuildException {
        JpsSdk<SdkProperties> sdk = BuilderUtil.getSdk(context, module);

        for (String contentRootUrl : module.getContentRootsList().getUrls()) {
            String contentRootPath = extractPath(contentRootUrl);
            File contentRootDir = new File(contentRootPath);

            File mixConfigFile = new File(contentRootDir, MIX_CONFIG_FILE_NAME);

            if (!mixConfigFile.exists()) {
                continue;
            }

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
                                   Set<String> absolutePaths,
                                   File outputDirectory) throws ProjectBuildException {
        GeneralCommandLine commandLine = getElixircCommandLine(target, context, compilerOptions, absolutePaths, outputDirectory);

        run(commandLine, context, ElIXIRC_NAME, compilerOptions);
    }

    private static GeneralCommandLine getElixircCommandLine(Target target,
                                                            CompileContext context,
                                                            CompilerOptions compilerOptions,
                                                            Set<String> absolutePaths,
                                                            File outputDirectory) throws ProjectBuildException {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        // get executable
        JpsModule module = target.getModule();
        JpsSdk<SdkProperties> sdk = BuilderUtil.getSdk(context, module);
        File executable = Elixir.getByteCodeCompilerExecutable(sdk.getHomePath());

        List<String> compileFilePaths = getCompileFilePaths(module, target, context, absolutePaths);

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
                                                    Set<String> absolutePaths) {
        // make
        if (context.getScope().isBuildIncrementally(target.getTargetType())) {
            return getCompileFilePathsDefault(module, target);
        }

        // force build files
        return new ArrayList<>(absolutePaths);
    }

    @NotNull
    private static List<String> getCompileFilePathsDefault(@NotNull JpsModule module, @NotNull Target target) {
        CommonProcessors.CollectProcessor<File> exFilesCollector = new CommonProcessors.CollectProcessor<>() {
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

        Collection<File> fileCollection = exFilesCollector.getResults();

        return ContainerUtil.map(fileCollection, File::getAbsolutePath);
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
            if (!(dependency instanceof JpsModuleDependency moduleDependency)) continue;
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
            String path = extractPath(rootUrl);
            commandLine.addParameters(ADD_PATH_TO_FRONT_OF_CODE_PATH, path);
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
    @NotNull
    private static String erlangSdkLibraryToErlExePath(@NotNull JpsLibrary erlangSdkLibrary)
            throws FileNotFoundException, AccessDeniedException {
        JpsSdk<?> erlangSdk = (JpsSdk<?>) erlangSdkLibrary.getProperties();
        return erlangJpsSdkToErlExePath(erlangSdk);
    }

    @NotNull
    private static String erlangSdkNameToErlExePath(@NotNull String erlangSdkName,
                                                    @NotNull JpsModule module) throws LibraryNotFound, FileNotFoundException, AccessDeniedException {
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
    private static String erlangJpsSdkToErlExePath(@NotNull JpsSdk<?> erlangSdk)
            throws FileNotFoundException, AccessDeniedException {
        String erlangHomePath = erlangSdk.getHomePath();
        return erlangHomePathToErlExePath(erlangHomePath);
    }

    private static void prependCodePaths(@NotNull GeneralCommandLine commandLine, @NotNull JpsSdk<?> sdk) {
        JpsLibrary jpsLibrary = sdk.getParent();
        List<JpsLibraryRoot> compiledRoots = jpsLibrary.getRoots(JpsOrderRootType.COMPILED);

        for (JpsLibraryRoot compiledRoot : compiledRoots) {
            String url = compiledRoot.getUrl();

            assert url.startsWith(URL_PREFIX);

            String path = extractPath(url);

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
                               @NotNull JpsModule module,
                               boolean prependCodePaths) throws
            FileNotFoundException, AccessDeniedException, LibraryNotFound, ErlangSdkNameMissing {
        String erlExePath = sdkPropertiesToErlExePath(sdkProperties, module);
        setErl(commandLine, erlExePath, sdk, prependCodePaths);
    }

    private static void setErl(@NotNull GeneralCommandLine generalCommandLine,
                               @NotNull String exePath,
                               @NotNull JpsSdk<?> sdk,
                               boolean prependCodePaths) {
        generalCommandLine.setExePath(exePath);
        if (prependCodePaths) {
            prependCodePaths(generalCommandLine, sdk);
        }
    }

    private static GeneralCommandLine mixCommandLine(@NotNull JpsSdk<SdkProperties> sdk,
                                                     @NotNull JpsModule module,
                                                     @Nullable String workingDirectory,
                                                     @NotNull String task,
                                                     @NotNull CompilerOptions compilerOptions,
                                                     boolean includeCompileOptions) throws
            FileNotFoundException, ErlangSdkNameMissing, LibraryNotFound, AccessDeniedException, MissingHomePath, MissingSdkProperties {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withWorkDirectory(workingDirectory);
        SdkProperties sdkProperties = ensureSdkProperties(sdk);
        Elixir.maybeUpdateMixHome(commandLine.getEnvironment(), sdk.getHomePath());

        List<String> dryRunArguments = ElixirCliBase.dryRunArguments(
                commandLine.getEnvironment(),
                workingDirectory,
                sdk,
                Collections.emptyList()
        );
        if (dryRunArguments != null) {
            setErl(commandLine, sdk, sdkProperties, module, false);
            commandLine.addParameters(dryRunArguments);
        } else {
            setErl(commandLine, sdk, sdkProperties, module, true);
            ElixirCliBase.addFallbackArguments(commandLine, Collections.emptyList(), sdk);
        }
        commandLine.addParameter(task);
        if (includeCompileOptions) {
            addCompileOptions(commandLine, compilerOptions);
        }

        return commandLine;
    }

    private static void runMix(@NotNull JpsSdk<SdkProperties> sdk,
                               @NotNull JpsModule module,
                               @Nullable String contentRootPath,
                               @NotNull String task,
                               @NotNull CompilerOptions compilerOptions,
                               @NotNull CompileContext context) throws ProjectBuildException {
        GeneralCommandLine mixCommandLine;

        try {
            mixCommandLine = mixCommandLine(sdk, module, contentRootPath, task, compilerOptions, true);
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

        if (shouldRunMixToolingBootstrap(task, compilerOptions)) {
            runMixToolingBootstrap(sdk, module, contentRootPath, compilerOptions, context);
        }

        run(mixCommandLine, context, MIX_NAME, compilerOptions);
    }

    private static boolean shouldRunMixToolingBootstrap(@NotNull String task,
                                                        @NotNull CompilerOptions compilerOptions) {
        return compilerOptions.useMixToolingBootstrap && MixToolingBootstrapSpec.shouldBootstrapForTask(task);
    }

    private static void runMixToolingBootstrap(@NotNull JpsSdk<SdkProperties> sdk,
                                     @NotNull JpsModule module,
                                     @Nullable String contentRootPath,
                                     @NotNull CompilerOptions compilerOptions,
                                     @NotNull CompileContext context) throws ProjectBuildException {
        for (String task : MixToolingBootstrapSpec.TASKS) {
            runMixToolingBootstrapTask(sdk, module, contentRootPath, task, compilerOptions, context);
        }
    }

    private static void runMixToolingBootstrapTask(@NotNull JpsSdk<SdkProperties> sdk,
                                         @NotNull JpsModule module,
                                         @Nullable String contentRootPath,
                                         @NotNull String task,
                                         @NotNull CompilerOptions compilerOptions,
                                         @NotNull CompileContext context) throws ProjectBuildException {
        GeneralCommandLine mixCommandLine;
        try {
            mixCommandLine = mixCommandLine(sdk, module, contentRootPath, task, compilerOptions, false);
        } catch (AccessDeniedException |
                 ErlangSdkNameMissing |
                 FileNotFoundException |
                 LibraryNotFound |
                 MissingHomePath |
                 MissingSdkProperties exception) {
            throw new ProjectBuildException(
                    "Couldn't construct command line for mix tooling bootstrap: " + exception.getMessage(),
                    exception
            );
        }

        String builderName = BUILDER_NAME + " (" + task + ")";
        mixCommandLine.addParameters(MixToolingBootstrapSpec.ARGS);
        runMixToolingBootstrapCommand(mixCommandLine, context, builderName, compilerOptions);
    }

    private static void runMixToolingBootstrapCommand(@NotNull GeneralCommandLine commandLine,
                                            @NotNull CompileContext context,
                                            @NotNull String builderName,
                                            @NotNull CompilerOptions compilerOptions) throws ProjectBuildException {
        Process process;

        try {
            process = commandLine.createProcess();
        } catch (ExecutionException executionException) {
            String details = executionException.getMessage();
            String errorMessage = details == null || details.isBlank()
                    ? "Failed to run " + builderName
                    : "Failed to run " + builderName + ": " + details;
            context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage, executionException);
        }

        BaseOSProcessHandler handler = new BaseOSProcessHandler(
                process,
                commandLine.getCommandLineString(),
                Charset.defaultCharset()
        );
        ProcessAdapter adapter = new ProcessAdapter(
                context,
                builderName,
                commandLine.getWorkDirectory().getPath(),
                compilerOptions
        );
        handler.addProcessListener(adapter);
        handler.startNotify();
        int exitCode = waitForProcess(context, handler, process, builderName);
        if (exitCode != 0) {
            String errorMessage = builderName + " failed (exit code " + exitCode + ")";
            context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }
    }

    private static void run(@NotNull GeneralCommandLine commandLine,
                            @NotNull CompileContext context,
                            @NotNull String builderName,
                            @NotNull CompilerOptions compilerOptions) throws ProjectBuildException {
        Process process;

        try {
            process = commandLine.createProcess();
        } catch (ExecutionException executionException) {
            String details = executionException.getMessage();
            String errorMessage = details == null || details.isBlank()
                    ? "Failed to run " + builderName
                    : "Failed to run " + builderName + ": " + details;
            context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage, executionException);
        }

        BaseOSProcessHandler handler = new BaseOSProcessHandler(
                process,
                commandLine.getCommandLineString(),
                Charset.defaultCharset()
        );
        ProcessAdapter adapter = new ProcessAdapter(
                context,
                builderName,
                commandLine.getWorkDirectory().getPath(),
                compilerOptions
        );
        handler.addProcessListener(adapter);
        handler.startNotify();
        int exitCode = waitForProcess(context, handler, process, builderName);
        if (exitCode != 0) {
            String errorMessage = builderName + " failed (exit code " + exitCode + ")";
            context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }
    }

    private static int waitForProcess(@NotNull CompileContext context,
                                      @NotNull BaseOSProcessHandler handler,
                                      @NotNull Process process,
                                      @NotNull String builderName) throws ProjectBuildException {
        boolean finished = false;
        try {
            while (true) {
                if (context.isCanceled()) {
                    handler.destroyProcess();
                    throw new ProjectBuildException("Canceled " + builderName);
                }
                if (process.waitFor(200, TimeUnit.MILLISECONDS)) {
                    finished = true;
                    break;
                }
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            handler.destroyProcess();
            throw new ProjectBuildException("Interrupted while running " + builderName, interruptedException);
        } finally {
            if (finished) {
                handler.waitFor();
            }
        }
        return process.exitValue();
    }

    @Override
    public void build(@NotNull Target target,
                      @NotNull DirtyFilesHolder<SourceRootDescriptor, Target> holder,
                      @NotNull BuildOutputConsumer outputConsumer,
                      @NotNull CompileContext context) throws ProjectBuildException, IOException {
        logger.info(target.getPresentableName());

        final Set<String> absolutePathsToCompile = com.intellij.util.containers.CollectionFactory.createFilePathSet();

        holder.processDirtyFiles((target1, file, root) -> {
            boolean isAcceptFile = target1.isTests() ? ELIXIR_TEST_SOURCE_FILTER.accept(file) : ELIXIR_SOURCE_FILTER.accept(file);
            if (isAcceptFile && ourCompilableModuleTypes.contains(target1.getModule().getModuleType())) {
                absolutePathsToCompile.add(file.getAbsolutePath());
            }
            return true;
        });

        if (absolutePathsToCompile.isEmpty() && !holder.hasRemovedFiles()) {
            return;
        }

        logBuildInputs(target, holder, context, absolutePathsToCompile);

        JpsModule module = target.getModule();
        JpsProject project = module.getProject();
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(project).getOptions();

        if (compilerOptions.useMixCompiler) {
            doBuildWithMix(target, context, module, compilerOptions);
        } else {
            // elixirc can not compile tests now.
            if (!target.isTests()) {
                doBuildWithElixirc(target, context, module, compilerOptions, absolutePathsToCompile);
            }
        }
    }

    private static void logBuildInputs(@NotNull Target target,
                                       @NotNull DirtyFilesHolder<SourceRootDescriptor, Target> holder,
                                       @NotNull CompileContext context,
                                       @NotNull Set<String> absolutePathsToCompile) throws IOException {
        ProjectBuilderLogger builderLogger = context.getLoggingManager().getProjectBuilderLogger();
        if (!absolutePathsToCompile.isEmpty()) {
            builderLogger.logCompiledPaths(absolutePathsToCompile, BUILDER_NAME, "Compiling files:");
        }
        if (!holder.hasRemovedFiles()) {
            return;
        }

        Collection<Path> removedPaths = holder.getRemoved(target);
        if (removedPaths.isEmpty()) {
            return;
        }

        List<String> removedFilePaths = new ArrayList<>(removedPaths.size());
        boolean isTestTarget = target.isTests();
        for (Path removedPath : removedPaths) {
            File removedFile = removedPath.toFile();
            boolean isAcceptFile = isTestTarget
                    ? ELIXIR_TEST_SOURCE_FILTER.accept(removedFile)
                    : ELIXIR_SOURCE_FILTER.accept(removedFile);
            if (isAcceptFile) {
                removedFilePaths.add(removedFile.getAbsolutePath());
            }
        }

        if (!removedFilePaths.isEmpty()) {
            builderLogger.logDeletedFiles(removedFilePaths);
        }
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return BUILDER_NAME;
    }

}
