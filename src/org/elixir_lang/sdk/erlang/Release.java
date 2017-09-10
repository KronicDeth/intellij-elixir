package org.elixir_lang.sdk.erlang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Release {
    private static final Pattern VERSION_PATTERN = Pattern.compile("Erlang/OTP (\\S+) \\[erts-(\\S+)\\]");

    @NotNull
    final String otpRelease;
    @NotNull
    private final String ertsVersion;

    Release(@NotNull String otpRelease, @NotNull String ertsVersion) {
        this.otpRelease = otpRelease;
        this.ertsVersion = ertsVersion;
    }

    @Nullable
    static Release fromString(@Nullable String versionString) {
        Release release = null;

        if (versionString != null) {
            Matcher matcher = VERSION_PATTERN.matcher(versionString);

            if (matcher.matches()) {
                release = new Release(matcher.group(1), matcher.group(2));
            }
        }

        return release;
    }

    @NotNull
    @Override
    public String toString() {
        return "Erlang/OTP " + otpRelease + " [erts-" + ertsVersion + "]";
    }
}
