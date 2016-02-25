package org.elixir_lang.jps.builder;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.elixir_lang.jps.mix.JpsMixConfigurationExtension;
import org.elixir_lang.jps.model.ElixirCompilerOptions;
import org.elixir_lang.jps.model.JpsElixirCompilerOptionsExtension;
import org.elixir_lang.jps.model.JpsElixirModuleType;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.resources.ResourcesBuilder;
import org.jetbrains.jps.incremental.resources.StandardResourceBuilderEnabler;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
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
 * Created by zyuyou on 15/7/10.
 *
 * https://github.com/ignatov/intellij-erlang/blob/master/jps-plugin/src/org/intellij/erlang/jps/builder/ErlangBuilder.java
 * https://github.com/ignatov/intellij-erlang/tree/master/jps-plugin/src/org/intellij/erlang/jps/rebar
 */
public class ElixirBuilder extends TargetBuilder<ElixirSourceRootDescriptor, ElixirTarget> {
  public static final String BUILDER_NAME = "Elixir Builder";

  public static final String ELIXIR_SOURCE_EXTENSION = "ex";
  public static final String ELIXIR_SCRIPT_EXTENSION = "exs";
  public static final String ELIXIR_TEST_SOURCE_EXTENSION = ELIXIR_SCRIPT_EXTENSION;

  public static final String ElIXIRC_NAME = "elixirc";
  public static final String MIX_NAME = "mix";

  private static final String MIX_CONFIG_FILE_NAME = "mix." + ELIXIR_SCRIPT_EXTENSION;

  private final static Logger LOG = Logger.getInstance(ElixirBuilder.class);

  public static final String ADD_PATH_TO_FRONT_OF_CODE_PATH = "-pa";

