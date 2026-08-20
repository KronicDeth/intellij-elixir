package sdk

import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.CommandLineArgumentProvider
import java.io.File
import java.io.Serializable

/**
 * Provides JVM arguments for tests based on resolved SDK paths stored in a properties file.
 */
class ElixirErlangSdkArgumentProvider(
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val propertiesFile: File
) : CommandLineArgumentProvider, Serializable {

    override fun asArguments(): Iterable<String> {
        // Read the shared SDK properties file produced by ResolveElixirErlangSdksTask.
        val props = readPropertiesFile(propertiesFile)
        val erlangSdkPath = props["erlang.sdk.path"]
            ?: throw GradleException("Missing erlang.sdk.path in ${propertiesFile.absolutePath}")
        val elixirSdkPath = props["elixir.sdk.path"]
            ?: throw GradleException("Missing elixir.sdk.path in ${propertiesFile.absolutePath}")

        return listOf(
            "-DerlangSdkPath=$erlangSdkPath",
            "-DelixirSdkPath=$elixirSdkPath"
        )
    }
}
