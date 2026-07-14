package org.elixir_lang.facet

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import org.jetbrains.annotations.TestOnly

@Service
class SdksService : Disposable {
    private var model: ProjectSdksModel? = null

    init {
        // Invalidate the cached model whenever the JDK table changes outside of it - e.g. the
        // tool-manager "Configure from mise" action registers SDKs directly via SdkRegistrar. The
        // next getModel() then rebuilds from the current table. Settings views that are already
        // open keep their own model reference (via `by lazy`), so this does not disturb them; it
        // only ensures the *next* dialog reflects the change (previously it took an IDE restart).
        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
                override fun jdkAdded(jdk: Sdk) = invalidate()
                override fun jdkRemoved(jdk: Sdk) = invalidate()
                override fun jdkNameChanged(jdk: Sdk, previousName: String) = invalidate()
            })
    }

    /** Drops the cached model (without disposing it, so already-open views keep working). */
    private fun invalidate() {
        model = null
    }

    override fun dispose() {}

    companion object {
        fun getInstance(): SdksService? = ApplicationManager.getApplication().getService(SdksService::class.java)
    }

    /**
     * Discards the cached model so the next [getModel] rebuilds it from the current JDK table.
     * The model is an application-level singleton that lives for the whole test JVM; teardown that
     * mutates the JDK table directly (bypassing the model) would otherwise leak SDK clones into the
     * next test. Not used in production - the SDK settings UI keeps the model in sync via listeners.
     */
    @TestOnly
    fun resetForTests() {
        model?.disposeUIResources()
        model = null
    }

    fun <T> projectJdkImplList(clazz: Class<T>) =
        getModel().sdks.filter { clazz.isInstance(it.sdkType) }.map { it as ProjectJdkImpl }

    fun getModel(): ProjectSdksModel {
        val model = this.model ?: initModel()
        this.model = model

        return model
    }

    private fun initModel(): ProjectSdksModel {
        // ProjectSdksModel.reset requires a NON-NULL project to load SDKs from the JDK table -
        // reset(null) loads nothing. The JDK table is application-wide, so any open project triggers
        // a full load. The model is initialised once and cached; the combo/list SdkModel listeners
        // keep it in sync with edits, so it must NOT be reset again per view (re-cloning would
        // invalidate SDK references other open views hold and break removal - see ProjectSdksModel).
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        var model: ProjectSdksModel?

        do {
            model = try {
                ProjectSdksModel().apply { reset(project) }
            } catch (_: AssertionError) {
                null
            }
        } while (model == null)

        return model
    }
}
