package org.elixir_lang.sdk

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import java.io.IOException
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths

object SdkEbinPaths {
    private val LOGGER = Logger.getInstance(SdkEbinPaths::class.java)

    @JvmStatic
    fun eachEbinPath(homePath: String, ebinPathConsumer: (Path) -> Unit) {
        val lib = Paths.get(homePath, "lib")

        // For WSL paths, newDirectoryStream translates them to Linux paths, and there's no way to stop it. It's also the
        // most performant way of dealing with files so we prefer to keep it and then fix it with `resolve` to get back to the UNC path.
        val filter = DirectoryStream.Filter<Path> { path -> wslSafeIsDirectory(lib, path) }

        try {
            Files.newDirectoryStream(lib, filter).use { libDirectoryStream ->
                for (app in libDirectoryStream) {
                    val uncApp = maybeTranslateToUnc(lib, app)
                    try {
                        Files.newDirectoryStream(uncApp, "ebin").use { ebinDirectoryStream ->
                            for (ebinPath in ebinDirectoryStream) {
                                val uncEbinPath = maybeTranslateToUnc(uncApp, ebinPath)
                                ebinPathConsumer(uncEbinPath)
                            }
                        }
                    } catch (ioException: IOException) {
                        LOGGER.error("IOException processing app $uncApp", ioException)
                    }
                }
            }
        } catch (noSuchFileException: NoSuchFileException) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "${noSuchFileException.file} does not exist, so its ebin paths cannot be enumerated.",
                    NotificationType.ERROR,
                )
                // TODO: does this need project?
                .notify(null)
        } catch (ioException: IOException) {
            LOGGER.error("IOException opening DirectoryStream for lib", ioException)
        }
    }

    @JvmStatic
    fun hasEbinPath(homePath: String): Boolean {
        val lib = Paths.get(homePath, "lib")
        var hasEbinPath = false

        val filter = DirectoryStream.Filter<Path> { path -> wslSafeIsDirectory(lib, path) }
        try {
            Files.newDirectoryStream(lib, filter).use { libDirectoryStream ->
                for (app in libDirectoryStream) {
                    try {
                        Files.newDirectoryStream(app, "ebin").use { ebinDirectoryStream ->
                            if (ebinDirectoryStream.iterator().hasNext()) {
                                hasEbinPath = true
                                break
                            }
                        }
                    } catch (ioException: IOException) {
                        LOGGER.error(ioException)
                    }
                }
            }
        } catch (ioException: IOException) {
            LOGGER.error(ioException)
        }

        return hasEbinPath
    }

    private fun wslSafeIsDirectory(basePath: Path, path: Path): Boolean {
        return Files.isDirectory(maybeTranslateToUnc(basePath, path))
    }

    private fun maybeTranslateToUnc(basePath: Path, path: Path): Path {
        return basePath.resolve(path.fileName.toString())
    }
}
