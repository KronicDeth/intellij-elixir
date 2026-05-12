package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.Alarm
import org.elixir_lang.isElixirMixModule
import org.elixir_lang.mixContentRoots
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.package_manager.DepsStatusResult
import org.elixir_lang.package_manager.virtualFile
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.sdk.elixir.findElixirSdkForRoot
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.PROJECT)
class DepsCheckerService(private val project: Project) : Disposable {
    private val alarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    private val checkInProgress = AtomicBoolean(false)
    @Volatile
    private var checkPending = false

    /** Roots whose deps files changed during the current debounce window. */
    private val pendingRoots: MutableSet<VirtualFile> = ConcurrentHashMap.newKeySet()

    init {
        project.messageBus.connect(this).subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    val affectedRoots = findAffectedMixRoots(events)
                    if (affectedRoots.isNotEmpty()) {
                        pendingRoots.addAll(affectedRoots)
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
        if (!ElixirExperimentalSettings.instance.state.enableMixDepsCheck) {
            LOG.debug("DepsCheckerService: Mix deps check disabled in settings")
            return
        }

        if (!checkInProgress.compareAndSet(false, true)) {
            return
        }

        try {
            if (project.isDisposed) {
                return
            }

            // Drain the roots accumulated during the debounce window.
            // When empty (startup or explicit scheduleCheckNow), fall back to all Mix roots.
            val rootsToCheck: List<VirtualFile> = pendingRoots.toList().also { pendingRoots.clear() }
                .ifEmpty { selectTopLevelMixRoots(ProjectRootManager.getInstance(project).contentRootsFromAllModules) }

            LOG.debug("DepsCheckerService: Checking Mix deps ($reason) for ${rootsToCheck.size} root(s)")

            var sawSupported = false
            var sawNonOk = false

            for (root in rootsToCheck) {
                val sdk = findElixirSdkForRoot(project, root)
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

    /**
     * Returns the subset of top-level Mix roots (from Elixir Mix modules only) that are
     * touched by [events].  Only roots whose `deps/`, `_build/`, `mix.exs`, or `mix.lock`
     * paths are present among the event paths are returned.
     *
     * This is O(events × roots) rather than the previous O(events × allContentRoots),
     * and crucially skips non-Elixir modules entirely via [isElixirMixModule].
     */
    internal fun findAffectedMixRoots(events: List<VFileEvent>): Set<VirtualFile> {
        if (project.isDisposed) return emptySet()
        val eventPaths = events.map { FileUtil.toSystemIndependentName(it.path) }
        val candidateRoots = elixirMixContentRoots()
        return candidateRoots.filter { root ->
            val rootPath = FileUtil.toSystemIndependentName(root.path)
            eventPaths.any { eventPath -> isDepsPathForRoot(eventPath, rootPath) }
        }.toSet()
    }

    /**
     * Top-level Mix content roots from modules that pass [isElixirMixModule].
     *
     * Using module-level filtering here means non-Elixir modules (Java, Kotlin, etc.)
     * are excluded before any path comparison happens.
     */
    internal fun elixirMixContentRoots(): List<VirtualFile> {
        if (project.isDisposed) return emptyList()
        val allMixRoots = ModuleManager.getInstance(project).modules
            .filter { it.isElixirMixModule() }
            .flatMap { module -> module.mixContentRoots().map { it.root } }
        return selectTopLevelMixRoots(allMixRoots)
    }

    internal fun isDepsPathForRoot(eventPath: String, rootPath: String): Boolean =
        FileUtil.isAncestor("$rootPath/deps", eventPath, false) ||
                FileUtil.isAncestor("$rootPath/_build", eventPath, false) ||
                FileUtil.pathsEqual(eventPath, "$rootPath/mix.exs") ||
                FileUtil.pathsEqual(eventPath, "$rootPath/mix.lock")

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
