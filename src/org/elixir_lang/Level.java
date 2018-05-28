package org.elixir_lang;

import com.intellij.openapi.util.Key;
import gnu.trove.THashMap;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.sdk.elixir.Release;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public enum Level {
    V_1_1(1, 1, true, false, "to_char_list", false),
    V_1_2(1, 2, true, true, "to_char_list", false),
    V_1_3(1, 3, false, true, "to_charlist", false),
    V_1_4(1, 4, false, true, "to_charlist", true),
    V_1_5(1, 5, false, true, "to_charlist", true),
    V_1_6(1, 6, false, true, "to_charlist", true),
    V_1_7(1, 7, false, true, "to_charlist", true);

    public static final Key<Level> KEY = new Key<>("elixir.level");

    private static final Map<Integer, Map<Integer, Level>> VALUE_BY_MINOR_BY_MAJOR = new THashMap<>();
    private static final Map<Integer, IntRange>  MINOR_RANGE_BY_MAJOR = new THashMap<>();
    private static final int MAJOR_MINIMUM;
    private static final int MAJOR_MAXIMUM;
    private static final Level MINIMUM;
    public static final Level MAXIMUM;

    static {
        Level[] values = values();

        MINIMUM = values[0];
        MAXIMUM = values[values.length - 1];
        MAJOR_MINIMUM = MINIMUM.major;
        MAJOR_MAXIMUM = MAXIMUM.major;

        for (Level value : values) {
            int major = value.major;

            MINOR_RANGE_BY_MAJOR.compute(major, (key, minorRange) -> expand(minorRange, value.minor));
            VALUE_BY_MINOR_BY_MAJOR.computeIfAbsent(major, key -> new THashMap<>()).put(value.minor, value);
        }
    }

    private final int major;
    private final int minor;
    public final boolean supportsOpenHexadecimalEscapeSequence;
    public final boolean supportsMultipleAliases;
    public final String quoteBinaryFunctionIdentifier;
    public final boolean supportsMixTestFormatterFlag;

    Level(int major, int minor,
          boolean supportsOpenHexadecimalEscapeSequence,
          boolean supportsMultipleAliases,
          @NotNull String quoteBinaryFunctionIdentifier,
          boolean supportsMixTestFormatterFlag) {
        this.major = major;
        this.minor = minor;
        this.supportsOpenHexadecimalEscapeSequence = supportsOpenHexadecimalEscapeSequence;
        this.supportsMultipleAliases = supportsMultipleAliases;
        this.quoteBinaryFunctionIdentifier = quoteBinaryFunctionIdentifier;
        this.supportsMixTestFormatterFlag = supportsMixTestFormatterFlag;
    }

    @NotNull
    private static IntRange expand(@Nullable IntRange range, int value) {
        IntRange expandedRange;

        if (range != null) {
            int minorMinimum = range.getMinimumInteger();
            int minorMaximum = range.getMaximumInteger();

            if (value < minorMinimum) {
                expandedRange = new IntRange(value, minorMaximum);
            } else if (minorMaximum < value) {
                expandedRange = new IntRange(minorMinimum, value);
            } else {
                expandedRange = range;
            }
        } else {
            expandedRange = new IntRange(value, value);
        }

        return expandedRange;
    }

    @NotNull
    private static Optional<Integer> releaseToMajor(@NotNull Release release) {
        Optional<Integer> major;

        try {
            major = Optional.of(Integer.parseUnsignedInt(release.major));
        } catch (NumberFormatException numberFormatException) {
            major = Optional.empty();
        }

        return major;
    }

    @NotNull
    public static Level fromRelease(@NotNull Release release) {
        Level level;

        Optional<Integer> optionalMajor = releaseToMajor(release);

        if (optionalMajor.isPresent()) {
            int major = optionalMajor.get();

            if (major < MAJOR_MINIMUM) {
                level = MINIMUM;
            } else if (major > MAJOR_MAXIMUM) {
                level = MAXIMUM;
            } else {
                @NotNull IntRange minorRange = MINOR_RANGE_BY_MAJOR.get(major);
                int minorMinimum = minorRange.getMinimumInteger();
                int minorMaximum = minorRange.getMaximumInteger();

                Optional<Integer> optionalMinor = releaseToMinor(release);

                if (optionalMinor.isPresent()) {
                    int minor = optionalMinor.get();

                    if (minor < minorMinimum) {
                        level = VALUE_BY_MINOR_BY_MAJOR.get(major).get(minorMinimum);
                    } else if (minor > minorMaximum) {
                        level = VALUE_BY_MINOR_BY_MAJOR.get(major).get(minorMaximum);
                    } else {
                        level = VALUE_BY_MINOR_BY_MAJOR.get(major).get(minor);
                    }
                } else {
                    level = VALUE_BY_MINOR_BY_MAJOR.get(major).get(minorMaximum);
                }


            }
        } else {
            level = MAXIMUM;
        }

        return level;
    }

    @NotNull
    private static Optional<Integer> releaseToMinor(@NotNull Release release) {
        Optional<Integer> minor;
        @Nullable String releaseMinor = release.minor;

        if (releaseMinor != null) {
            try {
                minor = Optional.of(Integer.parseUnsignedInt(release.minor));
            } catch (NumberFormatException numberFormatException) {
                minor = Optional.empty();
            }
        } else {
            minor = Optional.empty();
        }

        return minor;
    }
}
