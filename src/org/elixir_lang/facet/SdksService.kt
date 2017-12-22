package org.elixir_lang.facet

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import org.elixir_lang.sdk.elixir.Type

class SdksService {
    private var model: ProjectSdksModel? = null

    companion object {
        fun getInstance(): SdksService? = ServiceManager.getService(SdksService::class.java)
    }

    fun elixirSdkList() = getModel().sdks.filter { it.sdkType is Type }

    fun getModel(): ProjectSdksModel {
        val model = this.model?: ProjectSdksModel().apply { reset(null) }
        this.model = model

        return model
    }
}
