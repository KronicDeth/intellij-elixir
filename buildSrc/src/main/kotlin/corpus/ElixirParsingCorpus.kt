package corpus

import groovy.json.JsonSlurper
import sdk.versionWithoutBuildTag

/**
 * A repository at one commit whose `.ex` and `.exs` files the parser tests parse and quote, as a `beam` pair's
 * `corpus` in `.github/ci-versions.json` declares it.
 */
data class CorpusEntry(val git: String, val sha: String) {
    private val repository: List<String> = GITHUB_REPOSITORY.matchEntire(git)?.groupValues
        ?: throw IllegalArgumentException(
            "corpus git '$git' is not https://github.com/<owner>/<repository>: only GitHub archives can be downloaded"
        )

    init {
        require(SHA.matches(sha)) { "corpus sha '$sha' of $git is not a full 40-character commit SHA" }
    }

    val owner: String get() = repository[1]
    val name: String get() = repository[2]
    val archiveUrl: String get() = "https://codeload.github.com/$owner/$name/tar.gz/$sha"
    val archiveFileName: String get() = "$owner-$name-$sha.tar.gz"

    /** Where the entry's files sit under the corpus root, and so how each test's name begins. */
    val directory: String get() = "$owner/$name@${sha.take(12)}"

    private companion object {
        val GITHUB_REPOSITORY = Regex("https://github\\.com/([A-Za-z0-9_.-]+)/([A-Za-z0-9_.-]+)")
        val SHA = Regex("[0-9a-f]{40}")
    }
}

/**
 * The corpus declared for [elixirVersion], empty when no pair declares one. Chosen by Elixir alone, since the
 * corpus is Elixir's own source: pairs of one Elixir that declare different corpora are an error.
 */
fun corpusFor(declaration: String, elixirVersion: String): List<CorpusEntry> {
    val beam = (JsonSlurper().parseText(declaration) as Map<*, *>)["beam"] as Map<*, *>
    val pairs = listOf(beam["baseline"]) + (beam["additional"] as List<*>? ?: emptyList<Any>())
    val corpora = pairs
        .map { it as Map<*, *> }
        .filter { versionWithoutBuildTag(it["elixir"].toString()) == elixirVersion }
        .map { pair ->
            (pair["corpus"] as List<*>? ?: emptyList<Any>()).map { entry ->
                entry as Map<*, *>
                CorpusEntry(entry["git"].toString(), entry["sha"].toString())
            }
        }
        .distinct()

    require(corpora.size <= 1) {
        "The pairs for Elixir $elixirVersion in .github/ci-versions.json declare different corpora: $corpora"
    }

    return corpora.singleOrNull().orEmpty()
}
