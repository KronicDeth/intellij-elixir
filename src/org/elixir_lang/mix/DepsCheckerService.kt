package org.elixir_lang.mix
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.elixir_lang.mix.sync.MixEventClassifier
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.package_manager.DepsStatusResult
import org.elixir_lang.package_manager.virtualFile
import org.elixir_lang.sdk.elixir.findElixirSdkForRoot
import org.elixir_lang.settings.ElixirExperimentalSettings
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds
@Service(Service.Level.PROJECT)
class DepsCheckerService(private val project: Project, private val cs: CoroutineScope) {
    private val checkMutex = Mutex()
    /** Roots whose deps files changed during the current debounce window. */
    private val pendingRoots: AtomicReference<Set<VirtualFile>> = AtomicReference(emptySet())
    private val checkFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    init {
        project.messageBus.connect(cs).subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    val affectedRoots = MixEventClassifier.findAffectedMixRoots(project, events)
                    if (affectedRoots.isNotEmpty()) {
                        pendingRoots.getAndUpdate { it + affectedRoots }
                        checkFlow.tryEmit("deps change")
                    }
                }
            }
        )
        @OptIn(FlowPreview::class)
        cs.launch {
            checkFlow
                .debounce(DEPS_CHECK_DEBOUNCE_MS.milliseconds)
                .collect { reason ->
                    supervisorScope {
                        val checkAttempt = async { runCheck(reason) }

                        try {
                            checkAttempt.await()
                        } catch (
                            @Suppress("IncorrectCancellationExceptionHandling")
                            _: CancellationException
                        ) {
                            currentCoroutineContext().ensureActive()
                            LOG.debug("DepsCheckerService: check cancelled, will retry on next trigger")
                        } catch (e: Throwable) {
                            LOG.error("DepsCheckerService: check failed, will retry on next trigger", e)
                        }
                    }
                }
        }
    }
    fun scheduleInitialCheck() {
        cs.launch { runCheck("startup") }
    }
    fun scheduleCheckNow(reason: String) {
        cs.launch { runCheck(reason) }
    }
    private suspend fun runCheck(reason: String) {
        if (!ElixirExperimentalSettings.instance.state.enableMixDepsCheck) {
            LOG.debug("DepsCheckerService: Mix deps check disabled in settings")
            return
        }
        checkMutex.withLock {
            if (project.isDisposed) {
                return
            }
            // Drain the roots accumulated during the debounce window atomically.
            // When empty (startup or explicit scheduleCheckNow), fall back to all Mix roots.
            val rootsToCheck: List<VirtualFile> = pendingRoots.getAndSet(emptySet()).toList()
                .ifEmpty {
                    val allRoots = readAction { ProjectRootManager.getInstance(project).contentRoots.asList() }
                    MixEventClassifier.selectTopLevelMixRoots(allRoots)
                }
            LOG.debug("DepsCheckerService: Checking Mix deps ($reason) for ${rootsToCheck.size} root(s)")
            var sawSupported = false
            var sawNonOk = false
            for (root in rootsToCheck) {
                val sdk = readAction { findElixirSdkForRoot(project, root) }
                when (val statusResult = withContext(Dispatchers.IO) { depsStatusResult(project, root, sdk) }) {
                    is DepsStatusResult.Available -> {
                        sawSupported = true
                        if (statusResult.status.hasNonOk) {
                            sawNonOk = true
                        }
                    }
                    is DepsStatusResult.Error -> {
                        withContext(Dispatchers.EDT) {
                            if (!project.isDisposed) Notifier.mixDepsCheckFailed(project, statusResult.message)
                        }
                        return
                    }
                    DepsStatusResult.Unsupported -> Unit
                }
            }
            if (!sawSupported) {
                return
            }
            withContext(Dispatchers.EDT) {
                if (!project.isDisposed) {
                    if (sawNonOk) {
                        Notifier.mixDepsOutdated(project)
                    } else {
                        Notifier.clearMixDepsOutdated(project)
                    }
                }
            }
        }
    }
    private fun depsStatusResult(project: Project, projectRoot: VirtualFile, sdk: Sdk?): DepsStatusResult {
        val packageManagerVirtualFile = virtualFile(projectRoot)
            ?: return DepsStatusResult.Unsupported
        val (packageManager, packageVirtualFile) = packageManagerVirtualFile
        return packageManager.depsStatus(project, packageVirtualFile, sdk)
    }
    companion object {
        private const val DEPS_CHECK_DEBOUNCE_MS: Long = 1500
        private val LOG = logger<DepsCheckerService>()
    }
}
