package org.elixir_lang.sdk.elixir;

import org.elixir_lang.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Release implements Comparable<Release> {
  /*
   * CONSTANTS
   */

  public static final Release V_1_0_4 = new Release("1", "0", "4", null, null);
  public static final Release LATEST = new Release("1", "6", "0", "dev", null);

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
  public static Release fromString(@Nullable String versionString){
    Matcher m = versionString != null ? VERSION_PATTERN.matcher(versionString) : null;
    return m != null && m.matches() ? new Release(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)) : null;
  }

  /*
   * Private Static Methods
   */

  @Contract(pure = true)
  private static int compareMaybeFormattedDecimals(@Nullable String mine, @Nullable String others) {
    int comparison;

    if (mine == null && others == null) {
      comparison = 0;
    } else if (mine == null) {
      comparison = -1;
    } else if (others == null) {
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
    } else if (mine == null) {
      // https://github.com/elixir-lang/elixir/blob/27c350da06ee4df5a4710507abe443ffba5b07dd/lib/elixir/lib/version.ex#L203
      comparison = 1;
    } else if (others == null) {
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
  public final String major;
  @Nullable
  public final String minor;
  @Nullable
  private final String patch;
  @Nullable
  private final String pre;
  @NotNull
  private final Level level;

  /*
   * Constructors
   */

  public Release(@NotNull String major,
                 @Nullable String minor,
                 @Nullable String patch,
                 @Nullable String pre,
                 @Nullable String build) {
    this(major, minor, patch, pre, build, null);
  }

  public Release(@NotNull String major,
                 @Nullable String minor,
                 @Nullable String patch,
                 @Nullable String pre,
                 @Nullable String build,
                 @Nullable Level level) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.pre = pre;
    this.build = build;

    if (minor == null && patch != null) {
      throw new IllegalArgumentException("patch MUST be null if minor is null");
    }

    if (level != null) {
      this.level = level;
    } else {
      this.level = Level.fromRelease(this);
    }
  }

  /*
   * Instance Methods
   */

  @Override
  public int compareTo(@NotNull Release other) {
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

  @Contract(pure = true)
  @NotNull
  public Level level() {
    return level;
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
