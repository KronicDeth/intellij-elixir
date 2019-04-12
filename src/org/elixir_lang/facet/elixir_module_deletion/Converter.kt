package org.elixir_lang.facet.elixir_module_deletion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ProjectConverter
import com.intellij.ide.impl.convert.JDomConvertingUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.impl.ModuleManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.SystemProperties
import com.intellij.util.io.exists
import org.elixir_lang.facet.conversion.backupDir
import org.elixir_lang.sdk.ProcessOutput
import org.jetbrains.jps.model.serialization.JDomSerializationUtil
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Converter(private val conversionContext: ConversionContext) : ProjectConverter() {
    override fun isConversionNeeded(): Boolean = elixirModuleFiles.isNotEmpty()

    override fun preProcessingFinished() {
        super.preProcessingFinished()

        val baseDir = conversionContext.projectBaseDir
        val backupDir = backupDir(conversionContext)

        elixirModuleFiles.forEach { moduleFile ->
            val modulePath = moduleFile.toPath()
            val relative = baseDir.toPath().relativize(modulePath)
            val destination = Paths.get(backupDir, relative.toString())

            if (!destination.exists()) {
                destination.parent.toFile().mkdirs()
                Files.copy(modulePath, destination)
            }
        }
    }

    override fun processingFinished() {
        val settingsBaseDir = File(conversionContext.projectBaseDir.absolutePath, Project.DIRECTORY_STORE_FOLDER)
        val modulesFile = File(settingsBaseDir, "modules.xml")
        val document = JDomConvertingUtil.loadDocument(modulesFile)
        val rootElement = document.rootElement

        JDomSerializationUtil.findComponent(rootElement, ModuleManagerImpl.COMPONENT_NAME)?.let { modulesManagerElement ->
            modulesManagerElement.getChild(ModuleManagerImpl.ELEMENT_MODULES)?.let { modulesElement ->
                modulesElement.getChildren(ModuleManagerImpl.ELEMENT_MODULE).removeAll { moduleElement ->
                    val externalFilePath = moduleElement.getAttributeValue(ModuleManagerImpl.ATTRIBUTE_FILEPATH)
                    val internalFilePath = conversionContext.expandPath(externalFilePath)
                    val internalFile = File(FileUtil.toSystemDependentName(internalFilePath))

                    elixirModuleFiles.contains(internalFile)
                }
            }
        }

        try {
            modulesFile.delete()
            JDOMUtil.writeDocument(document, modulesFile, SystemProperties.getLineSeparator())
        } catch (e: IOException) {
            LOGGER.info(e)
        }

        elixirModuleFiles.forEach { moduleFile ->
            moduleFile.delete()
        }
    }


    private val elixirModuleFiles: List<File> by lazy {
        if (ProcessOutput.isSmallIde()) {
            conversionContext.moduleFiles.filter { moduleFile ->
                conversionContext.getModuleSettings(moduleFile).moduleType == "ELIXIR_MODULE"
            }
        } else {
            emptyList()
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(this::class.java)
    }
}
