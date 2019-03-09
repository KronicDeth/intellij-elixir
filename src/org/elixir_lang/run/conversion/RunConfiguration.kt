package org.elixir_lang.run.conversion

import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.RunManagerSettings
import org.elixir_lang.espec.MIX_ESPEC
import org.elixir_lang.exunit.MIX_TEST
import org.elixir_lang.run.*
import org.jdom.Element

class RunConfiguration : ConversionProcessor<RunManagerSettings>() {
    override fun isConversionNeeded(settings: RunManagerSettings): Boolean =
        settings.runConfigurations.conversionNeeded().isNotEmpty()

    override fun process(settings: RunManagerSettings) {
        settings.runConfigurations.conversionNeeded().process()
    }
}

private val TYPES = arrayOf("MixRunConfigurationType", "MixExUnitRunConfigurationType", "MixEspecRunConfigurationType")
private val OLD_OPTION_NAMES = arrayOf("programParameters", "workingDirectory")

private fun Element.getOption(name: String): Element? = getChildren("option").first { it.getAttributeValue("name") == name }
private fun Element.hasAnyOption(names: Array<String>): Boolean = getChildren("option").any { it.getAttributeValue("name") in names }
private fun Element.hasOldOptions(): Boolean = hasAnyOption(OLD_OPTION_NAMES)
private fun Element.correctType(): Boolean = getAttributeValue("type") in TYPES
private fun Collection<Element>.conversionNeeded(): Collection<Element> = filter(Element::correctType).filter(Element::hasOldOptions)
private fun Collection<Element>.process() = forEach(Element::process)

private fun Element.process() {
    processProgramParametersOption()
    processWorkingDirectoryOption()
    putNewModuleFilters()
}

fun Element.processProgramParametersOption() {
    getOption("programParameters")?.let { programParametersOption ->
        val type = getAttributeValue("type")

        val command = when (type) {
            "MixRunConfigurationType" -> MIX
            "MixEspecRunConfigurationType" -> MIX_ESPEC
            "MixExUnitRunConfigurationType" -> MIX_TEST
            else -> null
        }

        if (command != null) {
            val programParameters = programParametersOption.getAttributeValue("value")

            val argumentList = mutableListOf<String>()
            argumentList.fromArguments(programParameters)

            writeExternalArgumentList(command, argumentList)
            removeContent(programParametersOption)
        }
    }
}

fun Element.processWorkingDirectoryOption() {
    getOption("workingDirectory")?.let { workingDirectoryOption ->
        val oldValue = workingDirectoryOption.getAttributeValue("value")
        writeExternalWorkingDirectory("file://$oldValue")
        removeContent(workingDirectoryOption)
    }
}

fun Element.putNewModuleFilters() {
    if (getChild("module-filters") == null) {
        writeModuleFilters(emptyList(), true)
    }
}

