package org.elixir_lang.jps.builder.sdk_type;

import org.elixir_lang.jps.builder.model.SdkProperties;
import org.elixir_lang.jps.shared.sdk.SdkPaths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

public class Elixir extends JpsSdkType<SdkProperties> implements JpsElementTypeWithDefaultProperties<SdkProperties> {
    public static final Elixir INSTANCE = new Elixir();

    public static final String ELIXIR_TOOL_MIX = "mix";
    private static final String BYTECODE_COMPILER = "elixirc";

    @NotNull
    public static File getByteCodeCompilerExecutable(@NotNull String sdkHome) {
        return new File(new File(sdkHome, "bin").getAbsolutePath(), SdkPaths.getExecutableFileName(BYTECODE_COMPILER, ".bat"));
    }

    @NotNull
    public static File mixExecutableFile(@NotNull String sdkHome) {
        // Use when invoking mix directly; includes .bat wrapper on Windows.
        return new File(SdkPaths.getExecutableFileName(mixFile(sdkHome).getAbsolutePath(), ".bat"));
    }

    @NotNull
    public static File mixFile(@NotNull String sdkHome) {
        // mix is passed as an erl argument; keep the script path without a .bat wrapper.
        return new File(new File(sdkHome, "bin").getAbsolutePath(), ELIXIR_TOOL_MIX);
    }

    public static void maybeUpdateMixHome(@NotNull java.util.Map<String, String> environment,
                                          @Nullable String homePath) {
        SdkPaths.maybeUpdateMixHome(environment, homePath);
    }

    @NotNull
    @Override
    public SdkProperties createDefaultProperties() {
        return new SdkProperties(null);
    }
}
