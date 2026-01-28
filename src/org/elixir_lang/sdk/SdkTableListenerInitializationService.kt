package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.platform.eel.provider.LocalEelMachine
import com.intellij.platform.workspace.storage.InternalEnvironmentName
import com.intellij.util.Alarm
import com.intellij.workspaceModel.ide.JpsGlobalModelLoadedListener
import com.intellij.workspaceModel.ide.impl.GlobalWorkspaceModel
import com.intellij.workspaceModel.ide.impl.legacyBridge.sdk.SdkBridgeImpl.Companion.findSdkEntity
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private val LOG = logger<SdkTableListenerInitializationService>()
private const val MIGRATION_RETRY_DELAY_MS = 1000
private const val MIGRATION_MAX_ATTEMPTS = 10

@Service
class SdkTableListenerInitializationService {
    @Volatile
    private var initialized = false
    private val migrationAlarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, ApplicationManager.getApplication())
    private val migrationAttempts = AtomicInteger(0)
    private val migrationInProgress = AtomicBoolean(false)

    init {
        ensureInitialized()
    }

    fun ensureInitialized() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    LOG.trace("Setting up SDK table listeners")
                    org.elixir_lang.sdk.elixir.Type.setupSdkTableListener()
                    org.elixir_lang.sdk.erlang.Type.setupSdkTableListener()
                    subscribeToGlobalModelLoaded()
                    subscribeToSdkTableChanges()
                    scheduleElixirSdkAdditionalDataMigration("startup")
                    initialized = true
                    LOG.trace("SDK table listeners initialized successfully")
                }
            }
        }
    }

    private fun subscribeToGlobalModelLoaded() {
        val messageBus = ApplicationManager.getApplication().messageBus
        messageBus.connect().subscribe(JpsGlobalModelLoadedListener.LOADED, object : JpsGlobalModelLoadedListener {
            override fun loaded(environmentName: InternalEnvironmentName) {
                if (environmentName != InternalEnvironmentName.Local) {
                    return
                }
                scheduleElixirSdkAdditionalDataMigration("global model loaded")
            }
        })
    }

    private fun scheduleElixirSdkAdditionalDataMigration(reason: String) {
        if (!migrationInProgress.compareAndSet(false, true)) {
            LOG.trace("Elixir SDK additional data migration already in progress (reason=$reason)")
            return
        }

        migrationAttempts.set(0)
        migrationAlarm.cancelAllRequests()
        LOG.trace("Scheduling Elixir SDK additional data migration (reason=$reason)")
        migrationAlarm.addRequest({ runMigrationAttempt(reason) }, 0)
    }

    private fun subscribeToSdkTableChanges() {
        val messageBus = ApplicationManager.getApplication().messageBus
        messageBus.connect().subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
            override fun jdkAdded(jdk: Sdk) {
                val elixirSdkType = org.elixir_lang.sdk.elixir.Type.instance
                if (jdk.sdkType === elixirSdkType) {
                    LOG.trace("Elixir SDK added: ${jdk.name}; scheduling migration")
                    scheduleElixirSdkAdditionalDataMigration("sdk added")
                }
            }
        })
    }

    private fun runMigrationAttempt(reason: String) {
        val application = ApplicationManager.getApplication()
        if (application.isDisposed) {
            migrationInProgress.set(false)
            return
        }

        val attempt = migrationAttempts.get() + 1
        LOG.trace("Elixir SDK additional data migration attempt $attempt (reason=$reason)")
        ProjectJdkTable.getInstance().preconfigure()

        val elixirSdkType = org.elixir_lang.sdk.elixir.Type.instance
        val elixirSdks = ProjectJdkTable.getInstance().allJdks.filter { it.sdkType === elixirSdkType }
        if (elixirSdks.isEmpty()) {
            val nextAttempt = migrationAttempts.incrementAndGet()
            LOG.trace("No Elixir SDKs found for migration (attempt=$nextAttempt, reason=$reason)")
            if (nextAttempt <= MIGRATION_MAX_ATTEMPTS) {
                migrationAlarm.addRequest({ runMigrationAttempt(reason) }, MIGRATION_RETRY_DELAY_MS)
            } else {
                LOG.trace("Elixir SDK additional data migration skipped; no Elixir SDKs after $nextAttempt attempts ($reason)")
                migrationInProgress.set(false)
            }
            return
        }

        if (!areSdkEntitiesReady(elixirSdks)) {
            val nextAttempt = migrationAttempts.incrementAndGet()
            LOG.trace("Elixir SDK entities not ready for migration (attempt=$nextAttempt, reason=$reason)")
            if (nextAttempt <= MIGRATION_MAX_ATTEMPTS) {
                migrationAlarm.addRequest({ runMigrationAttempt(reason) }, MIGRATION_RETRY_DELAY_MS)
            } else {
                LOG.warn("Elixir SDK additional data migration delayed; SDK entities not ready after $nextAttempt attempts ($reason)")
                migrationInProgress.set(false)
            }
            return
        }

        LOG.trace("Migrating additional data for ${elixirSdks.size} Elixir SDK(s)")
        application.runWriteAction {
            migrateElixirSdkAdditionalData(elixirSdks)
        }
        migrationInProgress.set(false)
    }

    private fun areSdkEntitiesReady(elixirSdks: List<Sdk>): Boolean {
        val snapshot = GlobalWorkspaceModel.getInstance(LocalEelMachine).currentSnapshot
        val missing = elixirSdks.filter { snapshot.findSdkEntity(it) == null }
        if (missing.isNotEmpty()) {
            LOG.trace("Elixir SDK entities missing in workspace model: ${missing.joinToString { it.name }}")
        }
        return missing.isEmpty()
    }

    private fun migrateElixirSdkAdditionalData(elixirSdks: List<Sdk>) {
        if (elixirSdks.isEmpty()) return

        for (sdk in elixirSdks) {
            val existingAdditionalData = sdk.sdkAdditionalData as? SdkAdditionalData
            if (existingAdditionalData == null) {
                LOG.trace("Elixir SDK additional data missing or wrong type for ${sdk.name} (${sdk.javaClass.name})")
                continue
            }
            val modAdditionalData = existingAdditionalData.clone() as? SdkAdditionalData ?: continue
            val changed = modAdditionalData.refreshDerivedValues()
            val versionChanged = modAdditionalData.ensureDataVersion()
            val shouldPersist = changed || versionChanged
            LOG.trace(
                "Elixir SDK migration candidate: name=${sdk.name}, class=${sdk.javaClass.name}, " +
                    "home=${sdk.homePath}, mixHome=${modAdditionalData.getMixHome()}, " +
                    "mixHomeReplacePrefix=${modAdditionalData.getMixHomeReplacePrefix()}, " +
                    "wslUncPath=${modAdditionalData.isWslUncPath()}, changed=$changed, " +
                    "versionChanged=$versionChanged, shouldPersist=$shouldPersist"
            )
            if (!shouldPersist) {
                LOG.trace("Elixir SDK additional data unchanged for ${sdk.name}")
                continue
            }

            val modificator = sdk.sdkModificator
            modificator.sdkAdditionalData = modAdditionalData
            modificator.commitChanges()
            LOG.trace("Elixir SDK additional data migrated for ${sdk.name}")
        }
    }

    companion object {
        fun getInstance(): SdkTableListenerInitializationService = service()
    }
}
