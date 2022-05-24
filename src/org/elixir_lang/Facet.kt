package org.elixir_lang

import com.intellij.facet.FacetType
import com.intellij.facet.FacetTypeId
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.libraries.LibraryTable
import org.elixir_lang.facet.Configuration

class Facet(
    facetType: FacetType<*, *>,
    module: Module,
    name: String,
    configuration: Configuration,
    underlyingFacet: com.intellij.facet.Facet<*>?
) : com.intellij.facet.Facet<Configuration?>(facetType, module, name, configuration, underlyingFacet) {
    var sdk: Sdk?
        get() = sdk(module)
        set(value) {
            val modifiableModuleRootManger = module.rootManager.modifiableModel
            val libraryTable = modifiableModuleRootManger.moduleLibraryTable
            removeElixirSDKs(libraryTable)

            if (value != null) {
                addElixirSDK(libraryTable, value)
            }

            modifiableModuleRootManger.commit()
        }

    companion object {
        val ID = FacetTypeId<Facet>("elixir")
        fun sdk(module: Module): Sdk? {
            var sdk: Sdk? = null
            val sdkByName = sdkByName()

            if (sdkByName.isNotEmpty()) {
                module.rootManager.orderEntries().forEachLibrary { library ->
                    sdk = sdkByName[library.name]

                    sdk == null
                }
            }

            return sdk
        }

        private fun removeElixirSDKs(moduleLibraryTable: LibraryTable) {
            sdks()
                .map { it.name }
                .mapNotNull { name ->
                    moduleLibraryTable.getLibraryByName(name)
                }
                .forEach { library ->
                    moduleLibraryTable.removeLibrary(library)
                }
        }

        private fun addElixirSDK(moduleLibraryTable: LibraryTable, sdk: Sdk) {
            moduleLibraryTable.createLibrary(sdk.name)
        }

        fun sdks(): kotlin.collections.List<Sdk> =
            ProjectJdkTable
                .getInstance()
                .getSdksOfType(org.elixir_lang.sdk.elixir.Type.instance)

        private fun sdkByName(): Map<String, Sdk> = sdks().associateBy { it.name }
    }
}
