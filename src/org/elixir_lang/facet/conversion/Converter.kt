package org.elixir_lang.facet.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ModuleSettings
import com.intellij.conversion.ProjectConverter
import com.intellij.conversion.impl.ProjectConversionUtil
import com.intellij.openapi.components.StorageScheme
import com.intellij.util.io.delete
import com.intellij.util.io.exists
import org.apache.commons.io.FilenameUtils
import org.elixir_lang.sdk.elixir.ForSmallIdes
import org.jdom.Element
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

val libraryParts = arrayOf("libraries", "${ForSmallIdes.LIBRARY_NAME.replace(' ', '_')}.xml")

class Converter(private val conversionContext: ConversionContext) : ProjectConverter() {
    override fun isConversionNeeded(): Boolean = libraryElement() != null
    override fun createModuleFileConverter(): ConversionProcessor<ModuleSettings> = ModuleSettings(conversionContext)
    override fun preProcessingFinished() {
        super.preProcessingFinished()

        when (conversionContext.storageScheme) {
            StorageScheme.DEFAULT ->
                TODO("This format is old")
            StorageScheme.DIRECTORY_BASED -> {
                existentLibraryPath()?.let {
                    val destination = Paths.get(backupDir(), ".idea", *libraryParts)

                    destination.parent.toFile().mkdirs()
                    Files.copy(it, destination)
                }
            }
        }
    }
    override fun processingFinished() {
        when (conversionContext.storageScheme) {
            StorageScheme.DEFAULT ->
                TODO("This format is old")
            StorageScheme.DIRECTORY_BASED -> {
                existentLibraryPath()?.delete()
            }
        }

        super.processingFinished()
    }

    private fun backupDir(): String {
        val backupDirFiles = conversionContext
                .projectBaseDir
                .listFiles(FileFilter {
                    it.isDirectory &&
                            FilenameUtils.getBaseName(it.path).startsWith(ProjectConversionUtil.PROJECT_FILES_BACKUP)
                })

        var maxBackupDirFile: File? = null
        var maxSuffixInt = -1

        for (backupDirFile in backupDirFiles) {
            val suffix = FilenameUtils
                    .getBaseName(backupDirFile.path)
                    .removePrefix(ProjectConversionUtil.PROJECT_FILES_BACKUP)

            val suffixInt = try {
                Integer.parseInt(suffix)
            } catch (e: NumberFormatException) {
                -1
            }

            if (suffixInt > maxSuffixInt) {
                maxSuffixInt = suffixInt
                maxBackupDirFile = backupDirFile
            }
        }

        if (maxBackupDirFile == null) {
            maxBackupDirFile = Paths
                    .get(conversionContext.projectBaseDir.path, ProjectConversionUtil.PROJECT_FILES_BACKUP)
                    .toFile()
        }

        return maxBackupDirFile!!.path
    }
    private fun existentLibraryPath(): Path? {
        val paths = Paths.get(
                        conversionContext.settingsBaseDir.path,
                        "libraries",
                        "${ForSmallIdes.LIBRARY_NAME.replace(' ', '_')}.xml")

        return if (paths.exists()) {
            paths
        } else {
            null
        }
    }
    private fun libraryElement(): Element? =
            conversionContext.projectLibrariesSettings.projectLibraries.find {
                it.getAttributeValue("name") == ForSmallIdes.LIBRARY_NAME
            }
}
