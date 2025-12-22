package org.elixir_lang.jps.sdk_type;

import org.elixir_lang.jps.HomePath;
import org.elixir_lang.jps.model.SdkProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

import static org.elixir_lang.jps.SdkType.exeFileToExePath;

public class Elixir extends JpsSdkType<SdkProperties> implements JpsElementTypeWithDefaultProperties<SdkProperties>{
  public static final Elixir INSTANCE = new Elixir();

  public static final String SCRIPT_INTERPRETER = "elixir";
  public static final String ELIXIR_TOOL_MIX = "mix";
  private static final String BYTECODE_COMPILER = "elixirc";
  private static final String ELIXIR_TOOL_IEX = "iex";

  @NotNull
  public static File getScriptInterpreterExecutable(@NotNull String sdkHome){
    return new File(new File(sdkHome, "bin").getAbsolutePath(), HomePath.getExecutableFileName(sdkHome, SCRIPT_INTERPRETER, ".bat"));
  }

  @NotNull
  public static File getByteCodeCompilerExecutable(@NotNull String sdkHome){
    return new File(new File(sdkHome, "bin").getAbsolutePath(), HomePath.getExecutableFileName(sdkHome, BYTECODE_COMPILER, ".bat"));
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
  public static File getIExExecutable(@NotNull String sdkHome){
    return new File(new File(sdkHome, "bin").getAbsolutePath(), HomePath.getExecutableFileName(sdkHome, ELIXIR_TOOL_IEX, ".bat"));
  }

  /**
   * Calculates the MIX_HOME directory path for the given SDK.
   * <p>
   * MIX_HOME is only calculated for asdf and mise installations, where it should be set to
   * the .mix directory in the version-specific installation directory.
   * For example: /home/user/.local/share/mise/installs/elixir/1.18.4-otp-28/.mix
   * <p>
   * For other installation types (system installs, custom locations), returns null to avoid
   * overriding user-configured MIX_HOME values. Fixes #3746
   *
   * @param maybeHomePath the SDK home path (may be null)
   * @return the calculated MIX_HOME path, or null if not applicable
   * @throws MissingHomePath if the home path is null
   */
  @Nullable
  private static String mixHome(@Nullable String maybeHomePath) throws MissingHomePath {
    String homePath = ensureHomePath(maybeHomePath);
    String source = org.elixir_lang.jps.HomePath.detectSource(homePath);

    // Only set MIX_HOME for asdf and mise installations
    // These version managers install to version-specific directories and expect MIX_HOME
    // to be set relative to the installation directory
    if ("asdf".equals(source) || "mise".equals(source)) {
      File mixFile = mixFile(homePath);
      // MIX_HOME = $(dirname mixPath)/../.mix
      return new File(mixFile.getParentFile().getParent(), ".mix").getAbsolutePath();
    }

    return null;
  }

  /**
   * Determines whether an existing MIX_HOME environment variable should be replaced.
   * <p>
   * Only replaces if the existing MIX_HOME is from asdf or mise. This preserves
   * user-configured MIX_HOME values for custom installation locations.
   *
   * @param existingMixHome the current MIX_HOME value (may be null)
   * @return true if the existing MIX_HOME should be replaced, false otherwise
   */
  private static boolean shouldReplaceMixHome(@Nullable String existingMixHome) {
    if (existingMixHome == null) {
      return true;  // No existing value, safe to set
    }

    // Check if the existing MIX_HOME path is from asdf or mise
    String posixPath = com.intellij.openapi.util.io.FileUtil.toSystemIndependentName(existingMixHome);
    return posixPath.contains("/.local/share/mise/installs/") || posixPath.contains("/.asdf/installs/");
  }

  /**
   * Updates the MIX_HOME environment variable if appropriate for asdf/mise installations.
   * <p>
   * This method handles the MIX_HOME environment variable for Elixir installations managed
   * by asdf or mise version managers. These tools install Elixir to version-specific
   * directories, and Mix expects MIX_HOME to point to a .mix directory within that
   * installation path.
   * <p>
   * The method will:
   * <ul>
   *   <li>Set MIX_HOME for asdf/mise installations if not already set</li>
   *   <li>Replace existing MIX_HOME only if it's also from asdf/mise</li>
   *   <li>Preserve user-configured MIX_HOME for custom installations</li>
   * </ul>
   *
   * @param environment the environment map to update
   * @param maybeHomePath the SDK home path (may be null)
   */
  public static void maybeUpdateMixHome(@NotNull java.util.Map<String, String> environment, @Nullable String maybeHomePath) {
    try {
      String mixHome = mixHome(maybeHomePath);
      if (mixHome != null) {
        String existingMixHome = environment.get("MIX_HOME");
        if (shouldReplaceMixHome(existingMixHome)) {
          environment.put("MIX_HOME", mixHome);
          environment.put("MIX_ARCHIVES", mixHome + File.separator + "archives");
        }
      }
    } catch (MissingHomePath e) {
      // Silently ignore - SDK home path is missing
    }
  }

  @NotNull
  @Override
  public SdkProperties createDefaultProperties() {
    return new SdkProperties(null);
  }

  @Nullable
  public static String homePathToElixirExePath(@NotNull String elixirHomePath) {
    File elixirFile = getScriptInterpreterExecutable(elixirHomePath);

    String elixirExePath;

    try {
      elixirExePath = exeFileToExePath(elixirFile);
    } catch (FileNotFoundException | AccessDeniedException e) {
      elixirExePath = null;
    }

    return elixirExePath;
  }
}