  public static final FileFilter ELIXIR_SOURCE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File file) {
      return FileUtilRt.extensionEquals(file.getName(), ELIXIR_SOURCE_EXTENSION);
    }
  };

  public static final FileFilter ELIXIR_TEST_SOURCE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File file) {
      return FileUtilRt.extensionEquals(file.getName(), ELIXIR_TEST_SOURCE_EXTENSION);
    }
  };

  // use JavaBuilderExtension?
  public static final Set<? extends JpsModuleType<?>> ourCompilableModuleTypes = Collections.singleton(JpsElixirModuleType.INSTANCE);

  public ElixirBuilder(){
    super(Arrays.asList(ElixirTargetType.PRODUCTION, ElixirTargetType.TEST));

    // disables java resource builder for elixir modules
    ResourcesBuilder.registerEnabler(new StandardResourceBuilderEnabler() {
      @Override
      public boolean isResourceProcessingEnabled(JpsModule module) {
        return !(module.getModuleType() instanceof JpsElixirModuleType);
      }
    });
  }

  @Override
  public void build(@NotNull ElixirTarget target,
                    @NotNull DirtyFilesHolder<ElixirSourceRootDescriptor, ElixirTarget> holder,
                    @NotNull BuildOutputConsumer outputConsumer,
                    @NotNull CompileContext context) throws ProjectBuildException, IOException {

    LOG.info(target.getPresentableName());
    final Set<File> filesToCompile = new THashSet<File>(FileUtil.FILE_HASHING_STRATEGY);

    holder.processDirtyFiles(new FileProcessor<ElixirSourceRootDescriptor, ElixirTarget>() {
      @Override
      public boolean apply(ElixirTarget target, File file, ElixirSourceRootDescriptor root) throws IOException {
        boolean isAcceptFile = target.isTests() ? ELIXIR_TEST_SOURCE_FILTER.accept(file) : ELIXIR_SOURCE_FILTER.accept(file);
        if(isAcceptFile && ourCompilableModuleTypes.contains(target.getModule().getModuleType())){
          filesToCompile.add(file);
        }
        return true;
      }
    });

    if(filesToCompile.isEmpty() && !holder.hasRemovedFiles()) return;

    JpsModule module = target.getModule();
    JpsProject project = module.getProject();
    ElixirCompilerOptions compilerOptions = JpsElixirCompilerOptionsExtension.getOrCreateExtension(project).getOptions();

    if(compilerOptions.myUseMixCompiler){
      doBuildWithMix(target, context, module, compilerOptions);
    } else {
      // elixirc can not compile tests now.
      if(!target.isTests()){
        doBuildWithElixirc(target, context, module, compilerOptions, filesToCompile);
      }
    }
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return BUILDER_NAME;
  }

  /**
   * Build With elixirc.
   * if "isMake": compile all files of the module and dependent module.
   * else: just for compile the target affected file.
   * */
  private static void doBuildWithElixirc(ElixirTarget target,
                                         CompileContext context,
                                         JpsModule module,
                                         ElixirCompilerOptions compilerOptions,
                                         Collection<File> filesToCompile) throws ProjectBuildException{

    // ensure compile output directory
    File outputDirectory = getBuildOutputDirectory(module, target.isTests(), context);

    runElixirc(target, context, compilerOptions, filesToCompile, outputDirectory);
  }

  private static void doBuildWithMix(ElixirTarget target,
                                     CompileContext context,
                                     JpsModule module,
                                     ElixirCompilerOptions compilerOptions) throws ProjectBuildException, IOException {

    String mixPath = getMixExecutablePath(module.getProject());
    if(mixPath == null){
      String errorMessage = "Mix path is not set.";
      context.processMessage(new CompilerMessage(MIX_NAME, BuildMessage.Kind.ERROR, errorMessage));
      throw new ProjectBuildException(errorMessage);
    }

    JpsSdk<JpsDummyElement> sdk = ElixirTargetBuilderUtil.getSdk(context, module);
    String elixirPath = JpsElixirSdkType.getScriptInterpreterExecutable(sdk.getHomePath()).getAbsolutePath();

    for(String contentRootUrl: module.getContentRootsList().getUrls()){
      String contentRootPath = new URL(contentRootUrl).getPath();
      File contentRootDir = new File(contentRootPath);
      File mixConfigFile = new File(contentRootDir, MIX_CONFIG_FILE_NAME);
      if(!mixConfigFile.exists()) continue;

      runMix(target, elixirPath, mixPath, contentRootPath, compilerOptions, context);
    }
  }

  /*** doBuildWithElixirc releated private methods */

  @NotNull
  private static File getBuildOutputDirectory(@NotNull JpsModule module,
                                              boolean forTests,
                                              @NotNull CompileContext context) throws ProjectBuildException{

    JpsJavaExtensionService instance = JpsJavaExtensionService.getInstance();
    File outputDirectory = instance.getOutputDirectory(module, forTests);
    if(outputDirectory == null){
      String errorMessage = "No output directory for module " + module.getName();
      context.processMessage(new CompilerMessage(ElIXIRC_NAME, BuildMessage.Kind.ERROR, errorMessage));
      throw new ProjectBuildException(errorMessage);
    }

    if(!outputDirectory.exists()){
      FileUtil.createDirectory(outputDirectory);
    }
    return outputDirectory;
  }

  private static void runElixirc(ElixirTarget target,
                                 CompileContext context,
                                 ElixirCompilerOptions compilerOptions,
                                 Collection<File> files,
                                 File outputDirectory) throws ProjectBuildException {

    GeneralCommandLine commandLine = getElixircCommandLine(target, context, compilerOptions, files, outputDirectory);

    Process process;
    try{
      process = commandLine.createProcess();
    }catch (ExecutionException e){
      throw new ProjectBuildException("Failed to launch elixir compiler", e);
    }

    BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
    ProcessAdapter adapter = new ElixirCompilerProcessAdapter(context, ElIXIRC_NAME, "");
    handler.addProcessListener(adapter);
    handler.startNotify();
    handler.waitFor();
  }

  private static GeneralCommandLine getElixircCommandLine(ElixirTarget target,
                                                          CompileContext context,
                                                          ElixirCompilerOptions compilerOptions,
                                                          Collection<File> files,
                                                          File outputDirectory) throws ProjectBuildException{

    GeneralCommandLine commandLine = new GeneralCommandLine();

    // get executable
    JpsModule module = target.getModule();
    JpsSdk<JpsDummyElement> sdk = ElixirTargetBuilderUtil.getSdk(context, module);
    File executable = JpsElixirSdkType.getByteCodeCompilerExecutable(sdk.getHomePath());

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
                                                  @NotNull ElixirTarget target,
                                                  @NotNull CompileContext context,
                                                  Collection<File> files){
    // make
    if(context.getScope().isBuildIncrementally(target.getTargetType())){
      return getCompileFilePathsDefault(module, target);
    }

    // force build files
    return ContainerUtil.map(files, new Function<File, String>() {
      @Override
      public String fun(File file) {
        return file.getAbsolutePath();
      }
    });
  }

  @NotNull
  private static List<String> getCompileFilePathsDefault(@NotNull JpsModule module, @NotNull ElixirTarget target){
    CommonProcessors.CollectProcessor<File> exFilesCollector = new CommonProcessors.CollectProcessor<File>(){
      @Override
      protected boolean accept(File file) {
        return !file.isDirectory() && FileUtilRt.extensionEquals(file.getName(), ELIXIR_SOURCE_EXTENSION);
      }
    };

    List<JpsModuleSourceRoot> sourceRoots = new ArrayList<JpsModuleSourceRoot>();
    ContainerUtil.addAll(sourceRoots, module.getSourceRoots(JavaSourceRootType.SOURCE));
    if(target.isTests()){
      ContainerUtil.addAll(sourceRoots, module.getSourceRoots(JavaSourceRootType.TEST_SOURCE));
    }

    for (JpsModuleSourceRoot root : sourceRoots){
      FileUtil.processFilesRecursively(root.getFile(), exFilesCollector);
    }

    return ContainerUtil.map(exFilesCollector.getResults(), new Function<File, String>() {
      @NotNull
      @Override
      public String fun(@NotNull File file) {
        return file.getAbsolutePath();
      }
    });
  }

  private static void addDependentModuleCodePath(@NotNull GeneralCommandLine commandLine,
                                                 @NotNull JpsModule module,
                                                 @NotNull ElixirTarget target,
                                                 @NotNull CompileContext context) throws ProjectBuildException{

    ArrayList<JpsModule> codePathModules = new ArrayList<JpsModule>();
    collectDependentModules(module, codePathModules, new HashSet<String>());

    addModuleToCodePath(commandLine, module, target.isTests(), context);
    for(JpsModule codePathModule : codePathModules){
      if(codePathModule != module){
        addModuleToCodePath(commandLine, codePathModule, false, context);
      }
    }
  }

  private static void collectDependentModules(@NotNull JpsModule module,
                                              @NotNull Collection<JpsModule> addedModules,
                                              @NotNull Set<String> addedModuleNames){

    String moduleName = module.getName();
    if(addedModuleNames.contains(moduleName)) return;
    addedModuleNames.add(moduleName);
    addedModules.add(module);

    for (JpsDependencyElement dependency : module.getDependenciesList().getDependencies()){
      if(!(dependency instanceof JpsModuleDependency)) continue;
      JpsModuleDependency moduleDependency = (JpsModuleDependency) dependency;
      JpsModule depModule = moduleDependency.getModule();
      if(depModule != null){
        collectDependentModules(depModule, addedModules, addedModuleNames);
      }
    }
  }

  private static void addModuleToCodePath(@NotNull GeneralCommandLine commandLine,
                                          @NotNull JpsModule module,
                                          boolean forTests,
                                          @NotNull CompileContext context) throws ProjectBuildException{

    File outputDirectory = getBuildOutputDirectory(module, forTests, context);
    commandLine.addParameters(ADD_PATH_TO_FRONT_OF_CODE_PATH, outputDirectory.getPath());
    for(String rootUrl : module.getContentRootsList().getUrls()){
      try{
        String path = new URL(rootUrl).getPath();
        commandLine.addParameters(ADD_PATH_TO_FRONT_OF_CODE_PATH, path);
      }catch (MalformedURLException e){
        context.processMessage(new CompilerMessage(ElIXIRC_NAME, BuildMessage.Kind.ERROR, "Failed to find content root for module: " + module.getName()));
      }
    }
  }

  private static void addCompileOptions(@NotNull GeneralCommandLine commandLine, ElixirCompilerOptions compilerOptions){
    if(!compilerOptions.myAttachDocsEnabled){
      commandLine.addParameter("--no-docs");
    }

    if(!compilerOptions.myAttachDebugInfoEnabled){
      commandLine.addParameter("--no-debug-info");
    }

    if(compilerOptions.myWarningsAsErrorsEnabled){
      commandLine.addParameter("--warnings-as-errors");
    }

    if(compilerOptions.myIgnoreModuleConflictEnabled){
      commandLine.addParameter("--ignore-module-conflict");
    }
  }

  /*** doBuildWithMix releated private methods */

  @Nullable
  private static String getMixExecutablePath(@Nullable JpsProject project){
    JpsMixConfigurationExtension mixConfigurationExtension = JpsMixConfigurationExtension.getExtension(project);
    String mixPath = mixConfigurationExtension != null ? mixConfigurationExtension.getMixPath() : null;
    return StringUtil.isEmptyOrSpaces(mixPath) ? null : mixPath;
  }

  private static void runMix(@NotNull ElixirTarget target,
                             @NotNull String elixirPath,
                             @NotNull String mixPath,
                             @Nullable String contentRootPath,
                             @NotNull ElixirCompilerOptions compilerOptions,
                             @NotNull CompileContext context) throws ProjectBuildException{

    GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.withWorkDirectory(contentRootPath);
    commandLine.setExePath(elixirPath);
    commandLine.addParameter(mixPath);
    commandLine.addParameter(target.isTests()? "test":"compile");
    addCompileOptions(commandLine, compilerOptions);

    Process process;
    try{
      process = commandLine.createProcess();
    } catch (ExecutionException e) {
      throw new ProjectBuildException("Failed to run mix.", e);
    }

    BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
    ProcessAdapter adapter = new ElixirCompilerProcessAdapter(context, MIX_NAME, commandLine.getWorkDirectory().getPath());
    handler.addProcessListener(adapter);
    handler.startNotify();
    handler.waitFor();
  }
}
