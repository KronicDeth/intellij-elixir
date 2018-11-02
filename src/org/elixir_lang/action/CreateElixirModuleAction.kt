package org.elixir_lang.action

import com.google.common.base.CaseFormat
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.CreateFromTemplateAction
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.elixir_lang.Icons
import org.elixir_lang.psi.ElixirFile
import org.jetbrains.annotations.Contract

import java.util.ArrayList
import java.util.Properties
import java.util.regex.Pattern

import com.intellij.openapi.util.Pair.pair

class CreateElixirModuleAction : CreateFromTemplateAction<ElixirFile>(NEW_ELIXIR_MODULE, DESCRIPTION, Icons.FILE) {
    override fun equals(other: Any?): Boolean = other is CreateElixirModuleAction
    override fun hashCode(): Int = 0
    override fun isDumbAware(): Boolean = true

    override fun buildDialog(project: Project,
                             directory: PsiDirectory,
                             builder: CreateFileFromTemplateDialog.Builder) {
        builder
                .setTitle(NEW_ELIXIR_MODULE)
                .addKind("Empty module", Icons.FILE, "Elixir Module")
                .addKind("Application", Icons.File.APPLICATION, "Elixir Application")
                .addKind("Supervisor", Icons.File.SUPERVISOR, "Elixir Supervisor")
                .addKind("GenServer", Icons.File.GEN_SERVER, "Elixir GenServer")
                .addKind("GenEvent", Icons.File.GEN_EVENT, "Elixir GenEvent")
                .setValidator(object : InputValidatorEx {
            override fun canClose(inputString: String): Boolean =
                    !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null

            override fun checkInput(inputString: String): Boolean =
                    checkFormat(inputString) && checkDoesNotExist(inputString)

            override fun getErrorText(inputString: String): String? =
                if (!StringUtil.isEmpty(inputString)) {
                    if (!checkFormat(inputString)) {
                        String.format(INVALID_MODULE_MESSAGE_FMT, inputString)
                    } else if (!checkDoesNotExist(inputString)) {
                        val fullPath = fullPath(directory, ancestorDirectoryNamesBaseNamePair(inputString))
                        String.format(EXISTING_MODULE_MESSAGE_FMT, fullPath)
                    } else {
                        null
                    }
                } else {
                    null
                }

            private fun checkDoesNotExist(moduleName: String): Boolean {
                val ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(
                        moduleName
                )
                val ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first
                var currentDirectory: PsiDirectory? = directory
                var doesNotExists = false

                for (ancestorDirectoryName in ancestorDirectoryNames) {
                    val subdirectory = currentDirectory!!.findSubdirectory(ancestorDirectoryName)

                    if (subdirectory == null) {
                        doesNotExists = true

                        break
                    }

                    currentDirectory = subdirectory
                }

                // if all the directories exist
                if (!doesNotExists) {
                    val baseName = ancestorDirectoryNamesBaseNamePair.second
                    doesNotExists = currentDirectory!!.findFile(baseName) == null
                }

                return doesNotExists
            }

            private fun checkFormat(inputString: String): Boolean = MODULE_NAME_PATTERN.matcher(inputString).matches()
        })
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String =
            NEW_ELIXIR_MODULE

    public override fun createFile(name: String, templateName: String, dir: PsiDirectory): ElixirFile? =
            createDirectoryAndModuleFromTemplate(name, dir, templateName)

    companion object {
        private const val NEW_ELIXIR_MODULE = "New Elixir Module"

        private const val ALIAS_REGEXP = "[A-Z][0-9a-zA-Z_]*"
        private const val MODULE_NAME_REGEXP = "$ALIAS_REGEXP(\\.$ALIAS_REGEXP)*"
        private val MODULE_NAME_PATTERN = Pattern.compile(MODULE_NAME_REGEXP)
        private const val DESCRIPTION = "Nested Aliases, like Foo.Bar.Baz, are created in subdirectory for the " + "parent Aliases, foo/bar/Baz.ex"
        private const val EXTENSION = ".ex"
        private const val EXISTING_MODULE_MESSAGE_FMT = "'%s' already exists"
        private const val INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Elixir module " +
                "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
                "capital letter. " + DESCRIPTION

        private fun ancestorDirectoryNamesBaseNamePair(moduleName: String): Pair<List<String>, String> {
            val directoryList: MutableList<String>
            val lastAlias: String

            if (moduleName.contains(".")) {
                val aliases = moduleName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val directoryListSize = aliases.size - 1
                directoryList = mutableListOf()

                for (i in 0 until directoryListSize) {
                    val alias = aliases[i]
                    val subdirectoryName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, alias)
                    directoryList.add(subdirectoryName)
                }

                lastAlias = aliases[aliases.size - 1]
            } else {
                directoryList = mutableListOf()
                lastAlias = moduleName
            }

            val basename = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastAlias) + EXTENSION

            return pair(directoryList, basename)
        }

        /**
         * @link com.intellij.ide.actions.CreateTemplateInPackageAction#checkOrCreate
         */
        private fun createDirectoryAndModuleFromTemplate(moduleName: String,
                                                         directory: PsiDirectory,
                                                         templateName: String): ElixirFile? {
            var currentDirectory = directory

            val ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(moduleName)
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

            val element: PsiElement?

            try {
                element = FileTemplateUtil.createFromTemplate(template, basename, properties, directory)
            } catch (exception: Exception) {
                CreateFromTemplateAction.LOG.error(exception)

                return null
            }

            return if (element == null) {
                null
            } else element as ElixirFile?
        }

        @Contract(pure = true)
        private fun fullPath(directory: PsiDirectory,
                             ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String =
                directory.virtualFile.canonicalPath + "/" + path(ancestorDirectoryNamesBaseNamePair)

        @Contract(pure = true)
        private fun path(ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String {
            val ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first
            val directoryPath = StringUtil.join(ancestorDirectoryNames, "/")

            return directoryPath + ancestorDirectoryNamesBaseNamePair.second
        }
    }
}
