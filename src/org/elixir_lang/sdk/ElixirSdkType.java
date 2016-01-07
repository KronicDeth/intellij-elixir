package org.elixir_lang.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.jps.model.JpsElixirModelSerializerExtension;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 * Created by zyuyou on 2015/5/27.
 *
 */
public class ElixirSdkType extends SdkType {
  private final Map<String, ElixirSdkRelease> mySdkHomeToReleaseCache = ApplicationManager.getApplication().isUnitTestMode() ?
      new HashMap<String, ElixirSdkRelease>() : new WeakHashMap<String, ElixirSdkRelease>();

  private static final Logger LOG = Logger.getInstance(ElixirSdkType.class);

  public ElixirSdkType() {
    super(JpsElixirModelSerializerExtension.ELIXIR_SDK_TYPE_ID);
  }

  @NotNull
  public static ElixirSdkType getInstance(){
    ElixirSdkType instance = SdkType.findInstance(ElixirSdkType.class);
    assert instance != null : "Make sure ElixirSdkType is registered in plugin.xml";
    return instance;
  }

  @Override
  public Icon getIcon() {
    return ElixirIcons.FILE;
  }

  @Override
  public Icon getIconForAddAction() {
    return getIcon();
  }

  @Nullable
  @Override
  public String suggestHomePath() {
    Iterator<String> iterator = suggestHomePaths().iterator();
    String suggestedHomePath = null;

    if (iterator.hasNext()) {
      suggestedHomePath = iterator.next();
    }

    return suggestedHomePath;
  }

  @Override
  public Collection<String> suggestHomePaths() {
    return homePathByVersion().values();
  }

  @Override
  public boolean isValidSdkHome(@NotNull String path) {
    File elixir = JpsElixirSdkType.getScriptInterpreterExecutable(path);
    File elixirc = JpsElixirSdkType.getByteCodeCompilerExecutable(path);
    File iex = JpsElixirSdkType.getIExExecutable(path);
    File mix = JpsElixirSdkType.getMixExecutable(path);

    // Determine whether everything is executable
    return elixir.canExecute() && elixirc.canExecute() && iex.canExecute() && mix.canExecute();
  }

