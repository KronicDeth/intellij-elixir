package org.elixir_lang.facet

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.common.runAll
import org.elixir_lang.Facet
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

/**
 * Assigning a module's Elixir SDK must put that SDK's `CLASSES` roots into the module's own order
 * entries.
 *
 * On a small IDE there is no `JdkOrderEntry` - Settings → Languages & Frameworks → Elixir writes
 * through [Facet.sdk], and the module-level library it creates is the only thing that carries an
 * SDK's `ebin` directories into the project's index. Stub-indexed files under those directories are
 * what module-name completion reads, so a module library without them leaves the whole Elixir
 * standard library unreachable however correctly the SDK itself is configured.
 *
 * [ModuleSdkConfigurableTest] covers the same setter, but only ever compares SDK *names*; nothing
 * asserted that any root arrived.
 */
class FacetSdkRootsTest : PlatformTestCase() {

    private val added = mutableListOf<Sdk>()

    override fun setUp() {
        super.setUp()
        SdksService.getInstance()!!.resetForTests()
        ensureElixirFacet()
    }

    override fun tearDown() {
        runAll(
            {
                WriteAction.run<Throwable> {
                    FacetManager.getInstance(module).getFacetByType(Facet.ID)?.let { it.sdk = null }
                }
            },
            {
                WriteAction.run<Throwable> {
                    val table = ProjectJdkTable.getInstance()
                    added.filter { table.allJdks.contains(it) }.forEach { table.removeJdk(it) }
                }
                added.clear()
            },
            { SdksService.getInstance()!!.resetForTests() },
            { super.tearDown() },
        )
    }

    private fun ensureElixirFacet() {
        val facetManager = FacetManager.getInstance(module)

        if (facetManager.getFacetByType(Facet.ID) == null) {
            FacetUtil.addFacet(module, FacetType.findInstance(Type::class.java))
        }
    }

    /** An Elixir SDK in the table whose `CLASSES` roots are [classesRoots], as a real SDK would have. */
    private fun registerElixirSdk(name: String, vararg classesRoots: VirtualFile): Sdk {
        val sdk = ProjectJdkImpl(name, ElixirSdkType.instance)

        WriteAction.run<Throwable> {
            sdk.sdkModificator.apply {
                classesRoots.forEach { addRoot(it, OrderRootType.CLASSES) }
                commitChanges()
            }
            ProjectJdkTable.getInstance().addJdk(sdk)
        }

        added.add(sdk)
        SdksService.getInstance()!!.resetForTests()

        return sdk
    }

    private fun setFacetSdk(sdk: Sdk?) {
        WriteAction.run<Throwable> {
            FacetManager.getInstance(module).getFacetByType(Facet.ID)!!.sdk = sdk
        }
    }

    private fun ebinDir(app: String): VirtualFile =
        myFixture.tempDirFixture.findOrCreateDir("$app/lib/$app/ebin")

    /** The module library [Facet.sdk] creates for [name], or `null` if the assignment made none. */
    private fun facetLibraryEntry(name: String): LibraryOrderEntry? =
        ModuleRootManager
            .getInstance(module)
            .orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .firstOrNull { it.libraryName == name }

    /** Every `CLASSES` root the module's order entries expose - what the index actually scans. */
    private fun moduleClassesRoots(): kotlin.collections.List<VirtualFile> =
        ModuleRootManager.getInstance(module).orderEntries().classes().roots.toList()

    fun testAssignmentPutsSdkClassesRootsOnTheModule() {
        val ebin = ebinDir("assigned_elixir")
        val sdk = registerElixirSdk("Elixir Roots Test A", ebin)

        assertEquals(
            "Precondition: the SDK itself carries the root",
            listOf(ebin),
            sdk.rootProvider.getFiles(OrderRootType.CLASSES).toList()
        )

        setFacetSdk(sdk)

        val entry = facetLibraryEntry(sdk.name)
        assertNotNull("Assigning the SDK created no module library named after it", entry)
        assertEquals(
            "The module library named after the SDK carries none of its CLASSES roots",
            listOf(ebin),
            entry!!.getFiles(OrderRootType.CLASSES).toList()
        )
        assertTrue(
            "The SDK's ebin directory never reaches the module's CLASSES roots, so nothing under " +
                    "it is indexed for the module",
            moduleClassesRoots().contains(ebin)
        )
    }

    fun testReassignmentReplacesTheRootsOfThePreviousSdk() {
        val firstEbin = ebinDir("first_elixir")
        val secondEbin = ebinDir("second_elixir")
        val first = registerElixirSdk("Elixir Roots Test B", firstEbin)
        val second = registerElixirSdk("Elixir Roots Test C", secondEbin)

        setFacetSdk(first)
        setFacetSdk(second)

        assertNull("The replaced SDK's module library outlived the reassignment", facetLibraryEntry(first.name))
        assertTrue(
            "The newly assigned SDK's ebin directory did not reach the module's CLASSES roots",
            moduleClassesRoots().contains(secondEbin)
        )
        assertFalse(
            "The replaced SDK's ebin directory is still on the module's CLASSES roots",
            moduleClassesRoots().contains(firstEbin)
        )
    }
}
