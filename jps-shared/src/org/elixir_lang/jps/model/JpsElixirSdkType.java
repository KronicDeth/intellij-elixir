package org.elixir_lang.jps.model;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

/**
 * Created by zyuyou on 2015/5/27.
 */
public class JpsElixirSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement>{
  public static final JpsElixirSdkType INSTANCE = new JpsElixirSdkType();

  public static final String SCRIPT_INTERPRETER = "elixir";
  public static final String BYTECODE_COMPILER = "elixirc";
  public static final String ELIXIR_TOOL_MIX = "mix";
  public static final String ELIXIR_TOOL_IEX = "iex";

  @NotNull
  public static File getScriptInterpreterExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, SCRIPT_INTERPRETER);
  }

  @NotNull
  public static File getByteCodeCompilerExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, BYTECODE_COMPILER);
  }

  @NotNull
  public static File getMixExecutable(@NotNull String sdkHome){
    return getSdkExecutable(sdkHome, ELIXIR_TOOL_MIX);
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
  public static String getExecutableFileName(@NotNull String executableName){
    return SystemInfo.isWindows ? executableName + ".bat" : executableName;
  }

  @NotNull
  @Override
  public JpsDummyElement createDefaultProperties() {
    return JpsElementFactory.getInstance().createDummyElement();
  }
}
