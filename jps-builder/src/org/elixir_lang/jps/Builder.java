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
import org.elixir_lang.jps.model.ModuleType;
import org.elixir_lang.jps.model.SdkProperties;
import org.elixir_lang.jps.sdk_type.Elixir;
import org.elixir_lang.jps.sdk_type.Erlang;
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
import org.jetbrains.jps.model.JpsElement;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/jps-plugin/src/org/intellij/erlang/jps/builder/ErlangBuilder.java
 * https://github.com/ignatov/intellij-erlang/tree/master/jps-plugin/src/org/intellij/erlang/jps/rebar
 */
public class Builder extends TargetBuilder<SourceRootDescriptor, Target> {
    private static final String URL_PREFIX = "file://";
    public static final String BUILDER_NAME = "Elixir Builder";

    public static final String ELIXIR_SOURCE_EXTENSION = "ex";
    public static final String ELIXIR_SCRIPT_EXTENSION = "exs";
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
        String mixPath = Elixir.getMixScript(sdk);

        for (String contentRootUrl : module.getContentRootsList().getUrls()) {
            String contentRootPath = new URL(contentRootUrl).getPath();
            File contentRootDir = new File(contentRootPath);
            File mixConfigFile = new File(contentRootDir, MIX_CONFIG_FILE_NAME);
            if (!mixConfigFile.exists()) continue;

            runMix(target, sdk, mixPath, contentRootPath, compilerOptions, context, module);
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

        Process process;
        try {
            process = commandLine.createProcess();
        } catch (ExecutionException e) {
            throw new ProjectBuildException("Failed to launch elixir compiler", e);
        }

        BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
        com.intellij.execution.process.ProcessAdapter adapter = new ProcessAdapter(context, ElIXIRC_NAME, "", compilerOptions);
        handler.addProcessListener(adapter);
        handler.startNotify();
        handler.waitFor();
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

    @Nullable
    private static String erlangSdkLibraryToErlExePath(@NotNull JpsLibrary erlangSdkLibrary) {
        String erlExePath = null;

        JpsElement erlangSdkLibraryProperties = erlangSdkLibrary.getProperties();

        if (erlangSdkLibraryProperties instanceof JpsSdk) {
            erlExePath = erlangSdkToErlExePath((JpsSdk) erlangSdkLibraryProperties);
        }

        return erlExePath;
    }

    @Nullable
    private static String erlangSdkNameToErlExePath(@NotNull String erlangSdkName, @NotNull JpsModule module) {
        JpsLibraryCollection libraryCollection = module.getProject().getModel().getGlobal().getLibraryCollection();
        JpsLibrary erlangSdkLibrary = libraryCollection.findLibrary(erlangSdkName);
        String erlExePath = null;

        if (erlangSdkLibrary != null) {
            erlExePath = erlangSdkLibraryToErlExePath(erlangSdkLibrary);
        }

        return erlExePath;
    }

    @Nullable
    private static String erlangSdkToErlExePath(@NotNull JpsSdk erlangSdk) {
        String erlangHomePath = erlangSdk.getHomePath();
        String erlExePath = null;

        if (erlangHomePath != null) {
            erlExePath = Erlang.homePathToErlExePath(erlangHomePath);
        }

        return erlExePath;
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

    @Nullable
    private static String sdkPropertiesToErlExePath(@NotNull SdkProperties sdkProperties, @NotNull JpsModule module) {
        String erlangSdkName = sdkProperties.erlangSdkName;
        String erlExePath = null;

        if (erlangSdkName != null) {
            erlExePath = erlangSdkNameToErlExePath(erlangSdkName, module);
        }

        return erlExePath;
    }

    private static void setElixir(@NotNull GeneralCommandLine commandLine) {
        String exePath = Elixir.getExecutableFileName(Elixir.SCRIPT_INTERPRETER);
        commandLine.setExePath(exePath);
    }

    private static void setElixir(@NotNull GeneralCommandLine commandLine,
                                  @Nullable JpsSdk<SdkProperties> sdk,
                                  @NotNull JpsModule module) {
        if (sdk != null) {
            SdkProperties sdkProperties = sdk.getSdkProperties();

            if (sdkProperties != null) {
                setElixir(commandLine, sdk, sdkProperties, module);
            } else {
                setElixir(commandLine);
            }
        } else {
            setElixir(commandLine);
        }
    }

    private static void setElixir(@NotNull GeneralCommandLine commandLine,
                                  @NotNull JpsSdk<SdkProperties> sdk,
                                  @NotNull SdkProperties sdkProperties,
                                  @NotNull JpsModule module) {
        String erlExePath = sdkPropertiesToErlExePath(sdkProperties, module);

        if (erlExePath != null) {
            setExePath(commandLine, erlExePath, sdk);
            commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
            commandLine.addParameter("-extra");
        } else {
            String elixirExePath = elixirSdkToElixirExePath(sdk);

            if (elixirExePath == null) {
                elixirExePath = Elixir.getExecutableFileName(Elixir.SCRIPT_INTERPRETER);
            }

            setExePath(commandLine, elixirExePath, sdk);
        }
    }

    private static void setExePath(@NotNull GeneralCommandLine generalCommandLine,
                                   @NotNull String exePath,
                                   @NotNull JpsSdk sdk) {
        generalCommandLine.setExePath(exePath);
        prependCodePaths(generalCommandLine, sdk);
    }

    private static void runMix(@NotNull Target target,
                               @Nullable JpsSdk<SdkProperties> sdk,
                               @NotNull String mixPath,
                               @Nullable String contentRootPath,
                               @NotNull CompilerOptions compilerOptions,
                               @NotNull CompileContext context,
                               @NotNull JpsModule module) throws ProjectBuildException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withWorkDirectory(contentRootPath);
        setElixir(commandLine, sdk, module);
        commandLine.addParameter(mixPath);
        commandLine.addParameter(target.isTests() ? "test" : "compile");
        addCompileOptions(commandLine, compilerOptions);

        Process process;
        try {
            process = commandLine.createProcess();
        } catch (ExecutionException e) {
            throw new ProjectBuildException("Failed to run mix.", e);
        }

        BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
        com.intellij.execution.process.ProcessAdapter adapter = new ProcessAdapter(context, MIX_NAME, commandLine.getWorkDirectory().getPath(), compilerOptions);
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
