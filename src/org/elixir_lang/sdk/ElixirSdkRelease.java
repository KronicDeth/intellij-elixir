package org.elixir_lang.sdk;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ElixirSdkRelease implements Comparable<ElixirSdkRelease> {
  /*
   * CONSTANTS
   */

  public static final ElixirSdkRelease V_1_0_4 = new ElixirSdkRelease("1", "0", "4", null, null);
  public static final ElixirSdkRelease V_1_2 = new ElixirSdkRelease("1", "2", null, null, null);
  public static final ElixirSdkRelease V_1_4 = new ElixirSdkRelease("1", "4", null, null, null);


  private static final Pattern VERSION_PATTERN = Pattern.compile(
          // @version_regex from Version in elixir itself
          "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\-([\\d\\w\\.\\-]+))?(?:\\+([\\d\\w\\-]+))?"
  );

  /*
   *
   * Static Methods
   *
   */

  /*
   * Public Static Methods
   */

  @Nullable
  public static ElixirSdkRelease fromString(@Nullable String versionString){
    Matcher m = versionString != null ? VERSION_PATTERN.matcher(versionString) : null;
    return m != null && m.matches() ? new ElixirSdkRelease(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)) : null;
  }

  /*
   * Private Static Methods
   */

  @Contract(pure = true)
  private static int compareMaybeFormattedDecimals(@Nullable String mine, @Nullable String others) {
    int comparison;

    if (mine == null && others == null) {
      comparison = 0;
    } else if (mine == null && others != null) {
      comparison = -1;
    } else if (mine != null && others == null) {
      comparison = 1;
    } else {
      try {
        int myInt = Integer.parseInt(mine);

        try {
          int othersInt = Integer.parseInt(others);

          if (myInt > othersInt) {
            comparison = 1;
          } else if (myInt < othersInt) {
            comparison = -1;
          } else {
            comparison = 0;
          }
        } catch (NumberFormatException numberFormatException) {
          comparison = mine.compareTo(others);
        }
      } catch (NumberFormatException numberFormatException) {
        comparison = mine.compareTo(others);
      }
    }

    return comparison;
  }

  /**
   * @see <a href="https://github.com/elixir-lang/elixir/blob/27c350da06ee4df5a4710507abe443ffba5b07dd/lib/elixir/lib/version.ex#L203-L206">Version.to_compare</a>
   */
  @Contract(pure = true)
  private static int comparePre(@Nullable String mine, @Nullable String others) {
    int comparison;

    if (mine == null && others == null) {
      comparison = 0;
    } else if (mine == null && others != null) {
      // https://github.com/elixir-lang/elixir/blob/27c350da06ee4df5a4710507abe443ffba5b07dd/lib/elixir/lib/version.ex#L203
      comparison = 1;
    } else if (mine != null && others == null) {
      // https://github.com/elixir-lang/elixir/blob/27c350da06ee4df5a4710507abe443ffba5b07dd/lib/elixir/lib/version.ex#L204
      comparison = -1;
    } else {
      comparison = mine.compareTo(others);
    }

    return comparison;
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
  public int compareTo(@NotNull ElixirSdkRelease other) {
    int comparison = compareMaybeFormattedDecimals(major, other.major);

    if (comparison == 0) {
      comparison = compareMaybeFormattedDecimals(minor, other.minor);

      if (comparison == 0) {
        comparison = compareMaybeFormattedDecimals(patch, other.patch);

        if (comparison == 0) {
          comparison = comparePre(pre, other.pre);
        }
      }
    }

    return comparison;
  }

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
