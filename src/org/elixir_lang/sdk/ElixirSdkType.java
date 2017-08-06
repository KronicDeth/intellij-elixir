package org.elixir_lang.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import gnu.trove.THashSet;
import org.apache.commons.io.FilenameUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.elixir_lang.sdk.ElixirSystemUtil.STANDARD_TIMEOUT;
import static org.elixir_lang.sdk.ElixirSystemUtil.transformStdoutLine;

public class ElixirSdkType extends SdkType {
    private static final Logger LOG = Logger.getInstance(ElixirSdkType.class);
    private static final Set<String> SDK_HOME_CHILD_BASE_NAME_SET = new THashSet<>(Arrays.asList("bin", "lib", "src"));
    private final Map<String, ElixirSdkRelease> mySdkHomeToReleaseCache =
            ApplicationManager.getApplication().isUnitTestMode() ? new HashMap<>() : new WeakHashMap<>();

    private static final Pattern NIX_PATTERN = Pattern.compile(".+-elixir-(\\d+)\\.(\\d+)\\.(\\d+)");public ElixirSdkType() {
        super(JpsElixirModelSerializerExtension.ELIXIR_SDK_TYPE_ID);
    }

    private static void configureSdkPaths(@NotNull Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();
        setupLocalSdkPaths(sdkModificator);
        String externalDocUrl = getDefaultDocumentationUrl(getRelease(sdk));
        if (externalDocUrl != null) {
            VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(externalDocUrl);

            if (fileByUrl != null) {
                sdkModificator.addRoot(fileByUrl, JavadocOrderRootType.getInstance());
            }
        }

        sdkModificator.commitChanges();
    }

    @TestOnly
    @NotNull
    public static Sdk createMockSdk(@NotNull String sdkHome, @NotNull ElixirSdkRelease version) {
        getInstance().mySdkHomeToReleaseCache.put(getVersionCacheKey(sdkHome), version);  // we'll not try to detect sdk version in tests environment
        Sdk sdk = new ProjectJdkImpl(getDefaultSdkName(sdkHome, version), getInstance());
        SdkModificator sdkModificator = sdk.getSdkModificator();
        sdkModificator.setHomePath(sdkHome);
        sdkModificator.setVersionString(getVersionString(version));// must be set after home path, otherwise setting home path clears the version string
        sdkModificator.commitChanges();
        configureSdkPaths(sdk);
        return sdk;
    }

    @Nullable
    private static String getDefaultDocumentationUrl(@Nullable ElixirSdkRelease version) {
        return version == null ? null : "http://elixir-lang.org/docs/stable/elixir/";
    }

    @NotNull
    private static String getDefaultSdkName(@NotNull String sdkHome, @Nullable ElixirSdkRelease release) {
        return release != null ? release.toString() : "Unknown Elixir version at " + sdkHome;
    }

    @NotNull
    public static ElixirSdkType getInstance() {
        return SdkType.findInstance(ElixirSdkType.class);
    }

    @NotNull
    public static ElixirSdkRelease getNonNullRelease(@NotNull PsiElement element) {
        ElixirSdkRelease nonNullRelease = getRelease(element);

        if (nonNullRelease == null) {
            nonNullRelease = ElixirSdkRelease.LATEST;
        }

        return nonNullRelease;
    }

    @Nullable
    public static ElixirSdkRelease getRelease(@NotNull PsiElement element) {
        ElixirSdkRelease release = null;
        Project project = element.getProject();

        if (ElixirSystemUtil.isSmallIde()) {
            release = getReleaseForSmallIde(project);
        } else {
      /* ModuleUtilCore.findModuleForPsiElement can fail with NullPointerException if the
         ProjectFileIndex.SERVICE.getInstance(Project) returns {@code null}, so check that the ProjectFileIndex is
         available first */
            if (ProjectFileIndex.SERVICE.getInstance(project) != null) {
                Module module = ModuleUtilCore.findModuleForPsiElement(element);

                if (module != null) {
                    @Nullable ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

                    if (moduleRootManager != null) {
                        Sdk sdk = moduleRootManager.getSdk();

                        if (sdk != null) {
                            release = getRelease(sdk);
                        }
                    }
                }
            }

            if (release == null) {
                release = getRelease(project);
            }
        }

        return release;
    }

