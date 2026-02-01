package org.elixir_lang.jps.sdk_type;

import com.intellij.util.system.OS;
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
    private static final boolean IS_WINDOWS = OS.CURRENT == OS.Windows;
    public static final Erlang INSTANCE = new Erlang();

    @NotNull
    public static File getByteCodeInterpreterExecutable(@NotNull String sdkHome, boolean wslUncPath) {
        return new File(new File(sdkHome, "bin"), getExecutableFileName(wslUncPath, BYTECODE_INTERPRETER, ".exe"));
    }

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
        return JpsElementFactory.getInstance().createDummyElement();
    }

    @NotNull
    public static String homePathToErlExePath(@NotNull String erlangHomePath, boolean wslUncPath)
            throws FileNotFoundException, AccessDeniedException {
        File erlFile = getByteCodeInterpreterExecutable(erlangHomePath, wslUncPath);
        return exeFileToExePath(erlFile);
    }

    @NotNull
    private static String getExecutableFileName(boolean wslUncPath, @NotNull String executableName, @NotNull String windowsExt) {
        if (wslUncPath) {
            return executableName;
        }
        return IS_WINDOWS ? executableName + windowsExt : executableName;
    }
}
