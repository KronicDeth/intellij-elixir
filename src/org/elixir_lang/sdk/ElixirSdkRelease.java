package org.elixir_lang.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 2015/5/27.
 *
 */
public final class ElixirSdkRelease {
  public static final ElixirSdkRelease V_1_0_0 = new ElixirSdkRelease("1", "0", "0");
  public static final ElixirSdkRelease V_1_0_1 = new ElixirSdkRelease("1", "0", "1");
  public static final ElixirSdkRelease V_1_0_2 = new ElixirSdkRelease("1", "0", "2");
  public static final ElixirSdkRelease V_1_0_3 = new ElixirSdkRelease("1", "0", "3");
  public static final ElixirSdkRelease V_1_0_4 = new ElixirSdkRelease("1", "0", "4");
  public static final ElixirSdkRelease V_1_0_5 = new ElixirSdkRelease("1", "0", "5");

  private static final Pattern VERSION_PATTERN = Pattern.compile("Elixir (\\d+)\\.(\\d+)\\.(\\d+)");

  private final String myRelease;

  public ElixirSdkRelease(@NotNull String release, @NotNull String version, @NotNull String fix){
    myRelease = release + "." + version + "." + fix;
  }

  @NotNull
  public String getRelease(){
    return myRelease;
  }

  @Override
  public String toString() {
    return "Elixir " + getRelease();
  }

  @Nullable
  public static ElixirSdkRelease fromString(@Nullable String versionString){
    Matcher m = versionString != null ? VERSION_PATTERN.matcher(versionString) : null;
    return m != null && m.matches() ? new ElixirSdkRelease(m.group(1), m.group(2), m.group(3)) : null;
  }
}
