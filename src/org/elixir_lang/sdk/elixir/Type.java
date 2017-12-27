package org.elixir_lang.sdk.elixir;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.UnknownSdkType;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import gnu.trove.THashSet;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.elixir_lang.Facet;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.jps.model.JpsElixirModelSerializerExtension;
import org.elixir_lang.jps.model.JpsElixirSdkType;
import org.elixir_lang.mix.runner.MixRunConfigurationBase;
import org.elixir_lang.sdk.HomePath;
import org.elixir_lang.sdk.erlang_dependent.SdkModificatorRootTypeConsumer;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.intellij.openapi.application.ModalityState.NON_MODAL;
import static org.elixir_lang.mix.runner.MixRunningStateUtil.module;
import static org.elixir_lang.sdk.HomePath.*;
import static org.elixir_lang.sdk.ProcessOutput.STANDARD_TIMEOUT;
import static org.elixir_lang.sdk.ProcessOutput.isSmallIde;
import static org.elixir_lang.sdk.ProcessOutput.transformStdoutLine;
import static org.elixir_lang.sdk.Type.addCodePaths;
import static org.elixir_lang.sdk.Type.ebinPathChainVirtualFile;

public class Type extends org.elixir_lang.sdk.erlang_dependent.Type {
    private static final String LINUX_DEFAULT_HOME_PATH = HomePath.LINUX_DEFAULT_HOME_PATH + "/elixir";
    private static final Logger LOG = Logger.getInstance(Type.class);
    private static final Pattern NIX_PATTERN = nixPattern("elixir");
    private static final Set<String> SDK_HOME_CHILD_BASE_NAME_SET = new THashSet<>(Arrays.asList("bin", "lib", "src"));
    private static final String WINDOWS_32BIT_DEFAULT_HOME_PATH = "C:\\Program Files\\Elixir";
    private static final String WINDOWS_64BIT_DEFAULT_HOME_PATH = "C:\\Program Files (x86)\\Elixir";
    private final Map<String, Release> mySdkHomeToReleaseCache =
            ApplicationManager.getApplication().isUnitTestMode() ? new HashMap<>() : new WeakHashMap<>();

    public Type() {
        super(JpsElixirModelSerializerExtension.ELIXIR_SDK_TYPE_ID);
    }

    @Nullable
    private static String releaseVersion(@NotNull SdkModificator sdkModificator) {
        String versionString = sdkModificator.getVersionString();
        final String releaseVersion;

        if (versionString != null) {
            Release release = Release.fromString(versionString);

            if (release != null) {
                releaseVersion = release.version();
            } else {
                releaseVersion = null;
            }
        } else {
            releaseVersion = null;
        }

        return releaseVersion;
    }

    private static void addDocumentationPath(@NotNull SdkModificator sdkModificator,
                                             @Nullable String releaseVersion,
                                             @NotNull String appName) {
        StringBuilder hexdocUrlBuilder =
                new StringBuilder("https://hexdoc.pm/").append(appName);

        if (releaseVersion != null) {
            hexdocUrlBuilder.append('/').append(releaseVersion);
        }

        VirtualFile hexdocUrlVirtualFile =
                VirtualFileManager.getInstance().findFileByUrl(hexdocUrlBuilder.toString());

        if (hexdocUrlVirtualFile != null) {
            OrderRootType documentationRootType = documentationRootType();

            if (documentationRootType != null) {
                sdkModificator.addRoot(hexdocUrlVirtualFile, documentationRootType);
            }
        }
    }

    @Nullable
    public static OrderRootType documentationRootType() {
        OrderRootType rootType;

        try {
            rootType = JavadocOrderRootType.getInstance();
        } catch (AssertionError assertionError) {
            rootType = null;
        }

        return rootType;
    }

    private static void addDocumentationPath(@NotNull SdkModificator sdkModificator,
                                             @Nullable String releaseVersion,
                                             @NotNull Path ebinPath) {
        String appName = ebinPath.getParent().getFileName().toString();

        addDocumentationPath(sdkModificator, releaseVersion, appName);
    }

    public static void addDocumentationPaths(@NotNull SdkModificator sdkModificator) {
        String releaseVersion = releaseVersion(sdkModificator);

        eachEbinPath(
                sdkModificator.getHomePath(),
                ebinPath -> addDocumentationPath(sdkModificator, releaseVersion, ebinPath)
        );
    }

