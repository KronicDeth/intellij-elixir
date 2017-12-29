package org.elixir_lang.facet

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel

class SdksService {
    private var model: ProjectSdksModel? = null

    companion object {
        fun getInstance(): SdksService? = ServiceManager.getService(SdksService::class.java)
    }

    fun <T> projectJdkImplList(clazz: Class<T>) =
            getModel().sdks.filter { clazz.isInstance(it.sdkType) }.map { it as ProjectJdkImpl }

    fun getModel(): ProjectSdksModel {
        val model = this.model?: ProjectSdksModel().apply { reset(null) }
        this.model = model

        return model
    }
}
