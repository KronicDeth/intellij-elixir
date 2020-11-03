package org.elixir_lang.create_module

import com.google.common.annotations.VisibleForTesting
import com.google.common.base.CaseFormat
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.TemplateKindCombo
import com.intellij.lang.LangBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.psi.PsiDirectory
import org.elixir_lang.Icons
import org.elixir_lang.psi.ElixirFile
import org.junit.Test

const val NEW_ELIXIR_MODULE = "New Elixir Module"
const val DESCRIPTION = "Nested Aliases, like Foo.Bar.Baz, are created in subdirectory for the " + "parent Aliases, foo/bar/baz.ex"

private const val EXTENSION = ".ex"

fun ancestorDirectoryNamesBaseNamePair(moduleName: String, extension: String): Pair<List<String>, String> {
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

    val basename = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastAlias) + extension

    return directoryList to basename
}

fun templateNameToExtension(templateName: String): String =
        when (templateName) {
            "ESpec" -> ".exs"
            "ExUnit.Case" -> ".exs"
            else -> ".ex"
        }

class Dialog(private val project: Project, val directory: PsiDirectory) : CreateFileFromTemplateDialog(project), InputValidatorEx {
    init {
        title = NEW_ELIXIR_MODULE

        kindCombo.apply {
            addItem("Empty module", Icons.FILE, "Elixir Module")
            addItem("Application", Icons.File.APPLICATION, "Elixir Application")
            addItem("Supervisor", Icons.File.SUPERVISOR, "Elixir Supervisor")
            addItem("GenServer", Icons.File.GEN_SERVER, "Elixir GenServer")
            addItem("GenEvent", Icons.File.GEN_EVENT, "Elixir GenEvent")
            addItem("ExUnit.Case", org.elixir_lang.exunit.configuration.Icons.TYPE, "ExUnit.Case")
            addItem("ESpec", org.elixir_lang.espec.configuration.Icons.TYPE, "ESpec")
        }

        setTemplateKindComponentsVisible(true)
    }

    var created: ElixirFile? = null
        private set

    override fun doValidate(): ValidationInfo? {
        val text = nameField.text.trim()
        val canClose = canClose(text)

        return if (!canClose) {
            val errorText = getErrorText(text) ?: LangBundle.message("incorrect.name")

            ValidationInfo(errorText, nameField)
        } else {
            super.doValidate()
        }
    }

    @VisibleForTesting
    public override fun doOKAction() {
        val created = org.elixir_lang.create_module.ElementCreator(project, directory, templateName).tryCreate(getEnteredName())

        if (created.isNotEmpty()) {
            this.created = created.single() as ElixirFile
            super.doOKAction()
        }
    }

    @VisibleForTesting
    var name: String
      get() = nameField.text.trim()
      set(newName) {
          nameField.text = newName.trim()
      }

    @VisibleForTesting
    var templateName: String
      get() = kindCombo.selectedName
      set(newTemplateName) {
          kindCombo.setSelectedName(newTemplateName)
      }

    private fun getEnteredName(): String {
        val text = nameField.text.trim()
        nameField.text = text
        return text
    }

    override fun canClose(inputString: String): Boolean =
        !inputString.isBlank() && getErrorText(inputString) == null

    override fun checkInput(inputString: String): Boolean =
        checkFormat(inputString) && checkDoesNotExist(inputString, extension)

    override fun getErrorText(inputString: String): String? =
        if (!inputString.isEmpty()) {
            if (!checkFormat(inputString)) {
                val invalidMessageFormat = invalidMessageFormat(templateName)
                String.format(invalidMessageFormat, inputString)
            } else {
                val extension = this.extension

                if (!checkDoesNotExist(inputString, extension)) {
                    val fullPath = fullPath(ancestorDirectoryNamesBaseNamePair(inputString, extension))
                    String.format(EXISTING_MODULE_MESSAGE_FMT, fullPath)
                } else {
                    null
                }
            }
        } else {
            "Module name cannot be empty"
        }

    private fun checkFormat(inputString: String): Boolean =
            when (templateName) {
                "ESpec" -> ESPEC_CASE_NAME_REGEX
                "ExUnit.Case" -> EX_UNIT_CASE_NAME_REGEX
                else -> MODULE_NAME_REGEX
            }.matches(inputString)

    private fun checkDoesNotExist(moduleName: String, extension: String): Boolean {
        val ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(
                moduleName,
                extension
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

    private val extension
      get() = templateNameToExtension(templateName)

    private fun fullPath(ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String =
            directory.virtualFile.canonicalPath + "/" + path(ancestorDirectoryNamesBaseNamePair)

}

private fun invalidMessageFormat(templateName: String): String =
        when (templateName) {
            "ExUnit.Case" -> INVALID_EX_UNIT_CASE_MESSAGE_FMT
            "ESpec" -> INVALID_ESPEC_CASE_MESSAGE_FMT
            else -> INVALID_MODULE_MESSAGE_FMT
        }

private const val INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Elixir module " +
                "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
                "capital letter. " + DESCRIPTION
private const val INVALID_ESPEC_CASE_MESSAGE_FMT = "'%s' is not a valid ESpec module name. ESpec module " +
        "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
        "capital letter and the final one ending in Spec as <code>mix espec</code> looks for files matching " +
        "<code>spec/**/*_spec.exs</code>.  Nested Aliases, like Foo.Bar.BazSpec, are created in subdirectory for the " +
        "parent Aliases, foo/bar/baz_spec.exs"
private const val INVALID_EX_UNIT_CASE_MESSAGE_FMT = "'%s' is not a valid ExUnit.Case module name. ExUnit.Case module " +
        "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
        "capital letter and the final one ending in Test as <code>mix test</code> looks for files matching " +
        "<code>test/**/*_test.exs</code>.  Nested Aliases, like Foo.Bar.BazTest, are created in subdirectory for the " +
        "parent Aliases, foo/bar/baz_test.exs"
private const val EXISTING_MODULE_MESSAGE_FMT = "'%s' already exists"
private val ALIAS_REGEX = Regex("[A-Z][0-9a-zA-Z_]*")
private val MODULE_NAME_REGEX = Regex("$ALIAS_REGEX(\\.$ALIAS_REGEX)*")
private val ESPEC_CASE_NAME_REGEX = Regex("${MODULE_NAME_REGEX}Spec")
private val EX_UNIT_CASE_NAME_REGEX = Regex("${MODULE_NAME_REGEX}Test")

private fun path(ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String {
    val (ancestorDirectoryNames, baseName) = ancestorDirectoryNamesBaseNamePair
    val directoryPath = ancestorDirectoryNames.joinToString("/")

    return directoryPath + baseName
}
