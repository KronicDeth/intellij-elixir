package org.elixir_lang.test;

import org.elixir_lang.sdk.elixir.Release;

import static junit.framework.TestCase.assertNotNull;

public class ElixirVersion {
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
