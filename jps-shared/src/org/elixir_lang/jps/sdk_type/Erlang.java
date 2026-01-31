package org.elixir_lang.jps.sdk_type;

import org.elixir_lang.jps.HomePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

import static org.elixir_lang.jps.SdkType.exeFileToExePath;

public class Erlang extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
    private static final String BYTECODE_INTERPRETER = "erl";
    public static final Erlang INSTANCE = new Erlang();

    @NotNull
    public static File getByteCodeInterpreterExecutable(@NotNull String sdkHome) {
        return new File(new File(sdkHome, "bin"), HomePath.getExecutableFileName(sdkHome, BYTECODE_INTERPRETER, ".exe"));
    }

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
        return JpsElementFactory.getInstance().createDummyElement();
    }

    @NotNull
    public static String homePathToErlExePath(@NotNull String erlangHomePath) throws FileNotFoundException, AccessDeniedException {
        File erlFile = getByteCodeInterpreterExecutable(erlangHomePath);
        return exeFileToExePath(erlFile);
    }
}
