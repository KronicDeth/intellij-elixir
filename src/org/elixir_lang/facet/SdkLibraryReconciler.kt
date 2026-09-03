package org.elixir_lang.facet

import com.intellij.configurationStore.StoreUtil
import com.intellij.facet.FacetManager
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.application.readAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.OrderRootType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.Facet

private val LOG = logger<SdkLibraryReconciler>()

/**
 * Repairs modules whose Elixir SDK is assigned but whose roots never arrived.
 *
 * Outside IntelliJ IDEA a module has no SDK entry, so [Facet.sdk] stands in by creating a module
 * library named after the SDK and copying the SDK's roots onto it. Versions before that copying
 * existed created the library empty, and the assignment is only re-run when a user changes the SDK
 * in Settings - which they have no reason to do, and which
 * [Configurable.isModified] would ignore anyway if they re-picked the SDK already selected. So an
 * affected project stays broken indefinitely on its own.
 *
 * The repair is to add the SDK's roots to the library that is already there.
 */
object SdkLibraryReconciler {

    /**
     * Modules of [project] whose Facet SDK resolves but whose module library carries none of that
     * SDK's roots, while the SDK itself has some.
     *
     * The last clause matters: an SDK whose own roots are empty (a broken install, or one whose
     * paths have not been detected yet) would otherwise look like this defect forever and be
     * "repaired" on every open to no effect.
     */
    private fun needsRepair(module: Module): Boolean {
        val sdk = Facet.sdk(module)
        if (sdk == null) {
            LOG.debug("needsRepair('${module.name}'): no Facet SDK resolves")
            return false
        }

        val sdkRootCount = ROOT_TYPES.sumOf { sdk.rootProvider.getUrls(it).size }
        if (sdkRootCount == 0) {
            LOG.debug("needsRepair('${module.name}'): SDK '${sdk.name}' declares no roots of its own")
            return false
        }

        val entry = module
            .rootManager
            .orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .firstOrNull { it.libraryName == sdk.name }

        if (entry == null) {
            val libraryNames = module.rootManager.orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .map { it.libraryName }
            LOG.debug("needsRepair('${module.name}'): no library entry named '${sdk.name}'; entries are $libraryNames")
            return false
        }

        val entryRootCount = ROOT_TYPES.sumOf { entry.getRootUrls(it).size }
        LOG.debug(
            "needsRepair('${module.name}'): SDK '${sdk.name}' declares $sdkRootCount root(s), " +
                    "its module library carries $entryRootCount"
        )

        return entryRootCount == 0
    }

    /**
     * Adds the missing roots to the empty Facet library of every module of [project] that
     * [needsRepair].
     *
     * Re-runs [Facet.sdk]'s setter rather than editing the existing library in place. A module-level
     * library belongs to the module's [com.intellij.openapi.roots.ModifiableRootModel]; an earlier
     * cut of this added the roots through the library's own modifiable model, outside that
     * transaction, and completion in a RubyMine sandbox still found nothing afterwards. Re-assigning
     * takes the same path a user's Apply does, which is the one observed to work. Why the other does
     * not is unestablished - neither the light nor the heavy fixture can tell the two apart, so treat
     * this as a route chosen by observation rather than a mechanism that has been pinned down.
     *
     * Callers must have waited for the JPS model: the workspace model is loaded from a binary cache
     * at startup and the real `.iml` state is applied over it afterwards, so a repair written before
     * that lands is both decided on cached state and silently discarded.
     */
    suspend fun repair(project: Project) {
        val stale = readAction {
            if (project.isDisposed) {
                emptyList()
            } else {
                ModuleManager.getInstance(project).modules.filter(::needsRepair)
            }
        }

        LOG.debug("repair('${project.name}'): ${stale.size} module(s) need their SDK roots attached")

        if (stale.isEmpty()) return

        var repaired = false

        edtWriteAction {
            for (module in stale) {
                if (module.isDisposed) continue

                val facet = FacetManager.getInstance(module).getFacetByType(Facet.ID) ?: continue
                val sdk = facet.sdk ?: continue

                LOG.info("Attaching roots of Elixir SDK '${sdk.name}' to module '${module.name}'")

                facet.sdk = sdk
                repaired = true
            }
        }

        if (!repaired) return

        // Write the repaired module files out, so the project is fixed rather than merely behaving as
        // if it were. Without this the roots live only in the workspace model until something else
        // happens to trigger a save, the `.iml` keeps the empty library, and every subsequent open
        // repairs it again.
        //
        // This is not a guard against DelayedProjectSynchronizer overwriting the repair - waiting for
        // the JPS model above is what handles that, which is the use its own documentation puts
        // JpsProjectLoadingManager to.
        withContext(Dispatchers.IO) {
            StoreUtil.saveSettings(project, true)
        }
    }

    /** The root types [Facet.sdk] copies, and therefore the ones whose absence means a repair. */
    private val ROOT_TYPES = listOf(OrderRootType.CLASSES, OrderRootType.SOURCES)
}
