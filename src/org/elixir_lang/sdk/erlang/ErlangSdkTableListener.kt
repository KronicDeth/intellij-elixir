package org.elixir_lang.sdk.erlang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import org.elixir_lang.util.WriteActions

object ErlangSdkTableListener {
    private val LOGGER = Logger.getInstance(ErlangSdkTableListener::class.java)

    fun setup() {
        val messageBus = ApplicationManager.getApplication().messageBus
        messageBus.connect().subscribe(ProjectJdkTable.JDK_TABLE_TOPIC,
            object : ProjectJdkTable.Listener {
                override fun jdkRemoved(jdk: Sdk) {
                    if (jdk.sdkType is Type) {
                        cleanupProjectReferences(jdk)
                    }
                }
            })
    }

    private fun cleanupProjectReferences(deletedSdk: Sdk) {
        LOGGER.warn("Erlang SDK removed: ${deletedSdk.name}, cleaning up project references")
        com.intellij.openapi.project.ProjectManager.getInstance().openProjects.forEach { project ->
            val projectRootManager = ProjectRootManager.getInstance(project)
            if (projectRootManager.projectSdk == deletedSdk) {
                WriteActions.runWriteActionLater {
                    projectRootManager.projectSdk = null
                    LOGGER.warn("Cleared removed Erlang SDK '${deletedSdk.name}' from project '${project.name}'")
                }
            }
        }
    }
}
