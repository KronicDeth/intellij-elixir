package org.elixir_lang.jps.shared.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SdkPaths {
    public static final String SOURCE_NAME_ASDF = "asdf";
    public static final String SOURCE_NAME_MISE = "mise";
    public static final String SOURCE_NAME_ELIXIR_INSTALL = "elixir-install";
    public static final String SOURCE_NAME_HOMEBREW = "Homebrew";
    public static final String SOURCE_NAME_NIX = "Nix";
    public static final String SOURCE_NAME_KERL = "kerl";

    public static final List<String> VERSION_MANAGERS =
            List.of(SOURCE_NAME_ASDF, SOURCE_NAME_MISE, SOURCE_NAME_ELIXIR_INSTALL);

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    private static final String MISE_POSIX_PREFIX = "/.local/share/mise/installs/";
    private static final String MISE_WINDOWS_PREFIX = "/appdata/local/mise/installs/";

    private SdkPaths() {
    }

    @Nullable
    public static String detectSource(@NotNull String homePath) {
        String posixPath = toSystemIndependentName(homePath);
        String matchPath = posixPath.toLowerCase(Locale.ROOT);

        if (matchPath.contains(MISE_POSIX_PREFIX) || matchPath.contains(MISE_WINDOWS_PREFIX)) {
            return SOURCE_NAME_MISE;
        }
        if (matchPath.contains("/.asdf/installs/")) {
            return SOURCE_NAME_ASDF;
        }
        if (matchPath.contains("/.elixir-install/")) {
            return SOURCE_NAME_ELIXIR_INSTALL;
        }
        if (matchPath.contains("/usr/local/cellar/") || matchPath.contains("/opt/homebrew/cellar/")) {
            return SOURCE_NAME_HOMEBREW;
        }
        if (matchPath.contains("/nix/store/")) {
            return SOURCE_NAME_NIX;
        }
        if (matchPath.contains("/otp/") || new File(homePath, ".kerl_config").exists()) {
            return SOURCE_NAME_KERL;
        }

        return null;
    }

    @Nullable
    public static String mixHome(@Nullable String homePath) {
        if (homePath == null) {
            return null;
        }

        String source = detectSource(homePath);
        if (source == null || !VERSION_MANAGERS.contains(source)) {
            return null;
        }

        File binDir = new File(homePath, "bin");
        File parentDir = binDir.getParentFile();
        if (parentDir == null) {
            return null;
        }

        return new File(parentDir, ".mix").getAbsolutePath();
    }

    @Nullable
    public static String mixHomeReplacePrefix(@Nullable String source, @Nullable String homePath) {
        if (SOURCE_NAME_MISE.equals(source)) {
            if (homePath != null) {
                String posixPath = toSystemIndependentName(homePath);
                String matchPath = posixPath.toLowerCase(Locale.ROOT);
                if (matchPath.contains(MISE_WINDOWS_PREFIX)) {
                    return MISE_WINDOWS_PREFIX;
                }
            }
            return MISE_POSIX_PREFIX;
        }
        if (SOURCE_NAME_ASDF.equals(source)) {
            return "/.asdf/installs/";
        }
        if (SOURCE_NAME_ELIXIR_INSTALL.equals(source)) {
            return "/.elixir-install/installs/";
        }
        return null;
    }

    public static boolean shouldReplaceMixHome(@Nullable String existingMixHome, @Nullable String replacePrefix) {
        if (existingMixHome == null) {
            return true;
        }
        if (replacePrefix == null) {
            return false;
        }

        String posixPath = toSystemIndependentName(existingMixHome);
        if (IS_WINDOWS) {
            String matchPath = posixPath.toLowerCase(Locale.ROOT);
            String matchPrefix = replacePrefix.toLowerCase(Locale.ROOT);
            return matchPath.contains(matchPrefix);
        }
        return posixPath.contains(replacePrefix);
    }

    public static void maybeUpdateMixHome(@NotNull Map<String, String> environment, @Nullable String homePath) {
        String mixHome = mixHome(homePath);
        if (mixHome == null) {
            return;
        }

        String source = detectSource(homePath);
        String replacePrefix = mixHomeReplacePrefix(source, homePath);
        String existingMixHome = environment.get("MIX_HOME");
        if (shouldReplaceMixHome(existingMixHome, replacePrefix)) {
            environment.put("MIX_HOME", mixHome);
            environment.put("MIX_ARCHIVES", mixHome + File.separator + "archives");
        }
    }

    @NotNull
    public static String getExecutableFileName(@NotNull String executableName, @NotNull String windowsExt) {
        return getExecutableFileName(IS_WINDOWS, executableName, windowsExt);
    }

    @NotNull
    public static String getExecutableFileName(boolean isWindows,
                                               @NotNull String executableName,
                                               @NotNull String windowsExt) {
        return isWindows ? executableName + windowsExt : executableName;
    }

    @NotNull
    private static String toSystemIndependentName(@NotNull String path) {
        return path.replace('\\', '/');
    }
}