    @Nullable
    private static ElixirSdkRelease getRelease(@NotNull Project project) {
        ElixirSdkRelease release;

        if (ElixirSystemUtil.isSmallIde()) {
            release = getReleaseForSmallIde(project);
        } else {
            ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);

            if (projectRootManager != null) {
                release = getRelease(projectRootManager.getProjectSdk());
            } else {
                release = null;
            }
        }

        return release;
    }

    @Nullable
    public static ElixirSdkRelease getRelease(@Nullable Sdk sdk) {
        if (sdk != null && sdk.getSdkType() == getInstance()) {
            ElixirSdkRelease fromVersionString = ElixirSdkRelease.fromString(sdk.getVersionString());
            return fromVersionString != null ? fromVersionString : getInstance().detectSdkVersion(StringUtil.notNullize(sdk.getHomePath()));
        }
        return null;
    }

    @Nullable
    private static ElixirSdkRelease getReleaseForSmallIde(@NotNull Project project) {
        String sdkPath = getSdkPath(project);
        return StringUtil.isEmpty(sdkPath) ? null : getInstance().detectSdkVersion(sdkPath);
    }

    @Nullable
    public static String getSdkPath(@NotNull final Project project) {
        // todo small ide
        if (ElixirSystemUtil.isSmallIde()) {
            return ElixirSdkForSmallIdes.getSdkHome(project);
        }

        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        return sdk != null && sdk.getSdkType() == getInstance() ? sdk.getHomePath() : null;
    }

    @Nullable
    private static String getVersionCacheKey(@Nullable String sdkHome) {
        return sdkHome != null ? new File(sdkHome).getAbsolutePath() : null;
    }

    @Nullable
    private static String getVersionString(@Nullable ElixirSdkRelease version) {
        return version != null ? version.toString() : null;
    }

    private static boolean isStandardLibraryDir(@NotNull File dir) {
        return dir.isDirectory();
    }

    private static void setupLocalSdkPaths(@NotNull SdkModificator sdkModificator) {
        String sdkHome = sdkModificator.getHomePath();

        {
            File stdLibDir = new File(new File(sdkHome), "lib");
            if (tryToProcessAsStandardLibraryDir(sdkModificator, stdLibDir)) {
                return;
            }
        }

        assert !ApplicationManager.getApplication().isUnitTestMode() : "Failed to setup a mock SDK";

        File stdLibDir = new File("/usr/lib/erlang");
        tryToProcessAsStandardLibraryDir(sdkModificator, stdLibDir);
    }

    /**
     * set the sdk libs
     * todo: differentiating `Elixir.*.beam` files and `*.ex` files.
     */
    private static boolean tryToProcessAsStandardLibraryDir(@NotNull SdkModificator sdkModificator, @NotNull File stdLibDir) {
        if (!isStandardLibraryDir(stdLibDir)) {
            return false;
        }
        VirtualFile dir = LocalFileSystem.getInstance().findFileByIoFile(stdLibDir);
        if (dir != null) {
            sdkModificator.addRoot(dir, OrderRootType.SOURCES);
            sdkModificator.addRoot(dir, OrderRootType.CLASSES);
        }
        return true;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Nullable
    private ElixirSdkRelease detectSdkVersion(@NotNull String sdkHome) {
        ElixirSdkRelease cachedRelease = mySdkHomeToReleaseCache.get(getVersionCacheKey(sdkHome));
        if (cachedRelease != null) {
            return cachedRelease;
        }

        File elixir = JpsElixirSdkType.getScriptInterpreterExecutable(sdkHome);
        if (!elixir.canExecute()) {
            String reason = elixir.getPath() + (elixir.exists() ? " is not executable." : " is missing.");
            LOG.warn("Can't detect Elixir version: " + reason);
            return null;
        }

        ElixirSdkRelease release = transformStdoutLine(
                ElixirSdkRelease::fromString,
                STANDARD_TIMEOUT,
                sdkHome,
                elixir.getAbsolutePath(),
                "-e",
                "IO.puts System.build_info[:version]"
        );

        if (release != null) {
            mySdkHomeToReleaseCache.put(getVersionCacheKey(sdkHome), release);
        }

        return release;
    }

    @Nullable
    @Override
    public String getDefaultDocumentationUrl(@NotNull Sdk sdk) {
        return getDefaultDocumentationUrl(getRelease(sdk));
    }

    @Override
    public Icon getIcon() {
        return ElixirIcons.FILE;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Elixir SDK";
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        return getVersionString(detectSdkVersion(sdkHome));
    }

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return Map
     */
    private Map<Version, String> homePathByVersion() {
        Map<Version, String> homePathByVersion = new TreeMap<>(
                (version1, version2) -> {
                    // compare version2 to version1 to produce descending instead of ascending order.
                    return version2.compareTo(version1);
                }
        );

        if (SystemInfo.isMac) {
            File homebrewRoot = new File("/usr/local/Cellar/elixir");

            if (homebrewRoot.isDirectory()) {
                File[] files = homebrewRoot.listFiles();
                if (files != null) {
                    for (File child : files) {
                        if (child.isDirectory()) {
                            String versionString = child.getName();
                            Version version = Version.parseVersion(versionString);
                            homePathByVersion.put(version, child.getAbsolutePath());
                        }
                    }
                }
            } else {
                File nixOSRoot = new File("/nix/store/");

                if (nixOSRoot.isDirectory()) {
                    nixOSRoot.listFiles(
                            (dir, name) -> {
                                Matcher matcher = NIX_PATTERN.matcher(name);
                                boolean accept = false;

                                if (matcher.matches()){
                                    int major = Integer.parseInt(matcher.group(1));
                                    int minor = Integer.parseInt(matcher.group(2));
                                    int bugfix = Integer.parseInt(matcher.group(3));

                                    Version version = new Version(major, minor, bugfix);

                                    homePathByVersion.put(version, new File(dir, name).getAbsolutePath());
                                    accept = true;
                                }
                                return accept;
                            }
                    );
                }
            }
        } else {
            Version version = new Version(0, 0, 0);
            String sdkPath = "";

            if (SystemInfo.isWindows) {
                if (SystemInfo.is32Bit) {
                    sdkPath = "C:\\Program Files\\Elixir";
                } else {
                    sdkPath = "C:\\Program Files (x86)\\Elixir";
                }
            } else if (SystemInfo.isLinux) {
                sdkPath = "/usr/local/lib/elixir";
            }

            homePathByVersion.put(version, sdkPath);
        }

        return homePathByVersion;
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
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        configureSdkPaths(sdk);
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

    @NotNull
    @Override
    public Collection<String> suggestHomePaths() {
        return homePathByVersion().values();
    }

    /**
     * If a path selected in the file chooser is not a valid SDK home path, and the base name is one of the commonly
     * incorrectly selected subdirectories - bin, lib, or src - then return the parent path, so it can be checked for
     * validity.
     *
     * @param homePath the path selected in the file chooser.
     * @return the path to be used as the SDK home.
     */
    @NotNull
    @Override
    public String adjustSelectedSdkHome(@NotNull String homePath) {
        File homePathFile = new File(homePath);
        String adjustedSdkHome = homePath;

        if (homePathFile.isDirectory()) {
            String baseName = FilenameUtils.getBaseName(homePath);

            if (SDK_HOME_CHILD_BASE_NAME_SET.contains(baseName)) {
                adjustedSdkHome = homePathFile.getParent();
            }
        }

        return adjustedSdkHome;
    }

    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        return getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome));
    }


}
