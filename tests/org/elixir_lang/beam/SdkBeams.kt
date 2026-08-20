package org.elixir_lang.beam

import java.io.File

/** A single `.beam` shipped by a resolved SDK, paired with a stable `"<tag>/<app>/<module>.beam"` label. */
data class SdkBeam(val label: String, val file: File)

/**
 * Enumerates every `.beam` an SDK ships under `<root>/lib/<app>/ebin/`, sorted by app then module.
 *
 * Shared by the two SDK-wide sweeps so their beam discovery can't drift apart:
 * [SdkBeamParseTest] turns each [SdkBeam] into its own parameterized case (platform-free, so one
 * cheap instance per beam), while [SdkDecompileParseableTest] loops over them inside a single
 * [org.elixir_lang.PlatformTestCase] (the decompile-then-parse path needs the platform fixture,
 * whose per-instance setup is too expensive to pay per beam - so it is amortized across one sweep).
 */
object SdkBeams {
    fun forSdk(root: String?, tag: String): List<SdkBeam> {
        if (root.isNullOrEmpty()) return emptyList()

        return File(root, "lib").listFiles().orEmpty()
            .sortedBy { it.name }
            .flatMap { app ->
                File(app, "ebin").listFiles { f -> f.name.endsWith(".beam") }.orEmpty()
                    .sortedBy { it.name }
                    .map { beam -> SdkBeam("$tag/${app.name}/${beam.name}", beam) }
            }
    }
}
