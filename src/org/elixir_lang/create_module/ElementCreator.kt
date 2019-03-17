package org.elixir_lang.create_module

import com.intellij.CommonBundle
import com.intellij.ide.actions.ElementCreator
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirFile
import java.util.*

class ElementCreator(project: Project, private val directory: PsiDirectory, private val templateName: String) :
        ElementCreator(project, CommonBundle.getErrorTitle()) {
    override fun create(moduleName: String): Array<PsiElement> =
        createDirectoryAndModuleFromTemplate(moduleName, directory, templateName)
                ?.let { arrayOf<PsiElement>(it) }
                ?: emptyArray()

    override fun getActionName(newName: String): String = NEW_ELIXIR_MODULE
}

private val LOGGER = Logger.getInstance(ElementCreator::class.java);

/**
 * @link com.intellij.ide.actions.CreateTemplateInPackageAction#checkOrCreate
 */
private fun createDirectoryAndModuleFromTemplate(moduleName: String,
                                                 directory: PsiDirectory,
                                                 templateName: String): ElixirFile? {
    var currentDirectory = directory

    val extension = templateNameToExtension(templateName)
    val ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(moduleName, extension)
    val ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first

    for (ancestorDirectoryName in ancestorDirectoryNames) {
        var subdirectory = currentDirectory.findSubdirectory(ancestorDirectoryName)

        if (subdirectory == null) {
            subdirectory = currentDirectory.createSubdirectory(ancestorDirectoryName)
        }

        currentDirectory = subdirectory
    }

    val basename = ancestorDirectoryNamesBaseNamePair.second

    return createModuleFromTemplate(currentDirectory, basename, moduleName, templateName)
}

/**
 * @link com.intellij.ide.acitons.CreateTemplateInPackageAction#doCreate
 * @link com.intellij.ide.actions.CreateClassAction
 * @link com.intellij.psi.impl.file.JavaDirectoryServiceImpl.createClassFromTemplate
 */
private fun createModuleFromTemplate(directory: PsiDirectory,
                                     basename: String,
                                     moduleName: String,
                                     templateName: String): ElixirFile? {
    val fileTemplateManager = FileTemplateManager.getDefaultInstance()
    val template = fileTemplateManager.getInternalTemplate(templateName)

    val defaultProperties = fileTemplateManager.defaultProperties
    val properties = Properties(defaultProperties)
    properties.setProperty(FileTemplate.ATTRIBUTE_NAME, moduleName)

    when (templateName) {
        "ESpec" -> {
            val sourceName= moduleName.removeSuffix("Spec")
            properties.setProperty("SOURCE_NAME", sourceName)

            properties.setProperty("ALIAS", sourceName.split(".").last())
        }
        "ExUnit.Case" -> {
            val sourceName= moduleName.removeSuffix("Test")
            properties.setProperty("SOURCE_NAME", sourceName)

            properties.setProperty("ALIAS", sourceName.split(".").last())
        }
    }

    return try {
        FileTemplateUtil.createFromTemplate(template, basename, properties, directory) as ElixirFile
    } catch (exception: Exception) {
        LOGGER.error(exception)

        null
    }
}
