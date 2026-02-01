package versioning

import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Version Fetcher Logic
 * Purpose: Isolates the complex logic required to fetch the latest IntelliJ EAP/RC build number.
 * Moved from the main build.gradle.kts to improve readability and separation of concerns.
 */
object VersionFetcher {
    // get platformType from gradle properties or default to "IU"
    fun getLatestEapBuild(platformType: String = "IU"): String {
        // Note that the order below is incorrect, as EAPs should be preferred over RCs, and RCs over Releases.
        val apiUris = listOf(
            URI("https://data.services.jetbrains.com/products/releases?code=$platformType&type=eap&latest=true&fields=build"),
            URI("https://data.services.jetbrains.com/products/releases?code=$platformType&type=rc&latest=true&fields=build"),
            URI("https://data.services.jetbrains.com/products/releases?code=$platformType&type=release&latest=true&fields=build")
        )

        for (uri in apiUris) {
            val json = runCatching {
                uri.toURL().openStream().bufferedReader().use { it.readText() }
            }.getOrNull()

            if (json != null) {
                val regex = """"build"\s*:\s*"([0-9.]+)"""".toRegex()
                val match = regex.find(json)
                if (match != null) {
                    val selectedVersion = match.groupValues[1]
                    println("Version: $selectedVersion found at $uri")
                    return selectedVersion
                }
            }
        }

        // Fallback logic
        return fetchSnapshotVersion()
    }

    private fun fetchSnapshotVersion(): String {
        val snapshotsUri = URI("https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/snapshots/com/jetbrains/intellij/idea/ideaIU/maven-metadata.xml")

        val versions = kotlin.runCatching {
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(snapshotsUri.toURL().openStream())
            doc.documentElement.normalize()
            val versioning = doc.getElementsByTagName("versioning").item(0)
            val versionsNode = versioning?.childNodes
            buildList {
                if (versionsNode != null) {
                    for (i in 0 until versionsNode.length) {
                        val n = versionsNode.item(i)
                        if (n.nodeName == "versions") {
                            val children = n.childNodes
                            for (j in 0 until children.length) {
                                val v = children.item(j)
                                if (v.nodeName == "version") add(v.textContent)
                            }
                        }
                    }
                }
            }
        }.getOrNull().orEmpty()

        // Look for versions like 253.17525.95-EAP-SNAPSHOT and convert to 253.17525.95
        val numericEap = versions.asSequence()
            .mapNotNull { v ->
                val m = Regex("""^(\d+\.\d+\.\d+)-EAP-SNAPSHOT$""").matchEntire(v)
                m?.groupValues?.get(1)
            }
            .lastOrNull()

        if (numericEap != null) {
            println("Fallback Version: $numericEap found at $snapshotsUri")
            return numericEap
        }

        // Return result or throw exception
        throw IllegalStateException("No numeric EAP build found")
    }
}