    public static void addSourcePaths(@NotNull SdkModificator sdkModificator) {
        eachEbinPath(
                sdkModificator.getHomePath(),
                ebinPath -> addSourcePath(sdkModificator, ebinPath)
        );
    }

    private static void addSourcePath(@NotNull SdkModificator sdkModificator, @NotNull File libFile) {
        VirtualFile sourcePath = VfsUtil.findFileByIoFile(libFile, true);

        if (sourcePath != null) {
            sdkModificator.addRoot(sourcePath, OrderRootType.SOURCES);
        }
    }

    private static void addSourcePath(@NotNull SdkModificator sdkModificator, @NotNull Path ebinPath) {
        Path parentPath = ebinPath.getParent();
        Path libPath = Paths.get(parentPath.toString(), "lib");
        File libFile = libPath.toFile();

        if (libFile.exists()) {
            addSourcePath(sdkModificator, libFile);
        }
    }

    private static void configureSdkPaths(@NotNull Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();
        addCodePaths(sdkModificator);
        addDocumentationPaths(sdkModificator);
        addSourcePaths(sdkModificator);
        configureInternalErlangSdk(sdk, sdkModificator);

        sdkModificator.commitChanges();
    }

    private static Sdk configureInternalErlangSdk(@NotNull Sdk elixirSdk, @NotNull SdkModificator elixirSdkModificator) {
        final Sdk erlangSdk = defaultErlangSdk();

        if (erlangSdk != null) {
            SdkAdditionalData sdkAdditionData =
                    new org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData(erlangSdk, elixirSdk);
            elixirSdkModificator.setSdkAdditionalData(sdkAdditionData);

            addNewCodePathsFromInternErlangSdk(elixirSdk, erlangSdk, elixirSdkModificator);
        }

        return erlangSdk;
    }

