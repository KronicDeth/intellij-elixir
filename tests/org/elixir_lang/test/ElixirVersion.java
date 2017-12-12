package org.elixir_lang.test;

import org.elixir_lang.Level;
import org.elixir_lang.sdk.elixir.Release;
import org.jetbrains.annotations.NotNull;

import static junit.framework.TestCase.assertNotNull;

public class ElixirVersion {
    @NotNull
    public static Level elixirSdkLevel() {
        return elixirSdkRelease().level();
    }

    @NotNull
    public static Release elixirSdkRelease() {
        String elixirVersion = elixirVersion();
        Release elixirSdkRelease = Release.fromString((elixirVersion));

        assertNotNull(
                "ELIXIR_VERSION (" + elixirVersion  + ") could not be parsed into an ElixirSdkRelease",
                elixirSdkRelease
        );

        return elixirSdkRelease;
    }

    private static String elixirVersion() {
        String elixirVersion = System.getenv("ELIXIR_VERSION");

        assertNotNull("ELIXIR_VERSION is not set", elixirVersion);

        return elixirVersion;
    }
}
