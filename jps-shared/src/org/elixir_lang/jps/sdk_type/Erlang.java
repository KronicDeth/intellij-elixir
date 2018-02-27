package org.elixir_lang.jps.sdk_type;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

public class Erlang extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
    private static final String BYTECODE_INTERPRETER = "erl";

    @NotNull
    public static File getByteCodeInterpreterExecutable(@NotNull String sdkHome) {
        return getSdkExecutable(sdkHome, BYTECODE_INTERPRETER);
    }

    @NotNull
    private static File getSdkExecutable(@NotNull String sdkHome, @NotNull String command) {
        return new File(new File(sdkHome, "bin"), getExecutableFileName(command));
    }

    @NotNull
    public static String getExecutableFileName(@NotNull String executableName) {
        return SystemInfo.isWindows ? executableName + ".exe" : executableName;
    }

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
        return JpsElementFactory.getInstance().createDummyElement();
    }
}
