package org.elixir_lang.jps.sdk_type;

import com.intellij.openapi.util.io.FileUtil;
import org.elixir_lang.jps.model.SdkProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;
import java.util.Locale;

public class Elixir extends JpsSdkType<SdkProperties> implements JpsElementTypeWithDefaultProperties<SdkProperties> {
    public static final Elixir INSTANCE = new Elixir();

    public static final String SCRIPT_INTERPRETER = "elixir";
    public static final String ELIXIR_TOOL_MIX = "mix";
    private static final String BYTECODE_COMPILER = "elixirc";
    private static final String ELIXIR_TOOL_IEX = "iex";
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");

    @NotNull
    public static File getScriptInterpreterExecutable(@NotNull String sdkHome) {
        return getScriptInterpreterExecutable(sdkHome, null);
    }

    @NotNull
    public static File getScriptInterpreterExecutable(@NotNull String sdkHome, @Nullable SdkProperties sdkProperties) {
        return new File(new File(sdkHome, "bin").getAbsolutePath(), getExecutableFileName(sdkProperties, SCRIPT_INTERPRETER, ".bat"));
    }

    @NotNull
    public static File getByteCodeCompilerExecutable(@NotNull String sdkHome, @Nullable SdkProperties sdkProperties) {
        return new File(new File(sdkHome, "bin").getAbsolutePath(), getExecutableFileName(sdkProperties, BYTECODE_COMPILER, ".bat"));
    }

    @NotNull
    public static String mixPath(@NotNull JpsSdk<SdkProperties> sdk) throws MissingHomePath {
        String maybeHomePath = sdk.getHomePath();
        return mixPath(maybeHomePath);
    }

    @NotNull
    public static String mixPath(@Nullable String maybeHomePath) throws MissingHomePath {
        String homePath = ensureHomePath(maybeHomePath);

        return mixFile(homePath).getPath();
    }

    @NotNull
    private static String ensureHomePath(@Nullable String homePath) throws MissingHomePath {
        if (homePath == null) {
            throw new MissingHomePath();
        }

        return homePath;
    }

    @NotNull
    public static File mixFile(@NotNull String sdkHome) {
        return new File(new File(sdkHome, "bin").getAbsolutePath(), ELIXIR_TOOL_MIX);
    }

    @NotNull
    public static File getIExExecutable(@NotNull String sdkHome, @Nullable SdkProperties sdkProperties) {
        return new File(new File(sdkHome, "bin").getAbsolutePath(), getExecutableFileName(sdkProperties, ELIXIR_TOOL_IEX, ".bat"));
    }

    public static void maybeUpdateMixHome(@NotNull java.util.Map<String, String> environment,
                                          @Nullable SdkProperties sdkProperties) {
        if (sdkProperties == null) {
            return;
        }

        String mixHome = sdkProperties.getMixHome();
        if (mixHome == null) {
            return;
        }

        String existingMixHome = environment.get("MIX_HOME");
        if (shouldReplaceMixHome(existingMixHome, sdkProperties.getMixHomeReplacePrefix())) {
            environment.put("MIX_HOME", mixHome);
            environment.put("MIX_ARCHIVES", mixHome + File.separator + "archives");
        }
    }

    private static boolean shouldReplaceMixHome(@Nullable String existingMixHome, @Nullable String replacePrefix) {
        if (existingMixHome == null) {
            return true;
        }
        if (replacePrefix == null) {
            return false;
        }

        String posixPath = FileUtil.toSystemIndependentName(existingMixHome);
        return posixPath.contains(replacePrefix);
    }

    @NotNull
    private static String getExecutableFileName(@Nullable SdkProperties sdkProperties,
                                                @NotNull String executableName,
                                                @NotNull String windowsExt) {
        if (sdkProperties != null && sdkProperties.isWslUncPath()) {
            return executableName;
        }
        return IS_WINDOWS ? executableName + windowsExt : executableName;
    }

    @NotNull
    @Override
    public SdkProperties createDefaultProperties() {
        return new SdkProperties(null, null, null, false);
    }
}