  @Override
  public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
    return getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome));
  }

  @Nullable
  @Override
  public String getVersionString(@NotNull String sdkHome) {
    return getVersionString(detectSdkVersion(sdkHome));
  }

  @Nullable
  @Override
  public String getDefaultDocumentationUrl(@NotNull Sdk sdk) {
    return getDefaultDocumentationUrl(getRelease(sdk));
  }

  @Nullable
  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
    return null;
  }

  @Override
  public String getPresentableName() {
    return "Elixir SDK";
  }

  @Override
  public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
  }

  @Override
  public void setupSdkPaths(@NotNull Sdk sdk) {
    configureSdkPaths(sdk);
  }

  @Nullable
  public static String getSdkPath(@NotNull final Project project){
    // todo small ide
    if(ElixirSystemUtil.isSmallIde()){
      return ElixirSdkForSmallIdes.getSdkHome(project);
    }

    Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
    return sdk != null && sdk.getSdkType() == getInstance() ? sdk.getHomePath() : null;
  }

  @Nullable
  public static ElixirSdkRelease getRelease(@NotNull PsiElement element){
    if(ElixirSystemUtil.isSmallIde()){
      return getReleaseForSmallIde(element.getProject());
    }

    Module module = ModuleUtilCore.findModuleForPsiElement(element);
    ElixirSdkRelease byModuleSdk = getRelease(module != null ? ModuleRootManager.getInstance(module).getSdk() : null);

    return byModuleSdk != null ? byModuleSdk : getRelease(element.getProject());
  }

  @Nullable
  public static ElixirSdkRelease getRelease(@NotNull Project project){
    if(ElixirSystemUtil.isSmallIde()){
      return getReleaseForSmallIde(project);
    }
    return getRelease(ProjectRootManager.getInstance(project).getProjectSdk());
  }


  @NotNull
  private static String getDefaultSdkName(@NotNull String sdkHome, @Nullable ElixirSdkRelease version){
    return version != null ? "Elixir " + version.getRelease() : "Unknown Elixir version at " + sdkHome ;
  }

  @Nullable
  private ElixirSdkRelease detectSdkVersion(@NotNull String sdkHome){
    ElixirSdkRelease cachedRelease = mySdkHomeToReleaseCache.get(getVersionCacheKey(sdkHome));
    if(cachedRelease != null){
      return cachedRelease;
    }

    assert !ApplicationManager.getApplication().isUnitTestMode() : "Unit tests should have their SDK versions pre-cached!";

    File elixir = JpsElixirSdkType.getScriptInterpreterExecutable(sdkHome);
    if(!elixir.canExecute()){
      String reason = elixir.getPath() + (elixir.exists() ? " is not executable." : " is missing.");
      LOG.warn("Can't detect Elixir version: " + reason);
      return null;
    }

    try{
      ProcessOutput output = ElixirSystemUtil.getProcessOutput(sdkHome, elixir.getAbsolutePath(), "-v");
      List<String> lines = output.getExitCode() != 0 || output.isTimeout() || output.isCancelled() ?
          ContainerUtil.<String>emptyList() : output.getStdoutLines();

      for (String line : lines) {
        if (line.startsWith("Elixir")) {
          ElixirSdkRelease release = ElixirSdkRelease.fromString(line);
          mySdkHomeToReleaseCache.put(getVersionCacheKey(sdkHome), release);
          return release;
        }
      }
    }catch (ExecutionException ignore){
      LOG.warn(ignore);
    }
    return null;
  }

  @Nullable
  private static String getVersionCacheKey(@Nullable String sdkHome){
    return sdkHome != null ? new File(sdkHome).getAbsolutePath() : null;
  }

  @Nullable
  private static String getVersionString(@Nullable ElixirSdkRelease version) {
    return version != null ? version.toString() : null;
  }

  @Nullable
  private static String getDefaultDocumentationUrl(@Nullable ElixirSdkRelease version) {
    return version == null ? null : "http://elixir-lang.org/docs/stable/elixir/";
  }

  @Nullable
  private static ElixirSdkRelease getRelease(@Nullable Sdk sdk) {
    if (sdk != null && sdk.getSdkType() == getInstance()) {
      ElixirSdkRelease fromVersionString = ElixirSdkRelease.fromString(sdk.getVersionString());
      return fromVersionString != null ? fromVersionString : getInstance().detectSdkVersion(StringUtil.notNullize(sdk.getHomePath()));
    }
    return null;
  }

  private static void configureSdkPaths(@NotNull Sdk sdk) {
    SdkModificator sdkModificator = sdk.getSdkModificator();
    setupLocalSdkPaths(sdkModificator);
    String externalDocUrl = getDefaultDocumentationUrl(getRelease(sdk));
    if (externalDocUrl != null) {
      VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(externalDocUrl);
      sdkModificator.addRoot(fileByUrl, JavadocOrderRootType.getInstance());
    }
    sdkModificator.commitChanges();
  }

  private static void setupLocalSdkPaths(@NotNull SdkModificator sdkModificator){
    String sdkHome = sdkModificator.getHomePath();

    {
      File stdLibDir = new File(new File(sdkHome), "lib");
      if(tryToProcessAsStandardLibraryDir(sdkModificator, stdLibDir)) return;
    }

    assert !ApplicationManager.getApplication().isUnitTestMode() : "Failed to setup a mock SDK";

    File stdLibDir = new File("/usr/lib/erlang");
    tryToProcessAsStandardLibraryDir(sdkModificator, stdLibDir);
  }

  /**
   * set the sdk libs
   * todo: differentiating `Elixir.*.beam` files and `*.ex` files.
   * */
  private static boolean tryToProcessAsStandardLibraryDir(@NotNull SdkModificator sdkModificator, @NotNull File stdLibDir) {
    if (!isStandardLibraryDir(stdLibDir)) return false;
    VirtualFile dir = LocalFileSystem.getInstance().findFileByIoFile(stdLibDir);
    if (dir != null) {
      sdkModificator.addRoot(dir, OrderRootType.SOURCES);
      sdkModificator.addRoot(dir, OrderRootType.CLASSES);
    }
    return true;
  }

  private static boolean isStandardLibraryDir(@NotNull File dir) {
    return dir.isDirectory();
  }

  /**
   * Map of home paths to versions in descending version order so that newer versions are favored.
   *
   * @return Map
   */
  private Map<Version, String> homePathByVersion() {
    Map<Version, String> homePathByVersion = new TreeMap<Version, String>(
            new Comparator<Version>() {
              @Override
              public int compare(Version version1, Version version2) {
                // compare version2 to version1 to produce descending instead of ascending order.
                return version2.compareTo(version1);
              }
            }
    );

    if (SystemInfo.isMac) {
      File homebrewRoot = new File("/usr/local/Cellar/elixir");

      if (homebrewRoot.isDirectory()) {
        File[] files = homebrewRoot.listFiles();
        if(files != null){
          for (File child : files) {
            if (child.isDirectory()) {
              String versionString = child.getName();
              String[] versionParts = versionString.split("\\.", 3);
              int major = Integer.parseInt(versionParts[0]);
              int minor = Integer.parseInt(versionParts[1]);
              int bugfix = Integer.parseInt(versionParts[2]);
              Version version = new Version(major, minor, bugfix);

              homePathByVersion.put(version, child.getAbsolutePath());
            }
          }
        }
      }
    } else {
      Version version = new Version(0,0,0);
      String sdkPath = "";

      if (SystemInfo.isWindows){
        if (SystemInfo.is32Bit){
          sdkPath = "C:\\Program Files\\Elixir";
        } else {
          sdkPath = "C:\\Program Files (x86)\\Elixir";
        }
      } else if (SystemInfo.isLinux){
        sdkPath = "/usr/local/lib/elixir";
      }

      homePathByVersion.put(version, sdkPath);
    }

    return homePathByVersion;
  }

  @Nullable
  private static ElixirSdkRelease getReleaseForSmallIde(@NotNull Project project){
    String sdkPath = getSdkPath(project);
    return StringUtil.isEmpty(sdkPath) ? null : getInstance().detectSdkVersion(sdkPath);
  }

  @TestOnly
  @NotNull
  public static Sdk createMockSdk(@NotNull String sdkHome, @NotNull ElixirSdkRelease version){
    getInstance().mySdkHomeToReleaseCache.put(getVersionCacheKey(sdkHome), version);  // we'll not try to detect sdk version in tests environment
    Sdk sdk = new ProjectJdkImpl(getDefaultSdkName(sdkHome, version), getInstance());
    SdkModificator sdkModificator = sdk.getSdkModificator();
    sdkModificator.setHomePath(sdkHome);
    sdkModificator.setVersionString(getVersionString(version));// must be set after home path, otherwise setting home path clears the version string
    sdkModificator.commitChanges();
    configureSdkPaths(sdk);
    return sdk;
  }




}
