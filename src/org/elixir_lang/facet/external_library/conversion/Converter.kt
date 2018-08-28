package org.elixir_lang.facet.external_library.conversion

import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ModuleSettings
import com.intellij.conversion.ProjectConverter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import org.elixir_lang.facet.SdksService
import org.elixir_lang.facet.sdks.addRoots
import org.elixir_lang.sdk.elixir.Type

class Converter : ProjectConverter() {
    override fun isConversionNeeded(): Boolean = missingLibraryNameSet().isNotEmpty()

    /**
     * Create Application-level External Libraries before they are added to modules in [createModuleFileConverter]
     */
    override fun preProcessingFinished() {
        super.preProcessingFinished()

        val libraryTable = LibraryTablesRegistrar.getInstance().libraryTable

        missingLibraryNameSet().forEach { missingLibraryName ->
            SdksService.getInstance()!!.getModel().findSdk(missingLibraryName)!!.let { sdk ->
                libraryTable.createLibrary(sdk.name).let { library ->
                    ApplicationManager.getApplication().runWriteAction {
                        library.modifiableModel.apply {
                            addRoots(sdk)
                            commit()
                        }
                    }
                }
            }
        }
    }

    override fun createModuleFileConverter(): ConversionProcessor<ModuleSettings> = ModuleSettings()
}

private fun missingLibraryNameSet(): Set<String> = sdkNameSet() - libraryNameSet()

private fun sdkNameSet(): Set<String> =
        SdksService.getInstance()!!.getModel().sdks.filter { it.sdkType == Type.getInstance() }.map { it.name }.toSet()

private fun libraryNameSet(): Set<String> =
        LibraryTablesRegistrar.getInstance().libraryTable.libraryIterator.asSequence().mapNotNull { it.name }.toSet()
