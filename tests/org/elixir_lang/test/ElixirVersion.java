package org.elixir_lang.test;

import org.elixir_lang.sdk.ElixirSdkRelease;

import static junit.framework.TestCase.assertNotNull;

public class ElixirVersion {
    public static ElixirSdkRelease elixirSdkRelease() {
        String elixirVersion = elixirVersion();
        ElixirSdkRelease elixirSdkRelease = ElixirSdkRelease.fromString((elixirVersion));

        assertNotNull(
                "ELIXIR_VERSION (" + elixirVersion  + ") could not be parsed into an ElixirSdkRelease",
                elixirSdkRelease
        );

        return elixirSdkRelease;
    }

    public static String elixirVersion() {
        String elixirVersion = System.getenv("ELIXIR_VERSION");

        assertNotNull("ELIXIR_VERSION is not set", elixirVersion);

        return elixirVersion;
    }
}
