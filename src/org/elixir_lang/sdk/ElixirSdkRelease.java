package org.elixir_lang.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ElixirSdkRelease {
  /*
   * CONSTANTS
   */

  public static final ElixirSdkRelease V_1_0_4 = new ElixirSdkRelease("1", "0", "4", null, null);

  private static final Pattern VERSION_PATTERN = Pattern.compile(
          // @version_regex from Version in elixir itself
          "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\-([\\d\\w\\.\\-]+))?(?:\\+([\\d\\w\\-]+))?"
  );

  /*
   * Static Methods
   */

  @Nullable
  public static ElixirSdkRelease fromString(@Nullable String versionString){
    Matcher m = versionString != null ? VERSION_PATTERN.matcher(versionString) : null;
    return m != null && m.matches() ? new ElixirSdkRelease(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)) : null;
  }

  /*
   * Fields
   */

  @Nullable
  private final String build;
  @NotNull
  private final String major;
  @Nullable
  private final String minor;
  @Nullable
  private final String patch;
  @Nullable
  private final String pre;

  /*
   * Constructors
   */

  public ElixirSdkRelease(@NotNull String major,
                          @Nullable String minor,
                          @Nullable String patch,
                          @Nullable String pre,
                          @Nullable String build) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.pre = pre;
    this.build = build;

    if (minor == null && patch != null) {
      throw new IllegalArgumentException("patch MUST be null if minor is null");
    }
  }

  /*
   * Instance Methods
   */

  @Override
  public String toString() {
    return "Elixir " + version();
  }

  @NotNull
  public String version(){
    StringBuilder version = new StringBuilder(major);

    if (minor != null) {
      version.append('.').append(minor);
    }

    if (patch != null) {
      version.append('.').append(patch);
    }

    if (pre != null) {
      version.append('-').append(pre);
    }

    if (build != null) {
      version.append('+').append(build);
    }

    return version.toString();
  }
}
