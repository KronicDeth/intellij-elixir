package org.elixir_lang.sdk.erlang;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.Version;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.jps.sdk_type.Erlang;
import org.elixir_lang.jps.HomePath;
import org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.elixir_lang.jps.HomePath.*;
import static org.elixir_lang.sdk.Type.addCodePaths;
import static org.elixir_lang.sdk.Type.documentationRootType;

/**
 * An Erlang SdkType for use when `intellij-erlang` is not installed
 */
public class Type extends SdkType {
    private static final String OTP_RELEASE_PREFIX_LINE = Type.class.getCanonicalName() + " OTP_RELEASE:";
    private static final String ERTS_VERSION_PREFIX_LINE = Type.class.getCanonicalName() + " ERTS_VERSION:";
    private static final String PRINT_VERSION_INFO_EXPRESSION =
            "io:format(\"~n~s~n~s~n~s~n~s~n\",[" +
                    "\"" + OTP_RELEASE_PREFIX_LINE + "\"," +
                    "erlang:system_info(otp_release)," +
                    "\"" + ERTS_VERSION_PREFIX_LINE + "\"," +
                    "erlang:system_info(version)" +
                    "]),erlang:halt().";
    private static final String WINDOWS_DEFAULT_HOME_PATH = "C:\\Program Files\\erl9.0";
    private static final Pattern NIX_PATTERN = nixPattern("erlang");
    private static final String LINUX_MINT_HOME_PATH = HomePath.LINUX_MINT_HOME_PATH + "/erlang";
    private static final String LINUX_DEFAULT_HOME_PATH = HomePath.LINUX_DEFAULT_HOME_PATH + "/erlang";
    private static final Function<File, File> VERSION_PATH_TO_HOME_PATH =
            versionPath -> new File(versionPath, "lib/erlang");
    private static final Logger LOGGER = Logger.getInstance(Type.class);
    private final Map<String, Release> releaseBySdkHome = ContainerUtil.createWeakMap();


    public Type() {
        // Don't use "Erlang SDK" as we want it to be different from the name in intellij-erlang
        super("Erlang SDK for Elixir SDK");
    }

    @NotNull
    private static String getDefaultSdkName(@NotNull String sdkHome, @Nullable Release version) {
        StringBuilder defaultSdkNameBuilder = new StringBuilder("Erlang for Elixir ");

        if (version != null) {
            defaultSdkNameBuilder.append(version.otpRelease);
        } else {
            defaultSdkNameBuilder.append(" at ").append(sdkHome);
        }

        return defaultSdkNameBuilder.toString();
    }

    @Nullable
    private static String getVersionCacheKey(@Nullable String sdkHome) {
        String versionCacheKey = null;

        if (sdkHome != null) {
            versionCacheKey = new File(sdkHome).getAbsolutePath();
        }

        return versionCacheKey;
    }

    @Nullable
    private static Release parseSdkVersion(@NotNull List<String> printVersionInfoOutput) {
        String otpRelease = null;
        String ertsVersion = null;

        ListIterator<String> iterator = printVersionInfoOutput.listIterator();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (OTP_RELEASE_PREFIX_LINE.equals(line) && iterator.hasNext()) {
                otpRelease = iterator.next();
            } else if (ERTS_VERSION_PREFIX_LINE.equals(line) && iterator.hasNext()) {
                ertsVersion = iterator.next();
            }
        }

        Release release;

        if (otpRelease != null && ertsVersion != null) {
            release = new Release(otpRelease, ertsVersion);
        } else {
            release = null;
        }

