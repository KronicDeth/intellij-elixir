package org.elixir_lang.jps;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Version;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePath {
    private static final String HEAD_PREFIX = "HEAD-";
    public static final String LINUX_MINT_HOME_PATH = "/usr/lib";
    public static final String LINUX_DEFAULT_HOME_PATH = "/usr/local/lib";
    public static final String NIX_STORE_PATH = "/nix/store";
    public static final Version UNKNOWN_VERSION = new Version(0, 0, 0);
    private static final File HOMEBREW_ROOT = new File("/usr/local/Cellar");
    private static final File NIX_STORE = new File(NIX_STORE_PATH);
    private static final Logger LOGGER = Logger.getInstance(HomePath.class);

    private HomePath() {
    }

    public static void eachEbinPath(@NotNull String homePath, @NotNull Consumer<Path> ebinPathConsumer) {
        Path lib = Paths.get(homePath, "lib");

        try (DirectoryStream<Path> libDirectoryStream = Files.newDirectoryStream(lib, path -> Files.isDirectory(path))) {
            libDirectoryStream.forEach(
                    app -> {
                        try (DirectoryStream<Path> ebinDirectoryStream = Files.newDirectoryStream(app, "ebin")) {
                            ebinDirectoryStream.forEach(ebinPathConsumer);
                        } catch (IOException ioException) {
                            LOGGER.error(ioException);
                        }
                    }
            );
        } catch (NoSuchFileException noSuchFileException) {
            NotificationGroupManager
                    .getInstance()
                    .getNotificationGroup("Elixir")
                    .createNotification(noSuchFileException.getFile() + " does not exist, so its ebin paths cannot be enumerated.", NotificationType.ERROR)
                    .notify();
        } catch (IOException ioException) {
            LOGGER.error(ioException);
        }
    }

    public static boolean hasEbinPath(@NotNull String homePath) {
        Path lib = Paths.get(homePath, "lib");
        boolean hasEbinPath = false;

        try (DirectoryStream<Path> libDirectoryStream = Files.newDirectoryStream(lib, path -> Files.isDirectory(path))) {
            for (Path app : libDirectoryStream) {
                try (DirectoryStream<Path> ebinDirectoryStream = Files.newDirectoryStream(app, "ebin")) {
                    if (ebinDirectoryStream.iterator().hasNext()) {
                        hasEbinPath = true;

                        break;
                    }
                } catch (IOException ioException) {
                    LOGGER.error(ioException);
                }
            }
        } catch (IOException ioException) {
            LOGGER.error(ioException);
        }

        return hasEbinPath;
    }

    @NotNull
    public static Pattern nixPattern(@NotNull String name) {
        return Pattern.compile(".+-" + name + "-(\\d+)\\.(\\d+)\\.(\\d+)");
    }

    public static void mergeASDF(@NotNull Map<Version, String> homePathByVersion, @NotNull String name) {
        mergeASDF(homePathByVersion, name, System.getProperty("user.home"));
    }

    public static void mergeASDF(@NotNull Map<Version, String> homePathByVersion, @NotNull String name, @NotNull String userHome) {
        mergeNameSubdirectories(homePathByVersion, Paths.get(userHome, ".asdf", "installs").toFile(), name, Function.identity());
    }

    public static void mergeMise(@NotNull Map<Version, String> homePathByVersion, @NotNull String name) {
        mergeMise(homePathByVersion, name, System.getProperty("user.home"));
    }

    public static void mergeMise(@NotNull Map<Version, String> homePathByVersion, @NotNull String name, @NotNull String userHome) {
        mergeNameSubdirectories(homePathByVersion, Paths.get(userHome, ".local", "share", "mise", "installs").toFile(), name, Function.identity());
    }

    public static void mergeHomebrew(@NotNull Map<Version, String> homePathByVersion,
                                     @NotNull String name,
                                     @NotNull Function<File, File> versionPathToHomePath) {
        mergeNameSubdirectories(homePathByVersion, HOMEBREW_ROOT, name, versionPathToHomePath);
    }

    private static void mergeNameSubdirectories(@NotNull Map<Version, String> homePathByVersion,
                                                @NotNull File parent,
                                                @NotNull String name,
                                                @NotNull Function<File, File> versionPathToHomePath) {
        if (parent.isDirectory()) {
            File nameDirectory = new File(parent, name);

            if (nameDirectory.isDirectory()) {
                File[] files = nameDirectory.listFiles();

                if (files != null) {
                    for (File child : files) {
                        if (child.isDirectory()) {
                            String versionString = child.getName();
                            @NotNull Version version = parseVersion(versionString);
                            File homePath = versionPathToHomePath.apply(child);
                            homePathByVersion.put(version, homePath.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    @NotNull
    private static Version parseVersion(@NotNull String versionString) {
        Version version = Version.parseVersion(versionString);

        if (version == null) {
            if (versionString.startsWith(HEAD_PREFIX)) {
                String sha1 = versionString.substring(HEAD_PREFIX.length());

                version = new Version(0, 0, Integer.parseInt(sha1, 16));
            } else {
                version = UNKNOWN_VERSION;
            }
        }

        return version;
    }

    public static void mergeNixStore(@NotNull Map<Version, String> homePathByVersion,
                                     @NotNull Pattern nixPattern,
                                     @NotNull Function<File, File> versionPathToHomePath) {
        mergeNixStore(homePathByVersion, nixPattern, versionPathToHomePath, NIX_STORE.getAbsolutePath());
    }

    public static void mergeNixStore(@NotNull Map<Version, String> homePathByVersion,
                                     @NotNull Pattern nixPattern,
                                     @NotNull Function<File, File> versionPathToHomePath,
                                     @NotNull String nixStorePath) {
        File nixStore = new File(nixStorePath);
        if (nixStore.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            nixStore.listFiles(
                    (dir, name) -> {
                        Matcher matcher = nixPattern.matcher(name);
                        boolean accept = false;

                        if (matcher.matches()) {
                            int major = Integer.parseInt(matcher.group(1));
                            int minor = Integer.parseInt(matcher.group(2));
                            int bugfix = Integer.parseInt(matcher.group(3));

                            Version version = new Version(major, minor, bugfix);
                            File homePath = versionPathToHomePath.apply(new File(dir, name));

                            homePathByVersion.put(version, homePath.getAbsolutePath());
                            accept = true;
                        }
                        return accept;
                    }
            );
        }
    }

    public static void mergeTravisCIKerl(@NotNull Map<Version, String> homePathByVersion,
                                         @NotNull Function<File, File> versionPathToHomePath) {
        final String userHome = System.getProperty("user.home");

        if (userHome != null) {
            mergeTravisCIKerl(homePathByVersion, versionPathToHomePath, userHome);
        }
    }

    public static void mergeTravisCIKerl(@NotNull Map<Version, String> homePathByVersion,
                                         @NotNull Function<File, File> versionPathToHomePath,
                                         @NotNull String userHome) {
        mergeNameSubdirectories(homePathByVersion, new File(userHome), "otp", versionPathToHomePath);
    }

    /**
     * Detects the source/manager of an SDK based on its home path.
     *
     * @param homePath the SDK home path
     * @return the source name (e.g., "mise", "asdf", "Homebrew", "Nix") or null if unknown
     */
    @org.jetbrains.annotations.Nullable
    public static String detectSource(@NotNull String homePath) {
        if (homePath.contains("/.local/share/mise/installs/")) {
            return "mise";
        } else if (homePath.contains("/.asdf/installs/")) {
            return "asdf";
        } else if (homePath.contains("/usr/local/Cellar/") || homePath.contains("/opt/homebrew/Cellar/")) {
            return "Homebrew";
        } else if (homePath.contains("/nix/store/")) {
            return "Nix";
        } else if (homePath.contains("/otp/")) {
            return "kerl";
        } else if (new File(homePath, ".kerl_config").exists()) {
            return "kerl";
        }
        return null;
    }

    /**
     * Merges Erlang installations from kerl by running `kerl list installations`.
     * Output format: "VERSION /path/to/installation"
     */
    public static void mergeKerl(@NotNull Map<Version, String> homePathByVersion,
                                 @NotNull Function<File, File> versionPathToHomePath) {
        // Check if kerl is available
        if (!isCommandAvailable("kerl")) {
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("kerl", "list", "installations");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Format: "28.2 /Users/josh/bar"
                    String[] parts = line.split(" ", 2);
                    if (parts.length == 2) {
                        String versionString = parts[0].trim();
                        String path = parts[1].trim();
                        File homePath = versionPathToHomePath.apply(new File(path));
                        if (homePath.isDirectory()) {
                            Version version = parseVersion(versionString);
                            homePathByVersion.put(version, homePath.getAbsolutePath());
                        }
                    }
                }
            }

            process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            // kerl failed - silently ignore
            LOGGER.debug("kerl list installations failed: " + e.getMessage());
        }
    }

    private static boolean isCommandAvailable(@NotNull String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("which", command);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
