package org.elixir_lang.jps.sdk_type;

import com.intellij.openapi.util.SystemInfo;
import org.elixir_lang.jps.model.SdkProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

import static org.elixir_lang.jps.SdkType.exeFileToExePath;

public class Elixir extends JpsSdkType<SdkProperties> implements JpsElementTypeWithDefaultProperties<SdkProperties>{
  public static final Elixir INSTANCE = new Elixir();

  public static final String SCRIPT_INTERPRETER = "elixir";
  public static final String ELIXIR_TOOL_MIX = "mix";
  private static final String BYTECODE_COMPILER = "elixirc";
  private static final String ELIXIR_TOOL_IEX = "iex";

  @NotNull
  public static File getScriptInterpreterExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, SCRIPT_INTERPRETER);
  }

  @NotNull
  public static File getByteCodeCompilerExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, BYTECODE_COMPILER);
  }

  @NotNull
  public static String getMixScript(@Nullable JpsSdk<SdkProperties> sdk) {
    String mixPath;

    if (sdk != null) {
      String homePath = sdk.getHomePath();

      if (homePath != null) {
        mixPath = getMixScript(homePath).getPath();
      } else {
        mixPath = ELIXIR_TOOL_MIX;
      }
    } else {
      mixPath = ELIXIR_TOOL_MIX;
    }

    return mixPath;
  }

  @NotNull
  public static File getMixScript(@NotNull String sdkHome) {
    return getSdkScript(sdkHome, ELIXIR_TOOL_MIX);
  }

  @NotNull
  public static File getIExExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, ELIXIR_TOOL_IEX);
  }

  @NotNull
  private static File getSdkExecutable(@NotNull String sdkHome, @NotNull String command){
    return new File(new File(sdkHome, "bin").getAbsolutePath(), getExecutableFileName(command));
  }

  @NotNull
  private static File getSdkScript(@NotNull String sdkHome, @NotNull String command) {
    return new File(new File(sdkHome, "bin").getAbsolutePath(), command);
  }

  @NotNull
  public static String getExecutableFileName(@NotNull String executableName){
    return SystemInfo.isWindows ? executableName + ".bat" : executableName;
  }

  @NotNull
  @Override
  public SdkProperties createDefaultProperties() {
    return new SdkProperties(null);
  }

  @Nullable
  public static String homePathToElixirExePath(@NotNull String elixirHomePath) {
      File elixirFile = getScriptInterpreterExecutable(elixirHomePath);
      return exeFileToExePath(elixirFile);
  }
}
