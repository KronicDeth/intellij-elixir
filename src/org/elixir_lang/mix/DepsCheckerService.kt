package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.Alarm
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.package_manager.DepsStatusResult
import org.elixir_lang.package_manager.virtualFile
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.PROJECT)
class DepsCheckerService(private val project: Project) : Disposable {
    private val alarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    private val checkInProgress = AtomicBoolean(false)
    @Volatile
    private var checkPending = false

    init {
        project.messageBus.connect(this).subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    if (events.any { isDepsChange(it) }) {
                        scheduleCheck("deps change")
                    }
                }
            }
        )
    }

    fun scheduleInitialCheck() {
        scheduleCheck("startup", 0)
    }

    fun scheduleCheckNow(reason: String) {
        scheduleCheck(reason, 0)
    }

    private fun scheduleCheck(reason: String, delayMs: Int = DEPS_CHECK_DEBOUNCE_MS) {
        if (project.isDisposed) {
            return
        }

        if (checkInProgress.get()) {
            checkPending = true
            return
        }

        alarm.cancelAllRequests()
        alarm.addRequest({ runCheck(reason) }, delayMs)
    }

    private fun runCheck(reason: String) {
        if (!checkInProgress.compareAndSet(false, true)) {
            return
        }

        try {
            if (project.isDisposed) {
                return
            }
            LOG.debug("DepsCheckerService: Checking Mix deps ($reason)")
            val sdk = findElixirSdk(project)
            val projectRoots = selectTopLevelMixRoots(ProjectRootManager.getInstance(project).contentRootsFromAllModules)

            var sawSupported = false
            var sawNonOk = false

            for (root in projectRoots) {
                when (val statusResult = depsStatusResult(project, root, sdk)) {
                    is DepsStatusResult.Available -> {
                        sawSupported = true
                        if (statusResult.status.hasNonOk) {
                            sawNonOk = true
                        }
                    }
                    is DepsStatusResult.Error -> {
                        notifyOnEdt { Notifier.mixDepsCheckFailed(project, statusResult.message) }
                        return
                    }
                    DepsStatusResult.Unsupported -> Unit
                }
            }

            if (!sawSupported) {
                return
            }

            if (sawNonOk) {
                notifyOnEdt { Notifier.mixDepsOutdated(project) }
            } else {
                notifyOnEdt { Notifier.clearMixDepsOutdated(project) }
            }
        } finally {
            checkInProgress.set(false)
            if (checkPending) {
                checkPending = false
                scheduleCheck("pending")
            }
        }
    }

    private fun depsStatusResult(project: Project, projectRoot: VirtualFile, sdk: Sdk?): DepsStatusResult {
        val packageManagerVirtualFile = virtualFile(projectRoot)
            ?: return DepsStatusResult.Unsupported
        val (packageManager, packageVirtualFile) = packageManagerVirtualFile
        return packageManager.depsStatus(project, packageVirtualFile, sdk)
    }

    private fun findElixirSdk(project: Project): Sdk? {
        val elixirSdkType = ElixirSdkType.instance

        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk?.sdkType === elixirSdkType) {
            return projectSdk
        }

        ModuleManager.getInstance(project).modules.forEach { module ->
            val moduleSdk = ModuleRootManager.getInstance(module).sdk
            if (moduleSdk?.sdkType === elixirSdkType) {
                return moduleSdk
            }
        }

        return null
    }

    private fun isDepsChange(event: VFileEvent): Boolean {
        val eventPath = FileUtil.toSystemIndependentName(event.path)
        val contentRoots = selectTopLevelMixRoots(ProjectRootManager.getInstance(project).contentRootsFromAllModules)

        return contentRoots.any { root ->
            val depsPath = FileUtil.toSystemIndependentName(root.path) + "/deps"
            FileUtil.isAncestor(depsPath, eventPath, false)
        }
    }

    internal fun selectTopLevelMixRoots(roots: List<VirtualFile>): List<VirtualFile> {
        val mixRoots = roots.filter { virtualFile(it) != null }
        if (mixRoots.size <= 1) {
            return mixRoots
        }

        val mixRootPaths = mixRoots.map { FileUtil.toSystemIndependentName(it.path) }

        return mixRoots.filter { root ->
            val rootPath = FileUtil.toSystemIndependentName(root.path)
            mixRootPaths.none { otherPath ->
                otherPath != rootPath && FileUtil.isAncestor(otherPath, rootPath, false)
            }
        }
    }

    internal fun selectTopLevelMixRoots(roots: Array<out VirtualFile>): List<VirtualFile> =
        selectTopLevelMixRoots(roots.asList())

    private fun notifyOnEdt(action: () -> Unit) {
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                action()
            }
        }
    }

    override fun dispose() {}

    companion object {
        private const val DEPS_CHECK_DEBOUNCE_MS: Int = 1500
        private val LOG = logger<DepsCheckerService>()
    }
}