        return release;
    }

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return Map
     */
    public static Map<Version, String> homePathByVersion() {
        Map<Version, String> homePathByVersion = HomePath.homePathByVersion();

        if (SystemInfo.isMac) {
            mergeASDF(homePathByVersion, "erlang");
            mergeHomebrew(homePathByVersion, "erlang", VERSION_PATH_TO_HOME_PATH);
            mergeNixStore(homePathByVersion, NIX_PATTERN, VERSION_PATH_TO_HOME_PATH);
        } else {
            if (SystemInfo.isWindows) {
                putIfDirectory(homePathByVersion, UNKNOWN_VERSION, WINDOWS_DEFAULT_HOME_PATH);
            } else if (SystemInfo.isLinux) {
                putIfDirectory(homePathByVersion, UNKNOWN_VERSION, LINUX_DEFAULT_HOME_PATH);
                putIfDirectory(homePathByVersion, UNKNOWN_VERSION, LINUX_MINT_HOME_PATH);

                mergeTravisCIKerl(homePathByVersion, Function.identity());
                mergeNixStore(homePathByVersion, NIX_PATTERN, VERSION_PATH_TO_HOME_PATH);
            }
        }

        return homePathByVersion;
    }

    private static void putIfDirectory(@NotNull Map<Version, String> homePathByVersion,
                                       @NotNull Version version,
                                       @NotNull String homePath) {
        File homeFile = new File(homePath);

        if (homeFile.isDirectory()) {
            homePathByVersion.put(version, homePath);
        }
    }

    @Override
    public boolean isRootTypeApplicable(@NotNull OrderRootType type) {
        return type == OrderRootType.CLASSES ||
                type == OrderRootType.SOURCES ||
                type == documentationRootType();
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();
        addCodePaths(sdkModificator);
        ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges);
    }

    /**
     * Returns a recommended starting path for a file chooser (where SDKs of this type are usually may be found),
     * or {@code null} if not applicable/no SDKs found.
     * <p/>
     * E.g. for Python SDK on Unix the method may return either {@code "/usr/bin"} or {@code "/usr/bin/python"}
     * (if there is only one Python interpreter installed on a host).
     */
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
    public boolean isValidSdkHome(String path) {
        File erl = Erlang.getByteCodeInterpreterExecutable(path);

        return erl.canExecute();
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return getDefaultSdkName(sdkHome, detectSdkVersion(sdkHome));
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        Release release = detectSdkVersion(sdkHome);
        String versionString;

        if (release != null) {
            versionString = release.otpRelease;
        } else {
            versionString = null;
        }

        return versionString;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel,
                                                                       @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return getName();
    }

    @Override
    public void saveAdditionalData(@NotNull com.intellij.openapi.projectRoots.SdkAdditionalData additionalData,
                                   @NotNull Element additional) {
        // Intentionally left blank
    }

    @Nullable
    private Release detectSdkVersion(@NotNull String sdkHome) {
        Release release = null;

        Release cachedRelease = releaseBySdkHome.get(getVersionCacheKey(sdkHome));

        if (cachedRelease != null) {
            release = cachedRelease;
        } else {
            File erl = Erlang.getByteCodeInterpreterExecutable(sdkHome);

            if (!erl.canExecute()) {
                StringBuilder messageBuilder = new StringBuilder("Can't detect Erlang version: ").append(erl.getPath());

                if (erl.exists()) {
                    messageBuilder.append(" is not executable.");
                } else {
                    messageBuilder.append(" is missing.");
                }

                LOGGER.warn(messageBuilder.toString());
            } else {
                try {
                    ProcessOutput output = org.elixir_lang.sdk.ProcessOutput.getProcessOutput(
                            10 * 1000,
                            sdkHome,
                            erl.getAbsolutePath(),
                            "-noshell",
                            "-eval",
                            PRINT_VERSION_INFO_EXPRESSION
                    );

                    if (!(output.getExitCode() != 0 || output.isCancelled() || output.isTimeout())) {
                        release = parseSdkVersion(output.getStdoutLines());
                    }

                    if (release != null) {
                        releaseBySdkHome.put(getVersionCacheKey(sdkHome), release);
                    } else {
                        LOGGER.warn("Failed to detect Erlang version.\n" +
                                "StdOut: " + output.getStdout() + "\n" +
                                "StdErr: " + output.getStderr());
                    }
                } catch (ExecutionException e) {
                    LOGGER.warn(e);
                }
            }
        }

        return release;
    }
}