    public static void addNewCodePathsFromInternErlangSdk(@NotNull Sdk elixirSdk,
                                                          @NotNull Sdk internalErlangSdk,
                                                          @NotNull SdkModificator elixirSdkModificator) {
        codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator,
                (sdkModificator, configuredRoots, expandedInternalRoot, type) -> {
                    if (!ArrayUtils.contains(configuredRoots, expandedInternalRoot)) {
                        sdkModificator.addRoot(expandedInternalRoot, type);
                    }
                }
        );
    }

    public static void removeCodePathsFromInternalErlangSdk(@NotNull Sdk elixirSdk,
                                                          @NotNull Sdk internalErlangSdk,
                                                          @NotNull SdkModificator elixirSdkModificator) {
        codePathsFromInternalErlangSdk(
                elixirSdk,
                internalErlangSdk,
                elixirSdkModificator,
                (sdkModificator, configuredRoots, expandedInternalRoot, type) -> {
                    sdkModificator.removeRoot(expandedInternalRoot, type);
                }
        );
    }

    private static void codePathsFromInternalErlangSdk(
            @NotNull Sdk elixirSdk,
            @NotNull Sdk internalErlangSdk,
            @NotNull SdkModificator elixirSdkModificator,
            @NotNull SdkModificatorRootTypeConsumer sdkModificatorRootTypeConsumer
    ) {
        final SdkType internalSdkType = (SdkType) internalErlangSdk.getSdkType();
        final SdkType elixirSdkType = (SdkType) elixirSdk.getSdkType();

        for (OrderRootType type : OrderRootType.getAllTypes()) {
            if (internalSdkType.isRootTypeApplicable(type) && elixirSdkType.isRootTypeApplicable(type)) {
                final VirtualFile[] internalRoots = internalErlangSdk.getSdkModificator().getRoots(type);
                final VirtualFile[] configuredRoots = elixirSdkModificator.getRoots(type);

                for (VirtualFile internalRoot : internalRoots) {
                    for (VirtualFile expandedInternalRoot : expandInternalErlangSdkRoot(internalRoot, type)) {
                        sdkModificatorRootTypeConsumer
                                .consume(elixirSdkModificator, configuredRoots, expandedInternalRoot, type);
                    }
                }
            }
        }
    }

    @NotNull
    private static Iterable<VirtualFile> expandInternalErlangSdkRoot(@NotNull VirtualFile internalRoot,
                                                                     @NotNull OrderRootType type) {
        java.util.List<VirtualFile> expandedInternalRootList;

        if (type == OrderRootType.CLASSES) {
            final String path = internalRoot.getPath();

            /* Erlang SDK from intellij-erlang uses lib/erlang/lib as class path, but intellij-elixir needs the ebin
               directories under lib/erlang/lib/APP-VERSION/ebin that works as a code path used by `-pa` argument to
               `erl.exe` */
            if (path.endsWith("lib/erlang/lib")) {
                expandedInternalRootList = new ArrayList<>();
                String parentPath = Paths.get(path).getParent().toString();
                eachEbinPath(parentPath, ebinPath -> ebinPathChainVirtualFile(ebinPath, expandedInternalRootList::add));
            } else {
                expandedInternalRootList = Collections.singletonList(internalRoot);
            }
        } else {
            expandedInternalRootList = Collections.singletonList(internalRoot);
        }

        return expandedInternalRootList;
    }

    @TestOnly
    @NotNull
    public static Sdk createMockSdk(@NotNull String sdkHome, @NotNull Release version) {
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
    private static String getDefaultDocumentationUrl(@Nullable Release version) {
        return version == null ? null : "http://elixir-lang.org/docs/stable/elixir/";
    }

    @NotNull
    private static String getDefaultSdkName(@NotNull String sdkHome, @Nullable Release release) {
        return release != null ? release.toString() : "Unknown Elixir version at " + sdkHome;
    }

    @Nullable
    private static String defaultErlangSdkHomePath() {
        SdkType erlangSdkForElixirSdkType = SdkType.findInstance(org.elixir_lang.sdk.erlang.Type.class);
        // Will suggest newest version, unlike `intellij-erlang`
        return erlangSdkForElixirSdkType.suggestHomePath();
    }

    @Nullable
    private static Sdk createDefaultErlangSdk(@NotNull ProjectJdkTable projectJdkTable,
                                              @NotNull SdkType erlangSdkType,
                                              @NotNull String homePath) {
        String sdkName = erlangSdkType.suggestSdkName("Default " + erlangSdkType.getName(), homePath);
        ProjectJdkImpl projectJdkImpl = new ProjectJdkImpl(sdkName, erlangSdkType);
        projectJdkImpl.setHomePath(homePath);
        erlangSdkType.setupSdkPaths(projectJdkImpl);
        final Sdk erlangSdk;

        if (projectJdkImpl.getVersionString() != null) {
            ApplicationManager.getApplication().invokeAndWait(
                    () -> ApplicationManager.getApplication().runWriteAction(() ->
                            projectJdkTable.addJdk(projectJdkImpl)
                    ),
                    NON_MODAL
            );

            erlangSdk = projectJdkImpl;
        } else {
            erlangSdk = null;
        }

        return erlangSdk;
    }

    @Nullable
    private static Sdk createDefaultErlangSdk(@NotNull ProjectJdkTable projectJdkTable,
                                              @NotNull SdkType erlangSdkType) {
        String homePath = defaultErlangSdkHomePath();
        final Sdk erlangSdk;

        if (homePath != null) {
            erlangSdk = createDefaultErlangSdk(projectJdkTable, erlangSdkType, homePath);
        } else {
            erlangSdk = null;
        }

        return erlangSdk;
    }

    public static SdkType erlangSdkType(@NotNull ProjectJdkTable projectJdkTable) {
        SdkType erlangSdkType;

        if (isSmallIde()) {
            /* intellij-erlang's "Erlang SDK" does not work in small IDEs because it uses JavadocRoot for documentation,
               which isn't available in Small IDEs. */
            erlangSdkType = SdkType.findInstance(org.elixir_lang.sdk.erlang.Type.class);
        } else {
            erlangSdkType = (SdkType) projectJdkTable.getSdkTypeByName("Erlang SDK");

            if (erlangSdkType instanceof UnknownSdkType) {
                erlangSdkType = SdkType.findInstance(org.elixir_lang.sdk.erlang.Type.class);
            }
        }

        return erlangSdkType;
    }

    @Nullable
    private static Sdk defaultErlangSdk() {
        ProjectJdkTable projectJdkTable = ProjectJdkTable.getInstance();
        SdkType erlangSdkType = erlangSdkType(projectJdkTable);

        Sdk mostRecentErlangSdk = projectJdkTable.findMostRecentSdkOfType(erlangSdkType);
        @Nullable Sdk defaultErlangSdk;

        if (mostRecentErlangSdk == null) {
            defaultErlangSdk = createDefaultErlangSdk(projectJdkTable, erlangSdkType);
        } else {
            defaultErlangSdk = mostRecentErlangSdk;
        }

        return defaultErlangSdk;
    }

    @Nullable
    public static Sdk putDefaultErlangSdk(@NotNull Sdk elixirSdk) {
        assert elixirSdk.getSdkType() == Type.getInstance();

        SdkModificator sdkModificator = elixirSdk.getSdkModificator();
        Sdk defaultErlangSdk = configureInternalErlangSdk(elixirSdk, sdkModificator);
        ApplicationManager.getApplication().invokeAndWait(
                () -> ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges),
                NON_MODAL
        );

        return defaultErlangSdk;
    }

    @NotNull
    public static Type getInstance() {
        return SdkType.findInstance(Type.class);
    }

    @NotNull
    public static Release getNonNullRelease(@NotNull PsiElement element) {
        Release nonNullRelease = getRelease(element);

        if (nonNullRelease == null) {
            nonNullRelease = Release.LATEST;
        }

        return nonNullRelease;
    }

    @Nullable
    public static Release getRelease(@NotNull PsiElement element) {
        return getRelease(mostSpecificSdk(element));
    }

    @Contract("null -> null")
    @Nullable
    public static Release getRelease(@Nullable Sdk sdk) {
        if (sdk != null && sdk.getSdkType() == getInstance()) {
            Release fromVersionString = Release.fromString(sdk.getVersionString());
            return fromVersionString != null ? fromVersionString : getInstance().detectSdkVersion(StringUtil.notNullize(sdk.getHomePath()));
        }
        return null;
    }

    @Nullable
    public static String getSdkPath(@NotNull final Project project) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        return sdk != null && sdk.getSdkType() == getInstance() ? sdk.getHomePath() : null;
    }

    @Nullable
    private static String getVersionCacheKey(@Nullable String sdkHome) {
        return sdkHome != null ? new File(sdkHome).getAbsolutePath() : null;
    }

    @Nullable
    private static String getVersionString(@Nullable Release version) {
        return version != null ? version.toString() : null;
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

    @Nullable
    private Release detectSdkVersion(@NotNull String sdkHome) {
        Release cachedRelease = mySdkHomeToReleaseCache.get(getVersionCacheKey(sdkHome));
        if (cachedRelease != null) {
            return cachedRelease;
        }

        File elixir = JpsElixirSdkType.getScriptInterpreterExecutable(sdkHome);
        if (!elixir.canExecute()) {
            String reason = elixir.getPath() + (elixir.exists() ? " is not executable." : " is missing.");
            LOG.warn("Can't detect Elixir version: " + reason);
            return null;
        }

        Release release = transformStdoutLine(
                Release::fromString,
                STANDARD_TIMEOUT,
                sdkHome,
                elixir.getAbsolutePath(),
                "-e",
                "System.version() |> IO.puts()"
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

    @NotNull
    @Override
    public FileChooserDescriptor getHomeChooserDescriptor() {
        final FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false) {
            @Override
            public void validateSelectedFiles(VirtualFile[] files) throws Exception {
                if (files.length != 0) {
                    validateSdkHomePath(files[0]);
                }
            }
        };

        descriptor.setTitle(ProjectBundle.message("sdk.configure.home.title", getPresentableName()));

        return descriptor;
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
            mergeHomebrew(homePathByVersion, "elixir", java.util.function.Function.identity());
            mergeNixStore(homePathByVersion, NIX_PATTERN, Function.identity());
        } else {
            String sdkPath;

            if (SystemInfo.isWindows) {
                if (SystemInfo.is32Bit) {
                    sdkPath = WINDOWS_32BIT_DEFAULT_HOME_PATH;
                } else {
                    sdkPath = WINDOWS_64BIT_DEFAULT_HOME_PATH;
                }

                homePathByVersion.put(UNKNOWN_VERSION, sdkPath);
            } else if (SystemInfo.isLinux) {
                homePathByVersion.put(UNKNOWN_VERSION, LINUX_DEFAULT_HOME_PATH);

                mergeNixStore(homePathByVersion, NIX_PATTERN, Function.identity());
            }
        }

        return homePathByVersion;
    }

    @NotNull
    private Exception invalidSdkHomeException(@NotNull VirtualFile virtualFile) {
        return new Exception(invalidSdkHomeMessage(virtualFile));
    }

    @NotNull
    private String invalidSdkHomeMessage(@NotNull VirtualFile virtualFile) {
        String message;

        if (virtualFile.isDirectory()) {
            message =
                    "A valid home for " + getPresentableName() + " has the following structure:\n" +
                            "\n" +
                            "ELIXIR_SDK_HOME\n" +
                            "* bin\n" +
                            "** elixir\n" +
                            "** elixirc\n" +
                            "** iex\n" +
                            "** mix\n" +
                            "* lib\n" +
                            "** eex\n" +
                            "** elixir\n" +
                            "** ex_unit\n" +
                            "** iex\n" +
                            "** logger\n" +
                            "** mix\n";
        } else {
            message = "A directory must be select for the home for " + getPresentableName();
        }

        return message;
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

    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        return getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome));
    }

    private void validateSdkHomePath(@NotNull VirtualFile virtualFile) throws Exception {
        final String selectedPath = virtualFile.getPath();
        boolean valid = isValidSdkHome(selectedPath);

        if (!valid) {
            valid = isValidSdkHome(adjustSelectedSdkHome(selectedPath));

            if (!valid) {
                throw invalidSdkHomeException(virtualFile);
            }
        }
    }

    @Nullable
    @Override
    public com.intellij.openapi.projectRoots.AdditionalDataConfigurable createAdditionalDataConfigurable(
            @NotNull SdkModel sdkModel,
            @NotNull SdkModificator sdkModificator
    ) {
        return new org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable(sdkModel, sdkModificator);
    }

    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
        if (additionalData instanceof org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData) {
            try {
                ((org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData) additionalData).writeExternal(additional);
            } catch (WriteExternalException e) {
                LOG.error(e);
            }
        }
    }

    public SdkAdditionalData loadAdditionalData(@NotNull Sdk elixirSdk, Element additional) {
        org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData sdkAdditionalData = new org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData(elixirSdk);

        try {
            sdkAdditionalData.readExternal(additional);
        } catch (InvalidDataException e) {
            LOG.error(e);
        }

        return sdkAdditionalData;
    }

    @Nullable
    private static Sdk facetSdk(@NotNull Facet facet) {
        return facet.getConfiguration().getSdk();
    }

    @Nullable
    private static Sdk moduleSdk(@NotNull Module module) {
        return sdk(ModuleRootManager.getInstance(module).getSdk());
    }

    @Nullable
    private static Sdk projectSdk(@NotNull Project project) {
        return sdk(ProjectRootManager.getInstance(project).getProjectSdk());
    }

    @Nullable
    private static Sdk sdk(@Nullable Sdk sdk) {
        Sdk elixirSdk;

        if (sdk != null && sdk.getSdkType() == Type.getInstance()) {
            elixirSdk = sdk;
        } else {
            elixirSdk = null;
        }

        return elixirSdk;
    }

    @Nullable
    public static Sdk mostSpecificSdk(@NotNull MixRunConfigurationBase mixRunConfigurationBase) {
        Module module = module(mixRunConfigurationBase);
        Sdk sdk;

        if (module != null) {
            sdk = mostSpecificSdk(module);
        } else {
            Project project = mixRunConfigurationBase.getProject();
            sdk = mostSpecificSdk(project);
        }

        return sdk;
    }

    @Nullable
    public static Sdk mostSpecificSdk(@NotNull Module module) {
        Facet elixirFacet = FacetManager.getInstance(module).getFacetByType(Facet.ID);
        Sdk elixirSdk = null;

        if (elixirFacet != null) {
            elixirSdk = facetSdk(elixirFacet);
        }

        if (elixirSdk == null) {
            elixirSdk = moduleSdk(module);
        }

        if (elixirSdk == null) {
            elixirSdk = mostSpecificSdk(module.getProject());
        }

        return elixirSdk;
    }

    @Nullable
    public static Sdk mostSpecificSdk(@NotNull PsiElement psiElement) {
        Project project = psiElement.getProject();
        Sdk elixirSdk;

        /* ModuleUtilCore.findModuleForPsiElement can fail with NullPointerException if the
           ProjectFileIndex.SERVICE.getInstance(Project) returns {@code null}, so check that the ProjectFileIndex is
           available first */
        if (ProjectFileIndex.SERVICE.getInstance(project) != null) {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiElement);

            if (module != null) {
                elixirSdk = mostSpecificSdk(module);
            } else {
                elixirSdk = mostSpecificSdk(project);
            }
        } else {
            elixirSdk = mostSpecificSdk(project);
        }

        return elixirSdk;
    }

    @Nullable
    public static Sdk mostSpecificSdk(@NotNull Project project) {
        return projectSdk(project);
    }
}
