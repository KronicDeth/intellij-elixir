package org.elixir_lang.jps.builder;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.elixir_lang.jps.mix.JpsMixConfigurationExtension;
import org.elixir_lang.jps.model.ElixirCompilerOptions;
import org.elixir_lang.jps.model.JpsElixirCompilerOptionsExtension;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * Created by zyuyou on 15/7/12.
 */
public class MixBuilder extends TargetBuilder<ElixirSourceRootDescriptor, ElixirTarget> {
  private static final String NAME = "mix";
  private static final String MIX_CONFIG_FILE_NAME = "mix.config";

  private final static Logger LOG = Logger.getInstance(MixBuilder.class);

  protected MixBuilder() {
    super(Collections.singleton(ElixirTargetType.PRODUCTION));
  }

  @Override
  public void build(@NotNull ElixirTarget target,
                    @NotNull DirtyFilesHolder<ElixirSourceRootDescriptor, ElixirTarget> holder,
                    @NotNull BuildOutputConsumer outputConsumer,
                    @NotNull CompileContext context) throws ProjectBuildException, IOException {

    if(!holder.hasDirtyFiles() && !holder.hasRemovedFiles()) return;

    JpsModule module = target.getModule();
    JpsProject project = module.getProject();
    ElixirCompilerOptions compilerOptions = JpsElixirCompilerOptionsExtension.getOrCreateExtension(project).getOptions();
    if(!compilerOptions.myUseMixCompiler) return;

    String mixPath = getMixExecutablePath(project);
    if(mixPath == null){
      String errorMessage = "Mix path is not set.";
      context.processMessage(new CompilerMessage(NAME, BuildMessage.Kind.ERROR, errorMessage));
      throw new ProjectBuildException(errorMessage);
    }

    JpsSdk<JpsDummyElement> sdk = ElixirTargetBuilderUtil.getSdk(context, module);
    String elixirPath = JpsElixirSdkType.getScriptInterpreterExecutable(sdk.getHomePath()).getAbsolutePath();

    for(String contentRootUrl: module.getContentRootsList().getUrls()){
      String contentRootPath = new URL(contentRootUrl).getPath();
      File contentRootDir = new File(contentRootPath);
      File mixConfigFile = new File(contentRootDir, MIX_CONFIG_FILE_NAME);
      if(!mixConfigFile.exists()) continue;
      runMix(elixirPath, mixPath, contentRootPath, compilerOptions.myAttachDebugInfoEnabled, context);
    }
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return NAME;
  }

  private static void runMix(@NotNull String elixirPath,
                             @NotNull String mixPath,
                             @Nullable String contentRootPath,
                             boolean addDebugInfo,
                             @NotNull CompileContext context) throws ProjectBuildException{

    GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.withWorkDirectory(contentRootPath);
    commandLine.setExePath(elixirPath);
    commandLine.addParameter(mixPath);
    commandLine.addParameter("compile");

    if(!addDebugInfo){
      commandLine.addParameter("--no-debug-info");
    }

    Process process;
    try{
      process = commandLine.createProcess();
    } catch (ExecutionException e) {
      throw new ProjectBuildException("Failed to run mix.", e);
    }

    BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
    ProcessAdapter adapter = new ElixirCompilerProcessAdapter(context, NAME, commandLine.getWorkDirectory().getPath());
    handler.addProcessListener(adapter);
    handler.startNotify();
    handler.waitFor();
  }

  @Nullable
  private static String getMixExecutablePath(@Nullable JpsProject project){
    JpsMixConfigurationExtension mixConfigurationExtension = JpsMixConfigurationExtension.getExtension(project);
    String mixPath = mixConfigurationExtension != null ? mixConfigurationExtension.getMixPath() : null;
    return StringUtil.isEmptyOrSpaces(mixPath) ? null : mixPath;
  }
}
