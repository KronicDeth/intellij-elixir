package org.elixir_lang.jps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Version;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePath {
    private static final String HEAD_PREFIX = "HEAD-";
    public static final String LINUX_DEFAULT_HOME_PATH = "/usr/local/lib";
    public static final Version UNKNOWN_VERSION = new Version(0, 0, 0);
    private static final File HOMEBREW_ROOT = new File("/usr/local/Cellar");
    private static final File NIX_STORE = new File("/nix/store/");
    private static final Logger LOGGER = Logger.getInstance(HomePath.class);

    private HomePath() {
    }

    public static void eachEbinPath(@NotNull String homePath, @NotNull Consumer<Path> ebinPathConsumer) {
        Path lib = Paths.get(homePath, "lib");

        try (DirectoryStream<Path> libDirectoryStream = Files.newDirectoryStream(lib)) {
            libDirectoryStream.forEach(
                    app -> {
                        try (DirectoryStream<Path> ebinDirectoryStream = Files.newDirectoryStream(app, "ebin")) {
                            ebinDirectoryStream.forEach(ebinPathConsumer);
                        } catch (IOException ioException) {
                            LOGGER.error(ioException);
                        }
                    }
            );
        } catch (IOException ioException) {
            LOGGER.error(ioException);
        }
    }

    @NotNull
    public static Pattern nixPattern(@NotNull String name) {
        return Pattern.compile(".+-" + name + "-(\\d+)\\.(\\d+)\\.(\\d+)");
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
        if (NIX_STORE.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            NIX_STORE.listFiles(
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
            mergeNameSubdirectories(homePathByVersion, new File(userHome), "otp", versionPathToHomePath);
        }
    }

    @Contract(pure = true)
    @NotNull
    public static Map<Version, String> homePathByVersion() {
        return new TreeMap<>(
                (version1, version2) -> {
                    // compare version2 to version1 to produce descending instead of ascending order.
                    return version2.compareTo(version1);
                }
        );
    }
}
